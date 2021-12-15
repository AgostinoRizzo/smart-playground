/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.view.Strings;

/**
 * @author Agostino
 *
 */
public class TemperatureTileController extends EnvironmentTileController 
{

	public TemperatureTileController()
	{
		super(PlaygroundStatusTopic.TEMP_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.TEMP_STATUS )
			return;
		
		updateSensorLabel( status.getTemperature() + Strings.TEMPERATURE_UNIT );
	}
	
	
}
