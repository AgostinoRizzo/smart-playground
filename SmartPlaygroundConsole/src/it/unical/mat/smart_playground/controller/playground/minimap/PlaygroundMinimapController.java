/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground.minimap;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.util.GeometryUtil;
import it.unical.mat.smart_playground.util.Vector2Int;
import it.unical.mat.smart_playground.view.animation.MinimapWindLinesAnimator;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimator;
import it.unical.mat.smart_playground.view.playground.Configs;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * @author Agostino
 *
 */
public class PlaygroundMinimapController implements LayoutController, PlaygroundStatusObserver
{
	private static final int   X = 0, Y = 1;
	private static final int[] TOP_LEFT_FIELD_CORNER = {21, 22};
	private static final int[] FIELD_SIZES = {197, 359};
	private static final int   BALL_SIZE = 30;
	private static final int   HALF_BALL_SIZE = BALL_SIZE / 2;
	
	@FXML private ImageView ballImage;
	@FXML private ImageView windOrientationImage;
	@FXML private ImageView windSpeedFanImage;
	
	@FXML private Canvas fieldCanvas;
	@FXML private Canvas windLinesCanvas;
	
	private GraphicsContext fieldCanvasGC;
	
	private static final WindSpeedAnimator WIND_SPEED_ANIMATOR = WindSpeedAnimator.getInstance();
	private static final PlaygroundStatus  PLAYGROUND_STATUS   = PlaygroundStatus.getInstance();
	private static final MinimapWindLinesAnimator MINIMAP_WIND_LINES_ANIMATOR = MinimapWindLinesAnimator.getInstance();
	
	
	@Override
	public void onInitialize(Window win)
	{
		fieldCanvasGC = fieldCanvas.getGraphicsContext2D();
		ballImage.setVisible(false);
		
		WIND_SPEED_ANIMATOR.addImageView(windSpeedFanImage);
		PLAYGROUND_STATUS.addObserver(this);
		
		MINIMAP_WIND_LINES_ANIMATOR.setMinimapController(this);
	}

	@Override
	public void onFinalize()
	{
		WIND_SPEED_ANIMATOR.removeImageView(windSpeedFanImage);
		PLAYGROUND_STATUS.removeObserver(this);
		
		MINIMAP_WIND_LINES_ANIMATOR.removeMinimapController();
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
	
	public Canvas getWindLinesCanvas()
	{
		return windLinesCanvas;
	}
	
	private void onBallStatusChanged( final SmartBallStatus newBallStatus )
	{
		if ( newBallStatus.isKnown() )
		{
			final SmartBallLocation location = newBallStatus.getLocation();
			final int ballX = getBallImageCoord(location.getTop(), X),
					  ballY = getBallImageCoord(1f - location.getLeft(), Y);
			
			ballImage.setLayoutX( ballX - HALF_BALL_SIZE );
			ballImage.setLayoutY( ballY - HALF_BALL_SIZE );
			
			final Vector2Int directionVector = GeometryUtil.computeDirectionVector(newBallStatus.getOrientation(), 90);
			
			clearFieldCanvas();
			drawBallTrajectory(ballX, ballY, directionVector);
			drawBallOrientation(ballX, ballY, directionVector);
			
			if ( !ballImage.isVisible() )
				ballImage.setVisible(true);
		}
		else if ( ballImage.isVisible() )
			ballImage.setVisible(false);
	}
	
	private void onWindStatusChanged( final WindStatus newWindStatus )
	{ windOrientationImage.setRotate(newWindStatus.getDirection()); }
	
	private void drawBallOrientation( final int ballX, final int ballY, final Vector2Int directionVector )
	{		
		final Vector2Int startPoint = new Vector2Int(ballX, ballY);
		final Vector2Int arrowTailVector = directionVector.getPerpendicular();
		
		final Vector2Int arrowPoint = startPoint.sum(directionVector.scale(1.5));
		final Vector2Int firstArrowTailSceenCoords  = startPoint.sum(arrowTailVector.sum(directionVector.invert()).scale(2.5));
		final Vector2Int secondArrowTailSceenCoords = startPoint.sum(arrowTailVector.invert().sum(directionVector.invert()).scale(2.5));
		
		final double[] xArrowPoints = { firstArrowTailSceenCoords.getX(), startPoint.getX(), secondArrowTailSceenCoords.getX(), arrowPoint.getX() };
		final double[] yArrowPoints = { firstArrowTailSceenCoords.getY(), startPoint.getY(), secondArrowTailSceenCoords.getY(), arrowPoint.getY() };
		
		fieldCanvasGC.setFill(Configs.PLAYGROUND_MINIMAP_BALL_ORIENTATION_ARROW_COLOR);
		fieldCanvasGC.fillPolygon(xArrowPoints, yArrowPoints, 4);
	}
	
	private void drawBallTrajectory( final int ballX, final int ballY, final Vector2Int directionVector )
	{
		final int[] projectionPoint = GeometryUtil.computeProjectionPoint(ballX, ballY, directionVector, 
																		  TOP_LEFT_FIELD_CORNER, FIELD_SIZES);
		fieldCanvasGC.setLineWidth(3);
		fieldCanvasGC.setStroke(Color.GREEN);
		fieldCanvasGC.strokeLine(ballX, ballY, projectionPoint[X], projectionPoint[Y]);
		fieldCanvasGC.setFill(Color.YELLOW);
		fieldCanvasGC.fillOval(ballX-5, ballY-5, 10, 10);
		fieldCanvasGC.fillOval(projectionPoint[X]-5, projectionPoint[Y]-5, 10, 10);
	}
	
	private void clearFieldCanvas()
	{ fieldCanvasGC.clearRect(0, 0, fieldCanvas.getWidth(), fieldCanvas.getHeight()); }
	
	private static int getBallImageCoord( final float coordPerc, final int coordIndex )
	{
		return TOP_LEFT_FIELD_CORNER[coordIndex] + (int) (coordPerc * (float)FIELD_SIZES[coordIndex]);
	}
}
