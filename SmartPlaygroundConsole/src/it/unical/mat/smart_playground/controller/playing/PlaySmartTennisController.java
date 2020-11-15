/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playground.PlaygroundController;
import it.unical.mat.smart_playground.controller.playground.minimap.PlaygroundMinimapController;
import it.unical.mat.smart_playground.view.form.TennisMatchFormController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * @author Agostino
 *
 */
public class PlaySmartTennisController implements LayoutController
{
	@FXML private Parent playground;
	@FXML private PlaygroundController playgroundController;
	
	@FXML private Parent minimap;
	@FXML private PlaygroundMinimapController minimapController;
	
	@FXML private Parent tennisMatchForm;
	@FXML private TennisMatchFormController tennisMatchFormController;
	
	@Override
	public void onInitialize(Window win)
	{
		playgroundController.onInitialize(win);
		minimapController.onInitialize(win);
		tennisMatchFormController.onInitialize(win);
	}

	@Override
	public void onFinalize()
	{
		playgroundController.onFinalize();
		minimapController.onFinalize();
		tennisMatchFormController.onFinalize();
	}
	
}
