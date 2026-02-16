/**
 * 
 */
package it.unical.mat.smart_playground.view;

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
