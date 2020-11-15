/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class WindDirectionTileController implements LayoutController, PlaygroundStatusObserver
{
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	
	@FXML private ImageView windOrientationImage;
	
	@Override
	public void onInitialize(Window win)
	{
		PLAYGROUND_STATUS.addObserver(this, PlaygroundStatusTopic.WIND_STATUS);
		setWindImageViewOrientation(PLAYGROUND_STATUS.getWindStatus());
	}

	@Override
	public void onFinalize()
	{
		PLAYGROUND_STATUS.removeObserver(this, PlaygroundStatusTopic.WIND_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.WIND_STATUS )
			return;
		
		setWindImageViewOrientation(status.getWindStatus());
	}
	
	private void setWindImageViewOrientation( final WindStatus status )
	{
		windOrientationImage.setRotate(status.getDirection());
	}
}
