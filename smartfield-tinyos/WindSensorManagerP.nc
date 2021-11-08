/**
  * Wind Sensor Manager
  *		Module component
  *
  * Author : Agostino Rizzo
  * Date   : 09/07/21
  *
  */

#include "Msp430Adc12.h"
#include "WindSensor.h"

#define TRUE	1
#define FALSE	0

module WindSensorManagerP
{
	provides interface WindSensor;
	
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureWindOnOff;
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureWindDir;
	
	
	uses interface Read<uint16_t> as ReadWindOnOffSensor;
	uses interface Read<uint16_t> as ReadWindDirSensor;
	
	uses interface ReadStream<uint16_t> as ReadStreamWindOnOffSensor;
	uses interface ReadStream<uint16_t> as ReadStreamWindDirSensor;
	
	uses interface Timer<TMilli> as WindOnoffSampleTimer;
	uses interface Timer<TMilli> as WindDirSampleTimer;
}
implementation
{
	bool wind_on=FALSE;
	int64_t wind_on_time=0;
	
	bool last_fan_status=FALSE;
	uint64_t fan_status_count=0;
	
	windir_t wind_dir=0, wind_dir_to_send;
		
	int64_t getabs( int64_t x );
	
	command void WindSensor.init()
	{
		call WindOnoffSampleTimer.startOneShot(WIND_SENSOR_SAMPLE_READING_INTERVAL);
		call WindDirSampleTimer.startOneShot(WIND_SENSOR_SAMPLE_READING_INTERVAL);
	}
	
	event void WindOnoffSampleTimer.fired()
	{
		call ReadWindOnOffSensor.read();
	}
	
	event void WindDirSampleTimer.fired()
	{
		call ReadWindDirSensor.read();
	}
	
	/* wind onoff sensor event managers */
	event void ReadWindOnOffSensor.readDone( error_t result, uint16_t data ) 
	{
		if ( result == SUCCESS )
		{
			if ( data >= 2048 )		// on black
			{
				if ( wind_on_time > 0 ) wind_on_time=0;
				wind_on_time -= WIND_SENSOR_SAMPLE_READING_INTERVAL;
				if ( last_fan_status )
				{
					last_fan_status=FALSE;
					++fan_status_count;
				}
			}
			else					// on white
			{
				if ( wind_on_time < 0 ) wind_on_time=0;
				wind_on_time += WIND_SENSOR_SAMPLE_READING_INTERVAL;
				if ( !last_fan_status )
				{
					last_fan_status=TRUE;
					++fan_status_count;
				}
			}	
		}
		
		if ( abs(wind_on_time) >= 1000 )
		{
			bool was_wind_on = wind_on;
			
			wind_on_time=0;
			atomic { wind_on=FALSE; }
			last_fan_status=FALSE;
			fan_status_count=0;
			
			if ( was_wind_on )
				signal WindSensor.on_wind_off();
		}
		else if ( fan_status_count >= 3 )
		{
			bool send_update = !wind_on;
			wind_on=TRUE;
			
			if ( send_update )
			{
				atomic { wind_dir_to_send = wind_dir; }
				signal WindSensor.on_wind_on(wind_dir_to_send);
			}
		}
		
		call WindOnoffSampleTimer.startOneShot(WIND_SENSOR_SAMPLE_READING_INTERVAL);
	}
	event void ReadStreamWindOnOffSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamWindOnOffSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	/* wind dir sensor event managers */
	event void ReadWindDirSensor.readDone( error_t result, uint16_t data ) 
	{
		atomic { wind_dir = data; }
		call WindDirSampleTimer.startOneShot(WIND_SENSOR_SAMPLE_READING_INTERVAL);
	}
	event void ReadStreamWindDirSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamWindDirSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	
	/*** ADCs configurations ***/
	const msp430adc12_channel_config_t wind_onoff_sensor_config =
	{
		inch:           WIND_ONOFF_SENSOR_CH,   // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon 
	};
	const msp430adc12_channel_config_t wind_dir_sensor_config =
	{
		inch:           WIND_DIR_SENSOR_CH,     // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon 
	};
	async command const msp430adc12_channel_config_t* AdcConfigureWindOnOff.getConfiguration()
	{
		return &wind_onoff_sensor_config;
	}
	async command const msp430adc12_channel_config_t* AdcConfigureWindDir.getConfiguration()
	{
		return &wind_dir_sensor_config;
	}
	
	int64_t getabs( int64_t x )
	{
		if ( x < 0 )
			return -x;
		return x;
	}
}
