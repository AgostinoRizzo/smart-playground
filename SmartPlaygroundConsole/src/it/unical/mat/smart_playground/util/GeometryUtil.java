package it.unical.mat.smart_playground.util;

public class GeometryUtil
{
	private static final int X = 0, Y = 1;
	
	public static Vector2Int computeDirectionVector( final int orientation )
	{
		return computeDirectionVector(orientation, 0);
	}
	
	public static Vector2Int computeDirectionVector( final int orientation, final int shift )
	{
		int canonicalOrientation = (orientation == 0) ? 0 : 360 - orientation;
		canonicalOrientation = (canonicalOrientation + 90 + shift) % 360;
		
		final double radiansOrientation = Math.toRadians(canonicalOrientation);
		return new Vector2Int(  (int)(Math.cos(radiansOrientation) * 50.0), 
								-(int)(Math.sin(radiansOrientation) * 50.0) );
	}
	
	public static int[] computeProjectionPoint( final int startX, final int startY, final Vector2Int direction, 
													 final int[] offsets, final int[]sizes )
	{
		final double[] walkingDirection = direction.normalize();
		final double[] currPoint = {startX, startY}, prevPoint = {startX, startY};
		
		while ( isProjectionPointInArea(currPoint, offsets, sizes) )
		{
			prevPoint[X] = currPoint[X];
			prevPoint[Y] = currPoint[Y];
			
			currPoint[X] += walkingDirection[X];
			currPoint[Y] += walkingDirection[Y];
		}
		
		final int[] projectionPoint = { (int)Math.floor(prevPoint[X]), (int)Math.floor(prevPoint[Y]) };
		return projectionPoint;
	}
	
	private static boolean isProjectionPointInArea( final double[] point, final int[] offsets, final int[]sizes )
	{
		return ( Math.floor(point[X]) >= offsets[X] && Math.floor(point[Y]) >= offsets[Y] && 
				 Math.floor(point[X]) < offsets[X] + sizes[X] && Math.floor(point[Y]) < offsets[Y] + sizes[Y] );
	}
}
