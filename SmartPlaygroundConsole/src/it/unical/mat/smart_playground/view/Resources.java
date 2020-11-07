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
	public static final String RED_FLAG_FRAME_IMAGE_FILENAME_PREFIX = "res/drawable/animation/red_flag_";
	public static final String RED_FLAG_FRAME_IMAGE_FILENAME_SUFFIX = ".png";
}
