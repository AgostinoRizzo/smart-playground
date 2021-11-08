/**
  * Wind Sensor Interface
  *
  * Author : Agostino Rizzo
  * Date   : 09/07/21
  *
  */

#include "WindSensor.h"

interface WindSensor
{
	command void init();
	event void on_wind_on( windir_t dir );
	event void on_wind_off();
}
