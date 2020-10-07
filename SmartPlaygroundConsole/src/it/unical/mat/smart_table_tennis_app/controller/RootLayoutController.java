/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.controller;

import java.io.IOException;

import it.unical.mat.smart_table_tennis_app.view.popup.ActionPopup;
import it.unical.mat.smart_table_tennis_app.view.popup.EcosystemStatusPopup;
import it.unical.mat.smart_table_tennis_app.view.popup.StatusPopup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * @author Agostino
 *
 */
public class RootLayoutController implements ViewController
{	
	@FXML
	private Button statusButton;
	@FXML
	private Button ecosystemButton;
	@FXML
	private Button actionButton;
	@FXML
	private Slider zoomSlider;
	@FXML
	private ImageView zoomInImageView;
	@FXML
	private ImageView zoomOutImageView;
	
	private MainApp mainApp=null;
	private Node content=null;
	
	//private EcosystemStatusController ecosystemStatusController=null;
	
	private StatusPopup statusPopup=null;
	private ActionPopup actionPopup=null;
	//private EcosystemStatusPopup ecosystemStatusPopup=null;
	
	
	@Override
	public void init( final MainApp app, final Node content )
	{
		this.mainApp = app;
		this.content = content;
		
		statusPopup = new StatusPopup( this, statusButton );
		actionPopup = new ActionPopup( this, actionButton );
		
		zoomSlider.valueProperty().addListener( new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val)
			{
				handleContentZoom( new_val.doubleValue()/100.0 );
			}
		});
		//initEcosystemStatusView();
		//ecosystemStatusPopup = new EcosystemStatusPopup
		//		( this, ecosystemButton, ecosystemStatusController.getContent() );
	}
	
	@Override
	public void fin()
	{
		statusPopup.hideContent();
		actionPopup.hideContent();
		//ecosystemStatusPopup.hideContent();
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
	
	public StatusPopup getStatusPopup()
	{
		return statusPopup;
	}
	
	public ActionPopup getActionPopup()
	{
		return actionPopup;
	}
	
	public EcosystemStatusPopup getEcosystemStatusPopup()
	{
		return null;//ecosystemStatusPopup;
	}
	
	/*public EcosystemStatusController getEcosystemStatusController()
	{
		return ecosystemStatusController;
	}*/
	/*
	private void initEcosystemStatusView()
	{
		// load ecosystem status view
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApp.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.ECOSYSTEM_STATUS_FXML_FILENAME) );
				
		try
		{
			final AnchorPane ecosystemStatusView = (AnchorPane) loader.load();
			
			// give the controller access to the main app.
			ecosystemStatusController = loader.getController();
			ecosystemStatusController.init(mainApp, ecosystemStatusView);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	*/
	
	@FXML
	private void handleShowCurrentStatus()
	{
		statusPopup.showCurrentContent();
	}
	
	@FXML
	private void handleShowCurrentAction()
	{
		actionPopup.showCurrentContent();
	}
	
	@FXML
	private void handleShowCurrentEcosystemStatus()
	{
		//ecosystemStatusPopup.showCurrentContent();
	}
	
	@FXML
	private void handleContentZoomIn()
	{
		final double max = zoomSlider.getMax();
		double value = zoomSlider.getValue();
		
		value += zoomSlider.getMajorTickUnit();
		if ( value > max )
			value = max;
		
		zoomSlider.setValue( value );
	}
	
	@FXML
	private void handleContentZoomOut()
	{
		final double min = zoomSlider.getMin();
		double value = zoomSlider.getValue();
		
		value -= zoomSlider.getMajorTickUnit();
		if ( value < min )
			value = min;
		
		zoomSlider.setValue( value );
	}
	
	private void handleContentZoom( final double scale_factor )
	{
		if ( mainApp != null )
			mainApp.onEcosystemStatusZoom(scale_factor);
	}
}
