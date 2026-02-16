/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.smart_playground.controller.Window;
import it.unical.mat.smart_playground.view.ViewConfigs;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * @author Agostino
 *
 */
public class PlayerToolNotificationWindow extends Window
{
	private static final int WINDOW_WIDTH = 268;
	private static final int WINDOW_HEIGHT = 158;
	private static final int SHOW_HIDE_ANIMATION_DELTA = 10;
	private static final int VISIBLE_WINDOW_Y = 10;
	
	private static PlayerToolNotificationWindow instance = null;
	private final PlayerToolNotificationController layoutController;
	private final Timeline visibleTimeLine;
	private final AnimationTimer showCloseAnimationTimer;
	private final Lock lock = new ReentrantLock();
	private boolean isVisible = false;
	
	public static PlayerToolNotificationWindow getInstance()
	{
		if ( instance == null )
			instance = new PlayerToolNotificationWindow();
		return instance;
	}
	
	private PlayerToolNotificationWindow()
	{
		super("");
		
		layoutController = (PlayerToolNotificationController) getLayoutController();
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.getScene().setFill(Color.TRANSPARENT);
		
		stage.setX( (Screen.getPrimary().getBounds().getWidth() - WINDOW_WIDTH) / 2 );
		stage.setY(-WINDOW_HEIGHT);
		stage.setAlwaysOnTop(true);
		
		visibleTimeLine = new Timeline( new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				lock.lock();
				isVisible = false;
				showCloseAnimationTimer.start();
				lock.unlock();
			}
		}));
		visibleTimeLine.setCycleCount(1);
		
		showCloseAnimationTimer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				lock.lock();
				if ( isVisible )
				{
					stage.setY(stage.getY() + SHOW_HIDE_ANIMATION_DELTA);
					if ( stage.getY() >= VISIBLE_WINDOW_Y )
					{
						this.stop();
						visibleTimeLine.play();
					}
				}
				else
				{
					stage.setY(stage.getY() - SHOW_HIDE_ANIMATION_DELTA);
					if ( stage.getY() <= -WINDOW_HEIGHT )
						this.stop();
				}
				lock.unlock();
			}
		};
		
		stage.show();
	}

	@Override
	protected String getRootLayoutFxmlFilename() { return ViewConfigs.PLAYER_TOOL_NOTIFICATION_LAYOUT_FXML_FILENAME; }
	
	public void onPlayerToolChanged( final PlayerTool newTool )
	{
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				lock.lock();
				
				layoutController.onPlayerToolChanged(newTool);
				
				if ( isVisible )
					visibleTimeLine.setCycleCount(1);
				else
				{
					isVisible = true;
					showCloseAnimationTimer.start();
				}
				
				lock.unlock();
			}
		});
	}
}
