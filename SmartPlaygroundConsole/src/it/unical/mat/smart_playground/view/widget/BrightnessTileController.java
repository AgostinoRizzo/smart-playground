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
public class BrightnessTileController extends EnvironmentTileController
{
	public BrightnessTileController()
	{
		super(PlaygroundStatusTopic.BRIGHTNESS_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.BRIGHTNESS_STATUS )
			return;
		updateSensorLabel( status.getBrightness() + Strings.BRIGHTNESS_UNIT );
	}
}
