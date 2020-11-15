/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playground.PlaygroundController;
import it.unical.mat.smart_playground.controller.playground.minimap.PlaygroundMinimapController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * @author Agostino
 *
 */
public abstract class PlaySmartGameController implements LayoutController
{
	@FXML private Parent playground;
	@FXML private PlaygroundController playgroundController;
	
	@FXML private Parent minimap;
	@FXML protected PlaygroundMinimapController minimapController;
	
	protected PlaySmartGameController()
	{}
	
	@Override
	public void onInitialize(Window win)
	{
		playgroundController.onInitialize(win);
		minimapController.onInitialize(win);
	}

	@Override
	public void onFinalize()
	{
		playgroundController.onFinalize();
		minimapController.onFinalize();
	}
}
