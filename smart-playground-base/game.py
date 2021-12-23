#!/usr/bin/python3

from threading import Event, RLock, Condition, Thread
import random, time
import playground, smart_objects, wiimote_configs

class ArtificialTennisPlayer:
    def __init__(self, level) -> None:
        self.level = level
    
    def manageSwing(self):
        if self.__canSwing():
            smart_objects.SmartObjectsMediator.get_current_instance().onArtificialPlayerBallSwing(self.__getSwingDir())
    
    def __canSwing(self) -> bool:
        if self.level == 'Easy' : return random.randrange(3) == 0
        elif self.level == 'Medium' : return random.randrange(5) == 0
        elif self.level == 'Hard' : return random.randrange(10) == 0
        else: return True
    
    def __getSwingDir(self):
        if random.randrange(2) == 0: return wiimote_configs.LEFT_SWING
        else: return wiimote_configs.RIGHT_SWING

class GameManager(Thread):
    def __init__(self, jsonGameSettings, playgroundBaseStatus : playground.PlaygroundBaseStatus, networkCommunicator):
        Thread.__init__(self)
        self.setDaemon(True)
        self.lock = RLock()

        self.ballLocationChangedCond = Condition(self.lock)
        self.statusReadyForPlayingCond = Condition(self.lock)
        
        self.playgroundBaseStatus = playgroundBaseStatus
        self.networkCommunicator = networkCommunicator

        self.artificialPlayer = ArtificialTennisPlayer(jsonGameSettings['artificialPlayerLevel'])
        self.mainRacketEnabled = False

        self.ballLocation = playground.PointLocation(-1.0, -1.0)
        self.ballOrientation = -1
        self.playerOrientation = -1.0

        self.matchSets = jsonGameSettings['matchSetsBestOf']  # number of total sets
        self.setGames = jsonGameSettings['setGamesBestOf']  # number of games per set

        self.terminated = False

    def onBallLocationUpdate(self, newLocation):
        with self.lock:
            self.set(newLocation)
            self.ballLocationChangedCond.notify_all()
            self.statusReadyForPlayingCond.notify_all()
    
    def onBallOrientationUpdate(self, newOrientation):
        with self.lock:
            self.ballOrientation = newOrientation
            self.statusReadyForPlayingCond.notify_all()
    
    def onPlayerOrientationUpdate(self, newOrientation):
        with self.lock:
            self.playerOrientation = newOrientation
            self.statusReadyForPlayingCond.notify_all()

    def canMainRacketSwing(self) -> bool:
        with self.lock:
            answ = self.mainRacketEnabled and not self.terminated
            self.mainRacketEnabled = False
            return answ

    def run(self):
        self.manageMatch()
    
    def manageMatch(self):
        
        currentMatchSet = currentSetGame = 1
        mainPlayerScore = artificialPlayerScore = 0

        while True:
            
            terminated = currentMatchSet > self.matchSets
            with self.lock:
                self.terminated = True

            # send current match status/scores
            matchStatus = {'dataType': 'GAME_EVENT', 'subType' : 'match_status', 'currentMatchSet' : currentMatchSet, 'currentSetGame' : currentSetGame, \
                             'main' : mainPlayerScore, 'artificial' : artificialPlayerScore, \
                             'terminated' : terminated}
            self.networkCommunicator.sendData(matchStatus)

            if terminated:
                break
            
            time.sleep(2)

            # assert ball position+orientation and player orientation
            # to be proper for playing
            statusReadyManifest = self.__isStatusReadyForPlaying()
            if not statusReadyManifest['isReady']:
                self.networkCommunicator.sendData(statusReadyManifest)
                self.__waitForStatusReadyForPlaying()
            
            while True:
                # enable main racket swing
                self.__enableMainRacketSwing()
                
                # wait for ball on opposite (artificial player) field side
                if not self.__waitForArtificialPlayerCanHit():  ############ here main player net
                    artificialPlayerScore += 1
                    break # exit because ball on main player net

                # hit the ball based on artificial player level
                self.artificialPlayer.manageSwing()

                # wait for ball on opposite (main player) field side
                if not self.__waitForMainPlayerCanHit():  ############ here artificial player net
                    mainPlayerScore += 1
                    break # exit because ball on artificial player net
        
            # go to next game/set
            currentSetGame += 1
            if currentSetGame > self.setGames:
                currentSetGame = 0
                currentMatchSet += 1
                mainPlayerScore = artificialPlayerScore = 0
    
    
    def __enableMainRacketSwing(self):
        with self.lock:
            self.mainRacketEnabled = True
    
    def __isStatusReadyForPlaying(self) -> bool:
        with self.lock:
            statusReadyManifest = {'dataType': 'GAME_EVENT', 'subType' : 'game_status_ready', 'isReady' : True, \
                'ballReady' : self.__isBallReady(), 'playerReady' : self.__isPlayerReady()}
            statusReadyManifest['isReady'] = statusReadyManifest['ballReady'] and statusReadyManifest['playerReady']
            return statusReadyManifest
    def __waitForStatusReadyForPlaying(self):
        with self.lock:
            while not self.__isBallReady() or not self.__isPlayerReady():
                self.statusReadyForPlayingCond.wait()
    def __isBallReady(self) -> bool:
        return self.__isBallLocationKnown() and self.__isBallOrientationKnown() and \
                self.ballLocation.top <= 0.5 and \
                (self.ballOrientation <= 10 or self.ballOrientation >= 350)
    def __isPlayerReady(self) -> bool:
        return self.__isPlayerOrientationKnown() and (self.playerOrientation <= 10 or self.playerOrientation >= 350)
    
    def __waitForArtificialPlayerCanHit(self):
        with self.lock:
            while True:
                if not self.__isBallLocationKnown:
                    self.ballLocationChangedCond.wait()
                elif self.ballLocation.top < 0.1:
                    return False
                elif self.ballLocation.top < 0.7:
                    self.ballLocationChangedCond.wait()
                else:
                    return True
    def __waitForMainPlayerCanHit(self) -> bool:
        with self.lock:
            while True:
                if not self.__isBallLocationKnown:
                    self.ballLocationChangedCond.wait()
                elif self.ballLocation.top > 0.9:
                    return False
                elif self.ballLocation.top > 0.5:
                    self.ballLocationChangedCond.wait()
                else:
                    return True
    
    def __isBallLocationKnown(self) -> bool:
        return self.ballLocation.left >= 0.0 and self.ballLocation.top >= 0.0
    def __isBallOrientationKnown(self) -> bool:
        return self.ballOrientation >= 0
    def __isPlayerOrientationKnown(self) -> bool:
        return self.playerOrientation >= 0.0


gameManager = None
globalLock = RLock()

# this method is called when a new game starts (JSON game settings received).
def initializeGame(jsonGameSettings, networkCommunicator):
    global gameManager
    global globalLock
    assert(playground.playgroundBaseStatus is not None and \
        smart_objects.SmartObjectsMediator.get_current_instance() is not None)
    with globalLock:
        gameManager = GameManager(jsonGameSettings, playground.playgroundBaseStatus, networkCommunicator)
        gameManager.start()


def onBallLocationUpdate(newLocation:playground.PointLocation):
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is not None: gameManager.onBallLocationUpdate(newLocation)

def onBallOrientationUpdate(newOrientation):
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is not None: gameManager.onBallOrientationUpdate(newOrientation)

def onPlayerOrientationUpdateUpdate(newOrientation):
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is not None: gameManager.onPlayerOrientationUpdate(newOrientation)


def canMainRacketSwing():
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is None : return False
        else: return gameManager.canMainRacketSwing()