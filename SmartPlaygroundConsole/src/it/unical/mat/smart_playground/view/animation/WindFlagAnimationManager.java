/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.view.ImageFactory;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class WindFlagAnimationManager extends AnimationManager<ImageView>
{
	public static final int ANIMATION_FRAMES_COUNT = 4;
	
	private static WindFlagAnimationManager instance = null;
	
	private static final WindStatus WIND_STATUS = PlaygroundStatus.getInstance().getWindStatus();
	private static final ImageFactory IMAGE_FACTORY = ImageFactory.getInstance();
	
	public static WindFlagAnimationManager getInstance()
	{
		if ( instance == null )
			instance = new WindFlagAnimationManager();
		return instance;
	}
	
	private WindFlagAnimationManager()
	{}
	
	@Override
	protected AnimationStatus createAnimationStatus()
	{
		return new AnimationStatus(ANIMATION_FRAMES_COUNT);
	}
	
	@Override
	protected boolean isAnimated()
	{
		return WIND_STATUS.isActive();
	}

	@Override
	protected Animation createAnimation(ImageView subject)
	{
		return new SpriteAnimation(new ImageViewAnimationImpl(subject, IMAGE_FACTORY.getRedFlagImage()), 
									IMAGE_FACTORY.getRedFlagImage(), IMAGE_FACTORY.getRedFlagSpriteImage());
	}	
}
