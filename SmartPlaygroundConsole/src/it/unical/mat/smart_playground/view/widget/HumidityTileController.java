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
public class HumidityTileController extends EnvironmentTileController
{
	public HumidityTileController()
	{
		super(PlaygroundStatusTopic.HUMI_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.HUMI_STATUS )
			return;
		updateSensorLabel( status.getHumidity() + Strings.HUMIDITY_UNIT );
	}
}
