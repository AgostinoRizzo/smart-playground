/**
  * Smart Ball App
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 31/07/19
  *
  */

configuration SmartBallAppC {}
implementation
{
	components SmartBallC;
	components MainC;
	components LedsC;
	
	components new TimerMilliC() as TrajectorAdjTimer;
	components new TimerMilliC() as BouncingTimer;
	components new TimerMilliC() as BouncingTimerII;
	
	components LineDetectionManagerC;
	components MotorsManagerC;
	components CommunicationManagerC;
	components SensingManagerC;
	
	SmartBallC.Boot              -> MainC;
	SmartBallC.Leds              -> LedsC;
	SmartBallC.TrajectorAdjTimer -> TrajectorAdjTimer;
	SmartBallC.BouncingTimer    -> BouncingTimer;
	SmartBallC.BouncingTimerII   -> BouncingTimerII;
	
	SmartBallC.DetectLine        -> LineDetectionManagerC.DetectLine;
	SmartBallC.Drive             -> MotorsManagerC.Drive;
	SmartBallC.Communicate       -> CommunicationManagerC.Communicate;
	SmartBallC.Sense             -> SensingManagerC.Sense;
}

