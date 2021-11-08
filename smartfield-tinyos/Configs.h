#ifndef CONFIGS_H
#define CONFIGS_H

// UART incoming message structs.

// fans + lights on/off command message.
#define FIELD_COMMAND_CODE 10
typedef nx_struct field_cmd_msg
{
	nx_uint8_t code;
	nx_uint8_t cmd;
} field_cmd_msg_t;

#endif
