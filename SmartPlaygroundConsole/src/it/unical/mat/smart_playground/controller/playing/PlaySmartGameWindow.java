/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.choosegame.ChooseGameWindow;

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
	protected void onClose()
	{
		super.onClose();
		chooseGameWin.setChoosenGame(null);
	}

}
