/**
 * 
 */
package it.unical.mat.smart_playground.view.form;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.view.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * @author Agostino
 *
 */
public class GolfMatchFormController implements LayoutController
{
	@FXML private Label holeLocationStatusLabel;
	
	private boolean golfHoleLocated = false;
	
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
	
	@FXML private void playMatch()
	{
		if ( !golfHoleLocated )
		{
			Dialogs.showInfo("Golf hole", "Golf hole location not set.");
			return;
		}
		System.out.println("Play Golf match.");
	}
	
	public void onGolfHoleLocated( final double percX, final double percY )
	{
		holeLocationStatusLabel.setText("Located");
		holeLocationStatusLabel.setTextFill(Color.GREEN);
		golfHoleLocated = true;
	}
}
