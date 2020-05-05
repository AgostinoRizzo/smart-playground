#ifndef COMMUNICATION_H
#define COMMUNICATION_H

// message type codes for outcaming messages (from sensor to base station)
#define SENSOR_MSG_COLLISION_CODE            1
#define SENSOR_MSG_SAMPLE_CODE               2

// message type codes for incoming messages (from base station to sensor)
#define BASE_STATION_MSG_STOP_CODE           2
#define BASE_STATION_MSG_START_CODE          3
#define BASE_STATION_MSG_SWING_CODE          4

#define SWING_RIGHT_DIR    0
#define SWING_LEFT_DIR     1

#define SWING_A_SIDE     0
#define SWING_B_SIDE     1

typedef uint8_t swing_dir_t;
typedef uint8_t swing_side_t;

enum 
{
	AM_SENSOR_COMM_CODE       = 0x93,  // AM code for outcaming messages (from sensor to base station)
	AM_BASE_STATION_COMM_CODE = 0x94,  // AM code for incoming messages (from base station to sensor)
};

// outcaming sensor message struct
typedef nx_struct sensor_msg 
{
	nx_uint8_t code;
} sensor_msg_t;

// outcaming sensors sample message struct
typedef nx_struct sensors_sample_msg 
{
	nx_uint8_t  code;
	nx_uint16_t temp;
	nx_uint16_t humi;
	nx_uint16_t bright;
} sensors_sample_msg_t;

// incoming simple base station message struct
typedef nx_struct base_station_simple_msg 
{
	nx_uint8_t code;
} base_station_simple_msg_t;

// incoming composed base station message struct
typedef nx_struct base_station_composed_msg 
{
	nx_uint8_t code;
	nx_uint8_t value_a;
	nx_uint8_t value_b;
} base_station_composed_msg_t;


#endif
