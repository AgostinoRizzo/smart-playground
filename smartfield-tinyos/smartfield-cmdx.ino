/* smartfield command executor sketch */

#define CMD_RELAY_PIN 1  // command bits order: b1 b2 b3 b4
#define CMD_BIT1_PIN 10  // 1st more significant bit (most significant)
#define CMD_BIT2_PIN 11  // 2nd more significant bit
#define CMD_BIT3_PIN 12  // 3th more significant bit
#define CMD_BIT4_PIN 13  // 4th more significant bit (least significant)
#define READ_CMD_BITS_DT 10  // expressed in millis
#define STABLE_CMD_READ_COUNT 4

#define FR_LED_PIN 6
#define BR_LED_PIN 7
#define BL_LED_PIN 8
#define FL_LED_PIN 9

#define FR_FAN_PIN 2
#define BR_FAN_PIN 3
#define BL_FAN_PIN 4
#define FL_FAN_PIN 5

#define CMD_PINS_COUNT 4
#define LEDS_COUNT 4
#define FANS_COUNT 4

const int CMD_PINS[] = { CMD_BIT1_PIN, CMD_BIT2_PIN, CMD_BIT3_PIN, CMD_BIT4_PIN };
const int LEDS[] = { FR_LED_PIN, BR_LED_PIN, BL_LED_PIN, FL_LED_PIN };
const int FANS[] = { FR_FAN_PIN, BR_FAN_PIN, BL_FAN_PIN, FL_FAN_PIN };

byte last_cmdbits=0, cmdbits=0;

void CMD_setup()
{
  for ( byte b=0; b<CMD_PINS_COUNT; ++b )
    pinMode(CMD_PINS[b], INPUT);
  delay(100);
}
void LEDS_setup()
{
  for ( byte l=0; l<LEDS_COUNT; ++l )
  {
    pinMode(LEDS[l], OUTPUT);
    digitalWrite(LEDS[l], LOW);
  }
  delay(100);
}
void FANS_setup()
{
  for ( byte f=0; f<FANS_COUNT; ++f )
  {
    pinMode(FANS[f], OUTPUT);
    digitalWrite(FANS[f], HIGH);
  }
  delay(100);
}
void LEDS_welcome()
{
  for ( byte l=0; l<LEDS_COUNT; ++l )
  {
    digitalWrite(LEDS[l], HIGH); delay(1000);
    digitalWrite(LEDS[l], LOW);
  }
}
void FANS_welcome()
{
  for ( byte f=0; f<FANS_COUNT; ++f )
  {
    digitalWrite(FANS[f], LOW); delay(1000);
    digitalWrite(FANS[f], HIGH);
  }
}
void LEDS_set( const byte pattern )
{
  for ( byte l=0; l<LEDS_COUNT; ++l )
    ((pattern >> l) & 0x01) ? digitalWrite(LEDS[l], HIGH) 
                            : digitalWrite(LEDS[l], LOW);
}
void FANS_set( const byte pattern )
{
  for ( byte f=0; f<FANS_COUNT; ++f )
    ((pattern >> f) & 0x01) ? digitalWrite(FANS[f], LOW) 
                            : digitalWrite(FANS[f], HIGH);
}

void setup()
{
  pinMode(CMD_RELAY_PIN, OUTPUT);
  digitalWrite(CMD_RELAY_PIN, HIGH);
  
  CMD_setup();
  LEDS_setup();
  FANS_setup();

  LEDS_welcome();
  FANS_welcome();
  delay(3000);

  digitalWrite(CMD_RELAY_PIN, LOW);
  delay(100);
}

byte readCommandBits()
{
  byte bits=0, b=0;
  for ( ; b<CMD_PINS_COUNT; ++b )
  {
    bits <<= 1;
    if ( digitalRead(CMD_PINS[b]) )
      ++bits;
  }
  return bits;
}
byte readStableCommandBits()
{
  byte currbits, lastbits=0, stable_count=0;
  do
  {
    delay(READ_CMD_BITS_DT);
    currbits = readCommandBits();
    if ( currbits == lastbits ) ++stable_count;
    else { stable_count=0; lastbits=currbits; }
  
  } while ( stable_count < STABLE_CMD_READ_COUNT );
  
  return currbits;
}
void manageCommand( const byte cmd )
{
  switch ( cmd )
  {
    // leds setting cases
    case 1:  LEDS_set( 0b00000000 ); break;  // all off
    case 2:  LEDS_set( 0b00001000 ); break;  // FL only
    case 3:  LEDS_set( 0b00000100 ); break;  // BL only
    case 4:  LEDS_set( 0b00000010 ); break;  // BR only
    case 5:  LEDS_set( 0b00000001 ); break;  // FR only
    case 6:  LEDS_set( 0b00001111 ); break;  // all on
    
    // fans setting cases
    case 7:  FANS_set( 0b00000000 ); break;  // all off
    case 8:  FANS_set( 0b00001000 ); break;  // FL only
    case 9:  FANS_set( 0b00000100 ); break;  // BL only
    case 10: FANS_set( 0b00000010 ); break;  // BR only
    case 11: FANS_set( 0b00000001 ); break;  // FR only
    case 12: FANS_set( 0b00001111 ); break;  // all on
  }
}

void loop()
{
  cmdbits = readStableCommandBits();
  if ( cmdbits != last_cmdbits && cmdbits )
  {
    manageCommand(cmdbits);
    last_cmdbits = cmdbits;
  }
}
