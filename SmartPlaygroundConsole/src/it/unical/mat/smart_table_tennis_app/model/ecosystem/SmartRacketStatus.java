/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.model.ecosystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Agostino
 *
 */
public class SmartRacketStatus
{
	public static final int CAPACITY = 30;
	
	private final List< Integer > accXValues = new ArrayList<>();
	private final List< Integer > accYValues = new ArrayList<>();
	private final List< Integer > accZValues = new ArrayList<>();
	
	public void updateNewAccelerometerValues( final List< Integer > xValues,
											  final List< Integer > yValues,
											  final List< Integer > zValues )
	{
		accXValues.addAll(xValues);
		accYValues.addAll(yValues);
		accZValues.addAll(zValues);
		
		EcosystemStatus.shrinkValuesList(accXValues, CAPACITY);
		EcosystemStatus.shrinkValuesList(accYValues, CAPACITY);
		EcosystemStatus.shrinkValuesList(accZValues, CAPACITY);
		
	}
	
	public List<Integer> getAccXValues()
	{
		return accXValues;
	}
	public List<Integer> getAccYValues()
	{
		return accYValues;
	}
	public List<Integer> getAccZValues()
	{
		return accZValues;
	}
	
}
