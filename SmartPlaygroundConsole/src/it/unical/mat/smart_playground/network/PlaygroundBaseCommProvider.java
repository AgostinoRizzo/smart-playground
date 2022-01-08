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

import it.unical.mat.smart_playground.controller.playing.PlayerTool;
import it.unical.mat.smart_playground.controller.playing.PlayerToolNotificationWindow;
import it.unical.mat.smart_playground.model.ecosystem.EcosystemStatus;
import it.unical.mat.smart_playground.model.ecosystem.MotionControllerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartGamePlatformStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartFieldStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartRacketType;
import it.unical.mat.smart_playground.model.environment.EnvironmentSoundPlayer;
import it.unical.mat.smart_playground.model.environment.EnvironmentSoundType;
import it.unical.mat.smart_playground.model.playground.PlaygroundStatus;
import it.unical.mat.smart_playground.model.playground.WindStatus;
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
	
	private GameEventCallback gameEventCallback = null;
	
	private static PlaygroundBaseCommProvider instance = null;
	
	public static PlaygroundBaseCommProvider getInstance()
	{
		return instance;
	}
	
	public PlaygroundBaseCommProvider( final InetAddress baseStationAddress,
								   final PlaygroundBaseCommCallback callback ) throws IOException
	{
		if ( instance != null )
			throw new RuntimeException("An instance of the class already exists.");
		
		this.callback=callback;
		
		eventSocket = new Socket( baseStationAddress, Services.EVENT_SOCKET_PORT );
		out = new DataOutputStream( eventSocket.getOutputStream() );
		in = new BufferedReader( new InputStreamReader( eventSocket.getInputStream() ) );
		
		this.start();
		instance = this;
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
	
	public void sendCommand( final JsonObject command )
	{
		try
			{ out.writeBytes(command.toString() + "\n"); } 
		catch (IOException e)
			{ e.printStackTrace(); }
	}
	
	public void setGameEventCallback( final GameEventCallback callback )
	{
		gameEventCallback = callback;
	}
	public void removeGameEventCallback()
	{
		gameEventCallback = null;
	}
	
	private void handleEvent( final JsonObject event ) throws IOException
	{
		final String dataType = event.get("dataType").getAsString();
		
		if      ( dataType.equals( PlaygroundBaseCommConfigs.ACK_EVENT ) )                   {callback.onAckEvent(); System.out.println("A+B");}
		else if ( dataType.equals( PlaygroundBaseCommConfigs.SMARTBALL_STATUS ) )            handleSmartBallStatus(event.get("sample").getAsJsonArray());
		else if ( dataType.equals( PlaygroundBaseCommConfigs.SMARTFIELD_STATUS ) )           handleSmartFieldStatus(event.get("sample").getAsJsonArray());
		else if ( dataType.equals( PlaygroundBaseCommConfigs.FIELD_WIND_STATUS ) )           handleFieldWindStatus(event);
		else if ( dataType.equals( PlaygroundBaseCommConfigs.MAIN_SMART_RACKET_STATUS ) )    handleSmartRacketStatus(SmartRacketType.MAIN, event.get("accs_values").getAsJsonArray());
		else if ( dataType.equals( PlaygroundBaseCommConfigs.GAME_EVENT ) )                  handleGameEvent(event);
		else if ( dataType.equals( PlaygroundBaseCommConfigs.ENVSOUND ) )                    handleEnvironmentSoundPlay(event);
		else if ( dataType.equals( PlaygroundBaseCommConfigs.TOOL_CHANGED ) )                handleToolChangedEvent(event);
		else if ( dataType.equals( PlaygroundBaseCommConfigs.TOOL_SETTING_CHANGED ) )        handleToolSettingChangedEvent(event);
		else if ( dataType.equals( PlaygroundBaseCommConfigs.TOOL_ACTION ) )                 handleToolActionEvent(event);
	}
	
	private void handleSmartBallStatus( final JsonArray sensorsDataSample ) throws NumberFormatException, IOException
	{
		final JsonArray temperatureValuesJsonArray = new JsonArray(1);
		final JsonArray humidityValuesJsonArray    = new JsonArray(1);
		final JsonArray brightnessValuesJsonArray  = new JsonArray(1);
		
		// read temperature, humidity and brightness values.
		temperatureValuesJsonArray.add(sensorsDataSample.get(0));
		humidityValuesJsonArray   .add(sensorsDataSample.get(1));
		brightnessValuesJsonArray .add(sensorsDataSample.get(2));
		
		final List< Integer > temperatureValues = JSONUtil.fromJsonArrayToIntegerList(temperatureValuesJsonArray);
		final List< Integer > humidityValues    = JSONUtil.fromJsonArrayToIntegerList(humidityValuesJsonArray);
		final List< Integer > brightnessValues  = JSONUtil.fromJsonArrayToIntegerList(brightnessValuesJsonArray);
		
		// update model
		final SmartBallStatus smartBallStatus = EcosystemStatus.getInstance().getSmartBallStatus();
		smartBallStatus.updateNewTemperatureValues(temperatureValues);
		smartBallStatus.updateNewHumidityValues(humidityValues);
		smartBallStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartBallStatus();
		updatePlaygroundStatusTempHumiBright();
	}
	private void handleSmartFieldStatus( final JsonArray sensorsDataSample ) throws NumberFormatException, IOException
	{
		final JsonArray temperatureValuesJsonArray = new JsonArray(1);
		final JsonArray humidityValuesJsonArray    = new JsonArray(1);
		final JsonArray brightnessValuesJsonArray  = new JsonArray(1);
		
		// read temperature, humidity and brightness values.
		temperatureValuesJsonArray.add(sensorsDataSample.get(0));
		humidityValuesJsonArray   .add(sensorsDataSample.get(1));
		brightnessValuesJsonArray .add(sensorsDataSample.get(2));
		
		final List< Integer > temperatureValues = JSONUtil.fromJsonArrayToIntegerList(temperatureValuesJsonArray);
		final List< Integer > humidityValues    = JSONUtil.fromJsonArrayToIntegerList(humidityValuesJsonArray);
		final List< Integer > brightnessValues  = JSONUtil.fromJsonArrayToIntegerList(brightnessValuesJsonArray);
		
		// update model
		final SmartFieldStatus smartFieldStatus = EcosystemStatus.getInstance().getSmartFieldStatus();
		smartFieldStatus.updateNewTemperatureValues(temperatureValues);
		smartFieldStatus.updateNewHumidityValues(humidityValues);
		smartFieldStatus.updateNewBrightnessValues(brightnessValues);
		
		callback.onSmartFieldStatus();
		updatePlaygroundStatusTempHumiBright();
	}
	private void updatePlaygroundStatusTempHumiBright()
	{
		final PlaygroundStatus playgroundStatus = PlaygroundStatus.getInstance();
		final EcosystemStatus ecosystemStatus = EcosystemStatus.getInstance();
		
		playgroundStatus.updateTemperatureStatus( ecosystemStatus.getCurrentTemperatureAverage() );
		playgroundStatus.updateHumidityStatus(ecosystemStatus.getCurrentHumidityAverage() );
		playgroundStatus.updateBrightnessStatus(ecosystemStatus.getCurrentBrightnessAverage() );
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
	
	private void handleFieldWindStatus( final JsonObject windStatusJson )
	{
		final PlaygroundStatus playgroundStatus = PlaygroundStatus.getInstance();
		final boolean windOn = windStatusJson.get("status").getAsString().equals("on");
		
		final WindStatus windStatus = new WindStatus();
		windStatus.set(playgroundStatus.getWindStatus());
		windStatus.setActive(windOn);
		
		if ( windOn )
		{
			final short dir = (short) windStatusJson.get("dir").getAsInt();
			windStatus.setDirection(dir);
			//System.out.println("Wind direction: " + windStatus.getDirection() + " / " + windStatus.getDirectionDegrees());
		}
		
		System.out.println("Wind Status: " + windOn);
		PlaygroundStatus.getInstance().updateWindStatus(windStatus);
		
		EnvironmentSoundPlayer.getInstance().onWindStatusChanged(windOn);
	}
	
	private void handleGameEvent( final JsonObject gameEvent )
	{
		if ( gameEventCallback != null )
			gameEventCallback.onGameEvent(gameEvent);
	}
	
	private void handleEnvironmentSoundPlay( final JsonObject soundEvent )
	{
		final EnvironmentSoundType soundToPlay = EnvironmentSoundType.parse(soundEvent.get("sound").getAsString());
		EnvironmentSoundPlayer.getInstance().playSound(soundToPlay);
	}
	
	private void handleToolChangedEvent( final JsonObject toolChangedEvent )
	{
		PlayerToolNotificationWindow.getInstance().onPlayerToolChanged
			(PlayerTool.parse(toolChangedEvent.get("new_tool").getAsString()));
		EnvironmentSoundPlayer.getInstance().playSound(EnvironmentSoundType.CHOOSE);
	}
	
	private void handleToolSettingChangedEvent( final JsonObject toolSettingChangedEvent )
	{
		if ( toolSettingChangedEvent.get("tool").getAsString().equals("club") )
		{
			// TODO: notify info to the GolfMatchFormController 
			final String newSetting = toolSettingChangedEvent.get("setting").getAsString();
			if ( newSetting.equals("attempt") )
				System.out.println("Tool setting changed: attempt");
			else if ( newSetting.equals("stroke") )
				System.out.println("Tool setting changed: stroke");
			EnvironmentSoundPlayer.getInstance().playSound(EnvironmentSoundType.SUB_CHOOSE);
		}
	}
	
	private void handleToolActionEvent( final JsonObject toolActionEvent )
	{
		if ( toolActionEvent.get("tool").getAsString().equals("club") )
		{
			// TODO: notify info to the GolfMatchFormController 
			final String actionType = toolActionEvent.get("action_type").getAsString();
			System.out.println("Club action: " + actionType);
		}
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
		
		gamePLatformStatus.updateNewTemperatureValues(temperatureValues);
		gamePLatformStatus.updateNewBrightnessValues(brightnessValues);
		
		smartBallStatus.updateNewTemperatureValues(temperatureValues);
		smartBallStatus.updateNewBrightnessValues(brightnessValues);
		
		motionControllerStatus.updatePlayerOrientation(40);
		
		callback.onSmartBallStatus();
	}
}
