/**
 * 
 */
package it.unical.mat.smart_playground.network;

/**
 * @author Agostino
 *
 */
public interface NetDiscoveryCallback
{
	public void onNetDiscoveryStart();
	public void onNetServiceDiscovery( final NetService service );
}
