/**
  * Smart Field App
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 05/07/21
  *
  */

#include "WindSensor.h"

configuration SmartFieldC {
}
implementation {
  components MainC, SmartFieldP, LedsC;
  components ActiveMessageC as Radio, SerialActiveMessageC as Serial;
  
  components FieldCommandsManagerC;
  components WindSensorManagerC;
  
  MainC.Boot <- SmartFieldP;

  SmartFieldP.RadioControl -> Radio;
  SmartFieldP.SerialControl -> Serial;
  
  SmartFieldP.UartSend -> Serial;
  SmartFieldP.UartReceive -> Serial.Receive;
  SmartFieldP.UartPacket -> Serial;
  SmartFieldP.UartAMPacket -> Serial;
  
  SmartFieldP.RadioSend -> Radio;
  SmartFieldP.RadioReceive -> Radio.Receive;
  SmartFieldP.RadioSnoop -> Radio.Snoop;
  SmartFieldP.RadioPacket -> Radio;
  SmartFieldP.RadioAMPacket -> Radio;
  
  SmartFieldP.Leds -> LedsC;
  
  SmartFieldP.FieldCommands -> FieldCommandsManagerC.FieldCommands;
  SmartFieldP.WindSensor    -> WindSensorManagerC.WindSensor;
}
