/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view.popup;

import it.unical.mat.smart_table_tennis_app.view.ActionType;

/**
 * @author Agostino
 *
 */
public class ActionPopupContent implements PopupContent
{
	private final ActionType actionType;
	
	public ActionPopupContent( final ActionType actionType )
	{
		this.actionType=actionType;
	}
	public ActionType getActionType()
	{
		return actionType;
	}
}
