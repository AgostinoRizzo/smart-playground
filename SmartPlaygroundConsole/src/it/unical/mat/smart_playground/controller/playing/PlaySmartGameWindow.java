/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.choosegame.ChooseGameWindow;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.WindowEvent;

/**
 * @author Agostino
 *
 */
public abstract class PlaySmartGameWindow extends Window
{
	private final ChooseGameWindow chooseGameWin;
	
	public PlaySmartGameWindow( final String title, final ChooseGameWindow chooseGameWin )
	{ 
		super(title); 
		this.chooseGameWin = chooseGameWin;
		stage.resizableProperty().setValue(Boolean.FALSE);
		//stage.setMaximized(true);
	}
	
	@Override
	public void close()
	{
		super.close();
	}
	
	@Override
	protected void onClose( final WindowEvent event )
	{
		if ( event != null )
		{
			final Alert closeAlert = new Alert( AlertType.CONFIRMATION, "Are you sure to exit from the game?", 
												ButtonType.YES, ButtonType.CANCEL );
			closeAlert.showAndWait();
			if ( closeAlert.getResult() != ButtonType.YES )
			{
				event.consume();
				return;
			}
		}
		
		super.onClose(event);
		chooseGameWin.setChoosenGame(null);
	}

}
