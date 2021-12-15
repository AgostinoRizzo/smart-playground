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
	private double player_orientation = 0;
	
	public void updatePlayerOrientation( final double orientation )
	{
		player_orientation=orientation;
	}
	public double getPlayerOrientation()
	{
		return player_orientation;
	}
}
