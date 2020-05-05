/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.model.ecosystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Agostino
 *
 */
public class TelosbBasedStatus
{
	private final List< Integer > temperatureValues = new ArrayList<>();
	private final List< Integer > humidityValues    = new ArrayList<>();
	private final List< Integer > brightnessValues  = new ArrayList<>();
	
	public void updateNewTemperatureValues( final List< Integer > newValues )
	{
		temperatureValues.addAll(newValues);
	}
	
	public void updateNewHumidityValues( final List< Integer > newValues )
	{
		humidityValues.addAll(newValues);
	}
	
	public void updateNewBrightnessValues( final List< Integer > newValues )
	{
		brightnessValues.addAll(newValues);
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
