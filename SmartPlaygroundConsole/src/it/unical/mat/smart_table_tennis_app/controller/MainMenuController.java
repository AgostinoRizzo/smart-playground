/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.controller;

import javafx.scene.Node;

/**
 * @author Agostino
 *
 */
public class MainMenuController implements ViewController
{
	private MainApp mainApp=null;
	private Node content=null;
	
	@Override
	public void init( final MainApp app, final Node content )
	{
		this.mainApp = app;
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
