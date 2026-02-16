/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import javafx.scene.image.Image;

/**
 * @author Agostino
 *
 */
public abstract class AnimationImpl
{
	protected static final int X=0, Y=1;
	
	protected final int[] viewportSize = new int[2];
	
	public AnimationImpl( final int viewportWidth, final int viewportHeight )
	{
		viewportSize[X] = viewportWidth;
		viewportSize[Y] = viewportHeight;
	}
	
	public abstract void setImage( final Image img );
	public abstract void updateImageViewport( final int frameIndex );
	public abstract void setRotation( final int degrees );
}
