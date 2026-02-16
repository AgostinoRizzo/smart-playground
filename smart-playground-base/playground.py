#!/usr/bin/python3

import logging
from threading import Condition, Thread, RLock, Event
from util_logging import Logger
import socket
import services
import struct
import game
import time



class PointLocation:
    
    def __init__(self, left=0, top=0):
        self.left = left
        self.top = top
    
    def set(self, other:"PointLocation"):
        self.left = other.left
        self.top = other.top
    
    def isEqualsTo(self, other:"PointLocation") -> bool:
        return self.left == other.left and self.top == other.top


class PlaygroundBaseStatus:
    
    """
    thread-safe playground-base-status class
    """
    
    def __init__(self):
        self.ballLocation = PointLocation(-1.0, -1.0)
        self.ballOrientation = -1
        self.absPlayerOrientation = -1.0
        self.playerOrientationAnchor = 0
        self.windDirection = None
        self.lock = RLock()
    
    def update_ball_location(self, left, top):
        with self.lock:
            self.ballLocation.left = left
            self.ballLocation.top = top
            game.onBallLocationUpdate(self.ballLocation)
            #print("New ball location: %f, %f" % (left, top))
    
    def update_ball_orientation(self, orientation):
        with self.lock:
            self.ballOrientation = orientation
            game.onBallOrientationUpdate(self.ballOrientation)
            #print("New ball orientation: %d" % orientation)
    
    def set_unknown_ball_status(self):
        with self.lock:
            self.ballLocation.left = -1.0
            self.ballLocation.top = -1.0
            self.ballOrientation = -1
            game.onBallLocationUpdate(self.ballLocation)
            game.onBallOrientationUpdate(self.ballOrientation)
            #print("Ball status unknown.")
    
    def update_abs_player_orientation(self, newAbsOrientation):
        with self.lock:
            self.absPlayerOrientation = newAbsOrientation
            game.onPlayerOrientationUpdateUpdate(self.get_relative_player_orientation())
    
    def sync_player_orientation(self, orientationAnchor):
        with self.lock:
            self.playerOrientationAnchor = self.absPlayerOrientation = orientationAnchor
            game.onPlayerOrientationUpdateUpdate(self.get_relative_player_orientation())
    
    def set_unknown_player_orientation(self):
        with self.lock:
            self.absPlayerOrientation = -1.0
            game.onPlayerOrientationUpdateUpdate(self.get_relative_player_orientation())
    
    def updateFieldWindStatus(self, windDir):
        with self.lock:
            self.windDirection = windDir

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
            if self.absPlayerOrientation == self.playerOrientationAnchor:
                return 0.0
            if self.absPlayerOrientation > self.playerOrientationAnchor:
                return self.absPlayerOrientation - self.playerOrientationAnchor
            return 360.0 - (self.playerOrientationAnchor - self.absPlayerOrientation)
    
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
    
    def getBallSwingEffectDirection(self):
        """ assume interval 0-359 """
        with self.lock:

            if self.windDirection is None or not self.__check_ball_status(): return 0
            if self.ballOrientation == self.windDirection: return 0

            relativeWindDirection = (self.windDirection - self.ballOrientation) % 360

            if relativeWindDirection >=  45 and relativeWindDirection <= 135: return  1
            if relativeWindDirection >= 225 and relativeWindDirection <= 315: return -1
            return 0
            

    def __check_ball_status(self) -> bool:
        return self.ballLocation.left >= 0.0 and self.ballLocation.top >= 0.0 and self.ballOrientation >= 0.0


class BallTrackerMotionController(Thread):
    
    DATA_SOCKET_READ_TIMEOUT = 2.0
    MAX_DATA_BUFFER_SIZE = 14
    STATUS_DATA_BUFFER_SIZES = [4, 5, 6, 9, 12, 14]

    PACKET_TYPE_ORIENTATION_UPDATE  = 1
    PACKET_TYPE_ORIENTATION_SYNC    = 2
    PACKET_TYPE_ORIENTATION_UNKNOWN = 3

    MAX_SEQDATA_RESET_NUMBER = 50
    
    def __init__(self, playgroundBaseStatus:PlaygroundBaseStatus):
        Thread.__init__(self)
        self.stopEvent = Event()
        
        self.playgroundBaseStatus = playgroundBaseStatus
        
        self.dataSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.dataSocket.bind(('', services.BALL_TRACKING_MOTION_CTRL_SOCKET_PORT))
        self.dataSocket.settimeout(BallTrackerMotionController.DATA_SOCKET_READ_TIMEOUT)
        
        self.balltrack_seqdata_number = 0
        self.motionctrl_seqdata_number = 0

        self.is_balltrack_seqdata_number_reset = False
        self.is_motionctrl_seqdata_number_reset = False
    
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
        
        if databuff_length == 4 or databuff_length == 6 or databuff_length == 12 or databuff_length == 14:
            self.__on_balltrack_data(databuff, databuff_length)
        elif databuff_length == 5 or databuff_length == 9:
            self.__on_motionctrl_data(databuff, databuff_length)
    
    def __on_balltrack_data(self, databuff, databuff_length):
        buff_offset = 0
        
        seqnumber, = struct.unpack_from('>i', databuff, buff_offset)
        buff_offset += 4
        
        if seqnumber <= BallTrackerMotionController.MAX_SEQDATA_RESET_NUMBER:
            self.balltrack_seqdata_number = 0
            if not self.is_balltrack_seqdata_number_reset:
                Logger.default().info("Ball Tracking Seqnumb RESET.")
                self.is_balltrack_seqdata_number_reset = True
        else:
            self.is_balltrack_seqdata_number_reset = False
            if seqnumber <= self.balltrack_seqdata_number:
                return
        
        if databuff_length >= 12:
            
            ball_left, = struct.unpack_from('>f', databuff, buff_offset)
            buff_offset += 4
            ball_top, = struct.unpack_from('>f', databuff, buff_offset)
            buff_offset += 4
            self.playgroundBaseStatus.update_ball_location(ball_left, ball_top)
            
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
        
        if seqnumber <= BallTrackerMotionController.MAX_SEQDATA_RESET_NUMBER:
            self.motionctrl_seqdata_number = 0
            if not self.is_motionctrl_seqdata_number_reset:
                Logger.default().info("Motion Ctrl Seqnumb RESET.")
                self.is_motionctrl_seqdata_number_reset = True
        else:
            self.is_motionctrl_seqdata_number_reset = False
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
                Logger.default().info("Player orientaton SYNC.")
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

def updateFieldWindStatus(windDir):
    global playgroundBaseStatus
    playgroundBaseStatus.updateFieldWindStatus(__sanitizeWindDirection(windDir))

def getBallSwingEffectDirection():
    global playgroundBaseStatus
    return playgroundBaseStatus.getBallSwingEffectDirection()

def getBallLocation():
    global playgroundBaseStatus
    return playgroundBaseStatus.get_ball_location()

def __sanitizeWindDirection(direction):
    if direction is None: return None
    
    if direction < 0: direction = 0
    elif direction > 4095: direction = 4095
	
    # map direction degrees from 0-4095 to 0-269
    direction = direction*269/4095 - 120.0

    # adjust direction
    direction -= 90
    if direction < 0: direction = 359 + direction
    
    return direction