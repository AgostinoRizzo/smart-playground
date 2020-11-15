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
public class PlaySmartGolfWindow extends PlaySmartGameWindow
{
	public PlaySmartGolfWindow( final ChooseGameWindow chooseGameWin )
	{ super(Strings.SMART_GOLF_PLAYING_WINDOW_TITLE, chooseGameWin); }

	@Override
	protected String getRootLayoutFxmlFilename()
	{ return ViewConfigs.PLAY_SMART_GOLF_LAYOUT_FXML_FILENAME; }
}
