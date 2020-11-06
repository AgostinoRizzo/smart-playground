/**
 * 
 */
package it.unical.mat.smart_playground.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unical.mat.smart_playground.model.ecosystem.EcosystemStatus;
import it.unical.mat.smart_playground.model.ecosystem.MotionControllerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartGamePlatformStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartPoleStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.ecosystem.WindDirection;
import it.unical.mat.smart_playground.util.JSONUtil;

/**
 * @author Agostino
 *
 */
public class PlaygroundBaseCommProvider extends Thread
{
	private final Socket eventSocket;
	private final PlaygroundBaseCommCallback callback;
	
	private final DataOutputStream out;
	private final BufferedReader in;
	
	public PlaygroundBaseCommProvider( final InetAddress baseStationAddress,
								   final PlaygroundBaseCommCallback callback ) throws IOException
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
				handleEvent( JSONUtil.fromStringToJsonObject(event) );
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
			out.writeChars( PlaygroundBaseCommConfigs.CLOSE_CONNECTION_CODE );
			out.flush();
			
			out.close();
			in.close();
			eventSocket.close();
			
			interrupt();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleEvent( final JsonObject event ) throws IOException
	{
		final String dataType = event.get("dataType").getAsString();
		
		if      ( dataType.equals( PlaygroundBaseCommConfigs.ACK_EVENT ) )                   callback.onAckEvent();
		//else if ( dataType.equals( EcosystemEventConfigs.SMART_GAME_PLATFORM_STATUS ) )  handleSmartGamePlatformStatus(event.get("sample").getAsJsonArray());
		else if ( dataType.equals( PlaygroundBaseCommConfigs.SMARTBALL_STATUS ) )            handleSmartBallStatus(event.get("sample").getAsJsonArray());
		else if ( dataType.equals( PlaygroundBaseCommConfigs.SMARTPOLE_STATUS ) )            handleSmartPoleStatus(event.get("sample").getAsJsonArray());
		if      ( dataType.equals( PlaygroundBaseCommConfigs.MAIN_SMART_RACKET_STATUS ) )    handleSmartRacketStatus(SmartRacketType.MAIN, 
																												 event.get("accs_values").getAsJsonArray());
	}
	/*
	private void handleSmartGamePlatformStatus( final JsonArray sensorsDataSample ) throws NumberFormatException, IOException
	{
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(0).getAsJsonArray());
		final List< Integer > humidityValues    = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(1).getAsJsonArray());
		final List< Integer > brightnessValues  = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(2).getAsJsonArray());
		
		// update model
		final SmartGamePlatformStatus gamePlatformStatus = EcosystemStatus.getInstance().getSmartGamePlatformStatus();
		gamePlatformStatus.updateNewTemperatureValues(temperatureValues);
		gamePlatformStatus.updateNewHumidityValues(humidityValues);
		gamePlatformStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartGamePlatformStatus();
		
	}*/
	private void handleSmartBallStatus( final JsonArray sensorsDataSample ) throws NumberFormatException, IOException
	{
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(0).getAsJsonArray());
		final List< Integer > humidityValues    = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(1).getAsJsonArray());
		final List< Integer > brightnessValues  = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(2).getAsJsonArray());
		
		// update model
		final SmartBallStatus smartBallStatus = EcosystemStatus.getInstance().getSmartBallStatus();
		smartBallStatus.updateNewTemperatureValues(temperatureValues);
		smartBallStatus.updateNewHumidityValues(humidityValues);
		smartBallStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartBallStatus();
	}/*
	private void handleMotionControllerStatus() throws NumberFormatException, IOException
	{
		//final int direction = Integer.parseInt( in.readLine() );
		
		// TODO: update model
	}*/
	private void handleSmartPoleStatus( final JsonArray sensorsDataSample ) throws NumberFormatException, IOException
	{
		// read wind direction value
		//final int wind_direction = readNextIntegerValues().get(0);
		
		// read temperature, humidity and brightness values.
		final List< Integer > temperatureValues = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(0).getAsJsonArray());
		final List< Integer > humidityValues    = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(1).getAsJsonArray());
		final List< Integer > brightnessValues  = JSONUtil.fromJsonArrayToIntegerList(sensorsDataSample.get(2).getAsJsonArray());
	
		// update model
		final SmartPoleStatus smartPoleStatus = EcosystemStatus.getInstance().getSmartPoleStatus();
		smartPoleStatus.updateNewTemperatureValues(temperatureValues);
		smartPoleStatus.updateNewHumidityValues(humidityValues);
		smartPoleStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartPoleStatus();
	}
	private void handleSmartRacketStatus( final SmartRacketType smartRacket, final JsonArray accsValues ) throws NumberFormatException, IOException
	{
		// read xyz accelerometer values.
		final List< Integer > accXValues = JSONUtil.fromJsonArrayToIntegerList(accsValues.get(0).getAsJsonArray());
		final List< Integer > accYValues = JSONUtil.fromJsonArrayToIntegerList(accsValues.get(1).getAsJsonArray());
		final List< Integer > accZValues = JSONUtil.fromJsonArrayToIntegerList(accsValues.get(2).getAsJsonArray());
		
		// create new status.
		final SmartRacketStatus newSmartRacketStatus = new SmartRacketStatus();
		newSmartRacketStatus.updateNewAccelerometerValues(accXValues, accYValues, accZValues);
		
		callback.onSmartRacketStatus(smartRacket, newSmartRacketStatus);
	}
	
	// TODO: test function
	public static void testOnEcosystemStatus( final PlaygroundBaseCommCallback callback )
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
