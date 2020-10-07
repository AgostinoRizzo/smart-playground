import time
from enum import Enum
from threading import Lock

from pytinyos import tos
import tinyos_serial
from util_logging import Logger
import wiimote
import wiimote_configs
import environment
import network


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


class SmartBall(SmartObject):
    def __init__(self):
        """
        smart ball - serial communication setup with tinyos base station
        """

        self.tos_am = tos.AM()

        self.tos_start_msg = tinyos_serial.StartMsg()
        self.tos_start_msg.code = int(tinyos_serial.BASE_STATION_MSG_START_CODE)

        self.tos_swing_msg = tinyos_serial.SwingMsg()
        self.tos_swing_msg.code = int(tinyos_serial.BASE_STATION_MSG_SWING_CODE)

        self.tos_stop_msg = tinyos_serial.StopMsg()
        self.tos_stop_msg.code = int(tinyos_serial.BASE_STATION_MSG_STOP_CODE)

        Logger.default().config('smartball - tinyos serial communication setup')

        self.status = SmartBallStatus.READY
        self.serial_lock = Lock()
        self.on_collision_callback = None
        self.on_sensors_sample_callback = None

    def hit(self, swing_direction, swing_effect):
        
        if self.status == SmartBallStatus.READY:
            
            self.tos_start_msg.value_a = self.tos_start_msg.value_b = int(0)
            if swing_effect:
                self.tos_start_msg.value_a = int(swing_direction)
                self.tos_start_msg.value_b = int(RACKET_SWING_EFFECT_ANGLE)

            self.serial_lock.acquire()
            self.tos_am.write(self.tos_start_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)
            self.serial_lock.release()

            self.status = SmartBallStatus.RUNNING

        elif self.status == SmartBallStatus.RUNNING:
            
            self.tos_swing_msg.value_a = self.tos_swing_msg.value_b = int(0)
            if swing_effect:
                self.tos_swing_msg.value_a = int(swing_direction)
                self.tos_swing_msg.value_b = int(RACKET_SWING_EFFECT_ANGLE)

            self.serial_lock.acquire()
            self.tos_am.write(self.tos_swing_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)
            self.serial_lock.release()

    def stop(self):
        self.tos_am.write(self.tos_stop_msg, tinyos_serial.AM_BASE_STATION_COMM_CODE)

    def listen(self):
        self.serial_lock.acquire()
        p = self.tos_am.read(timeout=SMART_BALL_SERIAL_READ_TIMEOUT)
        self.serial_lock.release()

        if p and p.type == tinyos_serial.AM_SMART_BALL_COMM_CODE:

            size = len(p.data)

            if size == tinyos_serial.BallMsg.SIZE:
                rcv_msg = tinyos_serial.BallMsg(p.data)
                if rcv_msg.code == tinyos_serial.SMART_BALL_MSG_COLLISION_CODE and \
                        self.on_collision_callback is not None:
                    self.on_collision_callback()

            elif size == tinyos_serial.BallSensorsSampleMsg.SIZE:
                rcv_msg = tinyos_serial.BallSensorsSampleMsg(p.data)
                if rcv_msg.code == tinyos_serial.SMART_BALL_MSG_SENSORS_SAMPLE_CODE and \
                        self.on_sensors_sample_callback is not None:
                    self.on_sensors_sample_callback([rcv_msg.temp, rcv_msg.humi, rcv_msg.bright])

    def register_on_collision_callback(self, callback):
        self.on_collision_callback = callback

    def register_on_sensors_sample_callback(self, callback):
        self.on_sensors_sample_callback = callback


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

        self.swing_start = time.time()
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


def on_smart_ball_collision():
    SmartObjectsMediator.get_current_instance().on_smart_ball_collision()


def on_smart_ball_sensors_sample(sample):
    SmartObjectsMediator.get_current_instance().on_smart_ball_sensors_sample(sample)


def on_main_racket_buttons_changed(buttons):
    SmartObjectsMediator.get_current_instance().on_main_racket_buttons_changed(buttons)


def on_main_racket_accs_changed(xyz):
    SmartObjectsMediator.get_current_instance().on_main_racket_accs_changed(xyz)


class SmartObjectsMediator:
    __instance = None

    @staticmethod
    def get_instance(smart_ball, main_smart_racket):
        if SmartObjectsMediator.__instance is None:
            SmartObjectsMediator.__instance = SmartObjectsMediator(smart_ball, main_smart_racket)
        return SmartObjectsMediator.__instance

    @staticmethod
    def get_current_instance():
        if SmartObjectsMediator.__instance is None:
            raise RuntimeError('Illegal SmartObjetsMediator creation.')
        return SmartObjectsMediator.__instance

    def __init__(self, smart_ball, main_smart_racket):
        if SmartObjectsMediator.__instance is not None:
            raise Exception('Cannot create multiple instances of a Singleton class')

        self.running = True

        self.smart_ball = smart_ball
        self.main_smart_racket = main_smart_racket

        self.smart_ball.register_on_collision_callback(on_smart_ball_collision)
        self.smart_ball.register_on_sensors_sample_callback(on_smart_ball_sensors_sample)
        self.main_smart_racket.register_buttons_changed_callback(on_main_racket_buttons_changed)
        self.main_smart_racket.register_accs_changed_callback(on_main_racket_accs_changed)

    def run(self):
        while self.running:
            self.smart_ball.listen()

    def finalize(self):
        self.smart_ball.stop()

    @staticmethod
    def on_smart_ball_collision():
        environment.play_ball_hit_sound()

    @staticmethod
    def on_smart_ball_sensors_sample(sample):
        network.EcosystemEventProvider.get_instance().send_smartball_sensors_sample(sample)

    def on_main_racket_buttons_changed(self, buttons):
        main_racket_buttons = self.main_smart_racket.get_buttons()
        for btn in buttons:
            btn_id = btn[0]
            btn_status = btn[1]
            self.main_smart_racket.set_button(btn_id, btn_status)
            if btn_id == 'B':
                self.main_smart_racket.swing_effect = bool(btn_status)
            if btn_id == 'Home':
                self.running = False
            if btn_id == 'Down':
                environment.play_racket_hit_sound()
                self.smart_ball.hit(0, False)
            if (btn_id == 'A' or btn_id == 'B') and main_racket_buttons['A'] and main_racket_buttons['B']:
                network.EcosystemEventProvider.get_instance().notify_user_ack()

    def on_main_racket_accs_changed(self, xyz):
        # print("ACC: ", xyz)
        swing_dir = None

        if xyz[wiimote_configs.ACC_Z] >= \
                wiimote_configs.WIIMOTE_ACC_CENTER_VALUE + wiimote_configs.WIIMOTE_ACC_SWING_THRESHOLD:
            swing_dir = wiimote_configs.LEFT_SWING
        elif xyz[wiimote_configs.ACC_Z] <= \
                wiimote_configs.WIIMOTE_ACC_CENTER_VALUE - wiimote_configs.WIIMOTE_ACC_SWING_THRESHOLD:
            swing_dir = wiimote_configs.RIGHT_SWING

        if swing_dir is not None:
            swing_end = time.time()
            if swing_end - self.main_smart_racket.swing_start >= wiimote_configs.WIIMOTE_ACC_SWING_INVERVAL:
                self.main_smart_racket.swing_start = time.time()

                Logger.default().debug("SWING! [" + str(swing_dir) + "]")

                environment.play_racket_hit_sound()

                self.smart_ball.hit(swing_dir, self.main_smart_racket.swing_effect)
                self.main_smart_racket.rumble(0.1)

        # update status sample
        if self.main_smart_racket.status_sample.append_new_values(xyz) and \
                self.main_smart_racket.status_sample.is_full():
            network.EcosystemEventProvider.get_instance() \
                .send_smart_racket_status_sample(self.main_smart_racket.status_sample)
            self.main_smart_racket.status_sample.clear()
