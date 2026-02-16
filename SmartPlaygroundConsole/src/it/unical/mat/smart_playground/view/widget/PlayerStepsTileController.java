/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author Agostino
 *
 */
public class PlayerStepsTileController extends PlayerMotionStatusTileController
{
	@FXML private Label playerTotalStepsLabel;
	private int currentTotalSteps = 0;
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.PLAYER_STATUS || playerTotalStepsLabel == null )
			return;
		
		final PlayerStatus playerStatus = status.getPlayerStatus();
		final int playerTotalSteps = playerStatus.getTotalSteps();
		
		if ( playerTotalStepsLabel != null && currentTotalSteps != playerTotalSteps )
		{
			playerTotalStepsLabel.setText(Integer.toString(playerTotalSteps));
			currentTotalSteps = playerTotalSteps;
		}
	}
}
