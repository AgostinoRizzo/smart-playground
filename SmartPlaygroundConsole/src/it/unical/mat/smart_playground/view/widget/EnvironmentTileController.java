/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author Agostino
 *
 */
public abstract class EnvironmentTileController implements LayoutController
{
	@FXML protected Label sensorLabel;
	
	@Override
	public void onInitialize(Window win)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinalize()
	{
		// TODO Auto-generated method stub
		
	}
}
