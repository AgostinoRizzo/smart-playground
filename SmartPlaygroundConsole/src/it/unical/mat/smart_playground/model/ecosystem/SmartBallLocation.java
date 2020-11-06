/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class SmartBallLocation
{
	private float left = -1, top = -1;
	
	public SmartBallLocation()
	{}
	
	public SmartBallLocation( final float left, final float top )
	{
		this.left = left;
		this.top = top;
	}
	
	public SmartBallLocation( final SmartBallLocation other )
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
	
	public void set( final SmartBallLocation other )
	{
		this.left = other.left;
		this.top  = other.top;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( this == obj )
			return true;
		if ( obj instanceof SmartBallLocation )
		{
			final SmartBallLocation other = (SmartBallLocation) obj;
			return ( this.left == other.left && this.top == other.top );
		}
		return false;
	}
}
