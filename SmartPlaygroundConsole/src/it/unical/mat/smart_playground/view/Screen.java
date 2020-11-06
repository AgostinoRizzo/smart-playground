package it.unical.mat.smart_playground.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Screen extends Application
{	
	protected Parent root=null;
	protected Stage stage=null;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		root  = FXMLLoader.load( getClass().getResource( getFXMLFilename() ) );
		stage = primaryStage;
		
		stage.setTitle( getWindowsTitle() );
		stage.setScene( new Scene(root) );
		stage.setMaximized(true);
		
		stage.show();
		root.requestFocus();
	}
	
	protected abstract String getFXMLFilename();
	protected abstract String getWindowsTitle();
}
