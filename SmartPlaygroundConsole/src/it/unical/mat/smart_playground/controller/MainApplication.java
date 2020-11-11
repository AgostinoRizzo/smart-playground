package it.unical.mat.smart_playground.controller;

import java.io.IOException;

import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.WindStatus;
import it.unical.mat.smart_playground.network.NetDiscoveryCallback;
import it.unical.mat.smart_playground.network.NetService;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommCallback;
import it.unical.mat.smart_playground.view.ActionType;
import it.unical.mat.smart_playground.view.Strings;
import it.unical.mat.smart_playground.view.ViewConfigs;
import it.unical.mat.smart_playground.view.animation.MinimapWindLinesAnimator;
import it.unical.mat.smart_playground.view.animation.WindFlagAnimator;
import it.unical.mat.smart_playground.view.animation.WindSpeedAnimator;
import it.unical.mat.smart_playground.view.popup.ActionPopup;
import it.unical.mat.smart_playground.view.popup.ActionPopupContent;
import it.unical.mat.smart_playground.view.popup.StatusPopupContent;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApplication extends Application implements NetDiscoveryCallback, PlaygroundBaseCommCallback
{
	private static final ApplicationManager APPLICATION_MANAGER = ApplicationManager.getInstance();
	
	private Stage primaryStage=null;
	
	private BorderPane rootLayout=null;
	private BorderPane mainLayout=null;
	
	private RootLayoutController rootLayoutController=null;
	private LoadingController loadingLayoutController=null;
	private MainLayoutController mainLayoutController=null;
	
	private ViewController currentController=null;
	
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	public MainApplication()
	{}
	
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
		
		APPLICATION_MANAGER.initialize();
		APPLICATION_MANAGER.addNetworkDiscoveryCallback(this);
		APPLICATION_MANAGER.addPlaygroundBaseCommCallbacks(this);
		// TODO (uncomment): APPLICATION_MANAGER.initialize();

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
					Platform.runLater( new Runnable()
					{
						@Override
						public void run()
						{
							onAckEvent();
						}
					});
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		
		new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{ mainLoopAnimationUpdate(now); }
		}
		.start();
	}
	
	public Node getCurrentContent()
	{
		return currentController.getContent();
	}
	
	@Override
	public void onNetDiscoveryStart()
	{
		rootLayoutController.getStatusPopup().showNewContent
			( new StatusPopupContent(Strings.ECOSYSTEM_DISCOVERING_NOTIFICATION) );
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
		if ( rootLayoutController.getActionPopup().getCurrentActionType() != ActionType.USER_ACK )
			return;
		
		final ActionPopup actionPopup = rootLayoutController.getActionPopup();
		actionPopup.clearContent();
		actionPopup.hideContent();
		
		// show the scene containing the main layout
		final ScrollPane center = new ScrollPane(mainLayoutController.getEcosystemStatusController().getContent());
		rootLayout.setCenter(center);
	}
	
	@Override
	public void onSmartGamePlatformStatus()
	{
		mainLayoutController.getEcosystemStatusController().onGamePlatformStatus();
	}
	
	@Override
	public void onSmartBallStatus()
	{
		mainLayoutController.getEcosystemStatusController().onSmartBallStatus();
	}
	
	@Override
	public void onMotionControllerStatus()
	{
		mainLayoutController.getEcosystemStatusController().onMotionControllerStatus();
	}
	
	@Override
	public void onSmartPoleStatus()
	{
		mainLayoutController.getEcosystemStatusController().onSmartPoleStatus();
	}
	
	@Override
	public void onSmartRacketStatus(SmartRacketType smartRacket, SmartRacketStatus newStatus)
	{
		mainLayoutController.getEcosystemStatusController().onSmartRacketStatus(smartRacket);
	}
	
	public void onEcosystemStatusZoom( final double scale_factor )
	{
		mainLayoutController.getEcosystemStatusController().onScale(scale_factor);
	}
	
	private long lastUpdate = 0;
	private void mainLoopAnimationUpdate( final long now )
	{
		if ( (now - lastUpdate) < 100000000 )
			return;
		lastUpdate = now;
		
		final WindStatus newWindStatus = new WindStatus();
		newWindStatus.setActive(true);
		newWindStatus.setDirection((short) 45);
		PlaygroundStatus.getInstance().updateWindStatus(newWindStatus);
		
		WindFlagAnimator.getInstance().onUpdate(now);
		//WindSpeedAnimator.getInstance().onUpdate(now);
		MinimapWindLinesAnimator.getInstance().onUpdate(now);
		mainLayoutController.updateAnimation(now);
	}
	
	private void fin()
	{
		rootLayoutController.fin();
		APPLICATION_MANAGER.finalize();
	}
	
	private void iniRootLayout()
	{
		// load root layout from fxml file
		
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation( MainApplication.class.
				getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.ROOT_LAYOUT_FXML_FILENAME) );
		
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
		loader.setLocation( MainApplication.class.
				getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.MAIN_LAYOUT_FXML_FILENAME) );
		
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
		loader.setLocation( MainApplication.class.
				getResource(ControllerConfigs.VIEW_PATH + ViewConfigs.LOADING_FXML_FILENAME) );
		
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
}
