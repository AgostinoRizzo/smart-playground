/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view;

/**
 * @author Agostino
 *
 */
public class Loading extends Screen
{	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	protected String getFXMLFilename()
	{
		return ViewConfigs.LOADING_FXML_FILENAME;
	}

	@Override
	protected String getWindowsTitle()
	{
		return null; //ViewConfigs.LOADING_VIEW_TITLE;
	}
}
