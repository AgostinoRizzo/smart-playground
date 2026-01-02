from threading import Thread, RLock, Event
import socket
import services
import struct



class PointLocation:
    
    def __init__(self, left=0, top=0):
        self.left = left
        self.top = top


class PlaygroundBaseStatus:
    
    """
    thread-safe playground-base-status class
    """
    
    def __init__(self):
        self.ballLocation = PointLocation(-1.0, -1.0)
        self.lock = RLock()
    
    def update_ball_location(self, left, top):
        with self.lock:
            self.ballLocation.left = left
            self.ballLocation.top = top
    
    def get_ball_location(self) -> PointLocation:
        with self.lock:
            return PointLocation(self.ballLocation.left, self.ballLocation.top)
    
    def is_ball_in(self) -> bool:
        with self.lock:
            return self.ballLocation.left < 0.0 or self.ballLocation.top < 0.0
    
    def get_ascii_status(self):
        full_width = 50
        full_height = 30
        width = full_width - 2
        height = full_height - 2
        
        status = [[' ' for _ in range(full_width)] for _ in range(full_height)]
        for j in range(full_width):
            status[0][j] = status[-1][j] = '#'
        for i in range(full_height):
            status[i][0] = status[i][-1] = '#'
        
        with self.lock:
            status[int((height - 1) * self.ballLocation.top) + 1][int((width - 1) * self.ballLocation.left) + 1] = '@'
            ascii_status = ''
            for i in range(full_height):
                ascii_status += ' '.join(status[i]) + "\n"
            return ascii_status


class BallTracker(Thread):
    
    BALL_DATA_SOCKET_READ_TIMEOUT = 2.0
    BALL_DATA_BUFFER_SIZE = 12
    
    def __init__(self, playgroundBaseStatus:PlaygroundBaseStatus):
        Thread.__init__(self)
        self.stopEvent = Event()
        
        self.playgroundBaseStatus = playgroundBaseStatus
        
        self.ballDataSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.ballDataSocket.bind(('', services.BALL_TRACKING_SOCKET_PORT))
        self.ballDataSocket.settimeout(BallTracker.BALL_DATA_SOCKET_READ_TIMEOUT)
        
        self.seqdata_number = 0
    
    def finalize(self):
        self.stopEvent.set()
        self.join()
        self.ballDataSocket.close()
    
    def run(self):
        
        while not self.stopEvent.is_set():
            try:
                databuff, _ = self.ballDataSocket.recvfrom(BallTracker.BALL_DATA_BUFFER_SIZE)
                self.__on_ball_data(databuff)
                    
            except socket.timeout:
                pass
    
    def __on_ball_data(self, databuff):
        
        if len(databuff) != BallTracker.BALL_DATA_BUFFER_SIZE:
            return
        
        seqnumber, = struct.unpack_from('>i', databuff, 0)
        ball_left, = struct.unpack_from('>f', databuff, 4)
        ball_top, = struct.unpack_from('>f', databuff, 8)
        
        if seqnumber > self.seqdata_number:
            self.playgroundBaseStatus.update_ball_location(ball_left, ball_top)
            print(self.playgroundBaseStatus.get_ascii_status())
            self.seqdata_number += 1
    


playgroundBaseStatus = PlaygroundBaseStatus()
ballTracker = None

def initialize():
    global playgroundBaseStatus
    global ballTracker
    ballTracker = BallTracker(playgroundBaseStatus)
    ballTracker.start()

def finalize():
    global ballTracker
    ballTracker.finalize()
        