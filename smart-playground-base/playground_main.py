#!/usr/bin/python3

import sys

import runman
from smart_objects import SmartBall
from smart_objects import SmartRacket
from smart_objects import SmartObjectsMediator
import network
import logging
import util_logging
import playground


EXIT_CMDS = ['quit', 'exit']
logging.basicConfig(level=logging.DEBUG)

def wait_for_exit_command():
    cmd = input()
    while not cmd.strip() in EXIT_CMDS:
        cmd = input()

def main():
    
    if '-h' in sys.argv:
        print("Usage:", sys.argv[0], "serial@/dev/ttyUSB0:115200")
        sys.exit() 
    
    """
    initialization
    """
    util_logging.Logger.default().info('Initialization...')
    runman.init(sys.argv)
    
    """ 
    smart ball - serial communication setup with tinyos base station
    """
    runman.init_tinyos_serial()
    smart_ball = SmartBall()
    
    """
    smart racket - bluetooth connection setup with nintendo wiimote
    """
    smart_racket = runman.init_wiimote_conn()
    
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
    
    """
    playground status
    """
    playground.initialize()
    
    """
    core running statement
    """
    util_logging.Logger.default().info('Core running...')
    try:
        objs_mediator.run()
    except KeyboardInterrupt:
        pass
    
    """
    finalization
    """
    util_logging.Logger.default().info('Finalization...')
    objs_mediator.finalize()
    playground.finalize()
    

if __name__ == '__main__':
    main()
