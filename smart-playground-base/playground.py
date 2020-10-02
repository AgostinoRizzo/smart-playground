from threading import Thread, RLock, Event


class Point:
    
    def __init__(self, top=0, left=0):
        self.top = top
        self.left = left


class GamePlatform:
    
    """
    thread-safe game platform status class
    """
    
    def __init__(self):
        self.ballLocation = BallLocation()
        self.lock = RLock()


class BallTracker(Thread):
    
    TOP_LEFT = 0
    TOP_RIGHT = 1
    BOTTOM_LEFT = 2
    BOTTOM_RIGHT = 3
    
    def __init__(self, gamePlatform:GamePlatform):
        Thread.__init__(self)
        
        self.gamePlatform = gamePlatform
        self.corners = [None * 4]
        
        self.stopEvent = Event()
    
    def finalize(self):
        self.stopEvent.set()
        self.join()
    
    @Override
    def run(self):
        pass
    


gamePlatform = GamePlatform()
ballTracker = BallTracker(gamePlatform)

def initialize():
    ballTracker.start()

def finalize():
    ballTracker.finalize()
        