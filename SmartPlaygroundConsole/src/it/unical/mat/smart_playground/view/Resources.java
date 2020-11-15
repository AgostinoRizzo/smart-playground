/**
 * 
 */
package it.unical.mat.smart_playground.view;

import javafx.scene.image.Image;

/**
 * @author Agostino
 *
 */
public class Resources
{
	public static final String DRAWABLE_PATH  = "file:res/drawable/";
	
	public static final String USER_ACK_IMAGE_FILENAME = "user_ack3.png";
	public static final Image USER_ACK_IMAGE = new Image( DRAWABLE_PATH + USER_ACK_IMAGE_FILENAME );
	
	public static final String RED_FLAG_IMAGE_FILENAME              = "res/drawable/red_flag.png";
	public static final String RED_FLAG_SPRITE_IMAGE_FILENAME       = "res/drawable/animation/red_flag_sprite.png";
	public static final String RED_FLAG_FRAME_IMAGE_FILENAME_PREFIX = "res/drawable/animation/red_flag_";
	public static final String RED_FLAG_FRAME_IMAGE_FILENAME_SUFFIX = ".png";
	
	public static final String WIND_FAN_IMAGE_FILENAME              = "res/drawable/wind_fan.png";
	public static final String WIND_FAN_SPRITE_IMAGE_FILENAME       = "res/drawable/animation/wind_fan_sprite.png";
	
	public static final String GOLF_HOLE_MINI_IMAGE_FILENAME        = "res/drawable/golf_hole_mini.png";
}
