/**
 * 
 */
package it.unical.mat.smart_playground.view.popup;

import org.controlsfx.control.PopOver;

import it.unical.mat.smart_playground.controller.ViewController;
import it.unical.mat.smart_playground.view.Strings;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author Agostino
 *
 */
public class EcosystemStatusPopup extends PopOver implements Popup
{
	//private final ViewController parent;
	private final Node owner;
	
	public EcosystemStatusPopup( final ViewController parent, final Node owner, final Node content )
	{
		//this.parent=parent;
		this.owner=owner;
		
		setTitle( Strings.ECOSYSTEM_STATUS_POPUP_TITLE);
		setArrowLocation( ArrowLocation.TOP_LEFT );
		setHeaderAlwaysVisible(true);
		setContentNode(content);
	}
	
	@Override
	public void showNewContent(PopupContent c)
	{
		showCurrentContent();
	}

	@Override
	public void showCurrentContent()
	{
		show(owner);
	}

	@Override
	public void clearContent()
	{
		
	}

	@Override
	public void hideContent()
	{
		hide( Duration.ZERO );
	}

}
