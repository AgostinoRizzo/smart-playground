/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Agostino
 *
 */
public class EcosystemServiceProvider
{
	private final InetAddress baseStationAddress;
	private final Socket eventSocket;
	private final EcosystemServiceCallback callback;
	
	public EcosystemServiceProvider( final InetAddress baseStationAddress,
									 final EcosystemServiceCallback callback ) throws IOException
	{
		this.baseStationAddress=baseStationAddress;
		this.callback=callback;
		
		eventSocket = new Socket( baseStationAddress, Services.EVENT_SOCKET_PORT );
	}
}
