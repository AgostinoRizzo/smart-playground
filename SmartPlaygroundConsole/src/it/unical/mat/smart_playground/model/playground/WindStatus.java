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
	private short direction = 0;
		
	public short getDirection()
	{
		return direction;
	}
	public void setDirection(short direction)
	{
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
