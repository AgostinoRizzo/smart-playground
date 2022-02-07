/**
  * Line Detection Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 23/07/19
  *
  */

#include "Msp430Adc12.h"
#include "Timer.h"
#include "LineSensor.h"
#include "Configs.h"

module LineDetectionManagerP
{
	provides interface DetectLine;
	
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureFR;
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureFL;
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureBR;
	provides interface AdcConfigure<const msp430adc12_channel_config_t*> as AdcConfigureBL;
	
	
	uses interface Timer<TMilli> as Timer;
  
	uses interface Read<uint16_t> as ReadFRSensor;
	uses interface ReadStream<uint16_t> as ReadStreamFRSensor;
	
	uses interface Read<uint16_t> as ReadFLSensor;
	uses interface ReadStream<uint16_t> as ReadStreamFLSensor;
	
	uses interface Read<uint16_t> as ReadBRSensor;
	uses interface ReadStream<uint16_t> as ReadStreamBRSensor;
	
	uses interface Read<uint16_t> as ReadBLSensor;
	uses interface ReadStream<uint16_t> as ReadStreamBLSensor;
}
implementation
{		
	bool reading_sensor[] = { FALSE, FALSE, FALSE, FALSE };
	
	
	command void DetectLine.start_detection()
	{
		call Timer.startPeriodic(READINGS_INTERVAL);
	}
	
	event void Timer.fired()
	{
		if ( !reading_sensor[FRONT_RIGHT_SENSOR_ID] ) { reading_sensor[FRONT_RIGHT_SENSOR_ID] = TRUE; call ReadFRSensor.read(); }
		if ( !reading_sensor[FRONT_LEFT_SENSOR_ID] )  { reading_sensor[FRONT_LEFT_SENSOR_ID] = TRUE; call ReadFLSensor.read(); }
		if ( !reading_sensor[BACK_RIGHT_SENSOR_ID] )  { reading_sensor[BACK_RIGHT_SENSOR_ID] = TRUE; call ReadBRSensor.read(); }
		if ( !reading_sensor[BACK_LEFT_SENSOR_ID] )   { reading_sensor[BACK_LEFT_SENSOR_ID] = TRUE; call ReadBLSensor.read(); }
	}
	
	
	/* sensors event managers */
	void manage_read_done( error_t result, uint16_t data, line_sensor_id_t id )
	{
		if (result == SUCCESS)
		{
			if ( data >= 2048 )
				signal DetectLine.on_line(id);
			else 
				signal DetectLine.off_line(id);
		}
		reading_sensor[id] = FALSE;
	}
	
	
	/* front-right sensor event managers */
	event void ReadFRSensor.readDone( error_t result, uint16_t data ) 
	{
		manage_read_done( result, data, FRONT_RIGHT_SENSOR_ID );
	}
	event void ReadStreamFRSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamFRSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	
	/* front-left sensor event managers */
	event void ReadFLSensor.readDone( error_t result, uint16_t data ) 
	{
		manage_read_done( result, data, FRONT_LEFT_SENSOR_ID );
	}
	event void ReadStreamFLSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamFLSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	
	/* back-right sensor event managers */
	event void ReadBRSensor.readDone( error_t result, uint16_t data ) 
	{
		manage_read_done( result, data, BACK_RIGHT_SENSOR_ID );
	}
	event void ReadStreamBRSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamBRSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	
	/* back-left sensor event managers */
	event void ReadBLSensor.readDone( error_t result, uint16_t data ) 
	{
		manage_read_done( result, data, BACK_LEFT_SENSOR_ID );
	}
	event void ReadStreamBLSensor.bufferDone( error_t result, uint16_t* buffer, uint16_t count ) {}
	event void ReadStreamBLSensor.readDone(error_t result, uint32_t actualPeriod) {}
	
	
	/* line sensors configurations */
	
	/* configuration front-right sensor */
	const msp430adc12_channel_config_t fr_sensor_config =
	{
		inch:           LINE_DETECTION_FR_CH,   // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon 
	};
	/* configuration front-left sensor */
	const msp430adc12_channel_config_t fl_sensor_config =
	{
		inch:           LINE_DETECTION_FL_CH,   // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon 
	};
	/* configuration back-right sensor */
	const msp430adc12_channel_config_t br_sensor_config =
	{
		inch:           LINE_DETECTION_BR_CH,   // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon  
	};
	/* configuration back-left sensor */
	const msp430adc12_channel_config_t bl_sensor_config =
	{
		inch:           LINE_DETECTION_BL_CH,   // input channel 
		sref:           REFERENCE_VREFplus_AVss,// reference voltage 
		ref2_5v:        REFVOLT_LEVEL_2_5,      // reference voltage level 
		adc12ssel:      SHT_SOURCE_ACLK,        // clock source sample-hold-time 
		adc12div:       SHT_CLOCK_DIV_1,        // clock divider sample-hold-time 
		sht:            SAMPLE_HOLD_4_CYCLES,   // sample-hold-time
		sampcon_ssel:   SAMPCON_SOURCE_SMCLK,   // clock source sampcon signal 
		sampcon_id:     SAMPCON_CLOCK_DIV_1     // clock divider sampcon 
	};
	
	async command const msp430adc12_channel_config_t* AdcConfigureFR.getConfiguration()
	{
		return &fr_sensor_config;
	}
	async command const msp430adc12_channel_config_t* AdcConfigureFL.getConfiguration()
	{
		return &fl_sensor_config;
	}
	async command const msp430adc12_channel_config_t* AdcConfigureBR.getConfiguration()
	{
		return &br_sensor_config;
	}
	async command const msp430adc12_channel_config_t* AdcConfigureBL.getConfiguration()
	{
		return &bl_sensor_config;
	}
}
