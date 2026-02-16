package it.unical.mat.smart_playground.motioncontroller;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by utente on 20/12/2021.
 */
public class MotionManager
{
    private static final long SEND_TIME_DELTA = 300;  // expressed in millis
    private static MotionManager instance = null;

    private final MotionCommunicator communicator = new UDPMotionCommunicator();
    private float orientation = -1f;
    private long lastSendTime = 0;

    public static MotionManager getInstance()
    {
        if ( instance == null )
            instance = new MotionManager();
        return instance;
    }

    private MotionManager()
    {}

    public void updateOrientation( final float newOrientation )
    {
        if ( orientation < 0 || canSend() )
        {
            orientation = newOrientation;
            lastSendTime = communicator.sendOrientation(orientation);
        }
    }

    public void onOrientationSync()
    {
        communicator.sendOrientationSync(orientation);
    }

    public void setUnknownOrientation()
    {
        if ( orientation >= 0 || canSend() )
        {
            orientation = -1f;
            lastSendTime = communicator.sendUnknownOrientation();
        }
    }

    public void updateSteps( final int totalSteps ) { communicator.sendSteps(totalSteps); }

    private boolean canSend()
    {
        return ( System.currentTimeMillis() - lastSendTime >= SEND_TIME_DELTA );
    }
}
