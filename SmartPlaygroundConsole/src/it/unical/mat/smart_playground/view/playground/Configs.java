/**
 * 
 */
package it.unical.mat.smart_playground.view.playground;

import it.unical.mat.smart_playground.util.Vector2Int;
import javafx.scene.paint.Color;

/**
 * @author Agostino
 *
 */
public class Configs
{
	public static final Vector2Int PLAYGROUND_FIELD_SIZE   = new Vector2Int(302, 198);
	public static final Vector2Int PLAYGROUND_FIELD_ORIGIN = new Vector2Int(40, 168);
	
	public static final Vector2Int PLAYGROUND_TENNIS_FIELD_SIZE   = new Vector2Int(272, 150);
	public static final Vector2Int PLAYGROUND_TENNIS_FIELD_ORIGIN = new Vector2Int(118, 347);
	
	public static final Vector2Int PLAYGROUND_GOLF_FIELD_SIZE   = new Vector2Int(240, 132);
	public static final Vector2Int PLAYGROUND_GOLF_FIELD_ORIGIN = new Vector2Int(332, 488);
	
	public static final int PLAYGROUND_BALL_IMAGE_VIEW_SIZE = 30;
	public static final int HALF_PLAYGROUND_BALL_IMAGE_VIEW_SIZE = PLAYGROUND_BALL_IMAGE_VIEW_SIZE / 2;
	
	public static final Color PLAYGROUND_BALL_ORIENTATION_ARROW_COLOR         = Color.YELLOW;
	public static final Color PLAYGROUND_MINIMAP_BALL_ORIENTATION_ARROW_COLOR = Color.RED;
}
