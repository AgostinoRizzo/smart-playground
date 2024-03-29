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

module SmartBallC
{
	uses interface Boot;
	uses interface Leds;
	
	uses interface DetectLine;
	uses interface Drive;
	uses interface Communicate;
	uses interface Sense;
	
	uses interface Timer<TMilli> as TrajectorAdjTimer;
	uses interface Timer<TMilli> as BouncingTimer;
	uses interface Timer<TMilli> as BouncingTimerII;
}
implementation
{
	state_t             curr_state = READY;
	uint32_t            bouncing_start_time = 0;
	uint32_t            bouncing_end_time = 0;
	line_sensor_id_t    next_bouncing_sensor_id = NULL_SENSOR_ID;
	bool                stop_flag = FALSE;
	
	int32_t             running_dir = 0;
	
	running_dir_t       swing_running_dir = RUNNING_DIR_RIGHT;
	
	#define IS_ON_LINE     TRUE
	#define IS_OFF_LINE    FALSE
	
	bool                sensors_states[] = { IS_OFF_LINE, IS_OFF_LINE, IS_OFF_LINE, IS_OFF_LINE};
	
	int32_t  get_rotation_time( int32_t direction_angle );
	void     adjust_running_dir( int32_t rot );
	int32_t  mod( int32_t x );
	void     manage_bouncing( line_sensor_id_t loc );
	void     manage_bouncing_ii( line_sensor_id_t loc );
	
	void invert_swing_running_dir( running_dir_t* dir );
	bool running_swing_dir_eq( running_dir_t rdir, swing_dir_t sdir );
	
	
	event void Boot.booted()
	{
		call Drive.init();
		call Communicate.init();
		call DetectLine.start_detection();
		call Sense.start_sensing();
	}
	
	event void Communicate.on_start( int16_t direction_angle )
	{
		uint32_t rot_time;
		int32_t  dir_mod;
		
		if ( curr_state != READY )
			return;
		
		dir_mod     = mod(direction_angle);
		rot_time    = get_rotation_time(dir_mod);
		running_dir = dir_mod;
		
		if ( direction_angle >=0 )
		{
			swing_running_dir = RUNNING_DIR_LEFT;
			call Drive.turn_right();
		}
		else
		{
			swing_running_dir = RUNNING_DIR_RIGHT;
			call Drive.turn_left();
		}
		
		curr_state = TRAJECTORY_ADJ;
		call TrajectorAdjTimer.startOneShot( rot_time );
	}
	event void Communicate.on_swing( int16_t direction_angle )
	{
		uint32_t rot_time;
		int32_t  dir_mod;
		
		if ( curr_state != RUNNING )
			return;
		
		if ( direction_angle == 0 )
		{
			call Drive.invert_direction();
			call Drive.go_straight();
			curr_state = RUNNING;
		}
		else
		{
			dir_mod     = mod(direction_angle);
			rot_time    = get_rotation_time(dir_mod);
			curr_state  = BOUNCING;
			
			if ( direction_angle >=0 )
			{
				swing_running_dir = RUNNING_DIR_LEFT;
				call Drive.turn_right();
			}
			else
			{
				swing_running_dir = RUNNING_DIR_RIGHT;
				call Drive.turn_left();
			}
					
			invert_swing_running_dir( &swing_running_dir );
			call BouncingTimer.startOneShot( rot_time );
		}
	}
	event void Communicate.on_stop()
	{
		if ( curr_state == RUNNING )
		{
			call Drive.stop();
			call Drive.reset();
			curr_state = READY;
		}
		else
			stop_flag = TRUE;
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
			
			if ( stop_flag )
			{
				stop_flag = FALSE;
				call Drive.stop();
				call Drive.reset();
				curr_state = READY;
			}
			else
			{
				call Drive.go_straight();
				curr_state = RUNNING;
			}
		}
	}
	
	event void TrajectorAdjTimer.fired()
	{
		if ( stop_flag )
		{
			stop_flag = FALSE;
			call Drive.stop();
			call Drive.reset();
			curr_state = READY;
		}
		else
		{
			call Drive.go_straight();
			curr_state = RUNNING;
		}
	}
	event void BouncingTimer.fired()
	{
		if ( stop_flag )
		{
			stop_flag = FALSE;
			call Drive.stop();
			call Drive.reset();
			curr_state = READY;
		}
		else
		{
			call Drive.invert_direction();
			call Drive.go_straight();
			curr_state = RUNNING;
		}
	}
	event void BouncingTimerII.fired()
	{
		if ( stop_flag )
		{
			stop_flag = FALSE;
			call Drive.stop();
			call Drive.reset();
			curr_state = READY;
		}
		else
		{
			call Drive.invert_direction();
			call Drive.go_straight();
			curr_state = RUNNING;
		}
	}
	
	event void Sense.on_new_sample( sensor_value_t* sample, uint8_t size )
	{
		call Communicate.new_sensors_sample( sample, size );
	}
	
	
	int32_t get_rotation_time( int32_t direction_angle )
	{
		return (int32_t)( ROTATION_RATIO*((float)direction_angle) );
	}
	
	int32_t  mod( int32_t x )
	{
		if ( x>=0 )
			return x;
		return (-x);
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
				call Drive.turn_left();
				next_bouncing_sensor_id = FRONT_RIGHT_SENSOR_ID;
				invert_swing_running_dir( &swing_running_dir );
				
				call Communicate.collision();
			}
			else if ( loc == FRONT_LEFT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				call Drive.turn_right();
				next_bouncing_sensor_id = FRONT_LEFT_SENSOR_ID;
				invert_swing_running_dir( &swing_running_dir );
				
				call Communicate.collision();
			}
		}
		else
		{
			if ( loc == BACK_RIGHT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				call Drive.turn_right();
				next_bouncing_sensor_id = BACK_RIGHT_SENSOR_ID;
				invert_swing_running_dir( &swing_running_dir );
				
				call Communicate.collision();
			}
			else if ( loc == BACK_LEFT_SENSOR_ID )
			{
				curr_state = BOUNCING;
				call Drive.turn_left();
				next_bouncing_sensor_id = BACK_LEFT_SENSOR_ID;
				invert_swing_running_dir( &swing_running_dir );
				
				call Communicate.collision();
			}
		}
	}
	void adjust_running_dir( int32_t rot )
	{
		running_dir = (running_dir+rot)%360;
	}
	void invert_swing_running_dir( running_dir_t* dir )
	{
		if ( (*dir) == RUNNING_DIR_RIGHT )
			(*dir) = RUNNING_DIR_LEFT;
		else
			(*dir) = RUNNING_DIR_RIGHT;
	}
	bool running_swing_dir_eq( running_dir_t rdir, swing_dir_t sdir )
	{
		return ( rdir == sdir );
	}
}
