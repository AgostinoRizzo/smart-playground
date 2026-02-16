/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class BallOrientationTileController implements LayoutController, PlaygroundStatusObserver
{
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	
	@FXML private ImageView ballOrientationImage;
	
	@Override
	public void onInitialize(Window win)
	{
		PLAYGROUND_STATUS.addObserver(this, PlaygroundStatusTopic.BALL_STATUS);
		onPlaygroundStatusChanged(PLAYGROUND_STATUS, PlaygroundStatusTopic.BALL_STATUS);
	}

	@Override
	public void onFinalize()
	{
		PLAYGROUND_STATUS.removeObserver(this, PlaygroundStatusTopic.BALL_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.BALL_STATUS )
			return;
		
		final SmartBallStatus ballStatus = status.getBallStatus();
		OrientationTileUtil.setBallImageViewOrientation(ballOrientationImage, ballStatus.isKnown(), ballStatus.getOrientation());
	}
}
