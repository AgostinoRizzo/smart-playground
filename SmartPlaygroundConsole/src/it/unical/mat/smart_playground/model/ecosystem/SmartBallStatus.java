/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class SmartBallStatus extends TelosbBasedStatus
{
	private final SmartObjectLocation location = new SmartObjectLocation();
	private int orientation = -1;
	
	public SmartBallStatus()
	{}
	public SmartBallStatus( final SmartBallStatus other )
	{
		location.set(other.getLocation());
		setOrientation(other.getOrientation());
	}
	
	public SmartObjectLocation getLocation()
	{
		return location;
	}
	
	public int getOrientation()
	{
		return orientation;
	}
	
	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
	}
	
	public void setUnknownStatus()
	{
		location.setUnknown();
		orientation = -1;
	}
	
	public boolean isKnown()
	{
		return ( orientation >= 0.0 && location.isKnown() );
	}
	
	@Override
	public String toString()
	{
		if ( isKnown() )
			return "BALL LOCATION: " + location.getLeft() + ", " + location.getTop() + 
					"\nBALL ORIENTATION: " + orientation;
		else
			return "BALL STATUS UNKNOWN";
	}
}
