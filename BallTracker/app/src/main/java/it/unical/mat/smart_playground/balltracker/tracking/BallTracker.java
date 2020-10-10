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
    public static final int PADDING_TOP_INDEX    = 0;
    public static final int PADDING_RIGHT_INDEX  = 1;
    public static final int PADDING_BOTTOM_INDEX = 2;
    public static final int PADDING_LEFT_INDEX   = 3;

    private static final float MIN_BALL_LOCATION_DELTA_PERCENTAGE = 0.1f;

    private static final int TOP_LEFT_CORNER_INDEX     = 0;
    private static final int TOP_RIGHT_CORNER_INDEX    = 1;
    private static final int BOTTOM_LEFT_CORNER_INDEX  = 2;
    private static final int BOTTOM_RIGHT_CORNER_INDEX = 3;

    private static final short WIDTH = 0;
    private static final short HEIGHT = 1;

    private static BallTracker instance = null;

    private Vector2<Integer> platformFrameSize = null;
    private Vector2<Float> ballLocation = new Vector2<>(0.0f, 0.0f);
    private final Vector2<Integer>[] platformCornersLocations = new Vector2[4];
    private final int[] platformPaddings = {0, 0, 0, 0};

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
        for ( int i=0; i<platformCornersLocations.length; ++i )
            platformCornersLocations[i] = new Vector2<>(-1, -1);
    }

    public Vector2<Integer> getPlatformFrameSize()
    {
        return platformFrameSize;
    }

    public int[] getPlatformPaddings()
    {
        return platformPaddings;
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

    public void onTopLeftPlatforCornerMarkerDetected( final Marker marker )
    {
        updatePlatformCornerLocation(TOP_LEFT_CORNER_INDEX, marker);
    }
    public void onTopRightPlatforCornerMarkerDetected( final Marker marker )
    {
        updatePlatformCornerLocation(TOP_RIGHT_CORNER_INDEX, marker);
    }
    public void onBottomLeftPlatforCornerMarkerDetected( final Marker marker )
    {
        updatePlatformCornerLocation(BOTTOM_LEFT_CORNER_INDEX, marker);
    }
    public void onBottomRightPlatforCornerMarkerDetected( final Marker marker )
    {
        updatePlatformCornerLocation(BOTTOM_RIGHT_CORNER_INDEX, marker);
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

    private void updatePlatformCornerLocation( final int cornerIndex, final Marker cornerMarker )
    {
        if ( cornerIndex >=0 && cornerIndex < platformCornersLocations.length )
        {
            final Vector2<Integer> newCornerLocation = cornerMarker.getCenter();
            platformCornersLocations[cornerIndex].setX(newCornerLocation.getX());
            platformCornersLocations[cornerIndex].setY(newCornerLocation.getY());

            updatePlatformPaddings();
        }
    }

    private void updatePlatformPaddings()
    {
        if ( platformFrameSize == null )
            return;

        for ( int i=0; i<platformCornersLocations.length; ++i )
            if ( platformCornersLocations[i].getX() < 0 || platformCornersLocations[i].getY() < 0 )
                return;

        platformPaddings[PADDING_TOP_INDEX] =

        platformPaddings[PADDING_TOP_INDEX]    = getLocationsYAvg(TOP_LEFT_CORNER_INDEX, TOP_RIGHT_CORNER_INDEX);
        platformPaddings[PADDING_RIGHT_INDEX]  = platformFrameSize.getX() - getLocationsXAvg(TOP_RIGHT_CORNER_INDEX, BOTTOM_RIGHT_CORNER_INDEX);
        platformPaddings[PADDING_BOTTOM_INDEX] = platformFrameSize.getY() - getLocationsYAvg(BOTTOM_LEFT_CORNER_INDEX, BOTTOM_RIGHT_CORNER_INDEX);
        platformPaddings[PADDING_LEFT_INDEX]   = getLocationsXAvg(TOP_LEFT_CORNER_INDEX, BOTTOM_LEFT_CORNER_INDEX);
    }

    private int getLocationsXAvg( final int loc1Index, final int loc2Index )
    {
        return (platformCornersLocations[loc1Index].getX() +
                platformCornersLocations[loc2Index].getX()) / 2;
    }
    private int getLocationsYAvg( final int loc1Index, final int loc2Index )
    {
        return (platformCornersLocations[loc1Index].getY() +
                platformCornersLocations[loc2Index].getY()) / 2;
    }
}
