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
	public void onSmartBallStatus();
	public void onSmartFieldStatus();
	public void onSmartRacketStatus( final SmartRacketType smartRacket, final SmartRacketStatus newStatus );
}
