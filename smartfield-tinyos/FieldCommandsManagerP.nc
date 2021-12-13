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
	
	/* COMMAND pins */
	// command bits order: b1 b2 b3 b4
	uses interface HplMsp430GeneralIO as CMD_bit1_pin;	// 1st more significant bit (most significant)
	uses interface HplMsp430GeneralIO as CMD_bit2_pin;	// 2nd more significant bit
	uses interface HplMsp430GeneralIO as CMD_bit3_pin;	// 3th more significant bit
	uses interface HplMsp430GeneralIO as CMD_bit4_pin;	// 4th more significant bit (least significant)
}

implementation
{
	uint8_t cmd_write_buff;
	uint8_t i;
	
	command void FieldCommands.init()
	{
		// CMD communication pins setup.
		call CMD_bit1_pin.makeOutput();
		call CMD_bit2_pin.makeOutput();
		call CMD_bit3_pin.makeOutput();
		call CMD_bit4_pin.makeOutput();
		
		call CMD_bit1_pin.clr();
		call CMD_bit2_pin.clr();
		call CMD_bit3_pin.clr();
		call CMD_bit4_pin.clr();
	}
	
	command void FieldCommands.set( field_cmd_pattern_t cmd )
	{
		// only last 4 bits (less significant) of cmd are considered
		cmd_write_buff = cmd;
		(  cmd_write_buff     & 0x01 ) ? call CMD_bit4_pin.set() : call CMD_bit4_pin.clr();
		( (cmd_write_buff>>1) & 0x01 ) ? call CMD_bit3_pin.set() : call CMD_bit3_pin.clr();
		( (cmd_write_buff>>2) & 0x01 ) ? call CMD_bit2_pin.set() : call CMD_bit2_pin.clr();
		( (cmd_write_buff>>3) & 0x01 ) ? call CMD_bit1_pin.set() : call CMD_bit1_pin.clr();
	}
}  
