package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by utente on 05/10/2020.
 */
public class BallTrackerAnalyzer extends SyncCameraFrameAnalyzer
{
    private static final int BALL_MARKER_ID = 0;
    private static BallTrackerAnalyzer instance = null;

    public static BallTrackerAnalyzer getInstance()
    {
        if ( instance == null )
            instance = new BallTrackerAnalyzer();
        return instance;
    }

    private BallTrackerAnalyzer()
    {

    }

    @Override
    protected void syncAnalyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        final Mat grayInputFrame = inputFrame.gray();
        BallTracker.getInstance().updatePlatformFrameSize(grayInputFrame.cols(), grayInputFrame.rows());
        final List<Marker> detectMarkers = MarkerDetector.getInstance().detectMarkers(grayInputFrame);
        for ( final Marker marker : detectMarkers )
            analyzeMarker(marker);
    }

    private void analyzeMarker( final Marker marker )
    {
        final BallTracker ballTracker = BallTracker.getInstance();
        switch ( marker.getId() )
        {
            case BALL_MARKER_ID: ballTracker.onBallMarkerDetected(marker); break;
        }
    }
}
