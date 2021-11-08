/**
  * Wind Sensor Manager
  *		- Component configuration
  *
  * Author : Agostino Rizzo
  * Date   : 09/07/21
  *
  */

configuration WindSensorManagerC
{
	provides interface WindSensor;
}
implementation
{
	components WindSensorManagerP as WindSensorManager;
	
	components new AdcReadClientC() as AdcReadClientWindOnOff;
	components new AdcReadClientC() as AdcReadClientWindDir;
	
	components new AdcReadStreamClientC() as AdcReadStreamClientWindOnOff;
	components new AdcReadStreamClientC() as AdcReadStreamClientWindDir;
	
	components new TimerMilliC() as WindOnoffSampleTimer;
	components new TimerMilliC() as WindDirSampleTimer;
	
	
	WindSensor = WindSensorManager.WindSensor;
	
	
	WindSensorManager.ReadWindOnOffSensor -> AdcReadClientWindOnOff;
	WindSensorManager.ReadWindDirSensor   -> AdcReadClientWindDir;
	
	WindSensorManager.ReadStreamWindOnOffSensor -> AdcReadStreamClientWindOnOff;
	WindSensorManager.ReadStreamWindDirSensor   -> AdcReadStreamClientWindDir;
	
	AdcReadClientWindOnOff.AdcConfigure -> WindSensorManager.AdcConfigureWindOnOff;
	AdcReadClientWindDir.AdcConfigure   -> WindSensorManager.AdcConfigureWindDir;
	
	AdcReadStreamClientWindOnOff.AdcConfigure -> WindSensorManager.AdcConfigureWindOnOff;
	AdcReadStreamClientWindDir.AdcConfigure   -> WindSensorManager.AdcConfigureWindDir;
	
	WindSensorManager.WindOnoffSampleTimer -> WindOnoffSampleTimer;
	WindSensorManager.WindDirSampleTimer   -> WindDirSampleTimer;
}
