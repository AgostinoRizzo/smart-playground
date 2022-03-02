#!/usr/bin/python3

""" 
to use this module ensure:
$ apt-get install python3-rpi.gpio
$ pip3 install gpiozero

to enable pigpio pin factory:
sudo pigpiod
"""
from threading import Condition, RLock, Thread
from time import sleep
from gpiozero import Servo
from gpiozero.pins.pigpio import PiGPIOFactory

""" shortest/longest pulse sent to the servo (expressed in seconds) """
MIN_PULSE_WIDTH = 0.000544   #  544 us
MAX_PULSE_WIDTH = 0.0024     # 2400 us

ARTIFICIAL_RACKET_PIN = 24

""" artificalRacketServo.value move the servo from -1 to 1 where 0 is the mid-point """
MAX_FOLLOW_VALUE = 0.7  # a sign (+ or -) must be added for direction

""" artificalRacketServo direction sign (+ for left, - for right) """
RIGHT_DIRECTION_SIGN = -1
LEFT_DIRECTION_SIGN = 1

artificalRacketServo = Servo(ARTIFICIAL_RACKET_PIN, min_pulse_width=MIN_PULSE_WIDTH, max_pulse_width=MAX_PULSE_WIDTH, pin_factory=PiGPIOFactory())
artificalRacketServo.mid()
globalLock = RLock()
gameStarted = False
oppositeRacketSwingDone = False
currentBallSide = None
commandExecutor = None


def racketRelax():
    global commandExecutor
    commandExecutor.updateCommand(RacketRelaxCommand())

def racketFollow(ballProximity, ballSide):
    global commandExecutor
    commandExecutor.updateCommand(RacketFollowCommand(ballProximity, ballSide))

def racketSwing(ballSide=None):
    global commandExecutor
    commandExecutor.updateCommand(RacketSwingCommand(ballSide))

def onOppositeRacketSwing():
    global gameStarted
    global oppositeRacketSwingDone
    with globalLock:
        gameStarted = oppositeRacketSwingDone = True

def onGameInit():
    global gameStarted
    global oppositeRacketSwingDone
    with globalLock:
        gameStarted = oppositeRacketSwingDone = False


def getValueWithDirection(value, ballSide):
    """
    0 <= ballSide <= 1 (>= 0.5 for right, < 0.5 for left)
    """
    if ballSide >= 0.5: return value * RIGHT_DIRECTION_SIGN  # right direction
    return value * LEFT_DIRECTION_SIGN                       # left direction

def updateServoValue(servo:Servo, newValue):
    """ 
    this method is NOT thread-safe hence
    you must call it acquiring the proper lock
    """
    # TODO: check value diff
    #if servo.value is not None:
    #    diff = abs(servo.value - newValue)
    #    if diff < 0.1:
    #        return
    servo.value = newValue
    #sleep(0.1)
    #servo.value = None

class RacketCommand:
    def execute(self):
        pass

class RacketRelaxCommand(RacketCommand):
    def execute(self):
        global globalLock
        global artificalRacketServo
        global currentBallSide
        with globalLock:
            updateServoValue(artificalRacketServo, 0)
            currentBallSide = None

class RacketFollowCommand(RacketCommand):
    def __init__(self, ballProximity, ballSide) -> None:
        self.ballProximity = ballProximity
        self.ballSide = ballSide
    
    def execute(self):
        """
        currentBallProx < ballProximity ignone the ball (it is going far away)
        0.0 <= ballSide      <= 1.0 (>= 0.5 for right, < 0.5 for left)
        0.0 <= ballProximity <= 1.0 (lower value closer ball)
        0.5 <= ballProximity <= 1.0 follow the ball (lower value closer ball)
        0.0 <= ballProximity <  0.5 follow the ball w.r.t. lower value and closer ball
        """
        global globalLock
        global artificalRacketServo
        global gameStarted
        global oppositeRacketSwingDone
        global currentBallSide

        followValue = 0

        with globalLock:

            if gameStarted:
                if oppositeRacketSwingDone:
                    if self.ballProximity < 0.5: followValue = MAX_FOLLOW_VALUE  # 0.0 <= ballProximity <  0.5
                    else: return  # ignore the ball                              # 0.5 <= ballProximity <= 1.0
                else: return # ignore the ball
            else:
                if oppositeRacketSwingDone: return # ignore the ball                 
                else: followValue = MAX_FOLLOW_VALUE * (1.0 - self.ballProximity)

            followValue = getValueWithDirection(followValue, self.ballSide)
            updateServoValue(artificalRacketServo, followValue)

class RacketSwingCommand(RacketCommand):
    def __init__(self, ballSide) -> None:
        self.ballSide = ballSide
    
    def execute(self):
        """
        0 <= ballSide <= 1 (>= 0.5 for right, < 0.5 for left)
        """
        global globalLock
        global artificalRacketServo
        global currentBallSide
        global gameStarted
        global oppositeRacketSwingDone

        with globalLock:
            if self.ballSide is not None:
                currentBallSide = self.ballSide
            elif currentBallSide is None:
                currentBallSide = 0.8

            #swingStartValue = getValueWithDirection(1, currentBallSide)

            gameStarted = True
            oppositeRacketSwingDone = False

            #updateServoValue(artificalRacketServo, swingStartValue)
            #sleep(1.0)
            updateServoValue(artificalRacketServo, 0)
            sleep(1.0)
            

class CommandExecutor(Thread):

    def __init__(self) -> None:
        Thread.__init__(self)
        self.setDaemon(True)
        self.lock = RLock()
        self.commandAvailableCond = Condition(self.lock)
        self.currentCommand = None
    
    def updateCommand(self, command:RacketCommand):
        with self.lock:
            self.currentCommand = command
            self.commandAvailableCond.notify_all()
    
    def run(self) -> None:
        while True:
            
            self.lock.acquire()
            while self.currentCommand is None:
                self.commandAvailableCond.wait()
            commandToExecute = self.currentCommand
            self.currentCommand = None
            self.lock.release()

            commandToExecute.execute()
                
commandExecutor = CommandExecutor()
commandExecutor.start()


def basicTest():
    """ artificalRacketServo accessed with NO thread-safety """
    print('')
    print('Mid'); sleep(5)
    artificalRacketServo.min(); print('Min'); sleep(5)
    artificalRacketServo.mid(); print('Mid'); sleep(5)
    artificalRacketServo.max(); print('Max'); sleep(5)
    artificalRacketServo.mid(); print('Mid'); sleep(2)

def tennisPlayingTest():
    ATTEMPTS = 4
    BALL_SIDES = [0.8, 0.2]
    
    for a in range(0, ATTEMPTS):
        
        print("\nAttempt " + str(a+1))
        racketRelax()
        print('Racket is relaxing...')
        sleep(2)

        ballSide = BALL_SIDES[a%2]
        ballProximity = 1.0

        while ballProximity > 0.1:
            print('Following at ' + str(ballProximity) + ' heading ' + str(ballSide))
            racketFollow(ballProximity, ballSide)
            sleep(0.1)
            ballProximity -= 0.01
        
        print('Swing')
        racketSwing(ballSide)
        sleep(0.5)

if __name__ == '__main__':
    #basicTest()
    tennisPlayingTest()
