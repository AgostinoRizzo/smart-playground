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
	
	private int totalSteps = 0;
	private float totalDistance = 0f;
	private double burnedCalories = 0.0;
	private BurnedCaloriesEstimator burnedCaloriesEstimator = BurnedCaloriesEstimator.createStandard();
	
	public PlayerStatus() {}
	public PlayerStatus( final PlayerStatus other ) { this.copy(other); }
	
	public void set( final PlayerStatus other ) { this.copy(other); }
	
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
	
	public boolean isOrientationKnown()
	{
		return orientation >= 0f;
	}
	
	public int getTotalSteps()
	{
		return totalSteps;
	}
	public void updateTotalSteps(int totalSteps)
	{
		// update total steps
		this.totalSteps = totalSteps;
		
		// update total distance and burned calories based on new total steps
		totalDistance = this.totalSteps * BurnedCaloriesEstimator.STANDARD_STEP_LENGTH / 100f;
		burnedCalories = burnedCaloriesEstimator.onTotalSteps(this.totalSteps);
	}
	public float getTotalDistance()
	{
		return totalDistance;
	}
	public double getBurnedCalories()
	{
		return burnedCalories;
	}
	
	@Override
	public String toString()
	{
		return "PLAYER ORIENTATION: " + orientation + "\nORIENTATION ANCHOR: " + orientationAnchor + "\nTOTAL STEPS: " + totalSteps;
	}
	
	private void copy( final PlayerStatus other )
	{
		this.orientation = other.orientation;
		this.orientationAnchor = other.orientationAnchor;
		
		this.totalSteps = other.totalSteps;
		this.totalDistance = other.totalDistance;
		this.burnedCalories = other.burnedCalories;
		
		// no copy of burnedCaloriesEstimator
	}
}
