/**
  * Communicate Interface
  *
  * Author : Agostino Rizzo
  * Date   : 27/07/19
  *
  */

#include "Sensing.h"

interface Communicate
{
	// initialize radio communication with the base station
	command void init();
	
	/* commands used to send messages to the base station */
	
	// collision with border line
	command void collision();
	
	// new sensors sample
	command void new_sensors_sample( sensor_value_t* sample, uint8_t size );
	
	
	/* events used when receiving messages from the base station */
	
	// start running from a particular direction
	event void on_start( int16_t direction_angle );
	
	// manage swing bouncing
	event void on_swing( int16_t direction_angle );
	
	// stop running
	event void on_stop();
}
