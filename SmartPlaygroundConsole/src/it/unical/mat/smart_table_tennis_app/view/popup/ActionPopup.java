/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view.popup;

import org.controlsfx.control.PopOver;

import it.unical.mat.smart_table_tennis_app.controller.ViewController;
import it.unical.mat.smart_table_tennis_app.view.ActionType;
import it.unical.mat.smart_table_tennis_app.view.Resources;
import it.unical.mat.smart_table_tennis_app.view.Strings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Agostino
 *
 */
public class ActionPopup extends PopOver implements Popup
{
	private final ImageView actionImageView = new ImageView();
	private final ViewController parent;
	private final Node owner;
	
	private ActionType currentActionType = null;
	
	public ActionPopup( final ViewController parent, final Node owner )
	{
		this.parent=parent;
		this.owner=owner;
		
		setTitle( Strings.ACTION_POPUP_TITLE);
		setArrowLocation( ArrowLocation.BOTTOM_RIGHT );
		setHeaderAlwaysVisible(true);
		
		clearContent();
	}
	
	@Override
	public void showNewContent( PopupContent c )
	{
		switch ( ((ActionPopupContent) c).getActionType() )
		{
		case USER_ACK: 
			actionImageView.setImage( Resources.USER_ACK_IMAGE );
			
			final HBox hBox = new HBox(actionImageView);
			hBox.setAlignment(Pos.CENTER);
			
			setContentNode(hBox);
			currentActionType = ActionType.USER_ACK;
			break;
			
		default: 
			clearContent();
			currentActionType = null;
			break;
		}
		showCurrentContent();
	}

	@Override
	public void showCurrentContent()
	{
		// TODO: if ( is not empty )
		parent.getMainApp().getCurrentContent().setOpacity(0.3);
		show(owner);
	}

	@Override
	public void clearContent()
	{
		final Text noContentText = new Text( Strings.NO_CONTENT );
		final HBox hBox = new HBox(noContentText);
		
		HBox.setMargin( noContentText, Popup.POPUP_CONTENT_MARGIN );
		hBox.setAlignment(Pos.CENTER);
		
		setContentNode(hBox);
	}

	@Override
	public void hideContent()
	{
		hide( Duration.ZERO );
		parent.getMainApp().getCurrentContent().setOpacity(1.0);
	}
	
	public ActionType getCurrentActionType()
	{
		return currentActionType;
	}
	
}
