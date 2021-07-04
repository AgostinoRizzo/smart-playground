/**
  * Motor Drive Interface
  *
  * Author : Agostino Rizzo
  * Date   : 26/07/19
  *
  */

#include "Motors.h"

interface Drive
{
	command void init();
	command void reset();
	
	command void go_straight();
	command void go_straight_with_effect( goeffect_t effect );
	
	command void turn_right();
	command void turn_left();
	
	command void stop();
	
	command void set_direction( direction_t dir );
	command void invert_direction();
	command direction_t get_direction();
}
