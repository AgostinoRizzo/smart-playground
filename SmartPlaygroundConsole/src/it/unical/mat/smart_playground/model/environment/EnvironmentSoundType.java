/**
 * 
 */
package it.unical.mat.smart_playground.model.environment;

import java.security.InvalidParameterException;

/**
 * @author Agostino
 *
 */
public enum EnvironmentSoundType
{
	RACKET_SWING,
	RACKET_HIT,
	CLUB_ATTEMPT,
	CLUB_SWING,
	CLUB_SWING_LIGHT,
	BALL_BOUNCE,
	BALL_IN_HOLE,
	TENNIS_FINISH,
	GAME_READY,
	GAME_WIN,
	GAME_LOSE,
	
	CONFIRM,
	CHOOSE,
	SUB_CHOOSE,
	CLAP,
	WIND,
	STEP;
	
	public static EnvironmentSoundType parse( final String str ) throws InvalidParameterException
	{
		if ( str == null ) throw new InvalidParameterException();
		if ( str.equals("racket_swing") )     return EnvironmentSoundType.RACKET_SWING;
		if ( str.equals("racket_hit") )       return EnvironmentSoundType.RACKET_HIT;
		if ( str.equals("club_attempt") )     return EnvironmentSoundType.CLUB_ATTEMPT;
		if ( str.equals("club_swing") )       return EnvironmentSoundType.CLUB_SWING;
		if ( str.equals("club_swing_light") ) return EnvironmentSoundType.CLUB_SWING_LIGHT;
		if ( str.equals("ball_bounce") )      return EnvironmentSoundType.BALL_BOUNCE;
		if ( str.equals("ball_in_hole") )     return EnvironmentSoundType.BALL_IN_HOLE;
		if ( str.equals("tennis_finish") )    return EnvironmentSoundType.TENNIS_FINISH;
		if ( str.equals("game_ready") )       return EnvironmentSoundType.GAME_READY;
		if ( str.equals("game_wind") )        return EnvironmentSoundType.GAME_WIN;
		if ( str.equals("game_lose") )        return EnvironmentSoundType.GAME_LOSE;
		
		if ( str.equals("confirm") )          return EnvironmentSoundType.CONFIRM;
		if ( str.equals("choose") )           return EnvironmentSoundType.CHOOSE;
		if ( str.equals("sub_choose") )       return EnvironmentSoundType.SUB_CHOOSE;
		if ( str.equals("clap") )             return EnvironmentSoundType.CLAP;
		if ( str.equals("wind") )             return EnvironmentSoundType.WIND;
		if ( str.equals("step") )             return EnvironmentSoundType.STEP;
		throw new InvalidParameterException();
	}
}
