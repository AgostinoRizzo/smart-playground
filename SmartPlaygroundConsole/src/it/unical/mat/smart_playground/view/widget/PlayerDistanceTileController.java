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
public class PlayerDistanceTileController extends PlayerMotionStatusTileController
{
	@FXML private Label playerTotalDistanceLabel;
	private float currentTotalDistance = 0f;
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.PLAYER_STATUS || playerTotalDistanceLabel == null )
			return;
		
		final PlayerStatus playerStatus = status.getPlayerStatus();
		final float playerTotalDistance = playerStatus.getTotalDistance();
		
		if ( playerTotalDistanceLabel != null && currentTotalDistance != playerTotalDistance )
		{
			playerTotalDistanceLabel.setText(decimalFormat.format(playerTotalDistance) + " m");
			currentTotalDistance = playerTotalDistance;
		}
	}
}
