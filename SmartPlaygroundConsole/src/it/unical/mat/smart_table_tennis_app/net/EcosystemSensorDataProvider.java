/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Agostino
 *
 */
public class EcosystemSensorDataProvider extends Thread
{
	private static final int RECV_BUFFER_SIZE = 1024;
	
	private final EcosystemServiceCallback callback;
	private final DatagramSocket dataSocket;
	private final byte[] recvBuffer = new byte[RECV_BUFFER_SIZE];
	
	public EcosystemSensorDataProvider( final EcosystemServiceCallback callback ) throws IOException
	{
		this.callback=callback;
		dataSocket = new DatagramSocket( Services.SENSOR_DATA_SOCKET_PORT );
		
		this.start();
	}
	
	@Override
	public void run()
	{
		final DatagramPacket dataPacket = new DatagramPacket(recvBuffer, RECV_BUFFER_SIZE);
		String sensorData;
		
		while ( !isInterrupted() )
		{
			try
			{
				dataSocket.receive(dataPacket);
				sensorData = new String(dataPacket.getData());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				dataSocket.close();
				interrupt();
			}
		}
	}
}
