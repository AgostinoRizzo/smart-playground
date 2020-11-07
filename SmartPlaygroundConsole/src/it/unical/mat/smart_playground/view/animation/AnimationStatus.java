/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

/**
 * @author Agostino
 *
 */
public class AnimationStatus
{
	private static final int UPDATE_DELTA = 100;  // expressed in milliseconds
	private final int framesCount;
	private int currentIndex = 0;
	private long lastUpdateTime = -1;
	
	public AnimationStatus()
	{
		this.framesCount = 1;
	}
	
	public AnimationStatus( final int framesCount )
	{
		this.framesCount = framesCount;
	}
	
	public int getCurrentIndex()
	{
		return currentIndex;
	}
	
	public boolean update( final long now )
	{
		final long nowMillis = now / 1000000;
		
		if ( lastUpdateTime >= 0 && (nowMillis - lastUpdateTime) < UPDATE_DELTA )
			return false;
		
		currentIndex = (currentIndex + 1) % framesCount;
		lastUpdateTime = nowMillis;
		return true;
	}
	
	public void reset()
	{
		currentIndex = 0;
		lastUpdateTime = -1;
	}
}
