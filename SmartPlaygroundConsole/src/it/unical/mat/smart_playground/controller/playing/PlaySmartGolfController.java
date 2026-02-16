/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playground.minimap.PlaygroundMinimapController;
import it.unical.mat.smart_playground.view.form.GolfMatchFormController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * @author Agostino
 *
 */
public class PlaySmartGolfController extends PlaySmartGameController
{
	@FXML private Parent golfMatchForm;
	@FXML private GolfMatchFormController golfMatchFormController;
	
	@Override
	public void onInitialize(Window win)
	{
		super.onInitialize(win);
		golfMatchFormController.onInitialize(win);
		minimapController.setParentController(this);
	}

	@Override
	public void onFinalize()
	{
		super.onFinalize();
		golfMatchFormController.onFinalize();
	}
	
	public void onGolfHoleLocated( final double percX, final double percY, final PlaygroundMinimapController playgroundMinimapController )
	{
		playgroundController.locateGolfHole((float) percX, (float) percY);
		golfMatchFormController.onGolfHoleLocated(percX, percY, playgroundMinimapController);
	}
}
