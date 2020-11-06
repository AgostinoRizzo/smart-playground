/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * @author Agostino
 *
 */
public interface ViewController
{
	public void init( final MainApplication app, final Node content );
	public void fin();
	public MainApplication getMainApp();
	public Node getContent();
	
	public static void showAlert( final AlertType type,
								  final String title,
								  final String header,
								  final String content )
	{
		final Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		alert.showAndWait();
	}
	public static String showYesNoAlert( final String title,
										 final String header,
										 final String content )
	{
		final Alert alert = new Alert( AlertType.CONFIRMATION );
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		final ButtonType yesButton = new ButtonType("yes");
		final ButtonType noButton  = new ButtonType("no");
		alert.getButtonTypes().setAll( yesButton, noButton );
		
		final Optional< ButtonType > result = alert.showAndWait();
		if ( result.get() == yesButton )
			return "yes";
		return "no";
	}
}
