/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class MotionControllerStatus
{
	private int player_direction = 0;
	
	public void updatePlayerDirection( final int direction )
	{
		player_direction=direction;
	}
	public int getPlayerDirection()
	{
		return player_direction;
	}
}
