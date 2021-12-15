/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Agostino
 *
 */
public class EcosystemStatus
{
	public static final double TEMPERATURE_AVERAGE_VALUE_RATIO = 2.0;
	public static final double HUMIDITY_AVERAGE_VALUE_RATIO = 2.0;
	public static final double BRIGHTNESS_AVERAGE_VALUE_RATIO = 2.0;
	
	private static EcosystemStatus instance = null;
	
	private final SmartGamePlatformStatus gamePlatformStatus      = new SmartGamePlatformStatus();
	private final SmartBallStatus         smartBallStatus         = new SmartBallStatus();
	private final MotionControllerStatus  motionControllerStatus  = new MotionControllerStatus();
	private final SmartFieldStatus        smartFieldStatus        = new SmartFieldStatus();
	
	private final SmartRacketStatus       mainSmartRacketStatus   = new SmartRacketStatus();
	private final SmartRacketStatus       secondSmartRacketStatus = new SmartRacketStatus();
	
	public static EcosystemStatus getInstance()
	{
		if ( instance == null )
			instance = new EcosystemStatus();
		return instance;
	}
	
	private EcosystemStatus()
	{}
	
	public SmartGamePlatformStatus getSmartGamePlatformStatus()
	{
		return gamePlatformStatus;
	}
	public SmartBallStatus getSmartBallStatus()
	{
		return smartBallStatus;
	}
	public MotionControllerStatus getMotionControllerStatus()
	{
		return motionControllerStatus;
	}
	public SmartFieldStatus getSmartFieldStatus()
	{
		return smartFieldStatus;
	}
	public SmartRacketStatus getMainSmartRacketStatus()
	{
		return mainSmartRacketStatus;
	}
	public SmartRacketStatus getSecondSmartRacketStatus()
	{
		return secondSmartRacketStatus;
	}
	public SmartRacketStatus getSmartRacketStatus( final SmartRacketType smartRacket )
	{
		switch ( smartRacket )
		{
		case MAIN:   return mainSmartRacketStatus;
		case SECOND: return mainSmartRacketStatus;
		default:     return null;
		}
	}
	
	public Integer getCurrentTemperatureAverage()
	{
		return getTemperatureAverage(smartFieldStatus.getTemperatureValues().size() - 1);
	}
	public Integer getCurrentHumidityAverage()
	{
		return getHumidityAverage(smartFieldStatus.getHumidityValues().size() - 1);
	}
	public Integer getCurrentBrightnessAverage()
	{
		return getBrightnessAverage(smartFieldStatus.getBrightnessValues().size() - 1);
	}
	public Integer getTemperatureAverage( final int t )
	{
		final List< List< Integer > > valuesLists = new ArrayList<>();
		valuesLists.add( gamePlatformStatus.getTemperatureValues() );
		valuesLists.add( smartBallStatus.getTemperatureValues() );
		valuesLists.add( smartFieldStatus.getTemperatureValues() );
		
		return computeValuesAverage( valuesLists, t );
		
	}
	public Integer getHumidityAverage( final int t )
	{
		final List< List< Integer > > valuesLists = new ArrayList<>();
		valuesLists.add( gamePlatformStatus.getHumidityValues() );
		valuesLists.add( smartBallStatus.getHumidityValues() );
		valuesLists.add( smartFieldStatus.getHumidityValues() );
		
		return computeValuesAverage( valuesLists, t );
		
	}
	public Integer getBrightnessAverage( final int t )
	{
		final List< List< Integer > > valuesLists = new ArrayList<>();
		valuesLists.add( gamePlatformStatus.getBrightnessValues() );
		valuesLists.add( smartBallStatus.getBrightnessValues() );
		valuesLists.add( smartFieldStatus.getBrightnessValues() );
		
		return computeValuesAverage( valuesLists, t );
		
	}
	public List< Integer > getTemperatureAverageValues( final int n )
	{
		final List< Integer > avgValues = new ArrayList<>();
		for( int i=0; i<n; ++i )
			avgValues.add( getTemperatureAverage(i) );
		return avgValues;
	}
	public List< Integer > getHumidityAverageValues( final int n )
	{
		final List< Integer > avgValues = new ArrayList<>();
		for( int i=0; i<n; ++i )
			avgValues.add( getHumidityAverage(i) );
		return avgValues;
	}
	public List< Integer > getBrightnessAverageValues( final int n )
	{
		final List< Integer > avgValues = new ArrayList<>();
		for( int i=0; i<n; ++i )
			avgValues.add( getBrightnessAverage(i) );
		return avgValues;
	}
	
	protected static void shrinkValuesList( final List< Integer > values , final int capacity )
	{
		if ( capacity <= 0 )
			return;
		while( values.size() > capacity )
			values.remove(0);
	}
	
	private Integer computeValuesAverage( final List< List< Integer > > valuesLists, final int t )
	{
		int v=0;
		int c=0;
		
		for( List< Integer > lst : valuesLists )
			if ( t < lst.size() ) 
			{ 
				v+=lst.get(t); 
				++c; 
			}
		
		if ( c==0 )
			return null;
		return v/c;
	}
}
