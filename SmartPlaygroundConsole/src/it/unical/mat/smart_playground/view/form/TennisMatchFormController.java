/**
 * 
 */
package it.unical.mat.smart_playground.view.form;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

/**
 * @author Agostino
 *
 */
public class TennisMatchFormController implements LayoutController
{
	private static final String[] MATCH_SETS_OPTIONS = {"Best of 1", "Best of 2", "Best of 3"};
	private static final String[] SET_GAMES_OPTIONS  = {"Best of 1", "Best of 2", "Best of 3"};
	
	@FXML private ChoiceBox<String> matchSetsOptions;
	@FXML private ChoiceBox<String> setGamesOptions;
	
	@Override
	public void onInitialize(Window win)
	{
		initChoiceBox(matchSetsOptions, MATCH_SETS_OPTIONS);
		initChoiceBox(setGamesOptions, SET_GAMES_OPTIONS);
	}

	@Override
	public void onFinalize()
	{
		
	}
	
	@FXML private void playMatch()
	{
		System.out.println("Play Tennis match.");
	}
	
	private static void initChoiceBox( final ChoiceBox<String> chioceBox, final String[] options )
	{
		chioceBox.getItems().addAll(options);
		chioceBox.getSelectionModel().selectFirst();
	}
}
