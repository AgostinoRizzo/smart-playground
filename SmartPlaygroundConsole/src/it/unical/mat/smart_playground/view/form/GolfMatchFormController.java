/**
 * 
 */
package it.unical.mat.smart_playground.view.form;

import com.google.gson.JsonObject;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.controller.playground.minimap.PlaygroundMinimapController;
import it.unical.mat.smart_playground.model.environment.EnvironmentSoundPlayer;
import it.unical.mat.smart_playground.model.environment.EnvironmentSoundType;
import it.unical.mat.smart_playground.network.GameEventCallback;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommProvider;
import it.unical.mat.smart_playground.view.Dialogs;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Agostino
 *
 */
public class GolfMatchFormController implements LayoutController, GameEventCallback
{
	private static final EnvironmentSoundPlayer ENVIRONMENT_SOUND_PLAYER = EnvironmentSoundPlayer.getInstance();
	
	@FXML private Label gameStatusLabel;
	@FXML private Label holeLocationStatusLabel;
	@FXML private Label clubSettingLabel;
	@FXML private Label clubSwingDescriptionLabel;
	@FXML private ProgressBar clubSwingForceProgressBar;
	@FXML private Button playMatchButton;
	@FXML private Label infoErrorMessageLabel;
	
	private PlaygroundMinimapController playgroundMinimapController = null;
	private boolean golfHoleLocated = false;
	private double holeLeft, holeTop;
	
	private AnimationTimer golfSwingAnimationTimer = null;
	
	@Override
	public void onInitialize(Window win)
	{
		PlaygroundBaseCommProvider.getInstance().setGameEventCallback(this);
	}

	@Override
	public void onFinalize()
	{
		System.out.println("Sending game finalization...");
		
		final JsonObject gameReset = new JsonObject();
		gameReset.addProperty("type", "game_reset");
		
		final PlaygroundBaseCommProvider playgrandBaseCommProvider = PlaygroundBaseCommProvider.getInstance();
		playgrandBaseCommProvider.sendCommand(gameReset);
		playgrandBaseCommProvider.removeGameEventCallback();
	}
	
	@FXML private void playMatch()
	{
		if ( !golfHoleLocated )
		{
			Dialogs.showInfo("Golf hole", "Golf hole location not set.");
			return;
		}
		
		final JsonObject gameSettings = new JsonObject();
		final JsonObject holeConfig = new JsonObject();
		
		gameSettings.addProperty("type", "game_init");
		gameSettings.addProperty("gameType", "golf");
		
		holeConfig.addProperty("left", holeLeft);
		holeConfig.addProperty("top", holeTop);
		
		gameSettings.add("hole", holeConfig);
		
		playMatchButton.setText("Sending Match Request...");
		playMatchButton.setDisable(true);
		playgroundMinimapController.setCanLocateHole(false);
		
		PlaygroundBaseCommProvider.getInstance().sendCommand(gameSettings);
	}
	
	public void onGolfHoleLocated( final double percX, final double percY, final PlaygroundMinimapController playgroundMinimapController )
	{
		holeLocationStatusLabel.setText("Located");
		holeLocationStatusLabel.setTextFill(Color.GREEN);
		holeLeft = percY;
		holeTop = 1.0 - percX;
		golfHoleLocated = true;
		this.playgroundMinimapController = playgroundMinimapController;
		ENVIRONMENT_SOUND_PLAYER.playSound(EnvironmentSoundType.CONFIRM);
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
				__onGameEvent(gameEvent);
			}
		});
	}
	
	@Override
	public void onToolSettingChangedEvent(JsonObject toolSettingChangedEvent)
	{
		if ( !toolSettingChangedEvent.get("tool").getAsString().equals("club") )
			return;
		
		final String newSetting = toolSettingChangedEvent.get("setting").getAsString();
		
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				if ( newSetting.equals("attempt") )     clubSettingLabel.setText("Attempt");
				else if ( newSetting.equals("stroke") ) clubSettingLabel.setText("Stroke");
			}	
		});
		
		EnvironmentSoundPlayer.getInstance().playSound(EnvironmentSoundType.SUB_CHOOSE);
	}
	
	@Override
	public void onToolActionEvent(JsonObject toolActionEvent)
	{
		if ( !toolActionEvent.get("tool").getAsString().equals("club") )
			return;
		
		final String actionType = toolActionEvent.get("action_type").getAsString();
		
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				if ( actionType.equals("light_swing") )       displaySwingType("Light Swing", .2);
				else if ( actionType.equals("medium_swing") ) displaySwingType("Medium Swing", .5);
				else if ( actionType.equals("big_swing") )    displaySwingType("Big Swing", 1);
			}	
		});
	}
	
	private void __onGameEvent( final JsonObject gameEvent )
	{
		final String subType = gameEvent.get("subType").getAsString();
		
		// game initialization approved
		if ( subType.equals("game_init_approved") )
		{
			displayInfoMessage("Waiting for match initialization...", false);
			playMatchButton.setVisible(false);
			infoErrorMessageLabel.setVisible(true);
		}
		
		// game initialization denied
		else if ( subType.equals("game_init_denied") )
		{
			displayErrorDialog("Match initialization denied. An existing match is already playing.");
			playMatchButton.setText("Play Match");
			playMatchButton.setDisable(false);
			infoErrorMessageLabel.setVisible(false);
			playMatchButton.setVisible(true);
		}
		
		// ball location is [not] ready for first stroke
		else if ( subType.equals("ball_location_ready") )
		{
			final boolean isReady = gameEvent.get("isReady").getAsBoolean();
			if ( !isReady )
				displayErrorMessage("Smart ball not in place.", true);
		}
		
		// match status information
		else if ( subType.equals("match_status") )
		{
			final int     currentStroke = gameEvent.get("currentStroke").getAsInt();
			final boolean terminated    = gameEvent.get("terminated").getAsBoolean();
			
			gameStatusLabel.setText("Smart Golf (Stroke " + currentStroke + ")");
			
			if ( terminated )
			{
				final String gameOutcome = gameEvent.get("gameOutcome").getAsString();
				if ( gameOutcome.equals("collision") )
				{
					ENVIRONMENT_SOUND_PLAYER.playSound(EnvironmentSoundType.GAME_LOSE);
					displayInfoMessage("Game terminated. Smart ball collided!", true);
				}
				else if ( gameOutcome.equals("in_hole") )
				{
					ENVIRONMENT_SOUND_PLAYER.onGolfGameHole();
					displayInfoMessage("Hole in " + currentStroke + (currentStroke == 1 ? " stroke" : " strokes") + ", you win!", true);
				}
			}
			else
				ENVIRONMENT_SOUND_PLAYER.playSound(EnvironmentSoundType.TENNIS_FINISH);
		}
		
		// match turn information
		else if ( subType.equals("match_turn") )
		{
			final String turn = gameEvent.get("turn").getAsString();
			if ( turn.equals("club_swing") )
			{
				displayInfoMessage("Waiting for a club stroke...", false);
				ENVIRONMENT_SOUND_PLAYER.playSound(EnvironmentSoundType.GAME_READY);
			}
		}
		
		// match action information (stroke)
		else if ( subType.equals("match_action") )
		{
			final String action = gameEvent.get("action").getAsString();
			if ( action.equals("stroke") )
				clearInfoErrorMessage();
		}
	}
	
	private void displaySwingType( final String description, final double force )
	{
		clubSwingDescriptionLabel.setText("");
		clubSwingForceProgressBar.setProgress(0);
		
		if ( golfSwingAnimationTimer != null )
		{
			golfSwingAnimationTimer.stop();
			golfSwingAnimationTimer = null;
		}
		
		golfSwingAnimationTimer = new AnimationTimer()
		{
			private long canClose = -1;
			private boolean isClosing = false;
			@Override
			public void handle(long now)
			{
				if ( canClose >= 0 )
				{
					if ( now - canClose < 1500000000 ) return;
					else { isClosing = true; canClose = -1; }
				}
				
				final double currentProgress = clubSwingForceProgressBar.getProgress();
				if ( isClosing )
				{
					if ( currentProgress > 0 ) clubSwingForceProgressBar.setProgress(normalizeProgressBarValue(currentProgress - .05));
					else { clubSwingDescriptionLabel.setText(""); golfSwingAnimationTimer.stop(); golfSwingAnimationTimer = null; }
				}
				else
				{
					if ( currentProgress < force ) clubSwingForceProgressBar.setProgress(normalizeProgressBarValue(currentProgress + .05));
					else { clubSwingDescriptionLabel.setText(description); canClose = now; }
				}
				
			}
		};
		golfSwingAnimationTimer.start();
	}
	private void displayInfoMessage( final String message, final boolean displayAlert )
	{
		infoErrorMessageLabel.setText(message);
		infoErrorMessageLabel.setTextFill( Paint.valueOf("#0f68a3") );
		
		if ( displayAlert )
			displayDialog(AlertType.INFORMATION, message);
	}
	private void displayErrorMessage( final String message, final boolean displayAlert )
	{
		infoErrorMessageLabel.setText(message);
		infoErrorMessageLabel.setTextFill( Paint.valueOf("#a40e0e") );
		
		if ( displayAlert )
			displayDialog(AlertType.WARNING, message);
	}
	private void displayDialog( final AlertType type, final String message )
	{
		final Alert messageAlert = new Alert(type, message);
		messageAlert.showAndWait();
	}
	private void displayErrorDialog( final String message )
	{
		displayDialog(AlertType.ERROR, message);
	}
	private void clearInfoErrorMessage()
	{
		infoErrorMessageLabel.setText("");
	}
	
	private static double normalizeProgressBarValue( final double value )
	{
		if ( value < 0 ) return 0;
		if ( value > 1 ) return 1;
		return value;
	}
}
