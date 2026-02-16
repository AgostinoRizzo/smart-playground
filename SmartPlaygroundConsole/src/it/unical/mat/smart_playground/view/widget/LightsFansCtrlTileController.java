/**
 * 
 */
package it.unical.mat.smart_playground.view.widget;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.JsonObject;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.network.PlaygroundBaseCommProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Agostino
 *
 */
public class LightsFansCtrlTileController implements LayoutController
{
	// LIGHTS/FANS PATTERN
    protected static final int ALL_OFF        = 0;
    protected static final int FRONT_LEFT_ON  = 1;
    protected static final int BACK_LEFT_ON   = 2;
    protected static final int BACK_RIGHT_ON  = 3;
    protected static final int FRONT_RIGHT_ON = 4;
    protected static final int ALL_ON         = 5;
    
	private final JsonObject lightsCommand;
	private final JsonObject fansCommand;
	
	@FXML private Slider lightsThrSlider;
	@FXML private Slider fansThrSlider;
	
	private boolean allLightsOnLock = false;
	private boolean allFansOnLock = false;
	
	private static final Lock lock = new ReentrantLock();
	private static LightsFansCtrlTileController instance = null;
	
	public static LightsFansCtrlTileController getInstance()
	{
		try { lock.lock(); return instance; }
		finally { lock.unlock(); }
	}
	public static double getLightsThr()
	{
		try 
		{ 
			lock.lock();
			if ( instance == null )
				return -1;
			return instance.lightsThrSlider.getValue();
		}
		finally { lock.unlock(); }
	}
	public static double getFansThr()
	{
		try 
		{ 
			lock.lock();
			if ( instance == null )
				return -1;
			return instance.fansThrSlider.getValue();
		}
		finally { lock.unlock(); }
	}
	
	public LightsFansCtrlTileController()
	{
		lightsCommand = new JsonObject();
		lightsCommand.addProperty("type", "lights_cmd");
		lightsCommand.addProperty("pattern", ALL_OFF);
		
		fansCommand = new JsonObject();
		fansCommand.addProperty("type", "fans_cmd");
		fansCommand.addProperty("pattern", ALL_OFF);
		
		lock.lock();
		instance = this;
		lock.unlock();
	}
	
	@Override
	public void onInitialize(Window win)
	{}

	@Override
	public void onFinalize()
	{}
	
	public void onAllLightsTurnOn()  // button manager
	{
		//if ( lightsCommand.get("pattern").getAsInt() == ALL_ON )
		//	return;
		//System.out.println("Lights on");
		lightsCommand.remove("pattern");
		lightsCommand.addProperty("pattern", ALL_ON);
		PlaygroundBaseCommProvider.getInstance().sendCommand(lightsCommand);
	}
	public void onAllLightsTurnOff()  // button manager
	{
		if ( lightsCommand.get("pattern").getAsInt() == ALL_OFF )
			return;
		//System.out.println("Lights off");
		lightsCommand.remove("pattern");
		lightsCommand.addProperty("pattern", ALL_OFF);
		PlaygroundBaseCommProvider.getInstance().sendCommand(lightsCommand);
	}
	public void onAllFansTurnOn()  // button manager
	{
		//if ( fansCommand.get("pattern").getAsInt() == ALL_ON )
		//	return;
		//System.out.println("Fans on");
		fansCommand.remove("pattern");
		fansCommand.addProperty("pattern", ALL_ON);
		PlaygroundBaseCommProvider.getInstance().sendCommand(fansCommand);
	}
	public void onAllFansTurnOff()  // button manager
	{
		if ( fansCommand.get("pattern").getAsInt() == ALL_OFF )
			return;
		//System.out.println("Fans off");
		fansCommand.remove("pattern");
		fansCommand.addProperty("pattern", ALL_OFF);
		PlaygroundBaseCommProvider.getInstance().sendCommand(fansCommand);
	}
}
