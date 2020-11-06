/**
 * 
 */
package it.unical.mat.smart_playground.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public class BallTrackingCommProvider extends Thread
{
	private static final int SOCKET_PORT = 3000;
	private static final int MAX_RCV_DATA_BUFFER_SIZE = 14;
	private static final int[] BALL_STATUS_DATA_BUFFER_SIZES = {4, 6, 12, 14};
	
	private DatagramSocket socket;
	
	private int seqdata_number = 0;
	private SmartBallStatus ballStatus = new SmartBallStatus();
	
	private final BallTrackingCommCallback callback;
	
	public BallTrackingCommProvider( final BallTrackingCommCallback callback )
	{
		this.callback = callback;
		setDaemon(true);
	}
	
	@Override
	public void run()
	{
		try
		{ socket = new DatagramSocket(SOCKET_PORT); } 
		catch (SocketException e)
		{ socket = null; return; }
		
		final byte[] databuff = new byte[MAX_RCV_DATA_BUFFER_SIZE];
		final DatagramPacket rcvPacket = new DatagramPacket(databuff, databuff.length);
		
		float loc = 0f;
		float inc = .03f;
		
		while ( true )
		{
			ballStatus.setOrientation(0);
			final SmartBallLocation location = ballStatus.getLocation();
			location.setLeft(loc);
			location.setTop (loc);
			
			loc += inc;
			if ( loc > 1f )
			{
				loc = 1f;
				inc = -inc;
			}
			else if ( loc < 0f )
			{
				loc = 0f;
				inc = -inc;
			}
			
			onBallStatusChanged();
			
			try
			{
				sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			try
			{
				socket.receive(rcvPacket);
				manageReceivedDataPacket(rcvPacket);		
			} 
			catch (IOException e)
			{}
			*/
		}
	}
	
	private void manageReceivedDataPacket( final DatagramPacket rcvPacket )
	{
		final int rcvDataLength = rcvPacket.getLength();
		
		if ( !checkRecvDataBufferSize(rcvDataLength) )
			return;
		
		final byte[] rcvData = rcvPacket.getData();
		final ByteBuffer rcvByteBuffer = ByteBuffer.wrap(rcvData, 0, rcvDataLength);
		final int seqnumber = rcvByteBuffer.getInt();
		
		if ( seqnumber <= seqdata_number )
			return;
		
		if ( rcvDataLength >= 12 )
		{
			final float ballLeft = rcvByteBuffer.getFloat(),
						ballTop  = rcvByteBuffer.getFloat();
			
			final SmartBallLocation ballLocation = ballStatus.getLocation();
			ballLocation.setLeft(ballLeft);
			ballLocation.setTop(ballTop);
		}
		
		if ( rcvDataLength == 6 || rcvDataLength == 14 )
		{
			final short ballOrientation = rcvByteBuffer.getShort();
			ballStatus.setOrientation(ballOrientation);
			System.out.println("ORIENTATION: " + ballOrientation);
		}
		
		if ( rcvDataLength == 4 )
			ballStatus.setUnknownStatus();
		
		seqdata_number = seqnumber + 1;
		
		onBallStatusChanged();
	}
	
	private static boolean checkRecvDataBufferSize( final int dataBufferSize )
	{
		for ( final int size : BALL_STATUS_DATA_BUFFER_SIZES )
			if ( dataBufferSize == size )
				return true;
		return false;
	}
	
	private void onBallStatusChanged()
	{
		final SmartBallStatus newBallStatus = new SmartBallStatus();
		newBallStatus.getLocation().set(ballStatus.getLocation());
		newBallStatus.setOrientation(ballStatus.getOrientation());
		
		callback.onBallStatusChanged(ballStatus);
	}
}
