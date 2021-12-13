/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.network.BallTrackingCommCallback;
import it.unical.mat.smart_playground.network.BallTrackingCommProvider;
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
public class ApplicationManager implements NetDiscoveryCallback, PlaygroundBaseCommCallback, BallTrackingCommCallback
{
	private static ApplicationManager instance = new ApplicationManager();
	
	private final NetDiscoveryClient netDiscoveryClient;
	private final BallTrackingCommProvider ballTrackingCommProvider;
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
		ballTrackingCommProvider = new BallTrackingCommProvider(this);
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
		
		ballTrackingCommProvider.start();
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
	public void onMotionControllerStatus()
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final PlaygroundBaseCommCallback c : playgroundBaseCommApplicationCallbacks )
					c.onMotionControllerStatus();
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
}
