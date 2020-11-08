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
import it.unical.mat.smart_playground.view.animation.WindFlagAnimator;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimator;
import javafx.fxml.FXML;
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
	@FXML private ImageView windSpeedFanImage;
	
	private static final WindFlagAnimator  WIND_FLAG_ANIMATOR  = WindFlagAnimator.getInstance();
	private static final WindSpeedAnimator WIND_SPEED_ANIMATOR = WindSpeedAnimator.getInstance();
	private static final PlaygroundStatus  PLAYGROUND_STATUS   = PlaygroundStatus.getInstance();
	
	@Override
	public void onInitialize(Window win)
	{
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage0);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage1);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage2);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage3);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage4);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage5);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage6);
		WIND_FLAG_ANIMATOR.addImageView(windFlagImage7);
		
		WIND_SPEED_ANIMATOR.addImageView(windSpeedFanImage);
		
		PLAYGROUND_STATUS.addObserver(this);
	}
	
	@Override
	public void onFinalize()
	{
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage0);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage1);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage2);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage3);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage4);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage5);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage6);
		WIND_FLAG_ANIMATOR.removeImageView(windFlagImage7);
		
		WIND_SPEED_ANIMATOR.removeImageView(windSpeedFanImage);
		
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
