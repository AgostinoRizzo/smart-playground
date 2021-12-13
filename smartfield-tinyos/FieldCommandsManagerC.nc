/**
  * Field Commands Manager
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 05/07/21
  *
  */

#include "FieldCommands.h"

configuration FieldCommandsManagerC
{
	provides interface FieldCommands;
}
implementation
{
	components FieldCommandsManagerP as FieldCommandsManager;
	components HplMsp430GeneralIOC as IO;
	
	FieldCommands = FieldCommandsManager.FieldCommands;
	
	FieldCommandsManager.CMD_bit1_pin -> CMD_BIT1_PIN;
	FieldCommandsManager.CMD_bit2_pin -> CMD_BIT2_PIN
	FieldCommandsManager.CMD_bit3_pin -> CMD_BIT3_PIN;
	FieldCommandsManager.CMD_bit4_pin -> CMD_BIT4_PIN;
}
