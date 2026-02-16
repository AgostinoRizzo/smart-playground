/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public abstract class OrientationTileUtil
{
	public static void setBallImageViewOrientation( final ImageView orientationImage, final boolean known, final float orientation )
	{
		if ( known )
		{
			orientationImage.setRotate(orientation + 90);
			if ( !orientationImage.isVisible() )
				orientationImage.setVisible(true);
		}
		else if ( orientationImage.isVisible() )
			orientationImage.setVisible(false);
	}
}
