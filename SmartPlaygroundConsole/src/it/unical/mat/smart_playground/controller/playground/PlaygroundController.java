/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playing.PlaySmartGolfWindow;
import it.unical.mat.smart_playground.controller.playing.PlaySmartTennisWindow;
import it.unical.mat.smart_playground.model.ecosystem.SmartObjectLocation;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.util.Vector2Int;
import it.unical.mat.smart_playground.view.ImageFactory;
import it.unical.mat.smart_playground.view.animation.WindFlagAnimationManager;
import it.unical.mat.smart_playground.view.playground.Configs;
import it.unical.mat.smart_playground.view.playground.IsometricMap;
import it.unical.mat.smart_playground.view.playground.PlaygroundField;
import it.unical.mat.smart_playground.view.widget.BrightnessTileController;
import it.unical.mat.smart_playground.view.widget.HumidityTileController;
import it.unical.mat.smart_playground.view.widget.TemperatureTileController;
import it.unical.mat.smart_playground.view.widget.WindDirectionTileController;
import it.unical.mat.smart_playground.view.widget.WindSpeedTileController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlaygroundController implements LayoutController, PlaygroundStatusObserver
{
	private static final WindFlagAnimationManager WIND_FLAG_ANIMATOR = WindFlagAnimationManager.getInstance();
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	private static final ImageFactory IMAGE_FACTORY = ImageFactory.getInstance();
	
	@FXML private ImageView windFlagImage0;
	@FXML private ImageView windFlagImage1;
	@FXML private ImageView windFlagImage2;
	@FXML private ImageView windFlagImage3;
	@FXML private ImageView windFlagImage4;
	@FXML private ImageView windFlagImage5;
	@FXML private ImageView windFlagImage6;
	@FXML private ImageView windFlagImage7;
	
	@FXML private Canvas playgroundCanvas;
	@FXML private ImageView playgroundFieldBallImage;
	@FXML private ImageView golfHoleImage;
	
	@FXML private Parent windDirectionTile;
	@FXML private WindDirectionTileController windDirectionTileController;
	
	@FXML private Parent windSpeedTile;
	@FXML private WindSpeedTileController windSpeedTileController;
	
	@FXML private Parent temperatureTile;
	@FXML private TemperatureTileController temperatureTileController;
	
	@FXML private Parent brightnessTile;
	@FXML private BrightnessTileController brightnessTileController;
	
	@FXML private Parent humidityTile;
	@FXML private HumidityTileController humidityTileController;
	
	private boolean observingBallStatus = false;
	private boolean ballImageInFrontOfHoleImage = true;
	private float[] lastGolfHoleLocation = null;
	
	private PlaygroundField playgroundField = null;
	private IsometricMap golfFieldMap = new IsometricMap
			(Configs.PLAYGROUND_GOLF_FIELD_SIZE, Configs.PLAYGROUND_GOLF_FIELD_ORIGIN);
	
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
		
		windDirectionTileController.onInitialize(win);
		windSpeedTileController.onInitialize(win);
		temperatureTileController.onInitialize(win);
		brightnessTileController.onInitialize(win);
		humidityTileController.onInitialize(win);
		
		if ( win instanceof PlaySmartTennisWindow )
			initPlaygroundField(IMAGE_FACTORY.getTennisBallImage(), 
					Configs.PLAYGROUND_TENNIS_FIELD_SIZE, Configs.PLAYGROUND_TENNIS_FIELD_ORIGIN);
		else if ( win instanceof PlaySmartGolfWindow )
		{
			initPlaygroundField(IMAGE_FACTORY.getGolfBallImage(), 
					Configs.PLAYGROUND_GOLF_FIELD_SIZE, Configs.PLAYGROUND_GOLF_FIELD_ORIGIN);
			PLAYGROUND_STATUS.addObserver(this, PlaygroundStatusTopic.BALL_STATUS);
			observingBallStatus = true;
		}
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
		
		windDirectionTileController.onFinalize();
		windSpeedTileController.onFinalize();
		temperatureTileController.onFinalize();
		brightnessTileController.onFinalize();
		humidityTileController.onFinalize();
		
		if ( playgroundField != null )
			PLAYGROUND_STATUS.removeObserver(playgroundField, PlaygroundStatusTopic.BALL_STATUS);
		
		if ( observingBallStatus )
			PLAYGROUND_STATUS.addObserver(this, PlaygroundStatusTopic.BALL_STATUS);
	}
	
	public void locateGolfHole( final float left, final float top )
	{
		if ( lastGolfHoleLocation == null )
			lastGolfHoleLocation = new float[2];
		
		lastGolfHoleLocation[0] = 1f - top;
		lastGolfHoleLocation[1] = left;
		
		final Vector2Int coords = golfFieldMap.getScreenCoords(lastGolfHoleLocation[0], lastGolfHoleLocation[1]);
		golfHoleImage.setLayoutX(coords.getX() - 20);
		golfHoleImage.setLayoutY(coords.getY() - golfHoleImage.getFitHeight() + 10);
		if ( !golfHoleImage.isVisible() )
			golfHoleImage.setVisible(true);
	}
	
	private void initPlaygroundField( final Image ballImage, final Vector2Int fieldSize, final Vector2Int fieldOrigin )
	{
		playgroundFieldBallImage.setImage(ballImage);
		playgroundField = new PlaygroundField(playgroundCanvas, playgroundFieldBallImage, 
				fieldSize, fieldOrigin);
		PLAYGROUND_STATUS.addObserver(playgroundField, PlaygroundStatusTopic.BALL_STATUS);
	}

	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.BALL_STATUS || lastGolfHoleLocation == null )
			return;
		
		final SmartObjectLocation ballLocation = status.getBallStatus().getLocation();
		
		if ( ballLocation.getLeft() <= lastGolfHoleLocation[0] && ballLocation.getTop() >= lastGolfHoleLocation[1] )
			setBallImageInFrontGolfHole();
		else
			setBallImageBackGolfHole();
	}
	
	private void setBallImageInFrontGolfHole()
	{
		if ( !ballImageInFrontOfHoleImage )
		{
			playgroundFieldBallImage.toFront();
			ballImageInFrontOfHoleImage = true;
		}
	}
	
	private void setBallImageBackGolfHole()
	{
		if ( ballImageInFrontOfHoleImage )
		{
			golfHoleImage.toFront();
			ballImageInFrontOfHoleImage = false;
		}
	}
}
