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
	
	components LineDetectionManagerC;
	components MotorsManagerC;
	components CommunicationManagerC;
	components SensingManagerC;
	
	SmartBallC.Boot              -> MainC;
	SmartBallC.Leds              -> LedsC;
	
	SmartBallC.DetectLine        -> LineDetectionManagerC.DetectLine;
	SmartBallC.Drive             -> MotorsManagerC.Drive;
	SmartBallC.Communicate       -> CommunicationManagerC.Communicate;
	SmartBallC.Sense             -> SensingManagerC.Sense;
}

