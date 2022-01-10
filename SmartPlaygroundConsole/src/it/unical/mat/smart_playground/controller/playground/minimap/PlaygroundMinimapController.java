/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground.minimap;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playing.PlaySmartGolfController;
import it.unical.mat.smart_playground.controller.playing.PlaySmartGolfWindow;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.util.GeometryUtil;
import it.unical.mat.smart_playground.util.Vector2Int;
import it.unical.mat.smart_playground.view.ImageFactory;
import it.unical.mat.smart_playground.view.animation.MinimapWindLinesAnimator;
import it.unical.mat.smart_playground.view.playground.Configs;
import it.unical.mat.smart_playground.view.widget.BallOrientationTileController;
import it.unical.mat.smart_playground.view.widget.WindDirectionTileController;
import it.unical.mat.smart_playground.view.widget.WindSpeedTileController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
	
	@FXML private ImageView minimapFieldImage;
	@FXML private ImageView ballImage;
	
	@FXML private Parent windDirectionTile;
	@FXML private WindDirectionTileController windDirectionTileController;
	
	@FXML private Parent windSpeedTile;
	@FXML private WindSpeedTileController windSpeedTileController;
	
	@FXML private Parent ballOrientationTile;
	@FXML private BallOrientationTileController ballOrientationTileController;
	
	@FXML private Canvas fieldCanvas;
	@FXML private Canvas windLinesCanvas;
	
	@FXML private Canvas fieldArea;
	@FXML private Pane fieldAreaPane;
	
	@FXML private CheckMenuItem windLinesCheckbox;
	@FXML private CheckMenuItem ballOrientationCheckbox;
	@FXML private CheckMenuItem ballTrajectoryCheckbox;
	
	private GraphicsContext fieldCanvasGC;
	private final FeatureFlags featureFlags = new FeatureFlags();
	
	private PlaySmartGolfController parentController = null;
	
	private boolean canLocateHole = true;
	
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	private static final MinimapWindLinesAnimator MINIMAP_WIND_LINES_ANIMATOR = MinimapWindLinesAnimator.getInstance();
	private static final ImageFactory IMAGE_FACTORY = ImageFactory.getInstance();
	
	
	@Override
	public void onInitialize(Window win)
	{
		fieldCanvasGC = fieldCanvas.getGraphicsContext2D();
		ballImage.setVisible(false);
		
		if ( win instanceof PlaySmartGolfWindow )
		{
			minimapFieldImage.setImage(IMAGE_FACTORY.getGolfFieldMinimapImage());
			ballImage.setImage(IMAGE_FACTORY.getGolfBallImage());
		}
		
		windDirectionTileController.onInitialize(win);
		windSpeedTileController.onInitialize(win);
		ballOrientationTileController.onInitialize(win);
		
		PLAYGROUND_STATUS.addObserver(this);
		
		MINIMAP_WIND_LINES_ANIMATOR.setMinimapController(this);
	}

	@Override
	public void onFinalize()
	{
		windDirectionTileController.onFinalize();
		windSpeedTileController.onFinalize();
		ballOrientationTileController.onFinalize();
		
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
		default: break;
		}
	}
	
	public Canvas getWindLinesCanvas()
	{
		return windLinesCanvas;
	}
	
	public FeatureFlags getFeatureFlags()
	{
		return featureFlags;
	}
	
	public void setParentController( final PlaySmartGolfController parentController )
	{
		this.parentController = parentController;
	}
	
	public void setCanLocateHole( final boolean canLocate )
	{
		canLocateHole = canLocate;
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
			
			final Vector2Int directionVector = GeometryUtil.computeDirectionVector(newBallStatus.getOrientation(), -90);
			
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
	{
		
	}
	
	private void drawBallOrientation( final int ballX, final int ballY, final Vector2Int directionVector )
	{
		if ( !featureFlags.isBallOrientation() )
			return;
		
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
		if ( !featureFlags.isBallTrajectory() )
			return;
		
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
	
	@FXML private void handleWindLinesFlagChanged()
	{
		featureFlags.setWindLines(windLinesCheckbox.isSelected());
	}
	
	@FXML private void handleBallOrientationFlagChanged()
	{
		featureFlags.setBallOrientation(ballOrientationCheckbox.isSelected());
	}
	
	@FXML private void handleBallTrajectoryFlagChanged()
	{
		featureFlags.setBallTrajectory(ballTrajectoryCheckbox.isSelected());
	}
	
	@FXML private void handleFieldAreaClick( final MouseEvent event )
	{
		if ( !canLocateHole )
			return;
		
		final double areaWidth = fieldAreaPane.getWidth(), areaHeight = fieldAreaPane.getHeight();
		final double x = event.getX(), y = event.getY();
		final double percX = x / areaWidth, percY = y / areaHeight;
		
		if ( percX >= .1 && percX <= .9 && percY >= .1 && percY < .5 )
		{
			final GraphicsContext gc = fieldArea.getGraphicsContext2D();
			gc.clearRect(0, 0, fieldArea.getWidth(), fieldArea.getHeight());
			
			final Image golfHoleImage = ImageFactory.getInstance().getGolfHoleMiniImage();
			gc.drawImage(golfHoleImage, x - 20 + 20, 
										y - golfHoleImage.getHeight() + 10 + 20);
			
			if ( parentController != null )
				parentController.onGolfHoleLocated(percX, percY, this);
		}
	}
	
	private static int getBallImageCoord( final float coordPerc, final int coordIndex )
	{
		return TOP_LEFT_FIELD_CORNER[coordIndex] + (int) (coordPerc * (float)FIELD_SIZES[coordIndex]);
	}
}
