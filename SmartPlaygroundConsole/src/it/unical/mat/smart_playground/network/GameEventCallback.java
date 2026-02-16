/**
 * 
 */
package it.unical.mat.smart_playground.network;

import com.google.gson.JsonObject;

/**
 * @author Agostino
 *
 */
public interface GameEventCallback
{
	public void onGameEvent( final JsonObject event );
	public void onToolSettingChangedEvent( final JsonObject event );
	public void onToolActionEvent( final JsonObject event );
}
