/**
 * 
 */
package it.unical.mat.smart_playground.view.popup;

import javafx.geometry.Insets;

/**
 * @author Agostino
 *
 */
public interface Popup
{
	public static final Insets POPUP_CONTENT_MARGIN = new Insets(10, 30, 10, 30);
	
	public void showNewContent( final PopupContent c );
	public void showCurrentContent();
	public void clearContent();
	public void hideContent();
}
