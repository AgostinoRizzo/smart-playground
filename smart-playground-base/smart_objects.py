import time
from enum import Enum
from threading import Lock, RLock

from pytinyos import tos
import tinyos_serial
from util_logging import Logger
import wiimote
import wiimote_configs
import environment
import playground
import network
import game


class SmartObject:
    pass


"""
smart ball - constants and classes definitions
"""

RACKET_SWING_EFFECT_ANGLE = 30
SMART_BALL_SERIAL_READ_TIMEOUT = 0.01

MAIN_SMART_RACKET = 0
SECOND_SMART_RACKET = 1

class SmartBallStatus(Enum):
    READY = 1
    RUNNING = 2


class RacketSwingDirection(Enum):
    RIGHT = 0
    LEFT = 1


class SmartField(SmartObject):

    """
    LIGHTS/FANS COMMAND BYTE:
        - 0:  null command

        - 1:  all lights off
        - 2:  FL light only
        - 3:  BL light only
        - 4:  BR light only
        - 5:  FR light only
        - 6:  all lights on
        
        - 7:  all fans off
        - 8:  FL fan only
        - 9:  BL fan only
        - 10: BR fan only
        - 11: FR fan only
        - 12: all fans on
    """

    # LIGHTS/FANS PATTERN
    ALL_OFF        = 0
    FRONT_LEFT_ON  = 1
    BACK_LEFT_ON   = 2
    BACK_RIGHT_ON  = 3
    FRONT_RIGHT_ON = 4
    ALL_ON         = 5

    def __init__(self) -> None:
        """
        smart field - serial communication messages setup with tinyos base station
        """

        self.tos_field_commands_msg = tinyos_serial.FieldCommandsMsg.createNewMessage()

        Logger.default().config('smartfield - tinyos serial communication messages setup')
    
    def set_lights(self, pattern):
        self.__set_command(1 + pattern)
    
    def set_fans(self, pattern):
        self.__set_command(7 + pattern)
    
    def __set_command(self, cmd):  # clockwise order
        print("Sending command: " + str(cmd))
        self.tos_field_commands_msg.cmd = cmd
        tinyos_serial.tos_am_serial_write(self.tos_field_commands_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)



class SmartBall(SmartObject):
    def __init__(self):
        """
        smart ball - serial communication messages setup with tinyos base station
        """

        self.tos_racket_start_msg = tinyos_serial.StartMsg()
        self.tos_racket_start_msg.code = int(tinyos_serial.BASE_STATION_MSG_RACKET_START_CODE)

        self.tos_racket_swing_msg = tinyos_serial.SwingMsg()
        self.tos_racket_swing_msg.code = int(tinyos_serial.BASE_STATION_MSG_RACKET_SWING_CODE)

        self.tos_club_start_msg = tinyos_serial.StartMsg()
        self.tos_club_start_msg.code = int(tinyos_serial.BASE_STATION_MSG_CLUB_START_CODE)

        self.tos_stop_msg = tinyos_serial.StopMsg()
        self.tos_stop_msg.code = int(tinyos_serial.BASE_STATION_MSG_STOP_CODE)

        Logger.default().config('smartball - tinyos serial communication messages setup')

        self.status = SmartBallStatus.READY
        self.on_collision_callback = None
        self.on_smartball_sensors_sample_callback = None
        self.on_smartfield_sensors_sample_callback = None
        self.on_field_wind_status_callback = None

        self.lock = RLock()

    def racketSwingHit(self, swing_direction, swing_effect):
        
        with self.lock:
            if self.status == SmartBallStatus.READY:
                
                self.tos_racket_start_msg.value_a = self.tos_racket_start_msg.value_b = int(0)
                if swing_effect:
                    self.tos_racket_start_msg.value_a = int(swing_direction)
                    self.tos_racket_start_msg.value_b = int(RACKET_SWING_EFFECT_ANGLE)

                tinyos_serial.tos_am_serial_write(self.tos_racket_start_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)

                self.status = SmartBallStatus.RUNNING

            elif self.status == SmartBallStatus.RUNNING:
                
                self.tos_racket_swing_msg.value_a = self.tos_racket_swing_msg.value_b = int(0)
                if swing_effect:
                    self.tos_racket_swing_msg.value_a = int(swing_direction)
                    self.tos_racket_swing_msg.value_b = int(RACKET_SWING_EFFECT_ANGLE)

                tinyos_serial.tos_am_serial_write(self.tos_racket_swing_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)
    
    def clubStrokeHit(self, swingDirection):
        with self.lock:
            print("Club stroke with swing direction: " + str(swingDirection))
            self.tos_club_start_msg.value_a = self.tos_club_start_msg.value_b = int(0)
            if swingDirection is not None:
                self.tos_club_start_msg.value_a = int(swingDirection)
                self.tos_club_start_msg.value_b = int(RACKET_SWING_EFFECT_ANGLE)
            tinyos_serial.tos_am_serial_write(self.tos_club_start_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)

    def stop(self):
        with self.lock:
            tinyos_serial.tos_am_serial_write(self.tos_stop_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)
            self.status = SmartBallStatus.READY

    def listen(self):
        p = tinyos_serial.tos_am_serial_read(read_timeout=SMART_BALL_SERIAL_READ_TIMEOUT)

        if not p or (p.type != tinyos_serial.AM_FROM_BASE_STATION_COMM_CODE and \
            p.type != tinyos_serial.AM_FROM_BASE_STATION_WIND_STATUS_COMM_CODE and \
            p.type != 0):
            return
       
        size = len(p.data)

        if size == tinyos_serial.SingleCodeMsg.SIZE:
            rcv_msg = tinyos_serial.SingleCodeMsg(p.data)

            if rcv_msg.code == tinyos_serial.SMART_BALL_MSG_COLLISION_CODE and \
                    self.on_collision_callback is not None:
                self.on_collision_callback()
            elif rcv_msg.code == tinyos_serial.FIELDWIND_STATUS_OFF_MSG_CODE:
                print("Wind OFF.")
                if self.on_field_wind_status_callback is not None:
                    self.on_field_wind_status_callback(None)

        if size == tinyos_serial.BallMsg.SIZE:
            rcv_msg = tinyos_serial.BallMsg(p.data)
            if rcv_msg.code == tinyos_serial.SMART_BALL_MSG_COLLISION_CODE and \
                    self.on_collision_callback is not None:
                self.on_collision_callback()

        elif size == tinyos_serial.BallSensorsSampleMsg.SIZE:
            rcv_msg = tinyos_serial.BallSensorsSampleMsg(p.data)
            if rcv_msg.code == tinyos_serial.SMART_BALL_MSG_SENSORS_SAMPLE_CODE and \
                    self.on_smartball_sensors_sample_callback is not None:
                self.on_smartball_sensors_sample_callback([rcv_msg.temp, rcv_msg.humi, rcv_msg.bright])
            elif rcv_msg.code == tinyos_serial.SMART_FIELD_MSG_SENSORS_SAMPLE_CODE and \
                    self.on_smartfield_sensors_sample_callback is not None:
                self.on_smartfield_sensors_sample_callback([rcv_msg.temp, rcv_msg.humi, rcv_msg.bright])
        
        elif size == tinyos_serial.FieldWindStatusOnMsg.SIZE:
            rcv_msg = tinyos_serial.FieldWindStatusOnMsg(p.data)
            print("Wind ON: %d" % rcv_msg.dir)
            if self.on_field_wind_status_callback is not None:
                    self.on_field_wind_status_callback(rcv_msg.dir)

    def register_on_collision_callback(self, callback):
        self.on_collision_callback = callback

    def register_on_smartball_sensors_sample_callback(self, callback):
        self.on_smartball_sensors_sample_callback = callback
    
    def register_on_smartfield_sensors_sample_callback(self, callback):
        self.on_smartfield_sensors_sample_callback = callback
    
    def register_on_field_wind_status_callback(self, callback):
        self.on_field_wind_status_callback = callback


"""
smart racket - constants and classes definitions
"""

RACKET_ANIM_PATTERNS = [[1, 0, 0, 0],
                        [0, 1, 0, 0],
                        [0, 0, 1, 0],
                        [0, 0, 0, 1],
                        [0, 0, 1, 0],
                        [0, 1, 0, 0],
                        [1, 0, 0, 0]]


class NoWiimoteFoundException(Exception):
    pass


class SmartRacket(SmartObject):
    def __init__(self):
        """
        smart racket - bluetooth connection setup with nintendo wiimote
        """
    
        wiimotes = []
        finding_rounds = 4

        while finding_rounds > 0:
            Logger.default().info("Press the 'sync' button on the back of your Wiimote Plus " +
                                  "or buttons 1+2 on your classic Wiimote.")
            Logger.default().input("Press <return> once the LEDs of the Wiimote start blinking.")
            input()

            wiimotes = wiimote.find()
            finding_rounds -= 1

            if len(wiimotes) == 0:
                Logger.default().warning("Finding error: no Wiimotes found.")
                if finding_rounds <= 0:
                    Logger.default().warning("Unable to find Wiimotes. Closing...")
                    raise NoWiimoteFoundException()
            else:
                finding_rounds = 0

        self.wm_addr = wiimotes[0]
        self.wiimote = wiimote.connect(self.wm_addr)

        Logger.default().config('Connected to: ' + str(self.wm_addr))

        self.tennis_swing_start = time.time()
        self.buttons_changed_callback = None
        self.swing_effect = False

        self.buttons = \
            {'A' : False,
             'B' : False,
             'Down' : False,
             'Home' : False,
             'Left' : False,
             'Minus' : False,
             'One' : False,
             'Plus' : False,
             'Right' : False,
             'Two' : False,
             'Up' : False}

        self.status_sample = SmartRacketStatusSample(MAIN_SMART_RACKET)

    def get_wiimote_addr(self):
        return self.wm_addr

    def animation(self):
        for _ in range(5):
            for p in RACKET_ANIM_PATTERNS:
                self.wiimote.leds = p
                time.sleep(0.05)

        self.wiimote.rumble(0.1)

    def rumble(self, length):
        self.wiimote.rumble(length)

    def register_buttons_changed_callback(self, callback):
        self.wiimote.buttons.register_callback(callback)

    def register_accs_changed_callback(self, callback):
        self.wiimote.accelerometer.register_callback(callback)

    def set_button(self, btn, pressed):
        self.buttons[btn] = pressed

    def get_buttons(self):
        return self.buttons


class SmartRacketStatusSample:
    CAPACITY = 1
    SAMPLE_PERIOD = 0.1  # expressed in secs

    def __init__(self, _id):
        self.id = _id
        self.accXValues = []
        self.accYValues = []
        self.accZValues = []
        self.last_time = time.time()

    def append_new_values(self, xyz):
        curr_time = time.time()
        if curr_time - self.last_time >= SmartRacketStatusSample.SAMPLE_PERIOD:
            self.accXValues.append(xyz[wiimote_configs.ACC_X])
            self.accYValues.append(xyz[wiimote_configs.ACC_Y])
            self.accZValues.append(xyz[wiimote_configs.ACC_Z])
            self.last_time = curr_time
            return True
        return False

    def clear(self):
        self.accXValues.clear()
        self.accYValues.clear()
        self.accZValues.clear()

    def is_full(self):
        return (len(self.accXValues) >= SmartRacketStatusSample.CAPACITY or
                len(self.accYValues) >= SmartRacketStatusSample.CAPACITY or
                len(self.accZValues) >= SmartRacketStatusSample.CAPACITY)



class TennisRacketSwingDetector:

    def __init__(self, smartObjectMediator) -> None:
        self.smartObjectMediator = smartObjectMediator
    
    def onNewAccelValues(self, xyz):
        swing_dir = None

        if xyz[wiimote_configs.ACC_Z] >= \
                wiimote_configs.WIIMOTE_ACC_CENTER_VALUE + wiimote_configs.WIIMOTE_ACC_TENNIS_SWING_THRESHOLD:
            swing_dir = wiimote_configs.LEFT_SWING
        elif xyz[wiimote_configs.ACC_Z] <= \
                wiimote_configs.WIIMOTE_ACC_CENTER_VALUE - wiimote_configs.WIIMOTE_ACC_TENNIS_SWING_THRESHOLD:
            swing_dir = wiimote_configs.RIGHT_SWING

        if swing_dir is not None:
            swing_end = time.time()
            if swing_end - self.smartObjectMediator.main_smart_racket.tennis_swing_start >= wiimote_configs.WIIMOTE_ACC_TENNIS_SWING_INVERVAL:
                self.smartObjectMediator.main_smart_racket.tennis_swing_start = time.time()

                self.__onSwing(swing_dir, self.smartObjectMediator.main_smart_racket.swing_effect)
    
    def __onSwing(self, swingDir, swingEffect):
        self.smartObjectMediator.onTennisRacketSwingCallback(swingDir, swingEffect)



class GolfClubSwingDetector:
    """
    A golf swing is recognized when a down-up-down oscillation
    of the x axis is detected w.r.t SWING_ACCELX_REFS and within MIN_SWING_TIME seconds.
    A golf swing always starts when club is pointed down w.r.t the y axis.
    """
    SWING_ACCELX_REFS = [470, 530, 470]
    SWING_ACCELX_REFS_MARGIN = 10
    MIN_SWING_TIME = 0.5
    LIGHT_ACCEL_SWING_THRESHOLD = 700
    MEDIUM_ACCEL_SWING_THRESHOLD = 900

    LIGHT_SWING_TYPE = 0
    MEDIUM_SWING_TYPE = 1
    BIG_SWING_TYPE = 2
    
    @staticmethod
    def getSwingType(maxAccel):
        if maxAccel < GolfClubSwingDetector.LIGHT_ACCEL_SWING_THRESHOLD: return GolfClubSwingDetector.LIGHT_SWING_TYPE
        if maxAccel < GolfClubSwingDetector.MEDIUM_ACCEL_SWING_THRESHOLD: return GolfClubSwingDetector.MEDIUM_SWING_TYPE
        return GolfClubSwingDetector.BIG_SWING_TYPE

    def __init__(self, smartObjectMediator) -> None:
        self.swingAccelXRefsIndex = 0
        self.swingStarted = False
        self.swingDetected = False
        self.maxAccelXValue = -1

        self.smartObjectMediator = smartObjectMediator
        self.isAnAttempt = True

    def onSettingToggle(self):
        self.isAnAttempt = not self.isAnAttempt
        return self.isAnAttempt

    def onNewAccelValues(self, xyz, clubButtons):
        if not clubButtons['B']:
            self.swingStarted = False
            self.__resetSwingAccelXRefs()
            return

        ACCEL_X = xyz[wiimote_configs.ACC_X]
        ACCEL_Y = xyz[wiimote_configs.ACC_Y]

        absPointingDownThreshold = wiimote_configs.WIIMOTE_ACC_CENTER_VALUE + wiimote_configs.WIIMOTE_ACC_GOLF_POINTING_DOWN_THRESHOLD

        if self.swingStarted or ACCEL_Y >= absPointingDownThreshold:
            
            if not self.swingStarted:
                self.swingStarted = True
                self.maxAccelXValue = ACCEL_X

            if self.maxAccelXValue >= 0 and ACCEL_X > self.maxAccelXValue:
                self.maxAccelXValue = ACCEL_X

            absDiff  = abs(self.SWING_ACCELX_REFS[self.swingAccelXRefsIndex] - ACCEL_X)
            
            if absDiff  <= self.SWING_ACCELX_REFS_MARGIN:
                if self.swingAccelXRefsIndex == 0:
                    self.swingStartTime = time.time()
                self.swingAccelXRefsIndex += 1
            
            if self.swingAccelXRefsIndex >= len(self.SWING_ACCELX_REFS):
                if time.time() - self.swingStartTime >= self.MIN_SWING_TIME: 
                    self.__onSwing(self.maxAccelXValue)
                self.swingStarted = False
                self.__resetSwingAccelXRefs()
            
        else:
            self.__resetSwingAccelXRefs()

    def __resetSwingAccelXRefs(self):
        self.swingAccelXRefsIndex = 0
        self.swingDetected = False
    
    def __onSwing(self, maxAccel):
        if self.isAnAttempt: self.smartObjectMediator.onGolfClubAttemptCallback(maxAccel)
        else: self.smartObjectMediator.onGolfClubSwingCallback(maxAccel)


class GameToolsBag:
    RACKET = 0
    CLUB = 1

    def __init__(self) -> None:
        self.currentTool = GameToolsBag.RACKET
        self.lock = RLock()
    
    def changeTool(self):
        with self.lock:
            if self.currentTool == GameToolsBag.RACKET: self.currentTool = GameToolsBag.CLUB
            else: self.currentTool = GameToolsBag.RACKET
            return self.currentTool
     
    def getCurrentTool(self):
        with self.lock:
            return self.currentTool


def on_smart_ball_collision():
    SmartObjectsMediator.get_current_instance().on_smart_ball_collision()


def on_smart_ball_sensors_sample(sample):
    SmartObjectsMediator.get_current_instance().on_smart_ball_sensors_sample(sample)

def on_smart_field_sensors_sample(sample):
    SmartObjectsMediator.get_current_instance().on_smart_field_sensors_sample(sample)


def on_main_racket_buttons_changed(buttons):
    SmartObjectsMediator.get_current_instance().on_main_racket_buttons_changed(buttons)


def on_main_racket_accs_changed(xyz):
    SmartObjectsMediator.get_current_instance().on_main_racket_accs_changed(xyz)


class SmartObjectsMediator:
    __instance = None
    __lock = RLock()

    @staticmethod
    def get_instance(smart_ball, main_smart_racket):
        with SmartObjectsMediator.__lock:
            if SmartObjectsMediator.__instance is None:
                SmartObjectsMediator.__instance = SmartObjectsMediator(smart_ball, main_smart_racket)
            return SmartObjectsMediator.__instance

    @staticmethod
    def get_current_instance():
        with SmartObjectsMediator.__lock:
            if SmartObjectsMediator.__instance is None:
                raise RuntimeError('Illegal SmartObjetsMediator creation.')
            return SmartObjectsMediator.__instance

    def __init__(self, smart_ball, main_smart_racket):
        if SmartObjectsMediator.__instance is not None:
            raise Exception('Cannot create multiple instances of a Singleton class')

        self.running = True

        self.smart_ball = smart_ball
        self.smart_field = SmartField()
        self.main_smart_racket = main_smart_racket

        self.tennisSwingDetector = TennisRacketSwingDetector(self)
        self.golfSwingDetector = GolfClubSwingDetector(self)

        self.gameToolsBag = GameToolsBag()

        self.smart_ball.register_on_collision_callback(on_smart_ball_collision)
        self.smart_ball.register_on_smartball_sensors_sample_callback(on_smart_ball_sensors_sample)
        self.smart_ball.register_on_smartfield_sensors_sample_callback(on_smart_field_sensors_sample)
        self.smart_ball.register_on_field_wind_status_callback(SmartObjectsMediator.on_field_wind_status)
        self.main_smart_racket.register_buttons_changed_callback(on_main_racket_buttons_changed)
        self.main_smart_racket.register_accs_changed_callback(on_main_racket_accs_changed)

    def run(self):
        #time.sleep(10000)
        while self.running:
            self.smart_ball.listen()

    def finalize(self):
        pass
        #!!!!!!!!!!!!!!!self.smart_ball.stop()

    @staticmethod
    def on_smart_ball_collision():
        game.onGolfBallCollision()
        environment.play_sound(environment.BALL_BOUNCE_SOUND)

    @staticmethod
    def on_smart_ball_sensors_sample(sample):
        network.EcosystemEventProvider.get_instance().send_smartball_sensors_sample(sample)
    
    @staticmethod
    def on_smart_field_sensors_sample(sample):
        network.EcosystemEventProvider.get_instance().send_smartfield_sensors_sample(sample)
    
    @staticmethod
    def on_field_wind_status(dir):
        network.EcosystemEventProvider.get_instance().send_field_wind_status(dir)
        playground.updateFieldWindStatus(dir)

    def on_main_racket_buttons_changed(self, buttons):
        main_racket_buttons = self.main_smart_racket.get_buttons()
        for btn in buttons:

            btn_id = btn[0]
            btn_status = btn[1]
            self.main_smart_racket.set_button(btn_id, btn_status)

            # user confirm (ack) command
            if (btn_id == 'A' or btn_id == 'B') and main_racket_buttons['A'] and main_racket_buttons['B']:
                network.EcosystemEventProvider.get_instance().notify_user_ack()
            
            # change tool command (racket, club)
            if ( (btn_id == 'Up' or btn_id == 'Down') and btn_status ):
                newTool = self.gameToolsBag.changeTool()
                network.EcosystemEventProvider.get_instance().send_new_game_tool(newTool)
            
            # change tool setting command (club: attempt, stroke)
            if ( (btn_id == 'Right' or btn_id == 'Left') and btn_status ):
                if self.gameToolsBag.getCurrentTool() == GameToolsBag.CLUB:
                    isAnAttempt = self.golfSwingDetector.onSettingToggle()
                    network.EcosystemEventProvider.get_instance().send_new_club_setting(isAnAttempt)
            
            # tennis racket swing effect on/off command
            if btn_id == 'B':
                self.main_smart_racket.swing_effect = bool(btn_status)
            
            # smart ball stop command
            if btn_id == 'Home':
                self.smart_ball.stop()

    def on_main_racket_accs_changed(self, xyz):
        # detect tennis/golf swing based on the current game tool
        currentTool = self.gameToolsBag.getCurrentTool()
        if currentTool == GameToolsBag.RACKET:
            self.tennisSwingDetector.onNewAccelValues(xyz)
        elif currentTool == GameToolsBag.CLUB:
            self.golfSwingDetector.onNewAccelValues(xyz, self.main_smart_racket.get_buttons())

        # update status sample
        if self.main_smart_racket.status_sample.append_new_values(xyz) and \
                self.main_smart_racket.status_sample.is_full():
            network.EcosystemEventProvider.get_instance() \
                .send_smart_racket_status_sample(self.main_smart_racket.status_sample)
            self.main_smart_racket.status_sample.clear()
    
    """
    This callback is called whenever a new tennis swing is detected
    """
    def onTennisRacketSwingCallback(self, swing_dir, swing_effect):
        Logger.default().debug("SWING! [" + str(swing_dir) + "]")
        """
        the main racket swing has only effect during gameplay
        """
        if game.canMainRacketSwing():
            environment.play_sound(environment.RACKET_HIT_SOUND)
            self.main_smart_racket.rumble(0.1)

            self.smart_ball.racketSwingHit(swing_dir, swing_effect)
        else:
            environment.play_sound(environment.RACKET_SWING_SOUND)
    
    """
    This callback is called whenever a new golf swing is detected
    """
    def onGolfClubSwingCallback(self, max_accel):
        self.__onGolfClubEvent(max_accel)
    
    """
    This callback is called whenever a new golf attempt (with not actual action) is detected
    """
    def onGolfClubAttemptCallback(self, max_accel):
        self.__onGolfClubEvent(max_accel, is_an_attempt=True)
    
    def onArtificialPlayerBallSwing(self, swing_dir):
        environment.play_sound(environment.RACKET_HIT_SOUND)
        self.smart_ball.racketSwingHit(swing_dir, False)
    
    def onSmartBallStop(self):
        self.smart_ball.stop()

    def onGameReset(self):
        self.smart_ball.stop()
    
    def __onGolfClubEvent(self, max_accel, is_an_attempt=False):
        soundToPLay = environment.CLUB_ATTEMPT_SOUND
        swingType = GolfClubSwingDetector.getSwingType(max_accel)
        
        if not is_an_attempt and game.canClubSwing(swingType):

            if swingType == GolfClubSwingDetector.LIGHT_SWING_TYPE: soundToPLay = environment.CLUB_SWING_LIGHT_SOUND
            else: soundToPLay = environment.CLUB_SWING_SOUND

            effectDirectionValue = playground.getBallSwingEffectDirection()
            swingDirection = None

            if effectDirectionValue > 0: swingDirection = wiimote_configs.RIGHT_SWING
            elif effectDirectionValue < 0: swingDirection = wiimote_configs.LEFT_SWING

            # make a club stroke based on [wind] swing direction effect
            self.smart_ball.clubStrokeHit(swingDirection)
        
        environment.play_sound(soundToPLay)
        network.EcosystemEventProvider.get_instance().send_golf_club_action(swingType)