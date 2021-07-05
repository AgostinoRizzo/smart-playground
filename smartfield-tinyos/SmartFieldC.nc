/**
  * Smart Field App
  * 	- Component configuration 
  *
  * Author : Agostino Rizzo
  * Date   : 05/07/21
  *
  */

configuration SmartFieldC {
}
implementation {
  components MainC, SmartFieldP, LedsC;
  components ActiveMessageC as Radio, SerialActiveMessageC as Serial;
  
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
}
