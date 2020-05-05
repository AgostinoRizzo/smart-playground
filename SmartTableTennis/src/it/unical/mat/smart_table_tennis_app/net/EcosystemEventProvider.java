/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_table_tennis_app.model.ecosystem.EcosystemStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.MotionControllerStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartGamePlatformStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartPoleStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_table_tennis_app.model.ecosystem.WindDirection;

/**
 * @author Agostino
 *
 */
public class EcosystemEventProvider extends Thread
{
	private final Socket eventSocket;
	private final EcosystemServiceCallback callback;
	
	private final DataOutputStream out;
	private final BufferedReader in;
	
	public EcosystemEventProvider( final InetAddress baseStationAddress,
								   final EcosystemServiceCallback callback ) throws IOException
	{
		this.callback=callback;
		
		eventSocket = new Socket( baseStationAddress, Services.EVENT_SOCKET_PORT );
		out = new DataOutputStream( eventSocket.getOutputStream() );
		in = new BufferedReader( new InputStreamReader( eventSocket.getInputStream() ) );
		
		this.start();
	}
	@Override
	public void run()
	{
		String event;
		
		while ( !isInterrupted() )
		{
			try
			{
				event = in.readLine();
				handleEvent(event);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				interrupt();
			}
		}
		
		try
		{
			out.close();
			in.close();
			eventSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void fin()
	{
		try
		{
			out.writeChars( EcosystemEventConfigs.CLOSE_CONNECTION_CODE );
			out.flush();
			
			interrupt();
			
			eventSocket.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleEvent( final String event ) throws IOException
	{
		if      ( event.equals( EcosystemEventConfigs.ACK_EVENT ) )                   callback.onAckEvent();
		else if ( event.equals( EcosystemEventConfigs.ECOSYSTEM_STATUS ) )            handleEcosystemStatus();
		else if ( event.equals( EcosystemEventConfigs.SMART_GAME_PLATFORM_STATUS ) )  handleSmartGamePlatformStatus();
		else if ( event.equals( EcosystemEventConfigs.SMARTBALL_STATUS ) )            handleSmartBallStatus();
		else if ( event.equals( EcosystemEventConfigs.SMARTPOLE_STATUS ) )            handleSmartPoleStatus();
		if      ( event.equals( EcosystemEventConfigs.MAIN_SMART_RACKET_STATUS ) )    handleSmartRacketStatus(SmartRacketType.MAIN);
	}
	
	private void handleEcosystemStatus() throws IOException
	{
		for( int i=0; i<EcosystemEventConfigs.SMART_OBJECTS_STATUS_COUNT; ++i )
		{
			final String smart_obj = in.readLine();
			
			if      ( smart_obj.equals( EcosystemEventConfigs.SMART_GAME_PLATFORM_ID ) )     handleSmartGamePlatformStatus();
			else if ( smart_obj.equals( EcosystemEventConfigs.SMART_BALL_ID ) )              handleSmartBallStatus();
			else if ( smart_obj.equals( EcosystemEventConfigs.SMART_MOTION_CONTROLLER_ID ) ) handleMotionControllerStatus();
			else if ( smart_obj.equals( EcosystemEventConfigs.SMART_POLE_ID ) )              handleSmartPoleStatus();
		}
	}
	
	private void handleSmartGamePlatformStatus() throws NumberFormatException, IOException
	{
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = readNextIntegerValues();
		final List< Integer > humidityValues    = readNextIntegerValues();
		final List< Integer > brightnessValues  = readNextIntegerValues(); 
		
		// update model
		final SmartGamePlatformStatus gamePlatformStatus = EcosystemStatus.getInstance().getSmartGamePlatformStatus();
		gamePlatformStatus.updateNewTemperatureValues(temperatureValues);
		gamePlatformStatus.updateNewHumidityValues(humidityValues);
		gamePlatformStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartGamePlatformStatus();
		
	}
	private void handleSmartBallStatus() throws NumberFormatException, IOException
	{
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = readNextIntegerValues();
		final List< Integer > humidityValues    = readNextIntegerValues();
		final List< Integer > brightnessValues  = readNextIntegerValues(); 
		
		// update model
		final SmartBallStatus smartBallStatus = EcosystemStatus.getInstance().getSmartBallStatus();
		smartBallStatus.updateNewTemperatureValues(temperatureValues);
		smartBallStatus.updateNewHumidityValues(humidityValues);
		smartBallStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartBallStatus();
	}
	private void handleMotionControllerStatus() throws NumberFormatException, IOException
	{
		final int direction = Integer.parseInt( in.readLine() );
		
		// TODO: update model
	}
	private void handleSmartPoleStatus() throws NumberFormatException, IOException
	{
		// read wind direction value
		final int wind_direction = readNextIntegerValues().get(0);
		
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = readNextIntegerValues();
		final List< Integer > humidityValues    = readNextIntegerValues();
		final List< Integer > brightnessValues  = readNextIntegerValues(); 
	
		// update model
		final SmartPoleStatus smartPoleStatus = EcosystemStatus.getInstance().getSmartPoleStatus();
		smartPoleStatus.updateNewTemperatureValues(temperatureValues);
		smartPoleStatus.updateNewHumidityValues(humidityValues);
		smartPoleStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartPoleStatus();
	}
	private void handleSmartRacketStatus( final SmartRacketType smartRacket ) throws NumberFormatException, IOException
	{
		// read xyz accelerometer values.
		final List< Integer > accXValues = readNextIntegerValues();
		final List< Integer > accYValues = readNextIntegerValues();
		final List< Integer > accZValues = readNextIntegerValues(); 
		
		// update model
		final SmartRacketStatus smartRacketStatus = EcosystemStatus.getInstance().getSmartRacketStatus(smartRacket);
		smartRacketStatus.updateNewAccelerometerValues(accXValues, accYValues, accZValues);
		
		callback.onSmartRacketStatus(smartRacket);
	}
	
	private List< Integer > readNextIntegerValues() throws NumberFormatException, IOException
	{
		final List< Integer > values = new ArrayList<>();
		int n_values = Integer.parseInt( in.readLine() );
		
		for( int i=0; i<n_values; ++i )
			values.add( Integer.parseInt( in.readLine() ) );
		
		return values;
	}
	
	// TODO: test function
	public static void testOnEcosystemStatus( final EcosystemServiceCallback callback )
	{
		final List< Integer > temperatureValues = new ArrayList<>();
		final List< Integer > brightnessValues  = new ArrayList<>(); 
		
		temperatureValues.add(10);
		temperatureValues.add(30);
		temperatureValues.add(12);
		
		brightnessValues.add(10);
		brightnessValues.add(30);
		brightnessValues.add(12);
		
		final EcosystemStatus ecosystemStatus = EcosystemStatus.getInstance();
		final SmartGamePlatformStatus gamePLatformStatus = ecosystemStatus.getSmartGamePlatformStatus();
		final SmartBallStatus smartBallStatus = ecosystemStatus.getSmartBallStatus();
		final MotionControllerStatus motionControllerStatus = ecosystemStatus.getMotionControllerStatus();
		final SmartPoleStatus smartPoleStatus = ecosystemStatus.getSmartPoleStatus();
		
		gamePLatformStatus.updateNewTemperatureValues(temperatureValues);
		gamePLatformStatus.updateNewBrightnessValues(brightnessValues);
		
		smartBallStatus.updateNewTemperatureValues(temperatureValues);
		smartBallStatus.updateNewBrightnessValues(brightnessValues);
		
		motionControllerStatus.updatePlayerDirection(40);
		
		smartPoleStatus.updateWindDirection( WindDirection.EAST );
		smartPoleStatus.updateNewTemperatureValues(temperatureValues);
		smartPoleStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartGamePlatformStatus();
		callback.onSmartBallStatus();
		callback.onMotionControllerStatus();
		callback.onSmartPoleStatus();
	}
}
