/**
  * Field Commands Interface
  *
  * Author : Agostino Rizzo
  * Date   : 05/07/21
  *
  */

#include "FieldCommands.h"

interface FieldCommands
{
	command void init();
	command void set(field_cmd_pattern_t cmd);
}
