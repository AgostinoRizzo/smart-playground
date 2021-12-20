/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class PlayerStatus
{
	private float orientation = -1f, orientationAnchor = 0f;
	
	public PlayerStatus()
	{}
	public PlayerStatus( final PlayerStatus other )
	{
		this.orientation = other.orientation;
		this.orientationAnchor = other.orientationAnchor;
	}
	
	public void set( final PlayerStatus other )
	{
		this.orientation = other.orientation;
		this.orientationAnchor = other.orientationAnchor;
	}
	
	public float getAbsoluteOrientation()
	{
		return orientation;
	}
	public float getRelativeOrientation()
	{
		if ( orientation < 0f )
			return orientation;
		return (orientation - orientationAnchor) % 360f;
	}
	public void updateAbsoluteOrientation( final float orientation )
	{
		this.orientation = orientation;
	}
	public void syncOrientation( final float orientationAnchor )
	{
		this.orientationAnchor = this.orientation = orientationAnchor;
	}
	public void onUnknownOrientation()
	{
		orientation = -1f;
	}
	
	public boolean isKnown()
	{
		return orientation >= 0f;
	}
	
	@Override
	public String toString()
	{
		return "PLAYER ORIENTATION: " + orientation + "\nORIENTATION ANCHOR: " + orientationAnchor;
	}
}
