/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public class PlaygroundStatus
{
	private static PlaygroundStatus instance = null;
	
	private SmartBallStatus ballStatus = null;
	
	public static PlaygroundStatus getInstance()
	{
		if ( instance == null )
			instance = new PlaygroundStatus();
		return instance;
	}
	
	private PlaygroundStatus()
	{}
	
	public void updateBallStatus( final SmartBallStatus newBallStatus )
	{
		ballStatus = new SmartBallStatus();
		ballStatus.getLocation().set(newBallStatus.getLocation());
		ballStatus.setOrientation(newBallStatus.getOrientation());
		notifyStatusChange();
	}
	
	void notifyStatusChange()
	{
		
	}
}
