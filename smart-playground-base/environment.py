"""" sounds and light environment management """

#!!!from pygame import init
#!!!from pygame.mixer import pre_init, Sound
import resources
import time

#!!!pre_init(buffer=1024)
#!!!init()

#!!!racket_swing_sound = Sound(resources.RACKET_SWING_SOUND_FILENAME)
#!!!racket_hit_sound = Sound(resources.RACKET_HIT_SOUND_FILENAME)
#!!!ball_hit_sound = Sound(resources.BALL_HIT_SOUND_FILENAME)

def play_racket_swing_sound():
    pass #!!!racket_swing_sound.play()
    
def play_racket_hit_sound():
    pass #!!!racket_hit_sound.play()

def play_ball_hit_sound():
    pass #!!!ball_hit_sound.play()

if __name__ == '__main__':
    play_racket_swing_sound()
    time.sleep(3)
    play_racket_hit_sound()
    time.sleep(5)
    play_ball_hit_sound()
    time.sleep(3)