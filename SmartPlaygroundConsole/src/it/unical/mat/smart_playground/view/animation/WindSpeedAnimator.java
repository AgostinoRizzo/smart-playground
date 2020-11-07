/**
 * 
 */
package it.unical.mat.smart_playground.view.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * @author Agostino
 *
 */
public class WindSpeedAnimator
{
	public static final double WIND_FAN_ROTATION_DURATION = 1000;  // expressed in milliseconds.
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	
	private static WindSpeedAnimator instance = null;
	
	private final Map<ImageView, RotateTransition> windFanImages = new HashMap<>();
	
	public static WindSpeedAnimator getInstance()
	{
		if ( instance == null )
			instance = new WindSpeedAnimator();
		return instance;
	}
	
	private WindSpeedAnimator()
	{}
	
	public void addImageView( final ImageView toAdd )
	{
		windFanImages.put(toAdd, createRotateTransition(toAdd));
	}
	
	public void removeImageView( final ImageView toRemove )
	{
		windFanImages.remove(toRemove);
	}
	
	public void onUpdate( final long now )
	{
		if ( PLAYGROUND_STATUS.getWindStatus().isActive() )
			for ( final Entry<ImageView, RotateTransition> entry : windFanImages.entrySet() )
				entry.getValue().play();
		else
			for ( final Entry<ImageView, RotateTransition> entry : windFanImages.entrySet() )
				entry.getValue().stop();
	}
	
	private RotateTransition createRotateTransition( final ImageView imgView )
	{
		final RotateTransition rt = new RotateTransition(Duration.millis(WIND_FAN_ROTATION_DURATION), imgView);
		rt.setByAngle(360);
		rt.setCycleCount(Animation.INDEFINITE);
		rt.setInterpolator(Interpolator.LINEAR);
		return rt;
	}
}
