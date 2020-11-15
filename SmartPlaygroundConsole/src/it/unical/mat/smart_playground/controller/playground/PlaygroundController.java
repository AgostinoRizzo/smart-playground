/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.view.animation.WindFlagAnimationManager;
import it.unical.mat.smart_playground.view.widget.WindSpeedTileController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlaygroundController implements LayoutController, PlaygroundStatusObserver
{
	@FXML private ImageView windFlagImage0;
	@FXML private ImageView windFlagImage1;
	@FXML private ImageView windFlagImage2;
	@FXML private ImageView windFlagImage3;
	@FXML private ImageView windFlagImage4;
	@FXML private ImageView windFlagImage5;
	@FXML private ImageView windFlagImage6;
	@FXML private ImageView windFlagImage7;
	
	@FXML private ImageView windOrientationImage;
	
	@FXML private Parent windSpeedTile;
	@FXML private WindSpeedTileController windSpeedTileController;
	
	private static final WindFlagAnimationManager WIND_FLAG_ANIMATOR = WindFlagAnimationManager.getInstance();
	private static final PlaygroundStatus  PLAYGROUND_STATUS   = PlaygroundStatus.getInstance();
	
	@Override
	public void onInitialize(Window win)
	{
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage0);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage1);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage2);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage3);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage4);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage5);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage6);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage7);
		
		windSpeedTileController.onInitialize(win);
		
		PLAYGROUND_STATUS.addObserver(this);
	}
	
	@Override
	public void onFinalize()
	{
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage0);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage1);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage2);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage3);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage4);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage5);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage6);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage7);
		
		windSpeedTileController.onFinalize();
		
		PLAYGROUND_STATUS.removeObserver(this);
	}
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		switch ( topic )
		{
		case BALL_STATUS : onBallStatusChanged(status.getBallStatus()); break;
		case WIND_STATUS : onWindStatusChanged(status.getWindStatus()); break;
		case ALL :
			onBallStatusChanged(status.getBallStatus());
			onWindStatusChanged(status.getWindStatus()); break;
		}	
		
	}
	
	private void onBallStatusChanged( final SmartBallStatus newBallStatus )
	{
		// TODO: add management.
	}
	
	private void onWindStatusChanged( final WindStatus newWindStatus )
	{ windOrientationImage.setRotate(newWindStatus.getDirection()); }
	
}
