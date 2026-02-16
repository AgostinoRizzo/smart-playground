/**
 * 
 */
package it.unical.mat.smart_playground.controller;

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
	
	private MainApplication mainApp=null;
	private Node content=null;

	@Override
	public void init( final MainApplication app, final Node content )
	{
		this.mainApp=app;
		this.content = content;
	}

	@Override
	public void fin()
	{
		
	}
	
	@Override
	public MainApplication getMainApp()
	{
		return mainApp;
	}
	
	@Override
	public Node getContent()
	{
		return content;
	}
	
	@Override
	public void updateAnimation(long now)
	{}
}
