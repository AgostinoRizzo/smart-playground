/**
 * 
 */
package it.unical.mat.smart_playground.controller.playground;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.view.animation.WindFlagAnimationManager;
import it.unical.mat.smart_playground.view.widget.BrightnessTileController;
import it.unical.mat.smart_playground.view.widget.HumidityTileController;
import it.unical.mat.smart_playground.view.widget.TemperatureTileController;
import it.unical.mat.smart_playground.view.widget.WindDirectionTileController;
import it.unical.mat.smart_playground.view.widget.WindSpeedTileController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlaygroundController implements LayoutController
{
	@FXML private ImageView windFlagImage0;
	@FXML private ImageView windFlagImage1;
	@FXML private ImageView windFlagImage2;
	@FXML private ImageView windFlagImage3;
	@FXML private ImageView windFlagImage4;
	@FXML private ImageView windFlagImage5;
	@FXML private ImageView windFlagImage6;
	@FXML private ImageView windFlagImage7;
	
	@FXML private Parent windDirectionTile;
	@FXML private WindDirectionTileController windDirectionTileController;
	
	@FXML private Parent windSpeedTile;
	@FXML private WindSpeedTileController windSpeedTileController;
	
	@FXML private Parent temperatureTile;
	@FXML private TemperatureTileController temperatureTileController;
	
	@FXML private Parent brightnessTile;
	@FXML private BrightnessTileController brightnessTileController;
	
	@FXML private Parent humidityTile;
	@FXML private HumidityTileController humidityTileController;
	
	private static final WindFlagAnimationManager WIND_FLAG_ANIMATOR = WindFlagAnimationManager.getInstance();
	
	@Override
	public void onInitialize(Window win)
	{
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage0);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage1);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage2);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage3);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage4);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage5);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage6);
		WIND_FLAG_ANIMATOR.addAnimation(windFlagImage7);
		
		windDirectionTileController.onInitialize(win);
		windSpeedTileController.onInitialize(win);
		temperatureTileController.onInitialize(win);
		brightnessTileController.onInitialize(win);
		humidityTileController.onInitialize(win);
	}
	
	@Override
	public void onFinalize()
	{
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage0);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage1);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage2);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage3);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage4);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage5);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage6);
		WIND_FLAG_ANIMATOR.removeAnimation(windFlagImage7);
		
		windDirectionTileController.onFinalize();
		windSpeedTileController.onFinalize();
		temperatureTileController.onFinalize();
		brightnessTileController.onFinalize();
		humidityTileController.onFinalize();
	}	
}
