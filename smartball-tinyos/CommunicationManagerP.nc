/**
  * Communication Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 27/07/19
  *
  */

#include "Communication.h"

module CommunicationManagerP
{
	provides interface Communicate;
	
	uses interface SplitControl as RadioControl;
	uses interface Receive;
    uses interface AMSend;
    
    uses interface Leds;
}
implementation
{	
	message_t              send_buffer;
	sensor_msg_t           local_data;
	sensors_sample_msg_t   local_sensors_sample_data;
	
	bool                   send_busy = FALSE;
	
	
	command void Communicate.init()
	{
		call RadioControl.start();
	}
	
	command void Communicate.collision()
	{
		if ( !send_busy )
		{
			local_data.code = SENSOR_MSG_COLLISION_CODE;
			
			memcpy( call AMSend.getPayload( &send_buffer, sizeof(local_data) ), &local_data, sizeof local_data );
			if ( call AMSend.send( AM_BROADCAST_ADDR, &send_buffer, sizeof local_data) == SUCCESS )
					send_busy = TRUE;
		}
	}
	
	command void Communicate.new_sensors_sample( sensor_value_t* sample, uint8_t size )
	{
		if ( !send_busy && size==3 )
		{
			local_sensors_sample_data.code = SENSOR_MSG_SAMPLE_CODE;
			
			local_sensors_sample_data.temp   = sample[0];
			local_sensors_sample_data.humi   = sample[1];
			local_sensors_sample_data.bright = sample[2];
			
			memcpy( call AMSend.getPayload( &send_buffer, sizeof(local_sensors_sample_data) ), &local_sensors_sample_data, sizeof local_sensors_sample_data );
			if ( call AMSend.send( AM_BROADCAST_ADDR, &send_buffer, sizeof local_sensors_sample_data) == SUCCESS )
					send_busy = TRUE;
		}
	}
	
	event void RadioControl.startDone( error_t err ) 
	{
		if ( err != SUCCESS )
			call RadioControl.start();
	}

	event void RadioControl.stopDone( error_t err ) {}
	
	event void AMSend.sendDone(message_t* msg, error_t error) 
	{
		send_busy = FALSE;
	}
	
	event message_t* Receive.receive(message_t* bufPtr, void* payload, uint8_t len) 
	{	
		if ( len == sizeof(base_station_simple_msg_t) ) 
		{
    		base_station_simple_msg_t* msg = (base_station_simple_msg_t*)payload;
    		
    		if ( msg->code ==  BASE_STATION_MSG_STOP_CODE )
    			signal Communicate.on_stop();
		}
		else if ( len == sizeof(base_station_composed_msg_t) ) 
		{
    		base_station_composed_msg_t* msg = (base_station_composed_msg_t*)payload;
    		
    		if ( msg->code == BASE_STATION_MSG_START_CODE )
    		{
    			int16_t direction_angle = msg->value_b;
    			if ( msg->value_a > 0 )
    				direction_angle = -direction_angle;
    			
    			signal Communicate.on_start( direction_angle );
    		}
    		else if ( msg->code ==  BASE_STATION_MSG_SWING_CODE )
    		{
    			int16_t direction_angle = msg->value_b;
    			if ( msg->value_a > 0 )
    				direction_angle = -direction_angle;
    			
    			signal Communicate.on_swing( direction_angle );
    		}
		}
      	return bufPtr;
	}
}
