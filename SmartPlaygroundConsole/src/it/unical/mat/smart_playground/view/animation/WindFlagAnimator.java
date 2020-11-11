/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.view.ImageFactory;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class WindFlagAnimator
{
	public static final int ANIMATION_FRAMES_COUNT = 4;
	private static WindFlagAnimator instance = null;
	
	private final AnimationStatus animationStatus = new AnimationStatus(ANIMATION_FRAMES_COUNT);
	private final List<ImageView> flagImages = new ArrayList<>();
	
	private static final WindStatus WIND_STATUS = PlaygroundStatus.getInstance().getWindStatus();
	private static final ImageFactory IMAGE_FACTORY = ImageFactory.getInstance();
	
	public static WindFlagAnimator getInstance()
	{
		if ( instance == null )
			instance = new WindFlagAnimator();
		return instance;
	}
	
	private WindFlagAnimator()
	{}
	
	public void addImageView( final ImageView toAdd )
	{
		flagImages.add(toAdd);
	}
	
	public void removeImageView( final ImageView toRemove )
	{
		flagImages.remove(toRemove);
	}
	
	public void onUpdate( final long now )
	{
		final boolean isActive = WIND_STATUS.isActive();
		
		if ( isActive && animationStatus.update(now) )
			updateFlagImages(animationStatus.getCurrentIndex());
		else if ( !isActive )
		{
			animationStatus.reset();
			updateFlagImages(-1);
		}
	}
	
	private void updateFlagImages( final int frameIndex )
	{
		for ( final ImageView imgView : flagImages )
			imgView.setImage( ( frameIndex >= 0 ) 
							  ? IMAGE_FACTORY.getRedFlagFrameImage(frameIndex) 
							  : IMAGE_FACTORY.getRedFlagImage() );
	}
}
