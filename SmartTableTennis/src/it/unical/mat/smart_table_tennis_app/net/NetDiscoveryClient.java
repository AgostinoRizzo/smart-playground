/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Agostino
 *
 */
public class NetDiscoveryClient extends Thread
{
	private static final int RECV_TIMEOUT = 4000;
	
	private final NetDiscoveryCallback callback;
	private DatagramSocket socket=null;
	
	public NetDiscoveryClient( final NetDiscoveryCallback callback )
	{
		this.callback=callback;
	}
	@Override
	public void run()
	{
		try
		{
			final Enumeration< NetworkInterface > interfaces = 
					NetworkInterface.getNetworkInterfaces();
			
			if ( !interfaces.hasMoreElements() )
			{
				callback.onNetServiceDiscovery(null);
				return;
			}
			
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			socket.setSoTimeout( RECV_TIMEOUT );
			
			NetworkInterface iface;
			InetAddress broadcast;
			
			while ( interfaces.hasMoreElements() )
			{
				iface = interfaces.nextElement();
				
				if ( iface.isUp() && !iface.isLoopback() )
					for ( InterfaceAddress iface_addr : iface.getInterfaceAddresses() )
					{
						broadcast = iface_addr.getBroadcast();
						if ( broadcast != null )
						{
							final byte[] send_buffer = Services.DISCOVERY_REQUEST_CODE;
							final DatagramPacket sendPacket = 
									new DatagramPacket( send_buffer, send_buffer.length, broadcast, Services.DISCOVERY_PORT );
							socket.send(sendPacket);
							
							final byte[] rcv_buffer = new byte[ Services.DISCOVERY_RESPONSE_LENGTH ];
							final DatagramPacket rcvPacket = new DatagramPacket( rcv_buffer, rcv_buffer.length );
							
							socket.receive(rcvPacket);
							
							callback.onNetServiceDiscovery( new NetService( rcv_buffer, rcvPacket.getAddress() ) );
						}
					}
			}
			closeSocket();
			
		} 
		catch (SocketException e)
		{
			closeSocket();
			e.printStackTrace();
			callback.onNetServiceDiscovery(null);
		} 
		catch (IOException e)
		{
			closeSocket();
			e.printStackTrace();
			callback.onNetServiceDiscovery(null);
		}
	}
	private void closeSocket()
	{
		if ( socket != null )
		{
			socket.close();
			socket = null;
		}
	}
}
