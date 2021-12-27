/**
 * 
 */
package it.unical.mat.smart_playground.view.form;

import com.google.gson.JsonObject;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.network.GameEventCallback;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

/**
 * @author Agostino
 *
 */
public class TennisMatchFormController implements LayoutController, GameEventCallback
{
	private static final String[] MATCH_SETS_OPTIONS = {"Best of 1", "Best of 3", "Best of 5"};
	private static final String[] SET_GAMES_OPTIONS  = {"Best of 1", "Best of 3", "Best of 5"};
	private static final String[] ARTIFICIAL_PAYER_LEVEL_OPTIONS  = {"Easy", "Medium", "Hard", "Perfect"};
	
	private static final int PLAYER_A = 0;
	private static final int PLAYER_B = 1;
	
	@FXML private VBox matchOptionsBox;
	@FXML private ChoiceBox<String> matchSetsOptions;
	@FXML private ChoiceBox<String> setGamesOptions;
	@FXML private ChoiceBox<String> artificialPlayerLevelOptions;
	
	@FXML private AnchorPane matchStatusBox;
	@FXML private Label scoreboardLabel00;
	@FXML private Label scoreboardLabel01;
	@FXML private Label scoreboardLabel02;
	@FXML private Label scoreboardLabel03;
	@FXML private Label scoreboardLabel04;
	@FXML private Label scoreboardLabel05;
	@FXML private Label scoreboardLabel10;
	@FXML private Label scoreboardLabel11;
	@FXML private Label scoreboardLabel12;
	@FXML private Label scoreboardLabel13;
	@FXML private Label scoreboardLabel14;
	@FXML private Label scoreboardLabel15;
	@FXML private Label currentMatchSetLabel;
	@FXML private Label currentSetGameLabel;
	@FXML private Label infoErrorMessageLabel;
	
	private final Label[][] scoreboardLabels = new Label[2][6];
	
	@Override
	public void onInitialize(Window win)
	{
		matchStatusBox.setVisible(false);
		matchOptionsBox.setVisible(true);
		
		initChoiceBox(matchSetsOptions, MATCH_SETS_OPTIONS);
		initChoiceBox(setGamesOptions, SET_GAMES_OPTIONS);
		initChoiceBox(artificialPlayerLevelOptions, ARTIFICIAL_PAYER_LEVEL_OPTIONS);
		artificialPlayerLevelOptions.getSelectionModel().selectLast();
		
		initScoreboardLabels();
		
		PlaygroundBaseCommProvider.getInstance().setGameEventCallback(this);
	}

	@Override
	public void onFinalize()
	{
		PlaygroundBaseCommProvider.getInstance().removeGameEventCallback();
	}
	
	@FXML private void playMatch()
	{
		final int matchSetsBestOf = matchSetsOptions.getSelectionModel().getSelectedIndex() + 1;
		final int setGamesBestOf  = setGamesOptions.getSelectionModel().getSelectedIndex() + 1;
		final String artificialPlayerLevel = artificialPlayerLevelOptions.getSelectionModel().getSelectedItem();
		
		final JsonObject gameSettings = new JsonObject();
		gameSettings.addProperty("type", "game_init");
		gameSettings.addProperty("gameType", "tennis");
		gameSettings.addProperty("matchSetsBestOf", matchSetsBestOf);
		gameSettings.addProperty("setGamesBestOf", setGamesBestOf);
		gameSettings.addProperty("artificialPlayerLevel", artificialPlayerLevel);
		
		matchOptionsBox.setVisible(false);
		matchStatusBox.setVisible(true);
		
		initScoreboardLabelsText();
		displayInfoMessage("Waiting for match initialization...");
		
		PlaygroundBaseCommProvider.getInstance().sendCommand(gameSettings);
	}
	
	@Override
	public void onGameEvent(JsonObject event)
	{
		final JsonObject gameEvent = event.deepCopy();
		System.out.println(gameEvent);
		
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				//clearInfoErrorMessage();
				
				final String subType = gameEvent.get("subType").getAsString();
				if ( subType.equals("game_status_ready") )
				{
					final boolean isReady = gameEvent.get("isReady").getAsBoolean();
					if ( !isReady )
					{
						final boolean isBallReady = gameEvent.get("ballReady").getAsBoolean();
						final boolean isPlayerReady = gameEvent.get("playerReady").getAsBoolean();
						final StringBuilder message = new StringBuilder();
						
						if ( !isBallReady )
							message.append("Smart Ball not in place. ");
						if ( !isPlayerReady )
							message.append("Player not in place.");
						
						displayErrorMessage(message.toString());
					}
				}
				else if ( subType.equals("match_status") )
				{
					final int currentMatchSet            = gameEvent.get("currentMatchSet").getAsInt();
					final int currentSetGame             = gameEvent.get("currentSetGame").getAsInt();
					final int mainPlayerScore            = gameEvent.get("main").getAsInt();
					final int artificialPlayerScore      = gameEvent.get("artificial").getAsInt();
					final int totalMainPlayerScore       = gameEvent.get("totalMain").getAsInt();
					final int totalArtificialPlayerScore = gameEvent.get("totalArtificial").getAsInt();
					final boolean terminated             = gameEvent.get("terminated").getAsBoolean();
					
					updateScoreboardLabelsText(currentMatchSet, currentSetGame, mainPlayerScore, artificialPlayerScore, 
												totalMainPlayerScore, totalArtificialPlayerScore);
					if ( terminated )
						displayInfoMessage("Match terminated. Player " + 
									(totalMainPlayerScore > totalArtificialPlayerScore ? "A (human)" : "B (artificial)") + " wins!");
				}
				else if ( subType.equals("match_turn") )
				{
					final String playerTurn = gameEvent.get("turn").getAsString();
					if ( playerTurn.equals("player_a") )
						displayInfoMessage("Human player turn.");
					else if ( playerTurn.equals("player_b") )
						displayInfoMessage("Artificial player turn.");
				}
			}
		});
	}
	
	private void initScoreboardLabels()
	{
		scoreboardLabels[0][0] = scoreboardLabel00;
		scoreboardLabels[0][1] = scoreboardLabel01;
		scoreboardLabels[0][2] = scoreboardLabel02;
		scoreboardLabels[0][3] = scoreboardLabel03;
		scoreboardLabels[0][4] = scoreboardLabel04;
		scoreboardLabels[0][5] = scoreboardLabel05;
		
		scoreboardLabels[1][0] = scoreboardLabel10;
		scoreboardLabels[1][1] = scoreboardLabel11;
		scoreboardLabels[1][2] = scoreboardLabel12;
		scoreboardLabels[1][3] = scoreboardLabel13;
		scoreboardLabels[1][4] = scoreboardLabel14;
		scoreboardLabels[1][5] = scoreboardLabel15;
		
		initScoreboardLabelsText();
	}
	private void initScoreboardLabelsText()
	{
		int i=0, j;
		for ( ; i<scoreboardLabels.length; ++i )
			for ( j=0; j<scoreboardLabels[i].length; ++j )
				scoreboardLabels[i][j].setText("--");
		
		scoreboardLabels[PLAYER_A][5].setText("0");
		scoreboardLabels[PLAYER_B][5].setText("0");
		
		currentMatchSetLabel.setText("Current Match Set: --");
		currentSetGameLabel.setText("Current Set Game: --");
	}
	private void updateScoreboardLabelsText( final int currentMatchSet, final int currentSetGame, 
											 final int mainPlayerScore, final int artificialPlayerScore,
											 final int totalMainPlayerScore, final int totalArtificialPlayerScore )
	{
		final int currenttMatchSetIndex = currentMatchSet - 1;
		scoreboardLabels[PLAYER_A][currenttMatchSetIndex].setText(Integer.toString(mainPlayerScore));
		scoreboardLabels[PLAYER_B][currenttMatchSetIndex].setText(Integer.toString(artificialPlayerScore));
		
		scoreboardLabels[PLAYER_A][5].setText(Integer.toString(totalMainPlayerScore));
		scoreboardLabels[PLAYER_B][5].setText(Integer.toString(totalArtificialPlayerScore));
		
		currentMatchSetLabel.setText("Current Match Set: " + Integer.toString(currentMatchSet));
		currentSetGameLabel.setText("Current Set Game: " + Integer.toString(currentSetGame));
	}
	
	private static void initChoiceBox( final ChoiceBox<String> chioceBox, final String[] options )
	{
		chioceBox.getItems().addAll(options);
		chioceBox.getSelectionModel().selectFirst();
	}
	
	private void displayInfoMessage( final String message )
	{
		infoErrorMessageLabel.setText(message);
		infoErrorMessageLabel.setTextFill( Paint.valueOf("#0f68a3") );
	}
	private void displayErrorMessage( final String message )
	{
		infoErrorMessageLabel.setText(message);
		infoErrorMessageLabel.setTextFill( Paint.valueOf("#a40e0e") );
	}
	private void clearInfoErrorMessage()
	{
		infoErrorMessageLabel.setText("");
	}
}
