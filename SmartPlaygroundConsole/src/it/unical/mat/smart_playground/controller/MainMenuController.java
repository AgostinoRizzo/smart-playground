/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import javafx.scene.Node;

/**
 * @author Agostino
 *
 */
public class MainMenuController implements ViewController
{
	private MainApplication mainApp=null;
	private Node content=null;
	
	@Override
	public void init( final MainApplication app, final Node content )
	{
		this.mainApp = app;
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
