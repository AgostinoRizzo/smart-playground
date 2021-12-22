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
public class PlayerCaloriesTileController extends PlayerMotionStatusTileController
{
	@FXML private Label playerCaloriesLabel;
	private double currentCalories = 0;
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.PLAYER_STATUS || playerCaloriesLabel == null )
			return;
		
		final PlayerStatus playerStatus = status.getPlayerStatus();
		final double playerBurnedCalories = playerStatus.getBurnedCalories();
		
		if ( playerCaloriesLabel != null && currentCalories != playerBurnedCalories )
		{
			playerCaloriesLabel.setText(decimalFormat.format(playerBurnedCalories) + " cal");
			currentCalories = playerBurnedCalories;
		}
	}
}
