/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

/**
 * @author Agostino
 *
 */
public class WindStatus
{
	private boolean isActive = false;
	private short direction = 0; // 0-4095
		
	public short getDirection()
	{
		return direction;
	}
	public double getDirectionDegrees()
	{
		// from 0-4095 to 0-269
		return direction*269/4095 - 120.0;
	}
	public void setDirection(short direction)
	{
		if ( direction < 0 ) direction = 0;
		else if ( direction > 4095 ) direction = 4095;
		this.direction = direction;
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}
	
	public void set( final WindStatus newStatus )
	{
		this.isActive = newStatus.isActive;
		this.direction = newStatus.direction;
	}
}
