/**
 * 
 */
package it.unical.mat.smart_playground.view.field;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlaygroundField
{
	private IsometricMap playgroundFieldMap = 
			new IsometricMap(Configs.PLAYGROUND_FIELD_SIZE, Configs.PLAYGROUND_FIELD_ORIGIN);
	
	private Canvas playgroundFieldCanvas;
	private final GraphicsContext playgroundFieldGC;
	private final ImageView ballImageView;
	
	private SmartBallLocation lastBallLocation = null;
	private int lastBallOrientation = -1;
	
	private int ballRotation = 0;
	
	public PlaygroundField( final Canvas playgroundFieldCanvas, final ImageView ballImageView )
	{
		this.playgroundFieldCanvas = playgroundFieldCanvas;
		this.ballImageView = ballImageView;
		
		playgroundFieldGC = playgroundFieldCanvas.getGraphicsContext2D();
	}
	private int currOrientation = 0;
	public void onBallStatusChanged( final SmartBallStatus newBallStatus )
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
			
			if ( !ballImageView.isVisible() )
				ballImageView.setVisible(true);
		}
		else
		{
			clearFieldCanvas();
			ballImageView.setVisible(false);
			lastBallLocation = null;
			lastBallOrientation = -1;
		}
	}
	
	private void updateBallRotation()
	{
		ballRotation = (ballRotation + 10) % 360;
	}
	
	private void drawBallOrientation()
	{
		clearFieldCanvas();
		
		int canonicalBallOrientation = (lastBallOrientation == 0) ? 0 : 360 - lastBallOrientation;
		canonicalBallOrientation = (canonicalBallOrientation + 90) % 360;
		
		final double radiansOrientation = Math.toRadians(canonicalBallOrientation);
		final Vector2Int directionVector = new Vector2Int(  (int)(Math.cos(radiansOrientation) * 50.0), 
														   -(int)(Math.sin(radiansOrientation) * 50.0) );
		
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
