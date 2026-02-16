/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import java.security.InvalidParameterException;

/**
 * @author Agostino
 *
 */
public enum PlayerTool
{
	RACKET, CLUB;
	
	public static PlayerTool parse( final String str ) throws InvalidParameterException
	{
		if ( str == null ) throw new InvalidParameterException();
		if ( str.equals("racket") ) return PlayerTool.RACKET;
		if ( str.equals("club") ) return PlayerTool.CLUB;
		throw new InvalidParameterException();
	}
}
