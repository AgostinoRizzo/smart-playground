#ifndef FIELD_COMMANDS_H
#define FIELD_COMMANDS_H

// SPI communication pins
#define SPI_CS_PIN		IO.Port26;	// pin 4 (6-pin expansion connector)
#define SPI_CLK_PIN		IO.Port66;	// pin 1 (6-pin expansion connector)
#define SPI_DIN_PIN		IO.Port67;	// pin 2 (6-pin expansion connector) - MISO
#define SPI_DOUT_PIN	IO.Port23;	// pin 3 (6-pin expansion connector) - MOSI

typedef uint8_t field_cmd_pattern_t;

#endif
