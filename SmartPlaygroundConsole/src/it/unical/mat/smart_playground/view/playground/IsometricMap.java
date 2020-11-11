/**
 * 
 */
package it.unical.mat.smart_playground.view.playground;

import it.unical.mat.smart_playground.util.Vector2Int;

/**
 * @author Agostino
 *
 */
public class IsometricMap
{
	private static final double MAP_ANGLE = Math.PI / 6.0;
	
	private final Vector2Int size, origin;
	
	public IsometricMap( final Vector2Int size, final Vector2Int origin )
	{
		this.size = size;
		this.origin = origin;
	}
	
	public Vector2Int getScreenCoords( final float left, final float top )
	{
		final int[] xLegs = computeLegsFromHypo((int) (size.getX() * left));
		final int[] yLegs = computeLegsFromHypo((int) (size.getY() * top));
		
		return computeScreenCoordsFromLegs(xLegs, yLegs);
	}
	
	public Vector2Int getScreenCoordsFromPivot( final Vector2Int pivot, final Vector2Int direction )
	{
		return computeScreenCoordsFromLegs(pivot, computeLegsFromHypo(direction.getX()), computeLegsFromHypo(direction.getY()));
	}
	
	public Vector2Int getAbsLocation( final float left, final float top )
	{
		return new Vector2Int((int) (size.getX() * left), (int) (size.getY() * top));
	}
	
	private Vector2Int computeScreenCoordsFromLegs( final int[] xLegs, final int[] yLegs )
	{
		return computeScreenCoordsFromLegs(origin, xLegs, yLegs);
	}
	
	private static Vector2Int computeScreenCoordsFromLegs( final Vector2Int origin, final int[] xLegs, final int[] yLegs )
	{
		return new Vector2Int(origin.getX() + yLegs[0] + xLegs[0], origin.getY() + yLegs[1] - xLegs[1]);
	}
	
	private static int[] computeLegsFromHypo( int hypo )
	{
		int sign = 1;
		if ( hypo < 0 )
		{
			hypo = -hypo;
			sign = -1;
		}
		
		final int greaterLeg = (int) ((double)hypo * Math.cos(MAP_ANGLE));
		final int lowerLeg   = (int) Math.sqrt( (hypo*hypo) - (greaterLeg*greaterLeg) );
		final int[] legs = { greaterLeg * sign, lowerLeg * sign };
		return legs;
	}
}
