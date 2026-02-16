/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import javafx.scene.image.Image;

/**
 * @author Agostino
 *
 */
public class SpriteAnimation extends Animation
{
	private final Image idleImg, spriteImg;
	
	public SpriteAnimation( final AnimationImpl impl, final Image idle, final Image sprite )
	{
		super(impl);
		idleImg = idle;
		spriteImg = sprite;
	}

	@Override
	protected void onAnimationStart()
	{
		impl.setImage(spriteImg);
	}

	@Override
	protected void onAnimationUpdate(int frameIndex)
	{
		impl.updateImageViewport(frameIndex);
	}

	@Override
	protected void onIdleStart()
	{
		impl.setImage(idleImg);
	}	
}
