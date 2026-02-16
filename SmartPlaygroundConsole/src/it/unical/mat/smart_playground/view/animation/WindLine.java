/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import java.util.Random;

import it.unical.mat.smart_playground.util.Vector2Int;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Agostino
 *
 */
public class WindLine
{
	private static final int X=0, Y=1;
	private static final int VECTOR_SCALE_FACTOR = 3;
	private static final int START_VECTOR_MULTIPLY_FACTOR = 10;
	private static final Random RANDOM = new Random();
	
	private final GraphicsContext canvasGC;
	private final int[] canvasSize;
	private final int[] canvasCenter;
	private final int startPerpOffsetMultiplyFactor;
	
	private int[] currPosition = null;
	private Vector2Int windDirection = null;
	private Vector2Int perpWindDirection = null;
	private Vector2Int offsetPerpWindDirection = null;
	
	private boolean wasVisible = false;
	
	public WindLine( final GraphicsContext canvasGC, final int[] canvasSize, final int startPerpOffsetMultiplyFactor )
	{
		this.canvasGC = canvasGC;
		this.canvasSize = canvasSize;
		this.startPerpOffsetMultiplyFactor = startPerpOffsetMultiplyFactor;
		
		canvasCenter = new int[2];
		canvasCenter[X] = canvasSize[X] / 2;
		canvasCenter[Y] = canvasSize[Y] / 2;
	}
	
	public void setWindDirection( final Vector2Int windDirection )
	{
		if ( canUpdateOrDraw() && this.windDirection.equals(windDirection.scale(VECTOR_SCALE_FACTOR)) )
			return;
		
		this.windDirection = windDirection.scale(VECTOR_SCALE_FACTOR);
		perpWindDirection = this.windDirection.getPerpendicular();
		offsetPerpWindDirection = perpWindDirection.multiply(startPerpOffsetMultiplyFactor);
		currPosition = new int[2];
		
		resetPosition();
	}
	
	public void updatePosition()
	{
		if ( !canUpdateOrDraw() )
			return;
		
		final Vector2Int scaledWindDir = windDirection.scale(2);
		currPosition[X] += scaledWindDir.getX();
		currPosition[Y] += scaledWindDir.getY();
		
		final boolean isVisible = ( currPosition[X] >= 0 && currPosition[Y] >= 0 && 
									currPosition[X] < canvasSize[X] && currPosition[Y] < canvasSize[Y] );
		if ( isVisible )
			wasVisible = true;
		else if ( wasVisible )
			resetPosition();
			
	}
	
	public void drawLine()
	{
		if ( !canUpdateOrDraw() )
			return;
		
		final Vector2Int invertedPerpWindDirection = perpWindDirection.invert();
		
		canvasGC.setStroke(Color.WHITE);
		canvasGC.setLineWidth(1);
		
		canvasGC.strokeLine(currPosition[X] + windDirection.getX(), currPosition[Y] + windDirection.getY(),
							currPosition[X] + perpWindDirection.getX(), currPosition[Y] + perpWindDirection.getY());
		canvasGC.strokeLine(currPosition[X] + windDirection.getX(), currPosition[Y] + windDirection.getY(),
							currPosition[X] + invertedPerpWindDirection.getX(), currPosition[Y] + invertedPerpWindDirection.getY());
	}
	
	private boolean canUpdateOrDraw()
	{
		return currPosition != null && windDirection != null && perpWindDirection != null && offsetPerpWindDirection != null;
	}
	
	private void resetPosition()
	{
		final Vector2Int startVector = this.windDirection.multiply(RANDOM.nextInt(START_VECTOR_MULTIPLY_FACTOR) + 1);
		currPosition[X] = canvasCenter[X] + offsetPerpWindDirection.getX() - startVector.getX();
		currPosition[Y] = canvasCenter[Y] + offsetPerpWindDirection.getY() - startVector.getY();
		
		wasVisible = false;
	}
}
