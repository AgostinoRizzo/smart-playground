/**
  * Motors Manager
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 26/07/19
  *
  */

#include "Motors.h"

configuration MotorsManagerC
{
	provides interface Drive;
}
implementation
{
	components MotorsManagerP      as MotorsManager;
	components HplMsp430GeneralIOC as IO;
	
	Drive = MotorsManager.Drive;
	
	MotorsManager.IN1 -> IO_IN1_PIN;
	MotorsManager.IN2 -> IO_IN2_PIN;
	MotorsManager.IN3 -> IO_IN3_PIN;
	MotorsManager.IN4 -> IO_IN4_PIN;
}
