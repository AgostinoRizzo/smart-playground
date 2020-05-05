/**
  * Sensing Manager
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 27/07/19
  *
  */

configuration SensingManagerC
{
	provides interface Sense;
}
implementation
{
	components SensingManagerP as SensingManager;
	components new TimerMilliC() as Timer;
	components new SensirionSht11C() as TempHumiSensorDriver;
	components new HamamatsuS1087ParC() as BrightSensorDriver;
	
	Sense = SensingManager.Sense;
	
	SensingManager.Timer            -> Timer;
	SensingManager.ReadTempSensor   -> TempHumiSensorDriver.Temperature;
	SensingManager.ReadHumiSensor   -> TempHumiSensorDriver.Humidity;
	SensingManager.ReadBrightSensor -> BrightSensorDriver;
}
