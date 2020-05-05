/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view.popup;

/**
 * @author Agostino
 *
 */
public class StatusPopupContent implements PopupContent
{
	private final String status_str;
	
	public StatusPopupContent( final String status_str )
	{
		this.status_str=status_str;
	}
	public String getStatusString()
	{
		return status_str;
	}
}
