/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Agostino
 *
 */
public abstract class Window
{
	protected final Stage stage = new Stage();
	private LayoutController layoutController = null;
	
	public Window( final String title )
	{
		stage.setTitle(title);
		stage.setOnCloseRequest( new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{ onClose(); }
		});
		
		try 
		{
			final FXMLLoader loader = createLoader( getRootLayoutFxmlFilename() );
			stage.setScene( createScene(loader) ); 
			layoutController = loader.getController();
		}
		catch (IOException e) {}
	}
	
	public void show()
	{
		layoutController.onInitialize(this);
		stage.show();
	}
	
	public void close()
	{
		stage.close();
		onClose();
	}
	
	protected void onClose()
	{
		layoutController.onFinalize();
	}
	
	protected Scene createScene( final FXMLLoader loader ) throws IOException
	{
		return new Scene( loader.load() );
	}
	
	protected abstract String getRootLayoutFxmlFilename();
	
	private static FXMLLoader createLoader( final String fxmlFilename )
	{
		// load root layout from fxml file
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation( MainApplication.class.getResource(ControllerConfigs.VIEW_PATH + fxmlFilename) );
		
		return loader;
	}
}
