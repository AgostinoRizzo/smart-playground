/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.network.BallTrackingMotionCtrlCommCallback;
import it.unical.mat.smart_playground.network.BallTrackingMotionCtrlCommProvider;
import it.unical.mat.smart_playground.network.NetDiscoveryCallback;
import it.unical.mat.smart_playground.network.NetDiscoveryClient;
import it.unical.mat.smart_playground.network.NetService;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommCallback;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommProvider;
import javafx.application.Platform;

/**
 * @author Agostino
 *
 */
public class ApplicationManager implements NetDiscoveryCallback, PlaygroundBaseCommCallback, BallTrackingMotionCtrlCommCallback
{
	private static ApplicationManager instance = new ApplicationManager();
	
	private final NetDiscoveryClient netDiscoveryClient;
	private final BallTrackingMotionCtrlCommProvider ballTrackingMotionCtrlCommProvider;
	private PlaygroundBaseCommProvider playgroundBaseCommProvider=null;
	
	private final List<NetDiscoveryCallback>       netDiscoveryApplicationCallbacks = new ArrayList<>();
	private final List<PlaygroundBaseCommCallback> playgroundBaseCommApplicationCallbacks = new ArrayList<>();
	
	public static ApplicationManager getInstance()  // thread-safe method.
	{
		return instance;
	}
	
	private ApplicationManager()
	{
		netDiscoveryClient = new NetDiscoveryClient(this);
		ballTrackingMotionCtrlCommProvider = new BallTrackingMotionCtrlCommProvider(this);
	}
	
	public void addNetworkDiscoveryCallback( final NetDiscoveryCallback callback )
	{
		netDiscoveryApplicationCallbacks.add(callback);
	}
	
	public void addPlaygroundBaseCommCallbacks( final PlaygroundBaseCommCallback callback )
	{
		playgroundBaseCommApplicationCallbacks.add(callback);
	}
	
	public void initialize()
	{
		new Timer().schedule( new TimerTask()
		{
			@Override
			public void run()
			{
				Platform.runLater( new Runnable()
				{
					@Override
					public void run()
					{
						for ( final NetDiscoveryCallback c : netDiscoveryApplicationCallbacks )
							c.onNetDiscoveryStart();
						netDiscoveryClient.start();
					}
				});
			}
		}, 500 /*3000*/);
		
		ballTrackingMotionCtrlCommProvider.start();
	}
	
	public void finalize()
	{
		if ( playgroundBaseCommProvider != null )
			playgroundBaseCommProvider.fin();
	}
	
	@Override
	public void onNetDiscoveryStart()
	{}
	
	@Override
	public void onNetServiceDiscovery(NetService service)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final NetDiscoveryCallback c : netDiscoveryApplicationCallbacks )
					c.onNetServiceDiscovery(service);
			}
		});
	}

	@Override
	public void onAckEvent()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final PlaygroundBaseCommCallback c : playgroundBaseCommApplicationCallbacks )
					c.onAckEvent();
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
				for ( final PlaygroundBaseCommCallback c : playgroundBaseCommApplicationCallbacks )
					c.onSmartBallStatus();
			}
		});
	}
	
	@Override
	public void onSmartFieldStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final PlaygroundBaseCommCallback c : playgroundBaseCommApplicationCallbacks )
					c.onSmartFieldStatus();
			}
		});
	}
	
	@Override
	public void onSmartRacketStatus(SmartRacketType smartRacket, SmartRacketStatus newStatus)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final PlaygroundBaseCommCallback c : playgroundBaseCommApplicationCallbacks )
					c.onSmartRacketStatus(smartRacket, newStatus);
			}
		});
	}	

	@Override
	public void onBallStatusChanged(SmartBallStatus newBallStatus)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				PlaygroundStatus.getInstance().updateBallStatus(newBallStatus);
			}
		});
	}
	@Override
	public void onPlayerStatusChanged(PlayerStatus newPlayerStatus)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				PlaygroundStatus.getInstance().updatePlayerStatus(newPlayerStatus);
			}
		});
	}
	@Override
	public void onNewGolfHoleLocation(float left, float top)
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				PlaygroundStatus.getInstance().updateGolfHoleLocation(left, top);
			}
		});
	}
}
