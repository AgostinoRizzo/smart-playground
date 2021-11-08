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
	
	FieldCommandsManager.CS_pin   -> SPI_CS_PIN;
	FieldCommandsManager.CLK_pin  -> SPI_CLK_PIN
	FieldCommandsManager.DIN_pin  -> SPI_DIN_PIN;
	FieldCommandsManager.DOUT_pin -> SPI_DOUT_PIN;
}
