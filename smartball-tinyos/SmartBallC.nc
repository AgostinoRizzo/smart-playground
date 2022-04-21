/**
  * Smart Ball App
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 31/07/19
  *
  */

#include "SmartBall.h"
#include "LineSensor.h"
#include "Motors.h"
#include "Communication.h"
#include "Sensing.h"

#define TRUE	1
#define FALSE	0

module SmartBallC
{
	uses interface Boot;
	uses interface Leds;
	
	uses interface DetectLine;
	uses interface Drive;
	uses interface Communicate;
	uses interface Sense;
}
implementation
{	
	state_t          curr_state = READY;
	line_sensor_id_t next_bouncing_sensor_id = NULL_SENSOR_ID;
	
	#define IS_ON_LINE     TRUE
	#define IS_OFF_LINE    FALSE
	
	bool sensors_states[] = { IS_OFF_LINE, IS_OFF_LINE, IS_OFF_LINE, IS_OFF_LINE};
	
	void manage_bouncing( line_sensor_id_t loc );
	void manage_go_straight( int16_t direction_angle );
	
	
	event void Boot.booted()
	{
		call Drive.init();
		call Communicate.init();
		call DetectLine.start_detection();
		call Sense.start_sensing();
	}
	
	event void Communicate.on_start( int16_t direction_angle )
	{		
		if ( curr_state != READY )
			return;
			
		manage_go_straight(direction_angle);
		
		call Leds.led0On();
		curr_state = RUNNING;
	}
	event void Communicate.on_swing( int16_t direction_angle )
	{		
		if ( curr_state == READY )
			return;
		
		call Drive.invert_direction();
		manage_go_straight(direction_angle);
		
		call Leds.led0On();
		call Leds.led1Off();
		
		curr_state = RUNNING;
		next_bouncing_sensor_id = NULL_SENSOR_ID;
	}
	event void Communicate.on_rotate( int16_t rotation_side )
	{
		// side == 0 => turn left
		// side == 1 => turn right
		
		if ( curr_state != READY )
			return;
		
 		if ( rotation_side > 0 ) call Drive.turn_right();
		else                     call Drive.turn_left();
		
		call Leds.led0On();
		curr_state = RUNNING;
	}
	event void Communicate.on_stop()
	{
		call Drive.stop();
		call Drive.reset();
		call Leds.led0Off();
		call Leds.led1Off();
		
		curr_state = READY;
		next_bouncing_sensor_id = NULL_SENSOR_ID;
	}
	
	
	event void DetectLine.on_line( line_sensor_id_t loc )
	{		
		if ( curr_state == RUNNING )
			manage_bouncing( loc );
	}
	event void DetectLine.off_line( line_sensor_id_t loc )
	{
		sensors_states[loc] = IS_OFF_LINE;
		
		if ( curr_state == BOUNCING && loc == next_bouncing_sensor_id )
		{
			next_bouncing_sensor_id = NULL_SENSOR_ID;
			
			call Drive.go_straight();
			call Leds.led1Off();
			
			curr_state = RUNNING;
		}
	}
	
	event void Sense.on_new_sample( sensor_value_t* sample, uint8_t size )
	{
		call Communicate.new_sensors_sample( sample, size );
	}
	
	
	void manage_bouncing( line_sensor_id_t loc )
	{
		direction_t curr_dir;
		
		if ( sensors_states[loc] == IS_ON_LINE || next_bouncing_sensor_id != NULL_SENSOR_ID )
			return;
		
		sensors_states[loc] = IS_ON_LINE;
		
		curr_dir = call Drive.get_direction();
		if ( curr_dir == FORWARD )
		{
			if ( loc == FRONT_RIGHT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				next_bouncing_sensor_id = FRONT_RIGHT_SENSOR_ID;
				
				call Drive.turn_left();
				call Communicate.collision();
				call Leds.led1On();
			}
			else if ( loc == FRONT_LEFT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				next_bouncing_sensor_id = FRONT_LEFT_SENSOR_ID;
				
				call Drive.turn_right();				
				call Communicate.collision();
				call Leds.led1On();
			}
		}
		else
		{
			if ( loc == BACK_RIGHT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				next_bouncing_sensor_id = BACK_RIGHT_SENSOR_ID;
				
				call Drive.turn_right();
				call Communicate.collision();
				call Leds.led1On();
			}
			else if ( loc == BACK_LEFT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				next_bouncing_sensor_id = BACK_LEFT_SENSOR_ID;
				
				call Drive.turn_left();
				call Communicate.collision();
				call Leds.led1On();
			}
		}
	}
	
	void manage_go_straight( int16_t direction_angle )
	{
		if ( direction_angle == 0 )
			call Drive.go_straight();
		else
			call Drive.go_straight_with_effect( direction_angle > 0 ? GO_EFFECT_RIGHT : GO_EFFECT_LEFT );
	}
}
