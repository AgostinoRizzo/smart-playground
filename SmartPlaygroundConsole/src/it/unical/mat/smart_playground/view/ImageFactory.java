/**
 * 
 */
package it.unical.mat.smart_playground.view;

import java.io.File;

import it.unical.mat.smart_playground.view.animation.WindFlagAnimationManager;
import javafx.scene.image.Image;

/**
 * @author Agostino
 *
 */
public class ImageFactory
{
	private static ImageFactory instance = null;
	
	private Image redFlagImage = null;
	private Image redFlagSpriteImage = null;
	private Image[] redFlagFrameImages = new Image[WindFlagAnimationManager.ANIMATION_FRAMES_COUNT];
	
	private Image windFanImage = null;
	private Image windFanSpriteImage = null;
	
	private Image tennisBallImage = null;
	private Image golfBallImage = null;
	
	private Image golfHoleMiniImage = null;
	private Image golfFieldMinimapImage = null;
	
	public static ImageFactory getInstance()
	{
		if ( instance == null )
			instance = new ImageFactory();
		return instance;
	}
	
	private ImageFactory()
	{
		for ( int i=0; i<redFlagFrameImages.length; ++i )
			redFlagFrameImages[i] = null;
	}
	
	public Image getRedFlagImage()
	{
		if ( redFlagImage == null )
			redFlagImage = createImage( Resources.RED_FLAG_IMAGE_FILENAME );
		return redFlagImage;
	}
	
	public Image getRedFlagSpriteImage()
	{
		if ( redFlagSpriteImage == null )
			redFlagSpriteImage = createImage( Resources.RED_FLAG_SPRITE_IMAGE_FILENAME );
		return redFlagSpriteImage;
	}
	
	public Image getWindFanImage()
	{
		if ( windFanImage == null )
			windFanImage = createImage( Resources.WIND_FAN_IMAGE_FILENAME );
		return windFanImage;
	}
	
	public Image getWindFanSpriteImage()
	{
		if ( windFanSpriteImage == null )
			windFanSpriteImage = createImage( Resources.WIND_FAN_SPRITE_IMAGE_FILENAME );
		return windFanSpriteImage;
	}
	
	public Image getRedFlagFrameImage( final int frameIndex )
	{
		if ( redFlagFrameImages[frameIndex] == null )
			redFlagFrameImages[frameIndex] = createImage
				( Resources.RED_FLAG_FRAME_IMAGE_FILENAME_PREFIX + frameIndex + 
				  Resources.RED_FLAG_FRAME_IMAGE_FILENAME_SUFFIX );
		return redFlagFrameImages[frameIndex];
	}
	
	public Image getTennisBallImage()
	{
		if ( tennisBallImage == null )
			tennisBallImage = createImage( Resources.TENNIS_BALL_IMAGE_FILENAME );
		return tennisBallImage;
	}
	
	public Image getGolfBallImage()
	{
		if ( golfBallImage == null )
			golfBallImage = createImage( Resources.GOLF_BALL_IMAGE_FILENAME );
		return golfBallImage;
	}
	
	public Image getGolfHoleMiniImage()
	{
		if ( golfHoleMiniImage == null )
			golfHoleMiniImage = createImage( Resources.GOLF_HOLE_MINI_IMAGE_FILENAME );
		return golfHoleMiniImage;
	}
	
	public Image getGolfFieldMinimapImage()
	{
		if ( golfFieldMinimapImage == null )
			golfFieldMinimapImage = createImage( Resources.GOLF_FIELD_MINIMAP_IMAGE_FILENAME );
		return golfFieldMinimapImage;
	}
	
	private Image createImage( final String filename )
	{
		return new Image( new File(filename).toURI().toString() );
	}
}
