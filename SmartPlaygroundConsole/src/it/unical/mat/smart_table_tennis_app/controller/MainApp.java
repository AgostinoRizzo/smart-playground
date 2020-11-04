package it.unical.mat.smart_table_tennis_app.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_table_tennis_app.net.EcosystemEventProvider;
import it.unical.mat.smart_table_tennis_app.net.EcosystemServiceCallback;
import it.unical.mat.smart_table_tennis_app.net.NetDiscoveryCallback;
import it.unical.mat.smart_table_tennis_app.net.NetDiscoveryClient;
import it.unical.mat.smart_table_tennis_app.net.NetService;
import it.unical.mat.smart_table_tennis_app.view.ActionType;
import it.unical.mat.smart_table_tennis_app.view.Strings;
import it.unical.mat.smart_table_tennis_app.view.ViewConfigs;
import it.unical.mat.smart_table_tennis_app.view.popup.ActionPopup;
import it.unical.mat.smart_table_tennis_app.view.popup.ActionPopupContent;
import it.unical.mat.smart_table_tennis_app.view.popup.StatusPopupContent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application implements NetDiscoveryCallback, EcosystemServiceCallback
{
	private Stage primaryStage=null;
	
	private BorderPane rootLayout=null;
	private BorderPane mainLayout=null;
	
	private RootLayoutController rootLayoutController=null;
	private LoadingController loadingLayoutController=null;
	private MainLayoutController mainLayoutController=null;
	
	private ViewController currentController=null;
	
	private NetDiscoveryClient netDiscoveryClient=null;
	private EcosystemEventProvider ecosystemEventProvider=null;
	
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	public MainApp()
	{
		this.netDiscoveryClient = new NetDiscoveryClient(this);
	}
	
	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage=primaryStage;
		
		primaryStage.setTitle( Strings.APP_TITLE );
		primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>()
			{	@Override
				public void handle(WindowEvent event)
				{ fin(); System.exit(0); }
			});
		
		iniRootLayout();
		initMainLayout();
		
		showLoadingView();
		
		currentController = loadingLayoutController;
		
		new Timer().schedule( new TimerTask()
			{ @Override public void run() {	startDiscoveryClient();	} }, 500 ); //{ @Override public void run() {	startDiscoveryClient();	} }, 3000 );

		//EcosystemEventProvider.testOnEcosystemStatus(this);
		//rootLayoutController.showNewAction( Action.USER_ACK );
		
		// TODO: remove to re-activate initialization (check git repository).
		onNetServiceDiscovery( null );
		new Thread()
		{
			public void run() {
				try
				{
					sleep(3000);
					onAckEvent();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public Node getCurrentContent()
	{
		return currentController.getContent();
	}
	
	@Override
	public void onNetServiceDiscovery( final NetService service )
	{
		// TODO: uncomment (check git repository).
		/*if ( service == null )
		{
			rootLayoutController.getStatusPopup().clearContent();
			/*if ( ViewController.showYesNoAlert
					( "Ecosystem discovery error", "No ecosystem found over the network", "Do you want to retry?" ).equals( "yes" ) )
			{
				netDiscoveryClient = new NetDiscoveryClient(this);
				netDiscoveryClient.start();
			
			}
			else*/
				/*closeAppWithAlert( AlertType.ERROR, 
								   "Ecosystem discovery error", 
								   null, 
								   "No ecosystem found over the network" );
		}
		else*/
		{
			// TODO: uncomment everything to re-activate initialization.
			
			//final byte[] service_code = service.getCode();
			//if ( service_code.length == 1 && 
			//		service_code[0] == ControllerConfigs.ECOSYSTEM_SERVICE_CODE )
			{
				//final InetAddress baseStationAddress = service.getServerAddress();
				//try
				{
					//ecosystemEventProvider = new EcosystemEventProvider( baseStationAddress, this );
				
				
					Platform.runLater( new Runnable()
					{
						@Override
						public void run()
						{
							//rootLayoutController.getStatusPopup().showNewContent
							//	( new StatusPopupContent( "Ecosystem base station found at: " + baseStationAddress.getHostAddress() ) );						
							rootLayoutController.getActionPopup().showNewContent( new ActionPopupContent(ActionType.USER_ACK) );
						}
					});
				} /*catch (IOException e)
				{
					closeAppWithAlert( AlertType.ERROR, 
									   "Ecosystem connection error", 
									   "Ecosystem unreachable over " + baseStationAddress, 
									   e.getMessage() );
					e.printStackTrace();
				}*/
			}
		}
	}
	
	@Override
	public void onAckEvent()
	{
		if ( rootLayoutController.getActionPopup().getCurrentActionType() == ActionType.USER_ACK )
			Platform.runLater( new Runnable()
			{
				@Override
				public void run()
				{
					final ActionPopup actionPopup = rootLayoutController.getActionPopup();
					actionPopup.clearContent();
					actionPopup.hideContent();
					
					// show the scene containing the main layout
					final ScrollPane center = new ScrollPane(mainLayoutController.getEcosystemStatusController().getContent());
					//center.setStyle("-fx-background-color: white;");
					//center.applyCss();
					rootLayout.setCenter(center);
					
					//center.setContent(mainLayoutController.getEcosystemStatusController().getContent());
				}
			});
	}
	
	@Override
	public void onSmartGamePlatformStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				mainLayoutController.getEcosystemStatusController().onGamePlatformStatus();
			}
		});
	}
	
	@Override
	public void onSmartBallStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				mainLayoutController.getEcosystemStatusController().onSmartBallStatus();
			}
		});
	}
	
	@Override
	public void onMotionControllerStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				mainLayoutController.getEcosystemStatusController().onMotionControllerStatus();
			}
		});
	}
	
	@Override
	public void onSmartPoleStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				mainLayoutController.getEcosystemStatusController().onSmartPoleStatus();
			}
		});
	}
	
	@Override
	public void onSmartRacketStatus(SmartRacketType smartRacket)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				mainLayoutController.getEcosystemStatusController().onSmartRacketStatus(smartRacket);
			}
		});		
	}
	
	public void onEcosystemStatusZoom( final double scale_factor )
	{
		mainLayoutController.getEcosystemStatusController().onScale(scale_factor);
	}
	
	private void fin()
	{
		rootLayoutController.fin();
		
		if ( ecosystemEventProvider != null )
			ecosystemEventProvider.fin();
	}
	
	private void iniRootLayout()
	{
		// load root layout from fxml file
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApp.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.ROOT_LAYOUT_FXML_FILENAME) );
		
		try
		{
			// load the root layout
			
			rootLayout = (BorderPane) loader.load();
			rootLayoutController = loader.getController();
			
			rootLayoutController.init(this, rootLayout);
			
			// show the scene containing the layout
			
			final Scene scene = new Scene(rootLayout);
			
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			
			primaryStage.show();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void initMainLayout()
	{
		// load main layout from fxml file
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApp.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.MAIN_LAYOUT_FXML_FILENAME) );
		
		try
		{
			// load the main layout
			
			mainLayout = (BorderPane) loader.load();
			mainLayoutController = loader.getController();
			
			mainLayoutController.init(this, mainLayout);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void showLoadingView()
	{
		// load loading view
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation
			( MainApp.class.getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.LOADING_FXML_FILENAME) );
		
		try
		{
			final AnchorPane loadingView = (AnchorPane) loader.load();
			
			// set the loading view into the center of root layout.
			rootLayout.setCenter( loadingView );
			
			// give the controller access to the main app.
			loadingLayoutController = loader.getController();
			loadingLayoutController.init(this, loadingView);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	private void startDiscoveryClient()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				rootLayoutController.getStatusPopup().showNewContent( new StatusPopupContent(Strings.ECOSYSTEM_DISCOVERING_NOTIFICATION) );
				netDiscoveryClient.start(); 
			}
		});
	}
	
	private void closeAppWithAlert( final AlertType type,
								   final String title,
								   final String header,
								   final String content )
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				ViewController.showAlert( type, title, header, content );
				Platform.exit();
				System.exit(0);
			}
		});
	}
}
