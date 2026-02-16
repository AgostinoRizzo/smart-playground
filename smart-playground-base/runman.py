#!/usr/bin/python3

from threading import Thread
import tinyos_serial, wiimote_configs
from smart_objects import SmartRacket, SmartObjectsMediator, GolfClubSwingDetector
from network import EcosystemEventProvider

class KeyboardListener(Thread):
    def __init__(self):
        Thread.__init__(self)
        self.setDaemon(True)
    
    def run(self) -> None:
        while (True):
            inputKey = input()
            
            if inputKey == 'a' or inputKey == 'A':
                self.__onUserAckEvent()
            else:
                self.__onRacketSwingEvent()
                #self.__onClubHitEvent()
    
    def __printEvent(self, eventText:str):
        print('\033[1A' + 'KeyboardEvent: ' + eventText + '\033[K')

    def __onUserAckEvent(self):
        self.__printEvent('UserAck')
        EcosystemEventProvider.get_instance().notify_user_ack()

    def __onRacketSwingEvent(self):
        self.__printEvent('RacketSwing')
        SmartObjectsMediator.get_current_instance().onTennisRacketSwingCallback(wiimote_configs.LEFT_SWING, False)
    
    def __onClubHitEvent(self):
        self.__printEvent('ClubHit')
        SmartObjectsMediator.get_current_instance().onGolfClubSwingCallback(GolfClubSwingDetector.LIGHT_SWING_TYPE)
            

TELOSB_ON = True
WIIMOTE_ON = True
KEYBOARD_LISTENER = None

def init(argv: list):
    global TELOSB_ON
    global WIIMOTE_ON
    if '--telosb-off' in argv: TELOSB_ON = False
    if '--wiimote-off' in argv: WIIMOTE_ON = False
    if '--keyboard-event-on' in argv:
        KEYBOARD_LISTENER = KeyboardListener()
        KEYBOARD_LISTENER.start()

def init_tinyos_serial():
    if TELOSB_ON:
        tinyos_serial.tos_am_init()

def init_wiimote_conn():
    if WIIMOTE_ON:
        smart_racket = SmartRacket()
        smart_racket.animation()
        return smart_racket
    return None