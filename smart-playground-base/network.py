import socket
import services
import threading
import logging
import json
import sys
import smart_objects
from threading import Thread, RLock, Condition
import collections
import smart_objects
import game

LOCAL_HOST = '127.0.0.1'


class DiscoveryServer(threading.Thread):

    RCV_BUFFER_SIZE = 1024

    _instance = None

    @staticmethod
    def get_instance():
        if DiscoveryServer._instance is None:
            DiscoveryServer._instance = DiscoveryServer()
        return DiscoveryServer._instance

    def __init__(self):
        if DiscoveryServer._instance is not None:
            raise Exception('Cannot create multiple instances of a Singleton class')

        threading.Thread.__init__(self)
        self.setDaemon(True)

        # create a UDP socket.
        try:
            self.discovery_server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            self.discovery_server_socket.bind(('', services.NET_DISCOVERY_PORT))
        except socket.error as e:
            logging.error(e)
            sys.exit(1)

        logging.debug('Discovery Server - UDP socket created.')

    def run(self):
        while True:
            # wait for incoming data
            logging.debug('Discovery Server - waiting for incoming data...')
            try:
                _, addr = self.discovery_server_socket.recvfrom(self.RCV_BUFFER_SIZE)
            except socket.error as e:
                logging.error(e)
                sys.exit(1)

            # send discovery response
            logging.debug('Discovery Server - sending discovery response...')
            try:
                self.discovery_server_socket.sendto(bytes([services.DISCOVERY_SERVICE_CODE]), addr)
            except socket.error as e:
                logging.error(e)
                sys.exit(1)       


class ConsoleCommandReader(Thread):
    def __init__(self, inputStream, networkCommunicator):
        Thread.__init__(self)
        self.setDaemon(True)
        self.inputStream = inputStream
        self.networkCommunicator = networkCommunicator
    
    def run(self):
        while True:
            try:
                jsonObjStr = self.inputStream.readline()
                cmdJson = json.loads(jsonObjStr)

                if cmdJson['type'] == 'lights_cmd':
                    smart_objects.SmartObjectsMediator.get_current_instance().smart_field.set_lights(int(cmdJson['pattern']))
                elif cmdJson['type'] == 'fans_cmd':
                    smart_objects.SmartObjectsMediator.get_current_instance().smart_field.set_fans(int(cmdJson['pattern']))
                elif cmdJson['type'] == 'game_init':
                    game.initializeGame(cmdJson, self.networkCommunicator)
            except Exception as e:
                print("ConsoleCommandReader closed: " + str(e))
                break


class NetworkCommunicator(Thread):
    
    def __init__(self, serverPort, callback=None):
        Thread.__init__(self)
        self.setDaemon(True)
        self.serverPort = serverPort
        self.callback = callback
        self.has_connection = False
        self.pendingData = collections.deque()
        self.lock = RLock()
        self.cond = Condition(self.lock)
    
    def sendData(self, data):
        with self.lock:
            if self.has_connection:
                self.pendingData.append(str(data))
                self.cond.notify()
    
    def run(self):
        # create a TCP/IP socket.
        if not self.createSocket():
            return
        
        while True:
            # wait for a connection.
            connection, _ = self.server_socket.accept()
            inputFile = connection.makefile('r')
            outputFile = connection.makefile('w')

            self.consoleCommandReader = ConsoleCommandReader(inputFile, self)
            self.consoleCommandReader.start()
            
            with self.lock:
                
                self.pendingData.clear()
                self.has_connection = True
            
                try:
                    while True:
                        while len(self.pendingData) == 0:
                            self.cond.wait()
                        
                        while len(self.pendingData) > 0:
                            outputFile.writelines(self.pendingData.popleft()+"\n")
                        outputFile.flush()
                except Exception:
                    connection.close()
                    print("Connection closed")
                
                self.has_connection = False
        
        if self.server_socket:
            self.server_socket.close()
            print("Server socket closed")
    
    def createSocket(self):
        try:
            self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            self.server_socket.bind(('', self.serverPort))
            self.server_socket.listen(1)
            return True
        except socket.error as e:
            logging.error(e)
            self.server_socket.close()
            return False
        

class EcosystemEventProvider:
    
    _instance = None
    _lock = RLock()

    @staticmethod
    def get_instance():
        with EcosystemEventProvider._lock:
            if EcosystemEventProvider._instance is None:
                EcosystemEventProvider._instance = EcosystemEventProvider()
            return EcosystemEventProvider._instance

    def __init__(self):
        if EcosystemEventProvider._instance is not None:
            raise Exception('Cannot create multiple instances of a Singleton class')
        
        self.netcomm = NetworkCommunicator(services.NET_EVENT_PROVIDER_PORT)
        self.netcomm.start()
        logging.debug('Ecosystem Event Provider - Network Communicator created.')
    
    def notify_user_ack(self):
        self.netcomm.sendData( { 'dataType': services.USER_ACK_CODE } )

    def send_smart_game_platform_sensors_sample(self, sample):
        self.send_sensors_sample(services.SMART_GAME_PLATFORM_SENSORS_SAMPLE_CODE, sample)

    def send_smartball_sensors_sample(self, sample):
        self.send_sensors_sample(services.SMARTBALL_SENSORS_SAMPLE_CODE, sample)
    
    def send_smartfield_sensors_sample(self, sample):
        self.send_sensors_sample(services.SMARTFIELD_SENSORS_SAMPLE_CODE, sample)

    def send_smartpole_sensors_sample(self, sample):
        self.send_sensors_sample(services.SMARTPOLE_SENSORS_SAMPLE_CODE, sample)

    def send_smart_racket_status_sample(self, status_sample):
        accs_values = list()
        accs_values.append(status_sample.accXValues)
        accs_values.append(status_sample.accYValues)
        accs_values.append(status_sample.accZValues)
        
        self.netcomm.sendData( { 'dataType': EcosystemEventProvider.get_smart_racket_source_code(status_sample.id), 'accs_values': accs_values } )

    def send_sensors_sample(self, source_code, sample):
        self.netcomm.sendData( { 'dataType': source_code, 'sample': sample } )
    
    def send_field_wind_status(self, dir):
        if dir is None:
            self.netcomm.sendData( { 'dataType': 'WIND_STATUS', 'status': 'off'} )
        else:
            self.netcomm.sendData( { 'dataType': 'WIND_STATUS', 'status': 'on', 'dir': dir } )

    @staticmethod
    def handle_data_request(data):
        if data.startswith(services.CLOSE_CONNECTION_CODE.encode()):
            raise ConnectionError

    @staticmethod
    def get_smart_racket_source_code(id):
        if id == smart_objects.MAIN_SMART_RACKET:
            return services.MAIN_SMART_RACKET_STATUS_SAMPLE_CODE
        elif id == smart_objects.SECOND_SMART_RACKET:
            return services.MAIN_SMART_RACKET_STATUS_SAMPLE_CODE
        return None
