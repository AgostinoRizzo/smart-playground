#ifndef MOTORS_H
#define MOTORS_H

/*** driving direction ***/
#define FORWARD     0
#define BACKWARD    1

/*** right/left motors ***/
#define RIGHT_MOTOR		1
#define LEFT_MOTOR		0

/*** go straight effects ***/
#define GO_EFFECT_RIGHT		0
#define GO_EFFECT_LEFT		1

/*** PWM status ***/
#define PWM_STATUS_SET		1
#define PWM_STATUS_CLEAR	0

typedef uint8_t direction_t;
typedef uint8_t motor_t;
typedef uint8_t pinstat_t;
typedef uint8_t goeffect_t;
typedef uint8_t pwmstatus_t;


/*** IN1, IN2, IN3, IN4 pins configuration ****/

#define IO_IN1_PIN    IO.ADC3  // pin 10 (10-pin expansion connector)
#define IO_IN2_PIN    IO.SDA   // pin  8 (10-pin expansion connector)
#define IO_IN3_PIN    IO.SCL   // pin  6 (10-pin expansion connector)
#define IO_IN4_PIN    IO.ADC2  // pin  7 (10-pin expansion connector)


/*** PWM motor pins configuration ****/

#define IO_PWM_RIGHT_MOTOR_PIN 		IO.Port26	// pin 4 (6-pin expansion connector)
#define IO_PWM_LEFT_MOTOR_PIN		IO.Port23	// pin 3 (6-pin expansion connector)

#define PWM_MOTOR_INTERVAL			10		    // expressed in milliseconds
#define PWM_NORMAL_SPEED_VALUE		0.5			// PWM percentage
#define PWM_HIGHER_SPEED_VALUE		0.6			// PWM percentage

#define PWM_NORMAL_SPEED_SET_INTERVAL		(PWM_MOTOR_INTERVAL * PWM_NORMAL_SPEED_VALUE)
#define PWM_NORMAL_SPEED_CLEAR_INTERVAL		(PWM_MOTOR_INTERVAL * (1.0 - PWM_NORMAL_SPEED_VALUE))
#define PWM_HIGHER_SPEED_SET_INTERVAL		(PWM_MOTOR_INTERVAL * PWM_HIGHER_SPEED_VALUE)
#define PWM_HIGHER_SPEED_CLEAR_INTERVAL		(PWM_MOTOR_INTERVAL * (1.0 - PWM_HIGHER_SPEED_VALUE))

#endif
