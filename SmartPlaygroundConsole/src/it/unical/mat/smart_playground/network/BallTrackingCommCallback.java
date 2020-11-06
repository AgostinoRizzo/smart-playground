/**
 * 
 */
package it.unical.mat.smart_playground.network;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public interface BallTrackingCommCallback
{
	public void onBallStatusChanged( final SmartBallStatus newBallStatus );
}
