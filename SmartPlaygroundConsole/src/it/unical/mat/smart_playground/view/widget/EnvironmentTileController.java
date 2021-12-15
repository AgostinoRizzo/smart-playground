/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author Agostino
 *
 */
public abstract class EnvironmentTileController implements LayoutController, PlaygroundStatusObserver
{
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	
	private final PlaygroundStatusTopic topic;
	@FXML protected Label sensorLabel;
	
	public EnvironmentTileController( final PlaygroundStatusTopic playgroundTopic )
	{
		topic = playgroundTopic;
	}
	
	@Override
	public void onInitialize(Window win)
	{
		PLAYGROUND_STATUS.addObserver(this, topic);
	}

	@Override
	public void onFinalize()
	{
		PLAYGROUND_STATUS.removeObserver(this, topic);
	}
	
	protected void updateSensorLabel( final String labelText )
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{ sensorLabel.setText(labelText); }
		});
	}
}
