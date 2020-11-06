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
	private final SmartBallLocation location = new SmartBallLocation();
	private int orientation = -1;
	
	public SmartBallLocation getLocation()
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
}
