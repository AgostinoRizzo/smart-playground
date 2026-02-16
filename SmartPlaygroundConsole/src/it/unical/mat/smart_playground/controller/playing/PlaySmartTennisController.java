/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.view.form.TennisMatchFormController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * @author Agostino
 *
 */
public class PlaySmartTennisController extends PlaySmartGameController
{	
	@FXML private Parent tennisMatchForm;
	@FXML private TennisMatchFormController tennisMatchFormController;
	
	@Override
	public void onInitialize(Window win)
	{
		super.onInitialize(win);
		tennisMatchFormController.onInitialize(win);
	}

	@Override
	public void onFinalize()
	{
		super.onFinalize();
		tennisMatchFormController.onFinalize();
	}
	
}
