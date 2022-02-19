package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.List;

import it.unical.mat.smart_playground.balltracker.view.OpenCVTrackingView;

/**
 * Created by utente on 08/02/2022.
 */
public class ComposedBallTrackerAnalyzer extends SyncCameraFrameAnalyzer
{
    private static final long MIN_ANALYZE_FRAME_DELTA = 30;
    private static final long MARKER_BASED_ANALYZER_DELTA_TIME = 200;

    private static ComposedBallTrackerAnalyzer instance = null;

    private final BackgroundArucoBasedBallTrackerAnalyzer arucoBasedAnalyzer = BackgroundArucoBasedBallTrackerAnalyzer.getInstance();
    private final ColorBasedBallTrackerAnalyzer coloredBasedAnalyzer = new ColorBasedBallTrackerAnalyzer();
    private long lastAnalyzeFrameTime = System.currentTimeMillis();
    private long lastArucoBasedAnalyzerTime = System.currentTimeMillis();

    public static ComposedBallTrackerAnalyzer getInstance()
    {
        if ( instance == null )
            instance = new ComposedBallTrackerAnalyzer();
        return instance;
    }

    private ComposedBallTrackerAnalyzer() {}

    @Override
    public void onCameraStarted(int width, int height)
    {
        BallTracker.getInstance().updatePlatformFrameSize(width, height);
        arucoBasedAnalyzer.onCameraStarted(width, height);
        coloredBasedAnalyzer.onCameraStarted(width, height);
    }

    @Override
    protected Mat syncAnalyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        final Mat rgbaInputFrame = inputFrame.rgba();
        final TrackingSettings trackingSettings = BallTracker.getTrackingSettings();

        long now = System.currentTimeMillis();
        final long elapsed = now - lastAnalyzeFrameTime;
        if ( elapsed < trackingSettings.getMinAnalyzeFrameDelta() )
            return rgbaInputFrame;
        lastAnalyzeFrameTime = now;

        List<Marker> detectedMarkers = null;
        final boolean useTrackingBooster = BallTracker.getTrackingSettings().getUseColorBooster();

        if ( useTrackingBooster )
        {
            now = System.currentTimeMillis();

            coloredBasedAnalyzer.analyzeFrame(inputFrame);
            detectedMarkers = coloredBasedAnalyzer.getDetectedMarkers();

            final boolean noColorMarkerDetected = detectedMarkers == null || detectedMarkers.isEmpty();
            if ( now - lastArucoBasedAnalyzerTime >= trackingSettings.getArucoDetectDelta() )
            {
                arucoBasedAnalyzer.analyzeFrame(inputFrame);
                lastArucoBasedAnalyzerTime = now;
            }

            final List<Marker> detectedArucoMarkers = arucoBasedAnalyzer.getDetectedMarkers();
            if ( detectedArucoMarkers != null && !detectedArucoMarkers.isEmpty() )
            {
                if (noColorMarkerDetected)
                {
                    Marker.removeMarker(detectedArucoMarkers, Marker.BALL_MARKER_ID);
                    detectedMarkers = detectedArucoMarkers;
                }
                else
                {
                    final StaticallyOrientedMarker smartBallMarker = (StaticallyOrientedMarker) detectedMarkers.get(0);
                    for (final Marker am : detectedArucoMarkers)
                        if (am.getId() == Marker.BALL_MARKER_ID)
                            smartBallMarker.setDirection(am.getDirection());
                        else detectedMarkers.add(am);
                }
            }
        }
        else
        {
            arucoBasedAnalyzer.clearResult();
            arucoBasedAnalyzer.analyzeFrame(inputFrame);
            detectedMarkers = arucoBasedAnalyzer.waitForDetectedMarkers();
        }

        final BallTracker ballTracker = BallTracker.getInstance();
        final boolean smartBallMarkerDetected = Marker.findMarker(detectedMarkers, Marker.BALL_MARKER_ID);

        for ( final Marker marker : detectedMarkers )
            analyzeMarker(marker);

        if ( !smartBallMarkerDetected )
            ballTracker.onNoBallMarkerDetected();

        if ( detectedMarkers == null || detectedMarkers.isEmpty() )
            return rgbaInputFrame;

        final OpenCVTrackingView trackingView = OpenCVTrackingView.getInstance();
        trackingView.setFrame(rgbaInputFrame);
        trackingView.drawTracking( detectedMarkers, BallTracker.getInstance() );

        return rgbaInputFrame;
    }

    @Override
    protected void syncOnPause()
    {
        BallTracker.getInstance().getBallTrackingCommunicator().sendUnknownBallTrackingStatus();
    }

    @Override
    public List<Marker> getDetectedMarkers()
    {
        return null;
    }

    private void analyzeMarker( final Marker marker )
    {
        final BallTracker ballTracker = BallTracker.getInstance();
        switch ( marker.getId() )
        {
            case Marker.BALL_MARKER_ID: ballTracker.onBallMarkerDetected(marker); break;
            case Marker.TOP_LEFT_PLATFORM_CORNER_ID: ballTracker.onTopLeftPlatforCornerMarkerDetected(marker); break;
            case Marker.TOP_RIGHT_PLATFORM_CORNER_ID: ballTracker.onTopRightPlatforCornerMarkerDetected(marker); break;
            case Marker.BOTTOM_LEFT_PLATFORM_CORNER_ID: ballTracker.onBottomLeftPlatforCornerMarkerDetected(marker); break;
            case Marker.BOTTOM_RIGHT_PLATFORM_CORNER_ID: ballTracker.onBottomRightPlatforCornerMarkerDetected(marker); break;
        }
    }
}
