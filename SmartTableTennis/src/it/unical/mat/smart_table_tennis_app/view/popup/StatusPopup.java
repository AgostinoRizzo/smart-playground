/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view.popup;

import org.controlsfx.control.PopOver;

import it.unical.mat.smart_table_tennis_app.controller.ViewController;
import it.unical.mat.smart_table_tennis_app.view.Strings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Agostino
 *
 */
public class StatusPopup extends PopOver implements Popup
{
	private final Text statusText = new Text();
	//private final ViewController parent;
	private final Node owner;
	
	public StatusPopup( final ViewController parent, final Node owner )
	{
		//this.parent=parent;
		this.owner=owner;
		
		final HBox hBox = new HBox(statusText);
		setContentNode(hBox);
		
		HBox.setMargin( statusText, POPUP_CONTENT_MARGIN );
		hBox.setAlignment(Pos.CENTER);
		
		setTitle( Strings.STATUS_POPUP_TITLE );
		setArrowLocation( ArrowLocation.TOP_LEFT );
		setHeaderAlwaysVisible(true);
		setAnimated(true);
		
		clearContent();
	}
	
	@Override
	public void showNewContent( PopupContent c )
	{
		statusText.setText( ((StatusPopupContent) c).getStatusString() );
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
		statusText.setText( Strings.NO_CONTENT );
	}

	@Override
	public void hideContent()
	{
		hide( Duration.ZERO );
	}

}
