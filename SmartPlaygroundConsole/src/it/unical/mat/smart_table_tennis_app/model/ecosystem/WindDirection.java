/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.model.ecosystem;

/**
 * @author Agostino
 *
 */
public enum WindDirection
{
	NORD, SUD, EAST, WEST;
	
	public static String toString( final WindDirection dir )
	{
		switch ( dir )
		{
		case NORD : return "Nord";
		case SUD  : return "Sud";
		case EAST : return "East";
		case WEST : return "West";

		default   : return "";
		}
	}
	public static double toDouble( final WindDirection dir )
	{
		switch ( dir )
		{
		case NORD : return 0.0;
		case SUD  : return 180.0;
		case EAST : return 90.0;
		case WEST : return 270.0;

		default   : return 0.0;
		}
	}
}
