/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlayerOrientationTileController implements LayoutController
{
	@FXML private ImageView playerOrientationImage;
	
	private static final Lock lock = new ReentrantLock();
	private static PlayerOrientationTileController instance = null;
	
	public static PlayerOrientationTileController getInstance()
	{
		try { lock.lock(); return instance; }
		finally { lock.unlock(); }
	}
	public static void updateOrientation( final double degrees )
	{
		lock.lock();
		if ( instance != null )
			instance.playerOrientationImage.setRotate(degrees);
		lock.unlock();
	}
	
	@Override
	public void onInitialize(Window win)
	{}

	@Override
	public void onFinalize()
	{}

}
