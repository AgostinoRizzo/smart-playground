"""" 
Environment sounds management 
"""
from network import EcosystemEventProvider
import services

RACKET_SWING_SOUND = 'racket_swing'
RACKET_HIT_SOUND = 'racket_hit'
CLUB_ATTEMPT_SOUND = 'club_attempt'
CLUB_SWING_SOUND = 'club_swing'
CLUB_SWING_LIGHT_SOUND = 'club_swing_light'
BALL_BOUNCE_SOUND = 'ball_bounce'
BALL_IN_HOLE_SOUND = 'ball_in_hole'

__netcomm = EcosystemEventProvider.get_instance().netcomm

def play_sound(sound):
    __netcomm.sendData( {'dataType': services.ENVSOUND_CODE, 'sound': sound} )
