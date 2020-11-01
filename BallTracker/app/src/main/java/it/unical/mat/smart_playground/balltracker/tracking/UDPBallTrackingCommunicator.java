package it.unical.mat.smart_playground.balltracker.tracking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

/**
 * Created by utente on 05/10/2020.
 */
public class UDPBallTrackingCommunicator implements BallTrackingCommunicator
{
    private static final short SOCKET_PORT = 3000;
    private static UDPBallTrackingCommunicator instance = null;

    private List<InetAddress> destinationAddrs = null;
    private DatagramSocket udpSocket = null;
    private ByteBuffer buffer = null;
    private int sequenceNumber = 0;

    private static final TrackingCommStats TRACKING_COMM_STATS = TrackingCommStats.getInstance();
    private static final KeepAliveCommStat LOCATION_KEEP_ALIVE = TRACKING_COMM_STATS.getBallLocationCommStat();
    private static final KeepAliveCommStat ORIENTATION_KEEP_ALIVE = TRACKING_COMM_STATS.getBallOrientationCommStat();

    public static UDPBallTrackingCommunicator getInstance()
    {
        if ( instance == null )
            instance = new UDPBallTrackingCommunicator();
        return instance;
    }

    private UDPBallTrackingCommunicator()
    {
        createUDPSocket();
    }

    @Override
    public void sendBallTrackingLocation( final BallStatus status )
    {
        if ( createUDPSocket() )
        {
            buffer = ByteBuffer.allocate(12);
            buffer.putInt(sequenceNumber);
            addLocationToDataBuffer(status);

            if ( sendBufferData() )
                LOCATION_KEEP_ALIVE.onComm();

        }
    }

    @Override
    public void sendBallTrackingOrientation( final BallStatus status )
    {
        if ( createUDPSocket() )
        {
            buffer = ByteBuffer.allocate(6);
            buffer.putInt(sequenceNumber);
            addOrientationToDataBuffer(status);

            if ( sendBufferData() )
                ORIENTATION_KEEP_ALIVE.onComm();
        }
    }

    @Override
    public void sendBallTrackingStatus( final BallStatus status )
    {
        if ( createUDPSocket() )
        {
            buffer = ByteBuffer.allocate(14);
            buffer.putInt(sequenceNumber);
            addLocationToDataBuffer(status);
            addOrientationToDataBuffer(status);

            if ( sendBufferData() )
            {
                LOCATION_KEEP_ALIVE.onComm();
                ORIENTATION_KEEP_ALIVE.onComm();
            }
        }
    }

    @Override
    public void sendUnknownBallTrackingStatus()
    {
        if ( createUDPSocket() )
        {
            buffer = ByteBuffer.allocate(4);
            buffer.putInt(sequenceNumber);

            sendBufferData();
        }
    }

    private boolean createUDPSocket()
    {
        if ( destinationAddrs != null && !destinationAddrs.isEmpty() && udpSocket != null )
            return true;
        try
        {
            destinationAddrs = getDestinationAddrs();
            if ( destinationAddrs == null || destinationAddrs.isEmpty() )
                return false;

            udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);
            return true;
        }
        catch (SocketException e) { udpSocket = null; return false; }
    }

    private void addLocationToDataBuffer( final BallStatus status )
    {
        final Vector2<Float> ballLocation = status.getLocation();

        buffer.putFloat(ballLocation.getX());
        buffer.putFloat(ballLocation.getY());
    }

    private void addOrientationToDataBuffer( final BallStatus status )
    {
        final short orientation = status.getOrientation();
        buffer.putShort(orientation);
    }

    private boolean sendBufferData()
    {
        if ( destinationAddrs.isEmpty() || buffer == null || !buffer.hasArray() )
            return false;

        byte[] bufferData = buffer.array();

        for ( final InetAddress destAddr : destinationAddrs )
        {
            final DatagramPacket dataPacket = new DatagramPacket(bufferData, bufferData.length, destAddr, SOCKET_PORT);
            try {  udpSocket.send(dataPacket); ++sequenceNumber; }
            catch (IOException e) {}
        }

        return true;
    }

    private static List<InetAddress> getDestinationAddrs() throws SocketException
    {
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface netIface;
        while ( interfaces.hasMoreElements() )
        {
            netIface = interfaces.nextElement();
            if ( netIface.isUp() && !netIface.isLoopback() )
            {
                final List<InetAddress> destinationAddrs = new ArrayList<>();
                final List<InterfaceAddress> ifaceAddrs = netIface.getInterfaceAddresses();

                for ( final InterfaceAddress addr : ifaceAddrs )
                {
                    final InetAddress broadcast = addr.getBroadcast();
                    if ( broadcast != null )
                        destinationAddrs.add(broadcast);
                }

                return destinationAddrs;
            }
        }
        return null;
    }
}
