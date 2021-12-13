/**
 * 
 */
package it.unical.mat.smart_playground.network;

import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;

/**
 * @author Agostino
 *
 */
public interface PlaygroundBaseCommCallback
{
	public void onAckEvent();
	public void onSmartGamePlatformStatus();
	public void onSmartBallStatus();
	public void onMotionControllerStatus();
	public void onSmartRacketStatus( final SmartRacketType smartRacket, final SmartRacketStatus newStatus );
}
