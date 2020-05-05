#ifndef MOTORS_H
#define MOTORS_H

/*** driving direction ***/

#define FORWARD     0
#define BACKWARD    1

typedef uint8_t direction_t;


/*** IN1, IN2, IN3, IN4 pins configuration ****/

#define IO_IN1_PIN    IO.ADC3  // pin 10 (10-pin expansion connector)
#define IO_IN2_PIN    IO.SDA   // pin  8 (10-pin expansion connector)
#define IO_IN3_PIN    IO.SCL   // pin  6 (10-pin expansion connector)
#define IO_IN4_PIN    IO.ADC2  // pin  7 (10-pin expansion connector)

#endif
