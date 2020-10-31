package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import it.unical.mat.smart_playground.balltracker.util.Vector2;
import it.unical.mat.smart_playground.balltracker.util.Vector2Int;
import it.unical.mat.smart_playground.balltracker.view.OpenCVTrackingView;

/**
 * Created by utente on 05/10/2020.
 */
public class BallTrackerAnalyzer extends SyncCameraFrameAnalyzer
{
    public  static final int BALL_MARKER_ID                  = 0;
    private static final int TOP_LEFT_PLATFORM_CORNER_ID     = 1;
    private static final int TOP_RIGHT_PLATFORM_CORNER_ID    = 2;
    private static final int BOTTOM_LEFT_PLATFORM_CORNER_ID  = 3;
    private static final int BOTTOM_RIGHT_PLATFORM_CORNER_ID = 4;

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
    protected Mat syncAnalyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        final Mat rgbaInputFrame = inputFrame.rgba();
        final Mat grayInputFrame = inputFrame.gray();

        BallTracker.getInstance().updatePlatformFrameSize(grayInputFrame.cols(), grayInputFrame.rows());
        final List<Marker> detectMarkers = MarkerDetector.getInstance().detectMarkers(grayInputFrame);

        final BallTracker ballTracker = BallTracker.getInstance();
        final OpenCVTrackingView trackingView = OpenCVTrackingView.getInstance();

        trackingView.setFrame(rgbaInputFrame);  // for testing

        for ( final Marker marker : detectMarkers )
            analyzeMarker(marker);

        //trackingView.setFrame(rgbaInputFrame);
        trackingView.drawTracking( detectMarkers, BallTracker.getInstance() );

        return rgbaInputFrame;
    }

    private void analyzeMarker( final Marker marker )
    {
        final BallTracker ballTracker = BallTracker.getInstance();
        switch ( marker.getId() )
        {
            case BALL_MARKER_ID: ballTracker.onBallMarkerDetected(marker); break;
            case TOP_LEFT_PLATFORM_CORNER_ID: ballTracker.onTopLeftPlatforCornerMarkerDetected(marker); break;
            case TOP_RIGHT_PLATFORM_CORNER_ID: ballTracker.onTopRightPlatforCornerMarkerDetected(marker); break;
            case BOTTOM_LEFT_PLATFORM_CORNER_ID: ballTracker.onBottomLeftPlatforCornerMarkerDetected(marker); break;
            case BOTTOM_RIGHT_PLATFORM_CORNER_ID: ballTracker.onBottomRightPlatforCornerMarkerDetected(marker); break;
        }
    }
}
