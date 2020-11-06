/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Agostino
 *
 */
public class TelosbBasedStatus
{
	public static final int CAPACITY = 10;
	
	private final List< Integer > temperatureValues = new ArrayList<>();
	private final List< Integer > humidityValues    = new ArrayList<>();
	private final List< Integer > brightnessValues  = new ArrayList<>();
	
	public void updateNewTemperatureValues( final List< Integer > newValues )
	{
		temperatureValues.addAll(newValues);
		EcosystemStatus.shrinkValuesList(temperatureValues, CAPACITY);
	}
	
	public void updateNewHumidityValues( final List< Integer > newValues )
	{
		humidityValues.addAll(newValues);
		EcosystemStatus.shrinkValuesList(humidityValues, CAPACITY);
	}
	
	public void updateNewBrightnessValues( final List< Integer > newValues )
	{
		brightnessValues.addAll(newValues);
		EcosystemStatus.shrinkValuesList(brightnessValues, CAPACITY);
	}
	
	public List<Integer> getTemperatureValues()
	{
		return temperatureValues;
	}
	
	public List<Integer> getHumidityValues()
	{
		return humidityValues;
	}
	
	public List<Integer> getBrightnessValues()
	{
		return brightnessValues;
	}
}
