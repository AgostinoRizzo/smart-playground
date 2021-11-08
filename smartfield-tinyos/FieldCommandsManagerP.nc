/**
  * Field Commands Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 05/07/21
  *
  */

#include "FieldCommands.h"

module FieldCommandsManagerP
{
	provides interface FieldCommands;
	
	/* SPI pins */
	uses interface HplMsp430GeneralIO as CS_pin;	// ChipSelect pin
	uses interface HplMsp430GeneralIO as CLK_pin;	// Clock pin
	uses interface HplMsp430GeneralIO as DIN_pin;	// MISO pin
	uses interface HplMsp430GeneralIO as DOUT_pin;	// MOSI pin
}

implementation
{
	uint8_t spi_write_buff;
	uint8_t i;
	
	command void FieldCommands.init()
	{
		// SPI communication pins setup.
		call CS_pin.makeOutput();
		call CLK_pin.makeOutput();
		call DIN_pin.makeInput();
		call DOUT_pin.makeOutput();
		
		call CS_pin.set();
	}
	
	command void FieldCommands.set( field_cmd_pattern_t cmd )
	{
		call CS_pin.set();
		call CLK_pin.set();
		call CS_pin.clr();
		
		spi_write_buff = cmd;
		
		// write byte
		for ( i=8; i>=1; --i )
		{
			call CLK_pin.clr();
			if ( (spi_write_buff>>(i-1) & 0x01) != 0 )
				call DOUT_pin.set();
			else
				call DOUT_pin.clr();
			call CLK_pin.set();
		}
		
		call CS_pin.set();
	}
}  
