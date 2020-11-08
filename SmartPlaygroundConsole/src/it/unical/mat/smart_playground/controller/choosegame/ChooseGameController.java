/**
 * 
 */
package it.unical.mat.smart_playground.controller.choosegame;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playground.PlaygroundController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * @author Agostino
 *
 */
public class ChooseGameController implements LayoutController
{
	@FXML private Parent playground;
	@FXML private PlaygroundController playgroundController;
	
	private Window parentWindow = null;
	
	@Override
	public void onInitialize(Window win)
	{
		parentWindow = win;
		playgroundController.onInitialize(win);
	}
	@Override
	public void onFinalize()
	{
		playgroundController.onFinalize();
	}
	
	@FXML 
	private void handleChooseTennisGame()
	{
		if ( parentWindow != null )
		{
			System.out.println("CHOOSE TENNIS");
			parentWindow.close();
		}
	}
	
	@FXML 
	private void handleChooseGolfGame()
	{
		if ( parentWindow != null )
		{
			System.out.println("CHOOSE GOLF");
			parentWindow.close();
		}
	}

}
