/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

/**
 * @author Agostino
 *
 */
public abstract class Animation
{
	protected final AnimationImpl impl;
	private boolean isAnimated = false;
	
	protected Animation( final AnimationImpl impl )
	{
		this.impl = impl;
	}
	
	public void animate(int frameIndex)
	{
		if ( frameIndex >= 0 )
		{
			if ( !isAnimated )
				onAnimationStart();
			isAnimated = true;
			
			onAnimationUpdate(frameIndex);
		}
		else if ( isAnimated )
		{
			onIdleStart();
			isAnimated = false;
		}
	}
	
	protected abstract void onAnimationStart();
	protected abstract void onAnimationUpdate( final int frameIndex );
	protected abstract void onIdleStart();
}
