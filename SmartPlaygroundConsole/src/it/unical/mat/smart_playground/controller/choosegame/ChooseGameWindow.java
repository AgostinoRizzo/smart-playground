/**
 * 
 */
package it.unical.mat.smart_playground.controller.choosegame;

import it.unical.mat.smart_playground.controller.OneShowWindow;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playing.PlaySmartTennisWindow;
import it.unical.mat.smart_playground.view.Strings;
import it.unical.mat.smart_playground.view.ViewConfigs;

/**
 * @author Agostino
 *
 */
public class ChooseGameWindow extends OneShowWindow
{
	private ChoosenGame choosenGame = null;
	private Window playingWindow = null;
	
	public ChooseGameWindow()
	{ super(Strings.CHOOSE_GAME_WINDOW_TITLE); stage.resizableProperty().setValue(Boolean.FALSE); }

	@Override
	protected String getRootLayoutFxmlFilename()
	{ return ViewConfigs.CHOOSE_GAME_LAYOUT_FXML_FILENAME; }
	
	public ChoosenGame getChoosenGame()
	{
		return choosenGame;
	}
	
	public void setChoosenGame(ChoosenGame choosenGame)
	{
		this.choosenGame = choosenGame;
	}
	
	@Override
	public void show()
	{
		if ( choosenGame == null )
			super.show();
	}
	
	@Override
	public void close()
	{
		super.close();
		
		if ( choosenGame == null )
			return;
		
		switch ( choosenGame )
		{
		case SMART_TENNIS:
			playingWindow = new PlaySmartTennisWindow(this);
			playingWindow.show();
			break;
		default: break;
		}
	}
}
