/**
  * Sensing Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 27/07/19
  *
  */

#include "Sensing.h"

module SensingManagerP
{
	provides interface Sense;
	
	uses interface Timer<TMilli>;
	uses interface Read<uint16_t> as ReadTempSensor;
	uses interface Read<uint16_t> as ReadHumiSensor;
	uses interface Read<uint16_t> as ReadBrightSensor;
}
implementation
{
	// samples buffers
	
	#define TEMP_ID     0
	#define HUMI_ID     1
	#define BRIGHT_ID   2
	
	#define N_SENSORS   3
	
	typedef uint8_t sensor_id_t;
	
	uint16_t samples_buffers[3][ SAMPLES_BUFFER_CAPACITY ];
	uint8_t  samples_buffer_sizes[] = {0, 0, 0};
	
	
	uint16_t temp=0, humi=0, bright=0;
	
	
	void buffer_sample_value( sensor_id_t sensor, uint16_t value );
	void flush_buffered_samples();
	bool full_buffers();
	uint16_t gather_samples( sensor_id_t id );
	
	
	
	command void Sense.start_sensing()
	{
		call Timer.startPeriodic( SAMPLING_PERIOD );
	}
	
	event void Timer.fired()
	{
		call ReadTempSensor.read();
		call ReadHumiSensor.read();
		call ReadBrightSensor.read();
	}
	
	event void ReadTempSensor.readDone( error_t result, uint16_t data )
	{
		if ( result == SUCCESS )
		{
			temp = data; // -39+0.01*data;
			buffer_sample_value(TEMP_ID, temp);
		}
	}
	event void ReadHumiSensor.readDone( error_t result, uint16_t data )
	{
		if ( result == SUCCESS )
		{
			humi = data;
			buffer_sample_value(HUMI_ID, humi);
		}
	}
	event void ReadBrightSensor.readDone( error_t result, uint16_t data )
	{
		if ( result == SUCCESS )
		{
			bright = data;
			buffer_sample_value(BRIGHT_ID, bright);
		}
	}
	
	void buffer_sample_value( sensor_id_t sensor, uint16_t value )
	{
		if ( samples_buffer_sizes[sensor] < SAMPLES_BUFFER_CAPACITY )
		{
			samples_buffers[sensor][ samples_buffer_sizes[sensor] ] = value;
			++samples_buffer_sizes[sensor];
		}
		flush_buffered_samples();		
	}
	
	void flush_buffered_samples()
	{		
		uint16_t sample[N_SENSORS];
		sensor_id_t id=0;
		
		if ( !full_buffers() )
			return;
		
		call Timer.stop();
		
		for( ; id<N_SENSORS; ++id )
		{
			sample[id] = gather_samples(id);
			samples_buffer_sizes[id] = 0;
		}
		
		signal Sense.on_new_sample( sample, N_SENSORS );
		call Timer.startPeriodic( SAMPLING_PERIOD );
	}
	
	bool full_buffers()
	{
		sensor_id_t id=0;
		for(; id<N_SENSORS; ++id)
			if ( samples_buffer_sizes[id] < SAMPLES_BUFFER_CAPACITY )
				return FALSE;
		return TRUE;
	}
	
	uint16_t gather_samples( sensor_id_t id )
	{
		buffer_size_t j=0;
		uint16_t gathering=0;
		
		for( ; j<samples_buffer_sizes[id]; ++j )
			gathering += samples_buffers[id][j];
		
		return ( gathering / samples_buffer_sizes[id] );
	}
	
}
