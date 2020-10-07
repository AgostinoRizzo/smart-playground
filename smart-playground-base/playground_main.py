#!/usr/bin/env python3

import sys

from smart_objects import SmartBall
from smart_objects import SmartRacket
from smart_objects import SmartObjectsMediator
import network
import logging


EXIT_CMDS = ['quit', 'exit']
logging.basicConfig(level=logging.DEBUG)

def main():
    
    if '-h' in sys.argv:
        print("Usage:", sys.argv[0], "serial@/dev/ttyUSB0:115200")
        sys.exit() 
    
    """ 
    smart ball - serial communication setup with tinyos base station
    """
    smart_ball = SmartBall()
    
    """
    smart racket - bluetooth connection setup with nintendo wiimote
    """
    smart_racket = SmartRacket()
    smart_racket.animation()

    """
    smart objects mediator
    """
    objs_mediator = SmartObjectsMediator.get_instance(smart_ball, smart_racket)
    
    """
    net discovery server
    """
    network.DiscoveryServer.get_instance().start()
    
    """
    ecosystem event provider
    """
    network.EcosystemEventProvider.get_instance()
    
    objs_mediator.run()
    objs_mediator.finalize()


if __name__ == '__main__':
    main()
