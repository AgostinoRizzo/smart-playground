/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import javafx.scene.image.Image;

/**
 * @author Agostino
 *
 */
public class RotationAnimation extends Animation
{
	protected final Image img;
	private final int rotationDelta;
	
	public RotationAnimation( final AnimationImpl impl, final Image img, final int rotationDelta )
	{
		super(impl);
		this.img = img;
		this.rotationDelta = rotationDelta;
		impl.setImage(img);
	}

	@Override
	protected void onAnimationStart()
	{}

	@Override
	protected void onAnimationUpdate(int frameIndex)
	{
		impl.setRotation( (frameIndex * rotationDelta) % 360 );
	}

	@Override
	protected void onIdleStart()
	{
		impl.setRotation(0);
	}
}
