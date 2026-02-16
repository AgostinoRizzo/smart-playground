/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.io.IOException;

import it.unical.mat.smart_playground.view.ViewConfigs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

/**
 * @author Agostino
 *
 */
public class MainLayoutController implements ViewController
{
	private EcosystemStatusController ecosystemStatusController=null;
	private MainMenuController mainMenuController=null;
	
	private MainApplication mainApp=null;
	private Node content=null;
	
	@Override
	public void init( final MainApplication app, final Node content )
	{
		this.mainApp = app;
		this.content = content;
		
		initEcosystemStatusView();
		initMainMenuView();
		/*
		if ( content instanceof BorderPane )
		{
			final BorderPane paneContent = (BorderPane) content;
			
			paneContent.setLeft( ecosystemStatusController.getContent() );
			BorderPane.setAlignment( ecosystemStatusController.getContent(), Pos.CENTER_LEFT );
			//paneContent.setRight( mainMenuController.getContent() );
		}
		*/
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
	
	public EcosystemStatusController getEcosystemStatusController()
	{
		return ecosystemStatusController;
	}
	
	@Override
	public void updateAnimation(long now)
	{}
	
	private void initEcosystemStatusView()
	{
		// load ecosystem status view
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApplication.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.ECOSYSTEM_STATUS_FXML_FILENAME) );
				
		try
		{
			final ScrollPane ecosystemStatusView = (ScrollPane) loader.load();
			
			/*
			final Scale scale = new Scale();
			scale.setPivotX(300.0);
			scale.setPivotY(0.0);
			scale.setX(0.8);
			scale.setY(0.8);
			
			ecosystemStatusView.getTransforms().add(scale);
			*/
			
			//ecosystemStatusView.setScaleX(0.8);
			//ecosystemStatusView.setScaleY(0.8);
			
			// give the controller access to the main app.
			ecosystemStatusController = loader.getController();
			ecosystemStatusController.init(mainApp, ecosystemStatusView);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void initMainMenuView()
	{
		// load main menu view
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApplication.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.MAIN_MENU_FXML_FILENAME) );
				
		try
		{
			final AnchorPane mainMenuView = (AnchorPane) loader.load();
			
			// give the controller access to the main app.
			mainMenuController = loader.getController();
			mainMenuController.init(mainApp, mainMenuView);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
