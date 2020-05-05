/**
  * Line Detection Manager
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 23/07/19
  *
  */

configuration LineDetectionManagerC
{
	provides interface DetectLine;
}
implementation
{
	components new TimerMilliC() as Timer;
	components LineDetectionManagerP as DetectionManager;
	
	components new AdcReadClientC() as AdcReadClientFRC;
	components new AdcReadClientC() as AdcReadClientFLC;
	components new AdcReadClientC() as AdcReadClientBRC;
	components new AdcReadClientC() as AdcReadClientBLC;
	
	components new AdcReadStreamClientC() as AdcReadStreamClientFRC;
	components new AdcReadStreamClientC() as AdcReadStreamClientFLC;
	components new AdcReadStreamClientC() as AdcReadStreamClientBRC;
	components new AdcReadStreamClientC() as AdcReadStreamClientBLC;
	
		
	DetectLine = DetectionManager.DetectLine;
	
	DetectionManager.Timer -> Timer;
	
	DetectionManager.ReadFRSensor -> AdcReadClientFRC.Read;
	DetectionManager.ReadFLSensor -> AdcReadClientFLC.Read;
	DetectionManager.ReadBRSensor -> AdcReadClientBRC.Read;
	DetectionManager.ReadBLSensor -> AdcReadClientBLC.Read;
	
	
	DetectionManager.ReadStreamFRSensor -> AdcReadStreamClientFRC.ReadStream;
	DetectionManager.ReadStreamFLSensor -> AdcReadStreamClientFLC.ReadStream;
	DetectionManager.ReadStreamBRSensor -> AdcReadStreamClientBRC.ReadStream;
	DetectionManager.ReadStreamBLSensor -> AdcReadStreamClientBLC.ReadStream;
	
	AdcReadClientFRC.AdcConfigure -> DetectionManager.AdcConfigureFR;
	AdcReadClientFLC.AdcConfigure -> DetectionManager.AdcConfigureFL;
	AdcReadClientBRC.AdcConfigure -> DetectionManager.AdcConfigureBR;
	AdcReadClientBLC.AdcConfigure -> DetectionManager.AdcConfigureBL;
	  
	AdcReadStreamClientFRC.AdcConfigure -> DetectionManager.AdcConfigureFR;
	AdcReadStreamClientFLC.AdcConfigure -> DetectionManager.AdcConfigureFL;
	AdcReadStreamClientBRC.AdcConfigure -> DetectionManager.AdcConfigureBR;
	AdcReadStreamClientBLC.AdcConfigure -> DetectionManager.AdcConfigureBL;
}
