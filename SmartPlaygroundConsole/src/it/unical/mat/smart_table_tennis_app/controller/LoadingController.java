/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * @author Agostino
 *
 */
public class LoadingController implements ViewController
{
	@FXML
	private Label statusLabel;
	
	private MainApp mainApp=null;
	private Node content=null;

	@Override
	public void init( final MainApp app, final Node content )
	{
		this.mainApp=app;
		this.content = content;
	}

	@Override
	public void fin()
	{
		
	}
	
	@Override
	public MainApp getMainApp()
	{
		return mainApp;
	}
	
	@Override
	public Node getContent()
	{
		return content;
	}
}
