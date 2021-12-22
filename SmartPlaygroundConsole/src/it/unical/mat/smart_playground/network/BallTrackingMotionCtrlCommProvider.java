/**
 * 
 */
package it.unical.mat.smart_playground.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import it.unical.mat.smart_playground.model.ecosystem.PlayerStatus;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallLocation;
import it.unical.mat.smart_playground.model.ecosystem.SmartBallStatus;

/**
 * @author Agostino
 *
 */
public class BallTrackingMotionCtrlCommProvider extends Thread
{
	private static final int SOCKET_PORT = 3000;
	private static final int MAX_RCV_DATA_BUFFER_SIZE = 14;
	private static final int[] BALL_STATUS_DATA_BUFFER_SIZES = {4, 5, 6, 9, 12, 14};
	
	private static final byte PACKET_TYPE_ORIENTATION_UPDATE  = 1;
	private static final byte PACKET_TYPE_ORIENTATION_SYNC    = 2;
	private static final byte PACKET_TYPE_ORIENTATION_UNKNOWN = 3;
	private static final byte PACKET_TYPE_STEPS_UPDATE        = 4;
	
	private DatagramSocket socket;
	
	private int balltrack_seqdata_number = 0, motionctrl_seqdata_number = 0;
	private SmartBallStatus ballStatus = new SmartBallStatus();
	private PlayerStatus playerStatus = new PlayerStatus();
	
	private final BallTrackingMotionCtrlCommCallback callback;
	
	public BallTrackingMotionCtrlCommProvider( final BallTrackingMotionCtrlCommCallback callback )
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
		
		while ( true )
		{
			try
			{
				socket.receive(rcvPacket);
				manageReceivedDataPacket(rcvPacket);		
			} 
			catch (IOException e)
			{}
		}
	}
	
	private void manageReceivedDataPacket( final DatagramPacket rcvPacket )
	{
		final int rcvDataLength = rcvPacket.getLength();
		
		if ( !checkRecvDataBufferSize(rcvDataLength) )
			return;
		
		final byte[] rcvData = rcvPacket.getData();
		final ByteBuffer rcvByteBuffer = ByteBuffer.wrap(rcvData, 0, rcvDataLength);
		
		if ( rcvDataLength == 4 || rcvDataLength == 6 || rcvDataLength == 14 )
			manageBallTrackingReceivedData( rcvByteBuffer, rcvDataLength );
		else if ( rcvDataLength == 5 || rcvDataLength == 9 )
			manageMotionControllerReceivedData( rcvByteBuffer, rcvDataLength );
	}
	
	private void manageBallTrackingReceivedData( final ByteBuffer rcvByteBuffer, final int rcvDataLength )
	{
		final int seqnumber = rcvByteBuffer.getInt();
		if ( seqnumber <= balltrack_seqdata_number )
			return;
		balltrack_seqdata_number = seqnumber;
		
		if ( rcvDataLength >= 12 )
		{
			final float ballLeft = rcvByteBuffer.getFloat(),
						ballTop  = rcvByteBuffer.getFloat();
			
			final SmartBallLocation ballLocation = ballStatus.getLocation();
			ballLocation.setLeft(ballLeft);
			ballLocation.setTop(1.0f - ballTop);
		}
		
		if ( rcvDataLength == 6 || rcvDataLength == 14 )
		{
			final short ballOrientation = rcvByteBuffer.getShort();
			ballStatus.setOrientation(ballOrientation);
		}
		
		if ( rcvDataLength == 4 )
			ballStatus.setUnknownStatus();
		
		onBallStatusChanged();
	}
	
	private void manageMotionControllerReceivedData( final ByteBuffer rcvByteBuffer, final int rcvDataLength )
	{
		final int seqnumber = rcvByteBuffer.getInt();
		if ( seqnumber <= motionctrl_seqdata_number )
			return;
		motionctrl_seqdata_number = seqnumber;
		
		final byte type = rcvByteBuffer.get();
		
		if ( rcvDataLength == 9 )
		{
			if ( type == PACKET_TYPE_STEPS_UPDATE )
			{
				final int totalSteps = rcvByteBuffer.getInt();
				playerStatus.updateTotalSteps(totalSteps);
				onPlayerStatusChanged();
			}
			else
			{
				final float orientation = rcvByteBuffer.getFloat();
				
				switch (type)
				{
				case PACKET_TYPE_ORIENTATION_UPDATE:  playerStatus.updateAbsoluteOrientation(orientation); 
													  onPlayerStatusChanged(); break;
				case PACKET_TYPE_ORIENTATION_SYNC:    playerStatus.syncOrientation(orientation);
													  onPlayerStatusChanged(); break;
				}
			}
		}
		else if ( rcvDataLength == 5 && type == PACKET_TYPE_ORIENTATION_UNKNOWN )
		{
			playerStatus.onUnknownOrientation();
			onPlayerStatusChanged();
		}
		
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
		final SmartBallStatus newBallStatus = new SmartBallStatus(ballStatus);		
		System.out.println(newBallStatus);
		callback.onBallStatusChanged(newBallStatus);
	}
	
	private void onPlayerStatusChanged()
	{
		final PlayerStatus newPlayerStatus = new PlayerStatus(playerStatus);
		System.out.println(newPlayerStatus);
		callback.onPlayerStatusChanged(newPlayerStatus);
	}
}
