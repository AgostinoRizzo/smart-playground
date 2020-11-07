/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public class PlaygroundStatus
{
	private static PlaygroundStatus instance = null;
	
	private final SmartBallStatus ballStatus = new SmartBallStatus();
	private final WindStatus windStatus = new WindStatus();
	
	private List<PlaygroundStatusObserver> observers = new ArrayList<>();
	
	public static PlaygroundStatus getInstance()
	{
		if ( instance == null )
			instance = new PlaygroundStatus();
		return instance;
	}
	
	private PlaygroundStatus()
	{}
	
	public void addObserver( final PlaygroundStatusObserver toAdd )
	{
		observers.add(toAdd);
	}
	
	public void removeObserver( final PlaygroundStatusObserver toRemove )
	{
		observers.remove(toRemove);
	}
	
	public void updateBallStatus( final SmartBallStatus newBallStatus )
	{
		ballStatus.getLocation().set(newBallStatus.getLocation());
		ballStatus.setOrientation(newBallStatus.getOrientation());
		notifyStatusChange();
	}
	
	public void updateWindStatus( final WindStatus newWindStatus )
	{
		windStatus.set(newWindStatus);
		notifyStatusChange();
	}
	
	public SmartBallStatus getBallStatus()
	{
		return ballStatus;
	}
	
	public WindStatus getWindStatus()
	{
		return windStatus;
	}
	
	private void notifyStatusChange()
	{
		for ( final PlaygroundStatusObserver o : observers )
			o.onPlaygroundStatusChanged(this);
	}
}
