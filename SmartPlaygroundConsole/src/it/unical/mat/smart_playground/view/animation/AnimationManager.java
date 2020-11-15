/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Agostino
 *
 */
public abstract class AnimationManager<SUBJECT_CLASS>
{
	protected final Map<SUBJECT_CLASS, Animation> animations = new HashMap<>();
	private final AnimationStatus animationStatus;
	
	protected AnimationManager()
	{
		animationStatus = createAnimationStatus();
	}
	
	public void addAnimation( final SUBJECT_CLASS subject )
	{
		animations.put(subject, createAnimation(subject));
	}
	
	public void removeAnimation( final SUBJECT_CLASS subject )
	{
		animations.remove(subject);
	}
	
	public void onUpdate( final long now )
	{
		final boolean animated = isAnimated();
		
		if ( animated && animationStatus.update(now) )
			updateAnimations(animationStatus.getCurrentIndex());
		else if ( !animated )
		{
			animationStatus.reset();
			updateAnimations(-1);
		}
	}
	
	protected abstract Animation createAnimation( final SUBJECT_CLASS subject );
	protected abstract AnimationStatus createAnimationStatus();
	protected abstract boolean isAnimated();
	
	private void updateAnimations(int frameIndex)
	{
		for ( final Entry<SUBJECT_CLASS, Animation> subject2animation : animations.entrySet() )
			subject2animation.getValue().animate(frameIndex);
	}
}
