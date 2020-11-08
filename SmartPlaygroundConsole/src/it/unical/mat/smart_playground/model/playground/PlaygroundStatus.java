/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private Map<PlaygroundStatusTopic, List<PlaygroundStatusObserver>> observersMap = new HashMap<>();
	
	public static PlaygroundStatus getInstance()
	{
		if ( instance == null )
			instance = new PlaygroundStatus();
		return instance;
	}
	
	private PlaygroundStatus()
	{}
	
	public void addObserver( final PlaygroundStatusObserver toAdd )
	{ addObserver(toAdd, PlaygroundStatusTopic.ALL); }
	
	public void removeObserver( final PlaygroundStatusObserver toRemove )
	{ removeObserver(toRemove, PlaygroundStatusTopic.ALL); }
	
	public void addObserver( final PlaygroundStatusObserver toAdd, final PlaygroundStatusTopic topic )
	{
		if ( !observersMap.containsKey(topic) )
			observersMap.put(topic, new LinkedList<>());
		observersMap.get(topic).add(toAdd);
	}
	
	public void removeObserver( final PlaygroundStatusObserver toRemove, final PlaygroundStatusTopic topic )
	{
		observersMap.get(topic).remove(toRemove);
	}
	
	public void updateBallStatus( final SmartBallStatus newBallStatus )
	{
		ballStatus.getLocation().set(newBallStatus.getLocation());
		ballStatus.setOrientation(newBallStatus.getOrientation());
		notifyStatusChange(PlaygroundStatusTopic.BALL_STATUS);
	}
	
	public void updateWindStatus( final WindStatus newWindStatus )
	{
		windStatus.set(newWindStatus);
		notifyStatusChange(PlaygroundStatusTopic.WIND_STATUS);
	}
	
	public SmartBallStatus getBallStatus()
	{
		return ballStatus;
	}
	
	public WindStatus getWindStatus()
	{
		return windStatus;
	}
	
	private void notifyStatusChange( final PlaygroundStatusTopic topic )
	{
		PlaygroundStatusTopic key;
		for ( final Entry<PlaygroundStatusTopic, List<PlaygroundStatusObserver>> entry 
				: observersMap.entrySet() )
		{
			key = entry.getKey();
			if ( key == PlaygroundStatusTopic.ALL || key == topic )
				for ( final PlaygroundStatusObserver o : entry.getValue() )
					o.onPlaygroundStatusChanged(this, topic);
		}
	}
}
