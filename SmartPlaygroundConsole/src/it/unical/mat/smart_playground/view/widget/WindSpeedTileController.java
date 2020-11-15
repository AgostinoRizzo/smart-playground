/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimationManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

/**
 * @author Agostino
 *
 */
public class WindSpeedTileController implements LayoutController
{
	private static final WindSpeedAnimationManager ANIMATION_MANAGER = WindSpeedAnimationManager.getInstance();
	
	@FXML private Canvas windFanCanvas;

	@Override
	public void onInitialize(Window win)
	{
		ANIMATION_MANAGER.addAnimation(windFanCanvas);
	}

	@Override
	public void onFinalize()
	{
		ANIMATION_MANAGER.removeAnimation(windFanCanvas);
	}
}
