/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.view.widget.LightsFansCtrlTileController;

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
		
		final double fansThr = LightsFansCtrlTileController.getFansThr();
		if ( fansThr >= 0 )
		{
			for ( final Integer newTemp : newValues )
				if ( newTemp >= fansThr )
				{
					LightsFansCtrlTileController.getInstance().onAllFansTurnOn();
					return;
				}
			LightsFansCtrlTileController.getInstance().onAllFansTurnOff();
		}
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
		
		final double lightsThr = LightsFansCtrlTileController.getLightsThr();
		if ( lightsThr >= 0 )
		{
			for ( final Integer newBright : newValues )
				if ( newBright <= lightsThr )
				{
					LightsFansCtrlTileController.getInstance().onAllLightsTurnOn();
					return;
				}
			LightsFansCtrlTileController.getInstance().onAllLightsTurnOff();
		}
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
