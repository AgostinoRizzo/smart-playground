package it.unical.mat.smart_playground.balltracker.tracking;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

/**
 * Created by utente on 04/10/2020.
 */
public class BallTracker
{
    private static final float MIN_BALL_LOCATION_DELTA_PERCENTAGE = 0.1f;

    private static final short WIDTH = 0;
    private static final short HEIGHT = 1;

    private static BallTracker instance = null;

    private Vector2<Integer> platformFrameSize = null;
    private Vector2<Float> ballLocation = new Vector2<>(0.0f, 0.0f);

    private final BallTrackingCommunicator ballTrackingCommunicator;

    public static BallTracker getInstance()
    {
        if ( instance == null )
            instance = new BallTracker();
        return instance;
    }

    private BallTracker()
    {
        ballTrackingCommunicator = UDPBallTrackingCommunicator.getInstance();
    }

    public void updatePlatformFrameSize( final int width , final int height )
    {
        if ( platformFrameSize != null )
            return;
        platformFrameSize = new Vector2<>(width, height);
    }

    public void onBallMarkerDetected( final Marker marker )
    {
        if ( platformFrameSize == null )
            return;
        final Vector2<Integer> markerCenter = marker.getCenter();
        final Vector2<Float> newBallLocation =
                new Vector2<>(getStandardBallCoord(platformFrameSize.getX(), markerCenter.getX()),
                                getStandardBallCoord(platformFrameSize.getY(), markerCenter.getY()));

        if ( getBallLocationDeltaPercentage(newBallLocation) >= MIN_BALL_LOCATION_DELTA_PERCENTAGE )
        {
            ballLocation.set(newBallLocation);
            ballTrackingCommunicator.sendBallTrackingLocation(ballLocation);
        }
    }

    private float getBallLocationDeltaPercentage( final Vector2<Float> newBallLocation )
    {
        return Math.max(Math.abs(newBallLocation.getX() - ballLocation.getX()),
                Math.abs(newBallLocation.getY() - ballLocation.getY()));
    }

    private static float getStandardBallCoord( final int dimensionSize, final int absCoord )
    {
        final float stdCoord = absCoord / (float)(dimensionSize-1);
        if ( stdCoord < 0.0f ) return 0.0f;
        if ( stdCoord > 1.0f ) return 1.0f;
        return stdCoord;
    }
}
