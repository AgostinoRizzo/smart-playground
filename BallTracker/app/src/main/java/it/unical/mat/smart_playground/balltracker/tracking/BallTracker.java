package it.unical.mat.smart_playground.balltracker.tracking;

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
    private static final short MIN_BALL_ORIENTATION_DELTA = 1;  // 0-359 degrees

    private static final int TOP_LEFT_CORNER_INDEX     = 0;
    private static final int TOP_RIGHT_CORNER_INDEX    = 1;
    private static final int BOTTOM_LEFT_CORNER_INDEX  = 2;
    private static final int BOTTOM_RIGHT_CORNER_INDEX = 3;

    private static final short WIDTH = 0;
    private static final short HEIGHT = 1;

    private static final TrackingCommStats TRACKING_COMM_STATS = TrackingCommStats.getInstance();

    private static BallTracker instance = null;

    private Vector2<Integer> platformFrameSize = null;
    private BallStatus ballStatus = new BallStatus();
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

    public BallStatus getBallStatus()
    {
        return ballStatus;
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

        // compute new ball location.
        final Vector2<Integer> markerCenter = marker.getCenter();
        final Vector2<Float> newBallLocation =
                new Vector2<>(getStandardBallCoord(platformFrameSize.getX() - platformPaddings[PADDING_LEFT_INDEX] - platformPaddings[PADDING_RIGHT_INDEX],
                                                    markerCenter.getX() - platformPaddings[PADDING_LEFT_INDEX]),
                                getStandardBallCoord(platformFrameSize.getY() - platformPaddings[PADDING_TOP_INDEX] - platformPaddings[PADDING_BOTTOM_INDEX],
                                                    markerCenter.getY() - platformPaddings[PADDING_TOP_INDEX]));

        // compute new ball orientation.
        short newBallOrientation = -1;
        try { newBallOrientation = marker.getOrientation(); }
        catch (NoOrientationDetectedException e) {}

        // update and send new ball status.
        final boolean onLocationUpdate = getBallLocationDeltaPercentage(newBallLocation) >= MIN_BALL_LOCATION_DELTA_PERCENTAGE ||
                                            TRACKING_COMM_STATS.getBallLocationCommStat().onKeepAlive();
        final boolean onOrientationUpdate = (newBallOrientation >= 0 && Math.abs(newBallOrientation - ballStatus.getOrientation()) >= MIN_BALL_ORIENTATION_DELTA) ||
                                            TRACKING_COMM_STATS.getBallOrientationCommStat().onKeepAlive();

        if ( onLocationUpdate && onOrientationUpdate )
        {
            ballStatus.getLocation().set(newBallLocation);
            ballStatus.setOrientation(newBallOrientation);
            ballTrackingCommunicator.sendBallTrackingStatus(ballStatus);
        }
        else if ( onLocationUpdate )
        {
            ballStatus.getLocation().set(newBallLocation);
            ballTrackingCommunicator.sendBallTrackingLocation(ballStatus);
        }
        else if ( onOrientationUpdate )
        {
            ballStatus.setOrientation(newBallOrientation);
            ballTrackingCommunicator.sendBallTrackingOrientation(ballStatus);
        }

        TRACKING_COMM_STATS.getUnknownBallStatusFlag().onClear();
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

    public void onNoBallMarkerDetected()
    {
        final DelayedStatusFlag unknownBallStatusFlag = TRACKING_COMM_STATS.getUnknownBallStatusFlag();

        unknownBallStatusFlag.onSet();
        if ( unknownBallStatusFlag.tryGet() )
        {
            ballTrackingCommunicator.sendUnknownBallTrackingStatus();
            unknownBallStatusFlag.onClear();
        }
    }

    private float getBallLocationDeltaPercentage( final Vector2<Float> newBallLocation )
    {
        final Vector2<Float> ballLocation = ballStatus.getLocation();
        return Math.max(Math.abs(newBallLocation.getX() - ballLocation.getX()),
                Math.abs(newBallLocation.getY() - ballLocation.getY()));
    }

    private static float getStandardBallCoord( final int dimensionSize, final int absCoord )
    {
        final float stdCoord = (dimensionSize != 1) ? absCoord / (float)(dimensionSize-1) : 0.0f;
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
