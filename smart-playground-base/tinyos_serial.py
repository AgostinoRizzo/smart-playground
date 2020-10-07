from pytinyos import tos

AM_BASE_STATION_COMM_CODE = 0x94
AM_SMART_BALL_COMM_CODE = 0x93

BASE_STATION_MSG_STOP_CODE = 2
BASE_STATION_MSG_START_CODE = 3
BASE_STATION_MSG_SWING_CODE = 4

SMART_BALL_MSG_COLLISION_CODE = 1
SMART_BALL_MSG_SENSORS_SAMPLE_CODE = 2


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


class StopMsg(tos.Packet):
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
