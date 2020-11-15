/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class ImageViewAnimationImpl extends AnimationImpl
{	
	private final ImageView imgView;
	
	public ImageViewAnimationImpl( final ImageView target, final Image startImg )
	{
		super((int) startImg.getWidth(), (int) startImg.getHeight());
		imgView = target;
		setImage(startImg);
	}
	
	@Override
	public void setImage(Image img)
	{
		imgView.setImage(img);
		imgView.setViewport( new Rectangle2D(0, 0, viewportSize[X], viewportSize[Y]) );
	}

	@Override
	public void updateImageViewport(int frameIndex)
	{
		imgView.setViewport( new Rectangle2D(frameIndex*viewportSize[X], 0, 
							viewportSize[X], viewportSize[Y]) );
	}
	
	@Override
	public void setRotation(int degrees)
	{
		imgView.setRotate(degrees);
	}
}
