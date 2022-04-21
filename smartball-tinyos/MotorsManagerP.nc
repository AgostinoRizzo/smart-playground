/**
  * Motors Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 26/07/19
  *
  */

#include "Motors.h"

#define TRUE  1
#define FALSE 0

module MotorsManagerP
{
	provides interface Drive;
	
	uses interface HplMsp430GeneralIO as IN1;
	uses interface HplMsp430GeneralIO as IN2;
	uses interface HplMsp430GeneralIO as IN3;
	uses interface HplMsp430GeneralIO as IN4;
	
	uses interface HplMsp430GeneralIO as PWM_RIGHT_MOTOR;
	uses interface HplMsp430GeneralIO as PWM_LEFT_MOTOR;
	
	uses interface Timer<TMilli> as PwmRightMotorSpeedTimer;
	uses interface Timer<TMilli> as PwmLeftMotorSpeedTimer;
}
implementation
{	
	direction_t curr_dir=FORWARD;
	pinstat_t   pwm_status[]  = {FALSE, FALSE};
	bool        motor_boost[] = {FALSE, FALSE};
	
	
	/* right motor spinning routines */
	
	void right_motor_off()
	{
		/* stop right motor ( IN3 -> low, IN4 -> low ) */
		
		call IN3.clr();
		call IN4.clr();
	}
	void right_motor_forward()
	{
		/* spin right motor forward ( IN3 -> high, IN4 -> low ) */
		
		call IN3.set();
		call IN4.clr();
	}
	void right_motor_backward()
	{
		/* spin right motor backward ( IN3 -> low, IN4 -> high ) */
		
		call IN3.clr();
		call IN4.set();
	}
	
	/* left motor spinning routines */
	
	void left_motor_off()
	{
		/* stop left motor ( IN1 -> low, IN2 -> low ) */
		
		call IN1.clr();
		call IN2.clr();
	}
	void left_motor_forward()
	{
		/* spin left motor forward ( IN1 -> high, IN2 -> low ) */
		
		call IN1.set();
		call IN2.clr();
	}
	void left_motor_backward()
	{
		/* spin left motor backward ( IN1 -> low, IN2 -> high ) */
		
		call IN1.clr();
		call IN2.set();
	}
	
	void go_straight_proc()
	{
		if ( curr_dir==FORWARD )   // forward direction
		{
			right_motor_backward();
			left_motor_backward();
		}
		else                       // backward dirction
		{
			right_motor_forward();
			left_motor_forward();
		}
	}
	
	/* PWM motor boost effect routines */
	
	void setBoostEffect( motor_t motor )
	{
		motor_boost[motor] = TRUE;
	}
	
	void clearAllBoostEffects()
	{
		motor_boost[RIGHT_MOTOR] = FALSE;
		motor_boost[LEFT_MOTOR]  = FALSE;
	}
	
	uint16_t getPwmInterval( motor_t motor, pwmstatus_t pwm_motor_status )
	{
		uint16_t interval;
		
		if ( motor_boost[motor] )  // on motor boosting
			interval = pwm_motor_status == PWM_STATUS_SET ? PWM_HIGHER_SPEED_SET_INTERVAL : PWM_HIGHER_SPEED_CLEAR_INTERVAL;
		else                       // on normal motor speed
			interval = pwm_motor_status == PWM_STATUS_SET ? PWM_NORMAL_SPEED_SET_INTERVAL : PWM_NORMAL_SPEED_CLEAR_INTERVAL;
		
		return interval;
	}
	
	
	command void Drive.init()
	{
		call IN1.makeOutput();
		call IN2.makeOutput();
		call IN3.makeOutput();
		call IN4.makeOutput();
		
		call PWM_RIGHT_MOTOR.makeOutput();
		call PWM_LEFT_MOTOR.makeOutput();
		
		call Drive.reset();
		
		call PWM_RIGHT_MOTOR.clr();
		call PWM_LEFT_MOTOR.clr();
		
		call PwmRightMotorSpeedTimer.startOneShot(PWM_NORMAL_SPEED_CLEAR_INTERVAL);
		call PwmLeftMotorSpeedTimer.startOneShot(PWM_NORMAL_SPEED_CLEAR_INTERVAL);
	}
	
	command void Drive.reset()
	{
		call Drive.stop();
		curr_dir=FORWARD;
		clearAllBoostEffects();
	}
	
	command void Drive.go_straight()
	{
		go_straight_proc();
		clearAllBoostEffects();
	}
	
	command void Drive.go_straight_with_effect( goeffect_t effect )
	{
		clearAllBoostEffects();
		go_straight_proc();
		
		if ( curr_dir == FORWARD )
			( effect == GO_EFFECT_RIGHT )
				? setBoostEffect(LEFT_MOTOR) : setBoostEffect(RIGHT_MOTOR);
		else
			( effect == GO_EFFECT_RIGHT )
				? setBoostEffect(RIGHT_MOTOR) : setBoostEffect(LEFT_MOTOR);
	}
	
	command void Drive.turn_right()
	{
		right_motor_forward();
		left_motor_backward();
		
		clearAllBoostEffects();
		
		/*if ( curr_dir==FORWARD )   // forward direction
		{
			right_motor_off(); //right_motor_forward();
			left_motor_backward();
		}
		else                       // backward dirction
		{
			right_motor_forward();
			left_motor_off();
		}*/
	}
	command void Drive.turn_left()
	{
		right_motor_backward();
		left_motor_forward();
		
		clearAllBoostEffects();
		
		/*if ( curr_dir==FORWARD )   // forward direction
		{
			right_motor_backward();
			left_motor_off(); //left_motor_forward();
		}
		else                       // backward dirction
		{
			right_motor_off();
			left_motor_forward();
		}*/
	}
	command void Drive.stop()
	{
		right_motor_off();
		left_motor_off();
		
		clearAllBoostEffects();
	}
	
	command void Drive.set_direction( direction_t dir )
	{
		curr_dir=dir;
	}
	command void Drive.invert_direction()
	{
		if ( curr_dir==FORWARD )
			curr_dir=BACKWARD;
		else
			curr_dir=FORWARD;
	}
	command direction_t Drive.get_direction()
	{
		return curr_dir;
	}
	
	event void PwmRightMotorSpeedTimer.fired()
	{
		call PWM_RIGHT_MOTOR.toggle();
		if ( pwm_status[RIGHT_MOTOR] )
		{
			pwm_status[RIGHT_MOTOR] = FALSE;
			call PwmRightMotorSpeedTimer.startOneShot( getPwmInterval(RIGHT_MOTOR, PWM_STATUS_CLEAR) );
		}
		else
		{
			pwm_status[RIGHT_MOTOR] = TRUE;
			call PwmRightMotorSpeedTimer.startOneShot( getPwmInterval(RIGHT_MOTOR, PWM_STATUS_SET) );
		}
	}
	
	event void PwmLeftMotorSpeedTimer.fired()
	{
		call PWM_LEFT_MOTOR.toggle();
		if ( pwm_status[LEFT_MOTOR] )
		{
			pwm_status[LEFT_MOTOR] = FALSE;
			call PwmLeftMotorSpeedTimer.startOneShot( getPwmInterval(LEFT_MOTOR, PWM_STATUS_CLEAR) );
		}
		else
		{
			pwm_status[LEFT_MOTOR] = TRUE;
			call PwmLeftMotorSpeedTimer.startOneShot( getPwmInterval(LEFT_MOTOR, PWM_STATUS_SET) );
		}
	}
	
}
