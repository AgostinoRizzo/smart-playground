/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground.minimap;

/**
 * @author Agostino
 *
 */
public class FeatureFlags
{
	private boolean windLines, ballOrientation, ballTrajectory;
	
	public boolean isWindLines()
	{
		return windLines;
	}
	public boolean isBallOrientation()
	{
		return ballOrientation;
	}
	public boolean isBallTrajectory()
	{
		return ballTrajectory;
	}
	
	public void setWindLines(boolean windLines)
	{
		this.windLines = windLines;
	}
	public void setBallOrientation(boolean ballOrientation)
	{
		this.ballOrientation = ballOrientation;
	}
	public void setBallTrajectory(boolean ballTrajectory)
	{
		this.ballTrajectory = ballTrajectory;
	}
}
