/**
  * Motors Manager
  * 	- Module component
  *
  * Author : Agostino Rizzo
  * Date   : 26/07/19
  *
  */

module MotorsManagerP
{
	provides interface Drive;
	
	uses interface HplMsp430GeneralIO as IN1;
	uses interface HplMsp430GeneralIO as IN2;
	uses interface HplMsp430GeneralIO as IN3;
	uses interface HplMsp430GeneralIO as IN4;
}
implementation
{	
	direction_t curr_dir=FORWARD;
	
	
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
	
	
	command void Drive.init()
	{
		call IN1.makeOutput();
		call IN2.makeOutput();
		call IN3.makeOutput();
		call IN4.makeOutput();
		
		call Drive.reset();
	}
	
	command void Drive.reset()
	{
		call Drive.stop();
		curr_dir=FORWARD;
	}
	
	command void Drive.go_straight()
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
	
	command void Drive.turn_right()
	{
		right_motor_forward();
		left_motor_backward();
		
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
}
