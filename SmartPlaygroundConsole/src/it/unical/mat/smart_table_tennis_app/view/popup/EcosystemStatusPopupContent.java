/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view.popup;

import javafx.scene.Node;

/**
 * @author Agostino
 *
 */
public class EcosystemStatusPopupContent implements PopupContent
{
	private final Node contentNode;
	
	public EcosystemStatusPopupContent( final Node contentNode )
	{
		this.contentNode=contentNode;
	}
	public Node getContentNode()
	{
		return contentNode;
	}
}
