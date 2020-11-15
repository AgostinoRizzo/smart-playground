/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.view.ImageFactory;
import javafx.scene.canvas.Canvas;

/**
 * @author Agostino
 *
 */
public class WindSpeedAnimationManager extends AnimationManager<Canvas>
{
	private static final int FAN_ROTATION_DELTA = 20;  // expressed in degrees.
	private static final int ANIMATION_FRAMES_COUNT = 360 / FAN_ROTATION_DELTA;
	
	private static WindSpeedAnimationManager instance = null;
	
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	private static final ImageFactory IMAGE_FACTORY = ImageFactory.getInstance();
	
	public static WindSpeedAnimationManager getInstance()
	{
		if ( instance == null )
			instance = new WindSpeedAnimationManager();
		return instance;
	}
	
	protected WindSpeedAnimationManager()
	{}

	@Override
	protected boolean isAnimated()
	{
		return PLAYGROUND_STATUS.getWindStatus().isActive();
	}

	@Override
	protected Animation createAnimation(Canvas subject)
	{
		return new RotationAnimation(new CanvasAnimationImpl(subject, IMAGE_FACTORY.getWindFanImage()), 
				IMAGE_FACTORY.getWindFanImage(), FAN_ROTATION_DELTA);
	}

	@Override
	protected AnimationStatus createAnimationStatus()
	{
		return new AnimationStatus(ANIMATION_FRAMES_COUNT, 50);
	}
}
