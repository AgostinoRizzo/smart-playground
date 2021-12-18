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
        self.ballOrientation = -1
        self.lock = RLock()
    
    def update_ball_location(self, left, top):
        with self.lock:
            self.ballLocation.left = left
            self.ballLocation.top = top
            #print("New ball location: %f, %f" % (left, top))
    
    def update_ball_orientation(self, orientation):
        with self.lock:
            self.ballOrientation = orientation
            #print("New ball orientation: %d" % orientation)
    
    def set_unknown_ball_status(self):
        with self.lock:
            self.ballLocation.left = -1.0
            self.ballLocation.top = -1.0
            self.ballOrientation = -1
            #print("Ball status unknown.")
    
    def get_ball_location(self) -> PointLocation:
        with self.lock:
            if self.__check_ball_status():
                return PointLocation(self.ballLocation.left, self.ballLocation.top)
            return None
    
    def is_ball_in(self) -> bool:
        with self.lock:
            return self.__check_ball_status()
    
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
            if self.__check_ball_status():
                status[int((height - 1) * self.ballLocation.top) + 1][int((width - 1) * self.ballLocation.left) + 1] = '@'
            ascii_status = ''
            for i in range(full_height):
                ascii_status += ' '.join(status[i]) + "\n"
            return ascii_status
    
    def __check_ball_status(self) -> bool:
        return self.ballLocation.left >= 0.0 and self.ballLocation.top >= 0.0 and self.ballOrientation >= 0.0


class BallTracker(Thread):
    
    BALL_DATA_SOCKET_READ_TIMEOUT = 2.0
    MAX_BALL_DATA_BUFFER_SIZE = 14
    BALL_STATUS_DATA_BUFFER_SIZES = [4, 6, 12, 14]
    
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
                databuff, _ = self.ballDataSocket.recvfrom(BallTracker.MAX_BALL_DATA_BUFFER_SIZE)
                self.__on_ball_data(databuff)
                    
            except socket.timeout:
                pass
    
    def __on_ball_data(self, databuff):
        
        databuff_length = len(databuff)
        
        if databuff_length not in BallTracker.BALL_STATUS_DATA_BUFFER_SIZES:
            return
        
        buff_offset = 0
        
        seqnumber, = struct.unpack_from('>i', databuff, buff_offset)
        buff_offset += 4
        
        if seqnumber <= self.seqdata_number:
            return
        
        if databuff_length >= 12:
            
            ball_left, = struct.unpack_from('>f', databuff, buff_offset)
            buff_offset += 4
            ball_top, = struct.unpack_from('>f', databuff, buff_offset)
            buff_offset += 4
            self.playgroundBaseStatus.update_ball_location(ball_left, 1.0 - ball_top)
            
        if databuff_length == 6 or databuff_length == 14:
            ball_orientation, = struct.unpack_from('>h', databuff, buff_offset)
            buff_offset += 2
            self.playgroundBaseStatus.update_ball_orientation(ball_orientation)
        
        if databuff_length == 4:
            self.playgroundBaseStatus.set_unknown_ball_status()
        
        self.seqdata_number = seqnumber
        #print(self.playgroundBaseStatus.get_ascii_status())
    


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
        