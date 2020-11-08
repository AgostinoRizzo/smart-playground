/**
 * 
 */
package it.unical.mat.smart_playground.controller.choosegame;

import it.unical.mat.smart_playground.controller.OneShowWindow;
import it.unical.mat.smart_playground.view.Strings;
import it.unical.mat.smart_playground.view.ViewConfigs;

/**
 * @author Agostino
 *
 */
public class ChooseGameWindow extends OneShowWindow
{
	public ChooseGameWindow()
	{ super(Strings.CHOOSE_GAME_WINDOW_TITLE); stage.resizableProperty().setValue(Boolean.FALSE); }

	@Override
	protected String getRootLayoutFxmlFilename()
	{ return ViewConfigs.CHOOSE_GAME_LAYOUT_FXML_FILENAME; }
}
