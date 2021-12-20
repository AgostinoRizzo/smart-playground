package it.unical.mat.smart_playground.motioncontroller;

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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by utente on 20/12/2021.
 */
public class UDPMotionCommunicator extends Thread implements MotionCommunicator
{
    private static final short SOCKET_PORT = 3000;

    private static final byte ORIENTATION_UPDATE  = 1;
    private static final byte ORIENTATION_SYNC    = 2;
    private static final byte ORIENTATION_UNKNOWN = 3;

    private List<InetAddress> destinationAddrs = null;
    private DatagramSocket udpSocket = null;
    private ByteBuffer buffer = null;
    private int sequenceNumber = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition bufferAvailableCond = lock.newCondition();

    public UDPMotionCommunicator()
    {
        setDaemon(true);
        createUDPSocket();
        start();
    }

    @Override
    public long sendOrientation(float orientation)
    {
        try
        {
            lock.lock();
            if (createUDPSocket() && buffer == null)
            {
                buffer = ByteBuffer.allocate(9);
                buffer.putInt(sequenceNumber);
                buffer.put(ORIENTATION_UPDATE);
                buffer.putFloat(orientation);

                bufferAvailableCond.signal();
                return System.currentTimeMillis();
            }
            return -1;
        }
        finally { lock.unlock(); }
    }

    @Override
    public long sendOrientationSync(float orientation)
    {
        try
        {
            lock.lock();
            if (createUDPSocket() && buffer == null)
            {
                buffer = ByteBuffer.allocate(9);
                buffer.putInt(sequenceNumber);
                buffer.put(ORIENTATION_SYNC);
                buffer.putFloat(orientation);

                bufferAvailableCond.signal();
                return System.currentTimeMillis();
            }
            return -1;
        }
        finally { lock.unlock(); }
    }

    @Override
    public long sendUnknownOrientation()
    {
        try
        {
            lock.lock();
            if (createUDPSocket() && buffer == null)
            {
                buffer = ByteBuffer.allocate(5);
                buffer.putInt(sequenceNumber);
                buffer.put(ORIENTATION_UNKNOWN);

                bufferAvailableCond.signal();
                return System.currentTimeMillis();
            }
            return -1;
        }
        finally { lock.unlock(); }
    }

    @Override
    public void run()
    {
        lock.lock();
        while ( true )
        {
            try
            {
                while (buffer == null)
                    bufferAvailableCond.await();
                sendBufferData();
                buffer = null;
            }
            catch ( InterruptedException e ) {}
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
