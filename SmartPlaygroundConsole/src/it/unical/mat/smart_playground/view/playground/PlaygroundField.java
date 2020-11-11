/**
 * 
 */
package it.unical.mat.smart_playground.view.playground;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.util.GeometryUtil;
import it.unical.mat.smart_playground.util.Vector2Int;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlaygroundField implements PlaygroundStatusObserver
{
	private IsometricMap playgroundFieldMap = 
			new IsometricMap(Configs.PLAYGROUND_FIELD_SIZE, Configs.PLAYGROUND_FIELD_ORIGIN);
	
	private Canvas playgroundFieldCanvas;
	private final GraphicsContext playgroundFieldGC;
	
	private final ImageView ballImageView;
	private final ImageView ballOrientationImageView;
	
	private final List<ImageView> windOrientationImageViews = new ArrayList<>();
	
	private SmartBallLocation lastBallLocation = null;
	private int lastBallOrientation = -1;
	
	private int ballRotation = 0;
	
	public PlaygroundField( final Canvas playgroundFieldCanvas, final ImageView ballImageView, final ImageView ballOrientationImageView )
	{
		this.playgroundFieldCanvas = playgroundFieldCanvas;
		this.ballImageView = ballImageView;
		this.ballOrientationImageView = ballOrientationImageView;
		
		playgroundFieldGC = playgroundFieldCanvas.getGraphicsContext2D();
	}
	
	public void addWindOrientationImageView( final ImageView toAdd )
	{
		windOrientationImageViews.add(toAdd);
	}
	
	public void removeWindOrientationImageView( final ImageView toAdd )
	{
		windOrientationImageViews.remove(toAdd);
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
	
	private int currOrientation = 0;  // TODO: remove
	
	private void onBallStatusChanged( final SmartBallStatus newBallStatus )
	{
		if ( newBallStatus.isKnown() )
		{			
			final SmartBallLocation ballLocation = newBallStatus.getLocation();
			final Vector2Int screenCoords = playgroundFieldMap.
					getScreenCoords(ballLocation.getLeft(), ballLocation.getTop());
			
			ballImageView.setLayoutX(screenCoords.getX() - Configs.HALF_PLAYGROUND_BALL_IMAGE_VIEW_SIZE);
			ballImageView.setLayoutY(screenCoords.getY() - Configs.HALF_PLAYGROUND_BALL_IMAGE_VIEW_SIZE);
			
			if ( lastBallLocation == null || !lastBallLocation.equals(ballLocation) )
			{
				updateBallRotation();
				if ( lastBallLocation == null ) lastBallLocation = new SmartBallLocation(ballLocation);
				else                            lastBallLocation.set(ballLocation);
				ballImageView.setRotate(ballRotation);
			}
			
			lastBallOrientation = currOrientation; //newBallStatus.getOrientation();
			currOrientation = (currOrientation + 10) % 360;
			drawBallOrientation();
			
			ballOrientationImageView.setRotate(currOrientation);
			
			if ( !ballImageView.isVisible() )
				ballImageView.setVisible(true);
			if ( !ballOrientationImageView.isVisible() )
				ballOrientationImageView.setVisible(true);
		}
		else
		{
			clearFieldCanvas();
			ballImageView.setVisible(false);
			ballOrientationImageView.setVisible(false);
			lastBallLocation = null;
			lastBallOrientation = -1;
		}
	}
	
	private void onWindStatusChanged( final WindStatus newWindStatus )
	{
		for ( final ImageView imgView : windOrientationImageViews )
			imgView.setRotate(newWindStatus.getDirection());
	}
	
	private void updateBallRotation()
	{
		ballRotation = (ballRotation + 10) % 360;
	}
	
	private void drawBallOrientation()
	{
		clearFieldCanvas();
		
		final Vector2Int directionVector = GeometryUtil.computeDirectionVector(lastBallOrientation);
		final Vector2Int startPointSceenCoords = playgroundFieldMap.getScreenCoords(lastBallLocation.getLeft(), 
																					lastBallLocation.getTop());
		final Vector2Int arrowPointSceenCoords = playgroundFieldMap.getScreenCoordsFromPivot(startPointSceenCoords, 
																							 directionVector);
		
		final Vector2Int arrowTailVector = directionVector.getPerpendicular();
		final Vector2Int firstArrowTailSceenCoords = playgroundFieldMap.
				getScreenCoordsFromPivot(startPointSceenCoords, arrowTailVector.sum(directionVector.invert()).scale(2));
		
		final Vector2Int secondArrowTailSceenCoords = playgroundFieldMap.
				getScreenCoordsFromPivot(startPointSceenCoords, arrowTailVector.invert().sum(directionVector.invert()).scale(2));
		
		final double[] xArrowPoints = { firstArrowTailSceenCoords.getX(), startPointSceenCoords.getX(), secondArrowTailSceenCoords.getX(), arrowPointSceenCoords.getX() };
		final double[] yArrowPoints = { firstArrowTailSceenCoords.getY(), startPointSceenCoords.getY(), secondArrowTailSceenCoords.getY(), arrowPointSceenCoords.getY() };
		
		playgroundFieldGC.setFill(Configs.PLAYGROUND_BALL_ORIENTATION_ARROW_COLOR);
		playgroundFieldGC.fillPolygon(xArrowPoints, yArrowPoints, 4);
	}
	
	private void clearFieldCanvas()
	{
		playgroundFieldGC.clearRect(0, 0, playgroundFieldCanvas.getWidth(), playgroundFieldCanvas.getHeight());
	}
}
