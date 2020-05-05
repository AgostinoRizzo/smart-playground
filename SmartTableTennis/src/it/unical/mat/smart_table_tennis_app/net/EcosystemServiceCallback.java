/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartRacketType;

/**
 * @author Agostino
 *
 */
public interface EcosystemServiceCallback
{
	public void onAckEvent();
	public void onSmartGamePlatformStatus();
	public void onSmartBallStatus();
	public void onMotionControllerStatus();
	public void onSmartPoleStatus();
	public void onSmartRacketStatus( final SmartRacketType smartRacket );
}
