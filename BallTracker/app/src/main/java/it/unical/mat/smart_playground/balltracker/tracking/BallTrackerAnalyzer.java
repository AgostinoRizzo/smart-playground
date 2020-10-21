package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

/**
 * Created by utente on 05/10/2020.
 */
public class BallTrackerAnalyzer extends SyncCameraFrameAnalyzer
{
    private static final int BALL_MARKER_ID                  = 0;
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
        Vector2<Integer> markerCenter;

        for ( final Marker marker : detectMarkers )
        {
            analyzeMarker(marker);

            markerCenter = marker.getCenter();
            Imgproc.circle(rgbaInputFrame, new Point(markerCenter.getX(), markerCenter.getY()), 10, new Scalar(0, 0, 255), 10);
        }

        final BallTracker ballTracker = BallTracker.getInstance();
        final Vector2<Integer> platformSize = ballTracker.getPlatformFrameSize();
        if ( platformSize != null )
        {
            final int[] platformPaddings = ballTracker.getPlatformPaddings();
            final Scalar paddingColor = new Scalar(0, 255, 0);
            final int maxLeft = platformSize.getX() - 1;
            final int maxTop = platformSize.getY() - 1;

            Imgproc.line(rgbaInputFrame, new Point(0, platformPaddings[BallTracker.PADDING_TOP_INDEX]), new Point(maxLeft, platformPaddings[BallTracker.PADDING_TOP_INDEX]), paddingColor);
            Imgproc.line(rgbaInputFrame, new Point(maxLeft - platformPaddings[BallTracker.PADDING_RIGHT_INDEX], 0), new Point(maxLeft - platformPaddings[BallTracker.PADDING_RIGHT_INDEX], maxTop), paddingColor);
            Imgproc.line(rgbaInputFrame, new Point(0, maxTop - platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), new Point(maxLeft, maxTop - platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), paddingColor);
            Imgproc.line(rgbaInputFrame, new Point(platformPaddings[BallTracker.PADDING_LEFT_INDEX], 0), new Point(platformPaddings[BallTracker.PADDING_LEFT_INDEX], maxTop), paddingColor);

            /*
            Imgproc.rectangle(rgbaInputFrame, new Point(0, 0), new Point(platformSize.getX()-1, platformPaddings[BallTracker.PADDING_TOP_INDEX]), paddingColor);
            Imgproc.rectangle(rgbaInputFrame, new Point(platformSize.getX()-1-platformPaddings[BallTracker.PADDING_RIGHT_INDEX], 0), new Point(platformSize.getX()-1, platformSize.getY()-1), paddingColor);
            Imgproc.rectangle(rgbaInputFrame, new Point(0, platformSize.getY()-1-platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), new Point(platformSize.getX()-1, platformSize.getY()-1), paddingColor);
            Imgproc.rectangle(rgbaInputFrame, new Point(0, 0), new Point(0, platformPaddings[BallTracker.PADDING_LEFT_INDEX]), paddingColor);
            */
        }

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
