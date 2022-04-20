from threading import Lock
from pytinyos import tos

AM_BASE_STATION_COMM_CODE = 0x94
AM_FROM_BASE_STATION_COMM_CODE = 0x93
AM_FROM_BASE_STATION_WIND_STATUS_COMM_CODE = 0x95

BASE_STATION_MSG_STOP_CODE = 2
BASE_STATION_MSG_RACKET_START_CODE = 3
BASE_STATION_MSG_RACKET_SWING_CODE = 4
BASE_STATION_MSG_CLUB_START_CODE = 3  # same as racket start code
BASE_STATION_MSG_ROTATE_CODE = 5

SMART_FIELD_MSG_COMMANDS_CODE = 10

SMART_BALL_MSG_COLLISION_CODE = 1
SMART_BALL_MSG_SENSORS_SAMPLE_CODE = 2
SMART_FIELD_MSG_SENSORS_SAMPLE_CODE = 80

FIELD_WIND_STATUS_ON_MSG_CODE = 51
FIELDWIND_STATUS_OFF_MSG_CODE = 50


tos_am = None
tos_am_serial_lock = None

def tos_am_init():
    global tos_am
    global tos_am_serial_lock
    tos_am = tos.AM()
    tos_am_serial_lock = Lock()

def tos_am_serial_read(read_timeout):
    if tos_am is None:
        return
    with tos_am_serial_lock:
        return tos_am.read(timeout=read_timeout)

def tos_am_serial_write(msg: tos.Packet, comm_code):
    if tos_am is None:
        return
    with tos_am_serial_lock:
        tos_am.write(msg, comm_code)


class StartMsg(tos.Packet):
    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('value_a',  'int', 1), ('value_b',  'int', 1)],
                            packet)


class SwingMsg(tos.Packet):
    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('value_a',  'int', 1), ('value_b',  'int', 1)],
                            packet)

class RotateMsg(tos.Packet):
    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('value_a',  'int', 1), ('value_b',  'int', 1)],
                            packet)


class StopMsg(tos.Packet):
    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1)],
                            packet)


class SingleCodeMsg(tos.Packet):
    SIZE = 1

    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1)],
                            packet)


class BallMsg(tos.Packet):
    SIZE = 1

    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1)],
                            packet)


class BallSensorsSampleMsg(tos.Packet):
    SIZE = 7

    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('temp',  'int', 2), ('humi',  'int', 2), ('bright',  'int', 2)],
                            packet)


class FieldWindStatusOnMsg(tos.Packet):
    SIZE = 3

    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('dir',  'int', 2)],
                            packet)


class FieldWindStatusOffMsg(tos.Packet):
    SIZE = 1

    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1)],
                            packet)


class FieldCommandsMsg(tos.Packet):
    def __init__(self, packet=None):
        tos.Packet.__init__(self,
                            [('code', 'int', 1), ('cmd',  'int', 1)],
                            packet)
    @staticmethod
    def createNewMessage():
        msg = FieldCommandsMsg()
        msg.code = int(SMART_FIELD_MSG_COMMANDS_CODE)
        return msg
