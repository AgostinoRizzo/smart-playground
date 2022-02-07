/**
  * Communication Manager
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 27/07/19
  *
  */

#include "Communication.h"

configuration CommunicationManagerC
{
	provides interface Communicate;
}
implementation
{
	components CommunicationManagerP as CommunicationManager;
	components ActiveMessageC;
	
	components new AMSenderC( AM_SENSOR_COMM_CODE );
	components new AMReceiverC( AM_BASE_STATION_COMM_CODE );
	
	components LedsC;
	
	
	Communicate = CommunicationManager.Communicate;
	  
	CommunicationManager.RadioControl -> ActiveMessageC;
	CommunicationManager.AMSend       -> AMSenderC;
	CommunicationManager.Receive      -> AMReceiverC;
	
	CommunicationManager.Leds -> LedsC;
}
