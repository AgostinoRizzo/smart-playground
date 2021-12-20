/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlayerOrientationTileController implements LayoutController, PlaygroundStatusObserver
{
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	
	@FXML private ImageView playerOrientationImage;
	
	public PlayerOrientationTileController()
	{
		onInitialize(null);
	}
	
	@Override
	public void onInitialize(Window win)
	{
		PLAYGROUND_STATUS.addObserver(this, PlaygroundStatusTopic.PLAYER_STATUS);
		onPlaygroundStatusChanged(PLAYGROUND_STATUS, PlaygroundStatusTopic.PLAYER_STATUS);
	}

	@Override
	public void onFinalize()
	{
		PLAYGROUND_STATUS.removeObserver(this, PlaygroundStatusTopic.PLAYER_STATUS);
	}
	
	@Override
	public void onPlaygroundStatusChanged(PlaygroundStatus status, PlaygroundStatusTopic topic)
	{
		if ( topic != PlaygroundStatusTopic.PLAYER_STATUS || playerOrientationImage == null )
			return;
		
		final PlayerStatus playerStatus = status.getPlayerStatus();
		OrientationTileUtil.setBallImageViewOrientation(playerOrientationImage, playerStatus.isKnown(), playerStatus.getRelativeOrientation() - 90f);
	}
}
