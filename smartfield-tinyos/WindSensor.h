#ifndef WIND_SENSOR_H
#define WIND_SENSOR_H

#define WIND_SENSOR_SAMPLE_READING_INTERVAL 10	// expressed in millis

#define WIND_ONOFF_SENSOR_CH 	INPUT_CHANNEL_A0
#define WIND_DIR_SENSOR_CH 		INPUT_CHANNEL_A1

typedef uint16_t windir_t;


#define WIND_ON_MSG_CODE  51
#define WIND_OFF_MSG_CODE 50

enum
{
	AM_WIND_STATUS_COMM_CODE = 0x95  // AM code for outcoming messages about wind status (from sensor to playground base)
};

typedef nx_struct windon_msg
{
	nx_uint8_t  code;
	nx_uint16_t windir;
} windon_msg_t;

typedef nx_struct windoff_msg
{
	nx_uint8_t  code;
} windoff_msg_t;

#endif
