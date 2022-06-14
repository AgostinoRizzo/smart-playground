/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class SmartObjectLocation
{
	public static final float MIN_VISIBLE_DELTA = .01f;
	private float left = -1, top = -1;
	
	public SmartObjectLocation()
	{}
	
	public SmartObjectLocation( final float left, final float top )
	{
		this.left = left;
		this.top = top;
	}
	
	public SmartObjectLocation( final SmartObjectLocation other )
	{
		this.left = other.left;
		this.top = other.top;
	}
	
	public float getLeft()
	{
		return left;
	}
	
	public void setLeft(float left)
	{
		this.left = left;
	}
	
	public float getTop()
	{
		return top;
	}
	
	public void setTop(float top)
	{
		this.top = top;
	}
	
	public void setUnknown()
	{
		left = top = -1;
	}
	
	public boolean isKnown()
	{
		return ( left >= 0.0 && top >= 0.0 );
	}
	
	public void set( final SmartObjectLocation other )
	{
		this.left = other.left;
		this.top  = other.top;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( this == obj )
			return true;
		if ( obj instanceof SmartObjectLocation )
		{
			final SmartObjectLocation other = (SmartObjectLocation) obj;
			return ( this.left == other.left && this.top == other.top );
		}
		return false;
	}
	
	public float getMaxDelta( final float left, final float top )
	{
		return Math.max( Math.abs( this.left-left ),  Math.abs( this.top-top ) );
	}
}
