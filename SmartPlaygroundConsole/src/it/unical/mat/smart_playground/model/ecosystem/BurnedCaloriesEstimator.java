/**
 * 
 */
package it.unical.mat.smart_playground.model.ecosystem;

/**
 * @author Agostino
 *
 */
public class BurnedCaloriesEstimator
{
	public static final float STANDARD_BODY_WEIGHT = 70f;  // expressed in kg
	public static final float STANDARD_STEP_LENGTH = 50f;  // espressed in cm
	
	private static final double METRIC_RUNNING_FACTOR = 1.02784823;
	private static final double METRIC_WALKING_FACTOR = 0.708;
	
	private final boolean isRunning;
	private final float bodyWeight;
	private final float stepLength;  // expressed in cm
	
	private double burnedCalories = 0.0;
	
	public static BurnedCaloriesEstimator createStandard()
	{ return new BurnedCaloriesEstimator(false, STANDARD_BODY_WEIGHT, STANDARD_STEP_LENGTH); }
	
	public BurnedCaloriesEstimator( final boolean isRunning, final float bodyWeight, final float stepLength )
	{
		this.isRunning = isRunning;
		this.bodyWeight = bodyWeight;
		this.stepLength = stepLength;
	}
	
	public double getBurnedCalories()
	{
		return burnedCalories;
	}
	public void reset() { burnedCalories = 0.0; }
	
	public double onStep()
	{
		burnedCalories += computeSingleStepCalories();
		return burnedCalories;
				
	}
	public double onTotalSteps( final int totalSteps )
	{
		burnedCalories = computeSingleStepCalories() * totalSteps;
		return burnedCalories;
	}
	
	private double computeSingleStepCalories()
	{
		return ( bodyWeight * (isRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR) )
				* stepLength / 100000.0;
	}
}
