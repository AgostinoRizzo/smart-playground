#ifndef SMART_BALL_H
#define SMART_BALL_H

#define READY             0
#define TRAJECTORY_ADJ    1
#define RUNNING           2
#define BOUNCING          3

typedef uint8_t state_t;


#define RUNNING_DIR_RIGHT   0
#define RUNNING_DIR_LEFT    1

typedef uint8_t running_dir_t;


#define ROTATION_RATIO    5.8  // TIME/DIRECTION_ANGLE

#endif
