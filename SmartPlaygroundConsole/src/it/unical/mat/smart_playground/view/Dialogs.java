/**
 * 
 */
package it.unical.mat.smart_playground.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Agostino
 *
 */
public class Dialogs
{
	public static void showInfo( final String title, final String contextText )
	{
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contextText);
		alert.showAndWait();
	}
}
