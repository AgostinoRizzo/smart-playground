/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class SmartPoleStatus extends TelosbBasedStatus
{
	private WindDirection wind_direction = WindDirection.NORD;
	
	public void updateWindDirection( final WindDirection direction )
	{
		wind_direction=direction;
	}
	public WindDirection getWindDirection()
	{
		return wind_direction;
	}
}
