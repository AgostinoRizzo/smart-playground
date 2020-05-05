/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.view;

/**
 * @author Agostino
 *
 */
public class Welcome extends Screen
{
	@Override
	protected String getFXMLFilename()
	{
		return ViewConfigs.WELCOME_FXML_FILENAME;
	}

	@Override
	protected String getWindowsTitle()
	{
		return null; //ViewConfigs.WELCOME_VIEW_TITLE;
	}
}
