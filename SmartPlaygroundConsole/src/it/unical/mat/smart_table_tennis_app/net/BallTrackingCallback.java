/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public interface BallTrackingCallback
{
	public void onBallStatusChanged( final SmartBallStatus newBallStatus );
}
