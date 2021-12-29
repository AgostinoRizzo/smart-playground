/**
 * 
 */
package it.unical.mat.smart_playground.controller;

import javafx.stage.WindowEvent;

/**
 * @author Agostino
 *
 */
public abstract class OneShowWindow extends Window
{
	private static boolean isShown = false;
	
	public OneShowWindow( final String title )
	{ super(title); }
	
	@Override
	public void show()
	{
		if ( isShown )
			return;
		
		super.show();
		isShown = true;
	}
	
	@Override
	public void close()
	{
		super.close();
		isShown = false;
	}
	
	@Override
	protected void onClose( final WindowEvent event )
	{
		super.onClose(event);
		isShown = false;
	}
}
