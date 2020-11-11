/**
 * 
 */
package it.unical.mat.smart_playground.util;

/**
 * @author Agostino
 *
 */
public class Vector2Int
{
	private int x, y;
	
	public Vector2Int( final int x, final int y )
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}
	
	public Vector2Int sum( final Vector2Int other )
	{
		return new Vector2Int(this.x + other.x, this.y + other.y);
	}
	
	public Vector2Int invert()
	{
		return new Vector2Int(-x, -y);
	}
	
	public Vector2Int scale( final int scaleFactor )
	{
		return new Vector2Int(x / scaleFactor, y / scaleFactor);
	}
	
	public Vector2Int scale( final double scaleFactor )
	{
		return new Vector2Int((int)(x / scaleFactor), (int)(y / scaleFactor));
	}
	
	public Vector2Int getPerpendicular()
	{
		return new Vector2Int(-y, x);
	}
	
	public double length()
	{
		return Math.sqrt( (x*x) + (y*y) );
	}
	
	public double[] normalize()
	{
		final double length = length();
		final double[] normalized = { x / length, y / length };
		return normalized;
	}
}
