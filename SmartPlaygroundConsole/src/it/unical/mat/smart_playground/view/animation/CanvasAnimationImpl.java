/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * @author Agostino
 *
 */
public class CanvasAnimationImpl extends AnimationImpl
{
	private final GraphicsContext gc;
	private Image currentImage = null;
	
	public CanvasAnimationImpl( final Canvas canvas, final Image startImg )
	{
		super((int) startImg.getWidth(), (int) startImg.getHeight());
		this.gc = canvas.getGraphicsContext2D();
		setImage(startImg);
	}
	
	@Override
	public void setImage(Image img)
	{
		clearCanvas();
		gc.drawImage(img, 0, 0);
		currentImage = img;
	}

	@Override
	public void updateImageViewport(int frameIndex)
	{
		clearCanvas();
		gc.drawImage(currentImage, frameIndex*viewportSize[X], 0, viewportSize[X], viewportSize[Y],
									0, 0, viewportSize[X], viewportSize[Y]);
	}

	@Override
	public void setRotation(int degrees)
	{
		final Rotate r = new Rotate(degrees, viewportSize[X] / 2, viewportSize[Y] / 2);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		if ( currentImage != null )
			setImage(currentImage);
	}
	
	private void clearCanvas()
	{
		gc.clearRect(0, 0, viewportSize[X], viewportSize[Y]);
	}
}
