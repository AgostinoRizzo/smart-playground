#!/usr/bin/python3

from threading import Event, RLock, Condition, Thread
import random, time, ctypes
import playground, smart_objects, wiimote_configs


class ArtificialTennisPlayer:
    def __init__(self, level) -> None:
        self.level = level
    
    def manageSwing(self):
        if self.__canSwing():
            smart_objects.SmartObjectsMediator.get_current_instance().onArtificialPlayerBallSwing(self.__getSwingDir())
            print("ARTIFICIAL PLAYER SWING.")
    
    def __canSwing(self) -> bool:
        if self.level == 'Easy' : return random.randrange(3) == 0
        elif self.level == 'Medium' : return random.randrange(5) == 0
        elif self.level == 'Hard' : return random.randrange(10) == 0
        else: return True
    
    def __getSwingDir(self):
        if random.randrange(2) == 0: return wiimote_configs.LEFT_SWING
        else: return wiimote_configs.RIGHT_SWING


class GameManager(Thread):

    def __init__(self, playgroundBaseStatus : playground.PlaygroundBaseStatus, networkCommunicator):
        Thread.__init__(self)
        self.setDaemon(True)
        
        self.lock = RLock()
        self.environmentChangedCond = Condition(self.lock)
        self.resetEvent = Event()

        self.playgroundBaseStatus = playgroundBaseStatus
        self.networkCommunicator = networkCommunicator

        self.ballLocation = playground.PointLocation(-1.0, -1.0)
        self.ballOrientation = -1
        self.playerOrientation = -1.0

        self.terminated = False
    
    def onBallLocationUpdate(self, newLocation):
        with self.lock:
            self.ballLocation.set(newLocation)
            self.environmentChangedCond.notify_all()
    
    def onBallOrientationUpdate(self, newOrientation):
        with self.lock:
            self.ballOrientation = newOrientation
            self.environmentChangedCond.notify_all()
    
    def onPlayerOrientationUpdate(self, newOrientation):
        with self.lock:
            self.playerOrientation = newOrientation
            self.environmentChangedCond.notify_all()
    
    def run(self):
        self.manageMatch()
    
    def stopRunning(self):
        self.resetEvent.set()
        with self.lock:
            self.environmentChangedCond.notify_all()
    
    def manageMatch(self):
        pass
    
    # the following methods are NOT thread-safe hence
    # they have to be called acquiring the lock first!
    def _isBallLocationKnown(self) -> bool:
        return self.ballLocation.left >= 0.0 and self.ballLocation.top >= 0.0
    def _isBallOrientationKnown(self) -> bool:
        return self.ballOrientation >= 0
    def _isPlayerOrientationKnown(self) -> bool:
        return self.playerOrientation >= 0.0


class TennisGameManager(GameManager):
    PLAYER_A = 0
    PLAYER_B = 1

    def __init__(self, jsonGameSettings, playgroundBaseStatus : playground.PlaygroundBaseStatus, networkCommunicator):
        super().__init__(playgroundBaseStatus, networkCommunicator)

        self.artificialPlayer = ArtificialTennisPlayer(jsonGameSettings['artificialPlayerLevel'])
        self.mainRacketEnabled = False

        self.matchSets = jsonGameSettings['matchSetsBestOf']  # number of total sets
        self.setGames = jsonGameSettings['setGamesBestOf']  # number of games per set

    def canMainRacketSwing(self) -> bool:
        with self.lock:
            answ = self.mainRacketEnabled and not self.terminated
            self.mainRacketEnabled = False
            return answ
    
    def manageMatch(self):
        
        currentMatchSet = currentSetGame = 1
        mainPlayerScores = [0] * self.matchSets
        artificialPlayerScores = [0] * self.matchSets
        
        isServing = True

        while not self.resetEvent.is_set():
            
            terminated = currentMatchSet > self.matchSets
            if terminated:
                with self.lock:
                    self.terminated = True
                currentMatchSet = self.matchSets
                currentSetGame = self.setGames

            # send current match status/scores
            matchStatus = {'dataType': 'GAME_EVENT', 'subType' : 'match_status', 'currentMatchSet' : currentMatchSet, 'currentSetGame' : currentSetGame, \
                            'totalMatchSets' : self.matchSets, 'totalSetGames': self.setGames, \
                            'mainPlayerScores' : mainPlayerScores, 'artificialPlayerScores' : artificialPlayerScores, \
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
            
            while not self.resetEvent.is_set():

                # enable main racket swing
                self.__enableMainRacketSwing()
                self.networkCommunicator.sendData(self.__getMatchTurnInfo(TennisGameManager.PLAYER_A))

                # wait for ball on opposite (artificial player) field side
                if not self.__waitForArtificialPlayerCanHit(isServing):  ############ here main player net
                    artificialPlayerScores[currentMatchSet - 1] += 1
                    break # exit because ball on main player net
                isServing = False

                # hit the ball based on artificial player level
                self.networkCommunicator.sendData(self.__getMatchTurnInfo(TennisGameManager.PLAYER_B))
                self.artificialPlayer.manageSwing()

                # wait for ball on opposite (main player) field side
                if not self.__waitForMainPlayerCanHit():  ############ here artificial player net
                    mainPlayerScores[currentMatchSet - 1] += 1
                    break # exit because ball on artificial player net
            
            smart_objects.SmartObjectsMediator.get_current_instance().onSmartBallStop()

            # go to next game/set
            currentSetGame += 1
            isServing = True
            if currentSetGame > self.setGames:
                currentSetGame = 1
                currentMatchSet += 1

        smart_objects.SmartObjectsMediator.get_current_instance().onSmartBallStop()

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
            while not self.resetEvent.is_set() and (not self.__isBallReady() or not self.__isPlayerReady()):
                self.environmentChangedCond.wait()
    def __isBallReady(self) -> bool:
        return self._isBallLocationKnown() and self._isBallOrientationKnown() and \
                self.ballLocation.left >= 0.5 and self.ballLocation.left < 0.9 and \
                self.ballOrientation <= 270+10 and self.ballOrientation >= 270-10
    def __isPlayerReady(self) -> bool:
        return self._isPlayerOrientationKnown() and self.playerOrientation <= 10 or self.playerOrientation >= 350
    
    def __waitForArtificialPlayerCanHit(self, isServing) -> bool:
        with self.lock:
            while not self.resetEvent.is_set():
                if not self.__isBallLocationKnown():
                    self.environmentChangedCond.wait()
                elif not isServing and self.ballLocation.left > 0.9:  # main (human) player's net
                    return False
                elif self.ballLocation.left > 0.3:
                    self.environmentChangedCond.wait()
                else:
                    return True
            return False
    def __waitForMainPlayerCanHit(self) -> bool:
        with self.lock:
            while not self.resetEvent.is_set():
                if not self.__isBallLocationKnown():
                    self.environmentChangedCond.wait()
                elif self.ballLocation.left < 0.1:  # artificial player's net
                    return False
                elif self.ballLocation.left < 0.5:
                    self.environmentChangedCond.wait()
                else:
                    return True
            return False
    
    def __getMatchTurnInfo(self, turn):
        info = {'dataType': 'GAME_EVENT', 'subType' : 'match_turn', 'turn' : ''}
        if turn == GameManager.PLAYER_A: info['turn'] = 'player_a'
        elif turn == GameManager.PLAYER_B: info['turn'] = 'player_b'
        return info


class GolfGameManager(GameManager):
    FIRST_STROKE_BALL_PADDING = 0.2
    LOCATION_COORDS_COINCIDENCE_DELTA = 0.2

    def __init__(self, jsonGameSettings, playgroundBaseStatus : playground.PlaygroundBaseStatus, networkCommunicator):
        super().__init__(playgroundBaseStatus, networkCommunicator)

        self.clubStrokeDoneCond = Condition(self.lock)
        self.clubEnabled = False
        self.clubStrokeType = None
        self.ballCollided = False

        # location of the target hole
        self.holeLocation = playground.PointLocation(jsonGameSettings['hole']['left'], jsonGameSettings['hole']['left'])

    def canClubSwing(self, swingType) -> bool:
        with self.lock:
            answ = self.clubEnabled and not self.terminated
            self.clubEnabled = False
            self.clubStrokeType = swingType
            self.clubStrokeDoneCond.notify_all()
            return answ
    
    def onBallCollision(self):
        with self.lock:
            self.ballCollided = True
            self.environmentChangedCond.notify_all()
    
    def manageMatch(self):
        
        terminated = False
        gameOutcome = ''
        currentStroke = 1

        while not self.resetEvent.is_set():
            
            # send current match status/scores
            matchStatus = {'dataType': 'GAME_EVENT', 'subType' : 'match_status', 'currentStroke' : currentStroke, \
                           'terminated' : terminated, 'gameOutcome': gameOutcome}
            self.networkCommunicator.sendData(matchStatus)

            if terminated:
                break
            
            time.sleep(2)

            # (for the forse stroke) assert ball position to be proper for playing
            if currentStroke == 1:
                isReadyManifest = self.__isBallReadyForFirstStroke()
                while not isReadyManifest['isReady']:
                    self.networkCommunicator.sendData(isReadyManifest)
                    self.environmentChangedCond.wait()
                    isReadyManifest = self.__isBallReadyForFirstStroke()

            self.__enableClubSwing()
            self.networkCommunicator.sendData(self.__getMatchTurnInfo())

            # player can play (club swing/stroke): wait for a club stroke
            while self.canClubSwing: self.clubStrokeDoneCond.wait()

            # ball is moving: wait for a ball stop timeout or ball collition or ball in hole
            maxBallMovingTime = self.__getMaxBallMovingTime()
            ballStopTime = time.time() + maxBallMovingTime
            while not self.ballCollided and not self.__isBallInHole():
                currentTime = time.time()
                if currentTime >= ballStopTime: break
                self.environmentChangedCond.wait(timeout=ballStopTime - currentTime)
            
            # stop the ball anyway
            smart_objects.SmartObjectsMediator.get_current_instance().onSmartBallStop()

            # ball is collided, game stops
            if self.ballCollided:
                terminated = True
                gameOutcome = 'collision'
            # ball in hole, game stops
            elif self.__isBallInHole():
                terminated = True
                gameOutcome = 'in_hole'
            # ball stopped, game continues
            else: currentStroke += 1
    
    def __enableClubSwing(self):
        with self.lock:
            self.clubEnabled = True
    
    def __isBallReadyForFirstStroke(self):
        isReady = self._isBallLocationKnown() and \
                  self.ballLocation.left > 0.5 + GolfGameManager.FIRST_STROKE_BALL_PADDING and \
                  self.ballLocation.left < 1.0 - GolfGameManager.FIRST_STROKE_BALL_PADDING and \
                  self.ballLocation.top  >       GolfGameManager.FIRST_STROKE_BALL_PADDING and \
                  self.ballLocation.left < 1.0 - GolfGameManager.FIRST_STROKE_BALL_PADDING
        return {'dataType': 'GAME_EVENT', 'subType' : 'ball_location_ready', 'isReady' : isReady}
    
    def __getMatchTurnInfo(self):
        return {'dataType': 'GAME_EVENT', 'subType' : 'match_turn', 'turn' : 'club_swing'}
    
    def __getMaxBallMovingTime(self):
        if self.clubStrokeType == smart_objects.GolfClubSwingDetector.LIGHT_SWING_TYPE: return 0.5
        if self.clubStrokeType == smart_objects.GolfClubSwingDetector.MEDIUM_SWING_TYPE: return 1.0
        return 2.0
    
    def __isBallInHole(self) -> bool:
        return self._isBallLocationKnown() and \
               abs(self.ballLocation.left - self.holeLocation.left) <= GolfGameManager.LOCATION_COORDS_COINCIDENCE_DELTA and \
               abs(self.ballLocation.top  - self.holeLocation.top)  <= GolfGameManager.LOCATION_COORDS_COINCIDENCE_DELTA 



TENNIS_GAME_TYPE = 0
GOLF_GAME_TYPE = 1

gameManager = None
globalLock = RLock()

"""
Methods for games (tennis, golf) management access
"""

# this method is called when a new game (tennis or golf) starts (JSON game settings received).
def initializeGame(gameType, jsonGameSettings, networkCommunicator):
    global gameManager
    global globalLock

    if gameType != TENNIS_GAME_TYPE and gameType != GOLF_GAME_TYPE:
        return
    
    assert(playground.playgroundBaseStatus is not None and \
        smart_objects.SmartObjectsMediator.get_current_instance() is not None)
    
    with globalLock:
        if gameManager is None:

            if gameType == TENNIS_GAME_TYPE:
                gameManager = TennisGameManager(jsonGameSettings, playground.playgroundBaseStatus, networkCommunicator)
            else:
                gameManager = GolfGameManager(jsonGameSettings, playground.playgroundBaseStatus, networkCommunicator)
            
            gameManager.start()
            networkCommunicator.sendData({'dataType': 'GAME_EVENT', 'subType' : 'game_init_approved'})
        else:
            networkCommunicator.sendData({'dataType': 'GAME_EVENT', 'subType' : 'game_init_denied'})

# this method is called when the game is reset.
def resetGame():
    global gameManager
    global globalLock
    assert(playground.playgroundBaseStatus is not None and \
        smart_objects.SmartObjectsMediator.get_current_instance() is not None)
    with globalLock:
        if gameManager is not None:
            gameManager.stopRunning()
            gameManager.join()
            gameManager = None
            print("Game is reset.")
        smart_objects.SmartObjectsMediator.get_current_instance().onGameReset()
        


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
        if gameManager is None or type(gameManager) != TennisGameManager: return False
        else: return gameManager.canMainRacketSwing()

def canClubSwing(swingType):
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is None or type(gameManager) != GolfGameManager: return False
        else: return gameManager.canClubSwing(swingType)

def onGolfBallCollision():
    global gameManager
    global globalLock
    with globalLock:
        if gameManager is not None and type(gameManager) == GolfGameManager:
            gameManager.onBallCollision()