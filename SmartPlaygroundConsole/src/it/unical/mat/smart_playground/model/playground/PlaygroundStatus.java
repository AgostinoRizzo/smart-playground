/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public class PlaygroundStatus
{
	private static PlaygroundStatus instance = null;
	
	private final SmartBallStatus ballStatus = new SmartBallStatus();
	private final PlayerStatus playerStatus = new PlayerStatus();
	private final WindStatus windStatus = new WindStatus();
	private Integer temperature, humidity, brightness;
	
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
	
	public void updatePlayerStatus( final PlayerStatus newPlayerStatus )
	{
		playerStatus.set(newPlayerStatus);
		notifyStatusChange(PlaygroundStatusTopic.PLAYER_STATUS);
	}
	
	public void updateWindStatus( final WindStatus newWindStatus )
	{
		windStatus.set(newWindStatus);
		notifyStatusChange(PlaygroundStatusTopic.WIND_STATUS);
	}
	
	public void updateTemperatureStatus( final Integer currentTemp )
	{
		if ( currentTemp == null )
			return;
		temperature = currentTemp;
		notifyStatusChange(PlaygroundStatusTopic.TEMP_STATUS);
	}
	public void updateHumidityStatus( final Integer currentHumi )
	{
		if ( currentHumi == null )
			return;
		humidity = currentHumi;
		notifyStatusChange(PlaygroundStatusTopic.HUMI_STATUS);
	}
	public void updateBrightnessStatus( final Integer currentBright )
	{
		if ( currentBright == null )
			return;
		brightness = currentBright;
		notifyStatusChange(PlaygroundStatusTopic.BRIGHTNESS_STATUS);
	}
	
	public SmartBallStatus getBallStatus()
	{
		return ballStatus;
	}
	
	public PlayerStatus getPlayerStatus()
	{
		return playerStatus;
	}
	
	public WindStatus getWindStatus()
	{
		return windStatus;
	}
	
	public Integer getTemperature()
	{
		return temperature;
	}
	public Integer getHumidity()
	{
		return humidity;
	}
	public Integer getBrightness()
	{
		return brightness;
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
