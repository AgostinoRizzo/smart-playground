/**
 * 
 */
package it.unical.mat.smart_playground.model.playground;

/**
 * @author Agostino
 *
 */
public interface PlaygroundStatusObserver
{
	public void onPlaygroundStatusChanged( final PlaygroundStatus status );
}
