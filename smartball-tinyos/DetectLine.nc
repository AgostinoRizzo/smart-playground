/**
  * Detect Line Interface
  *
  * Author : Agostino Rizzo
  * Date   : 23/07/19
  *
  */

#include "LineSensor.h"

interface DetectLine
{
	command void start_detection();
	//event void onStartDone();
	event void on_line( line_sensor_id_t loc );
	event void off_line( line_sensor_id_t loc );
}
