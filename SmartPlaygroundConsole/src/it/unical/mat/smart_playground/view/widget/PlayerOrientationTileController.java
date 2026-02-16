/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public final class PlayerOrientationTileController extends PlayerMotionStatusTileController
{
	@FXML private ImageView playerOrientationImage;
	
	private boolean isCurrentOrientationKnown = false;
	private float currentRelativeOrientation = 0f;
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.PLAYER_STATUS || playerOrientationImage == null )
			return;
		
		final PlayerStatus playerStatus = status.getPlayerStatus();
		
		final boolean isOrientationKnown = playerStatus.isOrientationKnown();
		final float playerRelativeOrientation = playerStatus.getRelativeOrientation();
		
		if ( playerOrientationImage != null && isCurrentOrientationKnown != isOrientationKnown || playerRelativeOrientation != currentRelativeOrientation )
		{
			OrientationTileUtil.setBallImageViewOrientation(playerOrientationImage, isOrientationKnown, playerRelativeOrientation - 90f);
			isCurrentOrientationKnown = isOrientationKnown;
			currentRelativeOrientation = playerRelativeOrientation;
		}
	}
}
