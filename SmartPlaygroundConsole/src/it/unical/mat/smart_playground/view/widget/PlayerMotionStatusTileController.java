/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import java.text.DecimalFormat;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusObserver;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatusTopic;

/**
 * @author Agostino
 *
 */
public abstract class PlayerMotionStatusTileController implements LayoutController, PlaygroundStatusObserver
{
	private static final PlaygroundStatus PLAYGROUND_STATUS = PlaygroundStatus.getInstance();
	protected final DecimalFormat decimalFormat = new DecimalFormat();
	
	public PlayerMotionStatusTileController()
	{
		decimalFormat.setMaximumFractionDigits(1);
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
}
