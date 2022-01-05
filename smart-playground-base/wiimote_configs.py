
"""
Wiimote sensors (3 axes accelerometer) constants/configs
Each of the accelerometer values (x,y,z) are defined between 0 and 1023
"""
ACC_X = 0
ACC_Y = 1
ACC_Z = 2

RIGHT_SWING = 0
LEFT_SWING = 1

WIIMOTE_ACC_CENTER_VALUE = 512

# tennis swind parameters
WIIMOTE_ACC_TENNIS_SWING_THRESHOLD = 340  # 288
WIIMOTE_ACC_TENNIS_SWING_INVERVAL = 1

# golf swing parameters
WIIMOTE_ACC_GOLF_POINTING_DOWN_THRESHOLD = 50 
WIIMOTE_ACC_GOLF_SWING_THRESHOLD = 80
WIIMOTE_ACC_GOLF_SWING_INVERVAL = 0.5
WIIMOTE_ACC_GOLF_SWING_SPEED_VALUE = 50