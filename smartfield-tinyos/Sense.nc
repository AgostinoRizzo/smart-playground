/**
  * Sense Interface
  *
  * Author : Agostino Rizzo
  * Date   : 23/07/19
  *
  */

#include "Sensing.h"

interface Sense
{
	command void start_sensing();
	event void on_new_sample( sensor_value_t* sample, uint8_t size );
}
