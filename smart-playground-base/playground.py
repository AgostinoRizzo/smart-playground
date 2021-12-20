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
        self.absPlayerOrientation = -1.0
        self.playerOrientationAnchor = 0
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
    
    def update_abs_player_orientation(self, newAbsOrientation):
        with self.lock:
            self.absPlayerOrientation = newAbsOrientation
    
    def sync_player_orientation(self, orientationAnchor):
        with self.lock:
            self.playerOrientationAnchor = self.absPlayerOrientation = orientationAnchor
    
    def set_unknown_player_orientation(self):
        with self.lock:
            self.absPlayerOrientation = -1.0
    
    def get_ball_location(self) -> PointLocation:
        with self.lock:
            if self.__check_ball_status():
                return PointLocation(self.ballLocation.left, self.ballLocation.top)
            return None
    
    def is_ball_in(self) -> bool:
        with self.lock:
            return self.__check_ball_status()
    
    def is_player_orientation_known(self) -> bool:
        with self.lock:
            return self.absPlayerOrientation >= 0.0
    
    def get_abs_player_orientation(self) -> float:
        with self.lock:
            return self.absPlayerOrientation
    
    def get_relative_player_orientation(self) -> float:
        with self.lock:
            if self.absPlayerOrientation < 0.0:
                return self.absPlayerOrientation
            return (self.absPlayerOrientation - self.playerOrientationAnchor) % 360.0
    
    def get_player_ascii_status(self) -> str:
        status_str = 'PLAYER ORIENTATION: '
        
        with self.lock:
            if self.is_player_orientation_known():
                status_str += str(self.get_relative_player_orientation()) + ' (' + str(self.get_abs_player_orientation()) + ')'
            else:
                status_str += 'unknown'
        
        return status_str

    def get_ball_ascii_status(self) -> str:
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


class BallTrackerMotionController(Thread):
    
    DATA_SOCKET_READ_TIMEOUT = 2.0
    MAX_DATA_BUFFER_SIZE = 14
    STATUS_DATA_BUFFER_SIZES = [4, 5, 6, 9, 12, 14]

    PACKET_TYPE_ORIENTATION_UPDATE  = 1
    PACKET_TYPE_ORIENTATION_SYNC    = 2
    PACKET_TYPE_ORIENTATION_UNKNOWN = 3
    
    def __init__(self, playgroundBaseStatus:PlaygroundBaseStatus):
        Thread.__init__(self)
        self.stopEvent = Event()
        
        self.playgroundBaseStatus = playgroundBaseStatus
        
        self.dataSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.dataSocket.bind(('', services.BALL_TRACKING_MOTION_CTRL_SOCKET_PORT))
        self.dataSocket.settimeout(BallTrackerMotionController.DATA_SOCKET_READ_TIMEOUT)
        
        self.balltrack_seqdata_number = 0
        self.motionctrl_seqdata_number = 0
    
    def finalize(self):
        self.stopEvent.set()
        self.join()
        self.dataSocket.close()
    
    def run(self):
        
        while not self.stopEvent.is_set():
            try:
                databuff, _ = self.dataSocket.recvfrom(BallTrackerMotionController.MAX_DATA_BUFFER_SIZE)
                self.__on_data(databuff)
                    
            except socket.timeout:
                pass
    
    def __on_data(self, databuff):
        
        databuff_length = len(databuff)
        
        if databuff_length not in BallTrackerMotionController.STATUS_DATA_BUFFER_SIZES:
            return
        
        if databuff_length == 4 or databuff_length == 6 or databuff_length == 14:
            self.__on_balltrack_data(databuff, databuff_length)
        elif databuff_length == 5 or databuff_length == 9:
            self.__on_motionctrl_data(databuff, databuff_length)
    
    def __on_balltrack_data(self, databuff, databuff_length):
        buff_offset = 0
        
        seqnumber, = struct.unpack_from('>i', databuff, buff_offset)
        buff_offset += 4
        
        if seqnumber <= self.balltrack_seqdata_number:
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
        
        self.balltrack_seqdata_number = seqnumber
        #print(self.playgroundBaseStatus.get_ball_ascii_status())

    def __on_motionctrl_data(self, databuff, databuff_length):
        buff_offset = 0
        
        seqnumber, = struct.unpack_from('>i', databuff, buff_offset)
        buff_offset += 4
        
        if seqnumber <= self.motionctrl_seqdata_number:
            return
        
        ptype = struct.unpack_from('>b', databuff, buff_offset)[0]
        buff_offset += 1

        if databuff_length == 9:
            orientation = struct.unpack_from('>f', databuff, buff_offset)[0]
            buff_offset += 4

            if ptype == BallTrackerMotionController.PACKET_TYPE_ORIENTATION_UPDATE:
                self.playgroundBaseStatus.update_abs_player_orientation(orientation)
            elif ptype == BallTrackerMotionController.PACKET_TYPE_ORIENTATION_SYNC:
                self.playgroundBaseStatus.sync_player_orientation(orientation)
        elif databuff_length == 5 and ptype == BallTrackerMotionController.PACKET_TYPE_ORIENTATION_UNKNOWN:
            self.playgroundBaseStatus.set_unknown_player_orientation()
        
        self.motionctrl_seqdata_number = seqnumber
        #print(self.playgroundBaseStatus.get_player_ascii_status())
    


playgroundBaseStatus = PlaygroundBaseStatus()
ballTrackerMotionCrtl = None

def initialize():
    global playgroundBaseStatus
    global ballTrackerMotionCrtl
    ballTrackerMotionCrtl = BallTrackerMotionController(playgroundBaseStatus)
    ballTrackerMotionCrtl.start()

def finalize():
    global ballTrackerMotionCrtl
    ballTrackerMotionCrtl.finalize()
        