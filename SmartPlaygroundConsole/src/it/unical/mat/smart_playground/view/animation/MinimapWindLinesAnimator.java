/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.controller.playground.minimap.PlaygroundMinimapController;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.util.GeometryUtil;
import it.unical.mat.smart_playground.util.Vector2Int;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Agostino
 *
 */
public class MinimapWindLinesAnimator
{
	private static final int X=0, Y=1;
	
	private static MinimapWindLinesAnimator instance = null;
	
	private final AnimationStatus animationStatus = new AnimationStatus();
	private final List<WindLine> windLines = new ArrayList<>();
	private PlaygroundMinimapController minimapController = null;
	
	private boolean canvasCleared = true;
	
	private static final WindStatus WIND_STATUS = PlaygroundStatus.getInstance().getWindStatus();
	
	public static MinimapWindLinesAnimator getInstance()
	{
		if ( instance == null )
			instance = new MinimapWindLinesAnimator();
		return instance;
	}
	
	private MinimapWindLinesAnimator()
	{}
	
	public void setMinimapController(PlaygroundMinimapController minimapController)
	{
		this.minimapController = minimapController;
		updateNewWindLines();
	}
	
	public void removeMinimapController()
	{
		minimapController = null;
		windLines.clear();
	}
	
	public void onUpdate( final long now )
	{
		if ( !animationStatus.update(now) || !WIND_STATUS.isActive() || windLines.isEmpty() ||
				(minimapController != null && !minimapController.getFeatureFlags().isWindLines()) )
		{
			clearWindLinesCanvas();
			return;
		}
		onRefresh();
	}
	
	public void onRefresh()
	{
		if ( windLines.isEmpty() )
			return;
		
		final Vector2Int windDirection = GeometryUtil.computeDirectionVector(WIND_STATUS.getDirection(), 90);
		
		clearWindLinesCanvas();
		int i;
		
		for ( final WindLine wline : windLines )
		{
			wline.setWindDirection(windDirection);
			for ( i=0; i<2; ++i )
			{
				wline.updatePosition();
				wline.drawLine();
			}
		}
		
		canvasCleared = false;
	}
	
	private void updateNewWindLines()
	{
		windLines.clear();
		
		final Canvas windLinesCanvas = minimapController.getWindLinesCanvas();
		final GraphicsContext windLineCanvasGC = windLinesCanvas.getGraphicsContext2D();
		
		final int[] canvasSize = new int[2];
		canvasSize[X] = (int) (windLinesCanvas.getWidth());
		canvasSize[Y] = (int) (windLinesCanvas.getHeight()); 
		
		for ( int f=-8; f<=8; f+=2 )
			windLines.add( new WindLine(windLineCanvasGC, canvasSize, f) );
	}
	
	private void clearWindLinesCanvas()
	{
		if ( !canvasCleared && minimapController != null )
		{
			final Canvas windLinesCanvas = minimapController.getWindLinesCanvas();
			windLinesCanvas.getGraphicsContext2D()
				.clearRect(0, 0, windLinesCanvas.getWidth(), windLinesCanvas.getHeight());
			canvasCleared = true;
		}
	}
}
