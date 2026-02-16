/**
 * 
 */
package it.unical.mat.smart_playground.network;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public interface BallTrackingMotionCtrlCommCallback
{
	public void onBallStatusChanged( final SmartBallStatus newBallStatus );
	public void onPlayerStatusChanged( final PlayerStatus newPlayerStatus );
	public void onNewGolfHoleLocation( final float left, final float top );
}
