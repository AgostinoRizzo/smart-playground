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
    private static final short DATA_BUFFER_SIZE = 12;
    private static UDPBallTrackingCommunicator instance = null;

    private List<InetAddress> destinationAddrs = null;
    private DatagramSocket udpSocket = null;
    private final ByteBuffer buffer;
    private int sequenceNumber = 0;

    public static UDPBallTrackingCommunicator getInstance()
    {
        if ( instance == null )
            instance = new UDPBallTrackingCommunicator();
        return instance;
    }

    private UDPBallTrackingCommunicator()
    {
        buffer = ByteBuffer.allocate(DATA_BUFFER_SIZE);
        createUDPSocket();
    }

    @Override
    public void sendBallTrackingLocation(Vector2<Float> ballLocation)
    {
        if ( createUDPSocket() )
        {
            buffer.clear();
            buffer.putInt(sequenceNumber);
            buffer.putFloat(ballLocation.getX());
            buffer.putFloat(ballLocation.getY());


            for ( final InetAddress destAddr : destinationAddrs )
            {
                final DatagramPacket dataPacket = new DatagramPacket(buffer.array(), DATA_BUFFER_SIZE, destAddr, SOCKET_PORT);
                try {  udpSocket.send(dataPacket); ++sequenceNumber; }
                catch (IOException e) {}
            }
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
