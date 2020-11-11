/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.choosegame.ChooseGameWindow;
import it.unical.mat.smart_playground.view.Strings;
import it.unical.mat.smart_playground.view.ViewConfigs;

/**
 * @author Agostino
 *
 */
public class PlaySmartTennisWindow extends PlaySmartGameWindow
{	
	public PlaySmartTennisWindow( final ChooseGameWindow chooseGameWin )
	{ super(Strings.SMART_TENNIS_PLAYING_WINDOW_TITLE, chooseGameWin); }

	@Override
	protected String getRootLayoutFxmlFilename()
	{ return ViewConfigs.PLAY_SMART_TENNIS_LAYOUT_FXML_FILENAME; }
	
	

}
