/**
 * 
 */
package it.unical.mat.smart_playground.controller.playing;

import it.unical.mat.smart_playground.controller.LayoutController;
import it.unical.mat.smart_playground.controller.Window;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * @author Agostino
 *
 */
public class PlayerToolNotificationController implements LayoutController
{
	@FXML private ImageView racketToolImageView;
	@FXML private ImageView clubToolImageView;
	@FXML private Label toolNameLabel;
	
	@Override
	public void onInitialize(Window win)
	{}

	@Override
	public void onFinalize()
	{}
	
	public void onPlayerToolChanged( final PlayerTool newTool )
	{
		switch (newTool)
		{
		case RACKET: racketToolImageView.setVisible(true); 
		             clubToolImageView.setVisible(false);
		             toolNameLabel.setText("Racket");
		             break;
		case CLUB:   racketToolImageView.setVisible(false); 
                     clubToolImageView.setVisible(true);
                     toolNameLabel.setText("Club");
                     break;
		}
	}
}
