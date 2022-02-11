package it.unical.mat.smart_playground.balltracker.view;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.DecimalFormat;
import java.util.List;

import it.unical.mat.smart_playground.balltracker.tracking.ArucoBasedBallTrackerAnalyzer;
import it.unical.mat.smart_playground.balltracker.tracking.BallTracker;
import it.unical.mat.smart_playground.balltracker.tracking.Marker;
import it.unical.mat.smart_playground.balltracker.util.Vector2;
import it.unical.mat.smart_playground.balltracker.util.Vector2Int;

/**
 * Created by utente on 30/10/2020.
 */
public class OpenCVTrackingView implements TrackingView
{
    public static final Scalar MARKER_COLOR      = new Scalar(0, 0, 255);
    public static final Scalar ARROW_COLOR       = new Scalar(255, 0, 0);
    public static final Scalar ARROW_TEXT_COLOR  = new Scalar(0, 255, 0);
    public static final Scalar CORNER_COLOR      = new Scalar(0, 255, 0);
    public static final Scalar PADDING_COLOR     = new Scalar(0, 255, 0);

    private static OpenCVTrackingView instance = null;
    private final DecimalFormat decimalFormat = new DecimalFormat();
    private Mat frame = null;

    public static OpenCVTrackingView getInstance()
    {
        if ( instance == null )
            instance = new OpenCVTrackingView();
        return instance;
    }

    private OpenCVTrackingView() { decimalFormat.setMaximumFractionDigits(2); }

    public void setFrame( final Mat frame )
    {
        this.frame = frame;
    }

    @Override
    public void drawTracking(List<Marker> detectMarkers, BallTracker ballTracker)
    {
        if ( !canDraw() )
            return;

        for ( final Marker marker : detectMarkers )
        {
            if ( marker.getId() == Marker.BALL_MARKER_ID )
            {
                final Vector2<Float> relativeCenterCoords = marker.getRelativeCenterCoords();
                final Vector2Int direction = marker.getDirection();
                drawMarker(marker, "X:" + decimalFormat.format(relativeCenterCoords.getX()) + " Y:" + decimalFormat.format(relativeCenterCoords.getY()));
                if ( !direction.isZero() )
                    drawOrientation(marker.getCenter(), direction, ballTracker.getBallStatus().getOrientation());
                //drawCorners(marker.getCornersCoords());
            }
            else drawMarker(marker);
        }

        drawPlatformPaddings(ballTracker.getPlatformFrameSize(), ballTracker.getPlatformPaddings());
    }

    @Override
    public void drawMarker(Marker marker)
    {
        Imgproc.circle(frame, getPointFromVector2Int(marker.getCenter()), 10, MARKER_COLOR, 10);
    }

    @Override
    public void drawMarker(Marker marker, String label)
    {
        drawMarker(marker);
        Imgproc.putText(frame, label, getPointFromVector2Int(marker.getCenter()), 0, 0.5, ARROW_TEXT_COLOR, 2);
    }

    @Override
    public void drawArrow(Vector2Int origin, Vector2Int arrowPoint)
    {
        Imgproc.arrowedLine(frame, getPointFromVector2Int(origin), getPointFromVector2Int(arrowPoint), ARROW_COLOR);
    }

    @Override
    public void drawArrow(Vector2Int origin, Vector2Int arrowPoint, String label)
    {
        drawArrow(origin, arrowPoint);
        Imgproc.putText(frame, label, getPointFromVector2Int(arrowPoint), 0, 0.5, ARROW_TEXT_COLOR, 2);
    }

    @Override
    public void drawOrientation(Vector2Int origin, Vector2Int direction, short orientation)
    {
        drawArrow(origin, origin.sum(direction), Short.toString(orientation) + " degrees");
    }

    @Override
    public void drawCorners(int[][] cornersCoords)
    {
        for ( int c=0; c<cornersCoords.length; ++c )
            Imgproc.putText(frame, Integer.toString(c+1), new Point(cornersCoords[c][0], cornersCoords[c][1]), 0, 0.5, CORNER_COLOR, 2);
    }

    @Override
    public void drawPlatformPaddings(Vector2<Integer> platformSize, int[] platformPaddings)
    {
        if ( platformSize == null )
            return;

        final int maxLeft = platformSize.getX() - 1;
        final int maxTop = platformSize.getY() - 1;

        Imgproc.line(frame, new Point(0, platformPaddings[BallTracker.PADDING_TOP_INDEX]), new Point(maxLeft, platformPaddings[BallTracker.PADDING_TOP_INDEX]), PADDING_COLOR);
        Imgproc.line(frame, new Point(maxLeft - platformPaddings[BallTracker.PADDING_RIGHT_INDEX], 0), new Point(maxLeft - platformPaddings[BallTracker.PADDING_RIGHT_INDEX], maxTop), PADDING_COLOR);
        Imgproc.line(frame, new Point(0, maxTop - platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), new Point(maxLeft, maxTop - platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), PADDING_COLOR);
        Imgproc.line(frame, new Point(platformPaddings[BallTracker.PADDING_LEFT_INDEX], 0), new Point(platformPaddings[BallTracker.PADDING_LEFT_INDEX], maxTop), PADDING_COLOR);

        /*
        Imgproc.rectangle(rgbaInputFrame, new Point(0, 0), new Point(platformSize.getX()-1, platformPaddings[BallTracker.PADDING_TOP_INDEX]), paddingColor);
        Imgproc.rectangle(rgbaInputFrame, new Point(platformSize.getX()-1-platformPaddings[BallTracker.PADDING_RIGHT_INDEX], 0), new Point(platformSize.getX()-1, platformSize.getY()-1), paddingColor);
        Imgproc.rectangle(rgbaInputFrame, new Point(0, platformSize.getY()-1-platformPaddings[BallTracker.PADDING_BOTTOM_INDEX]), new Point(platformSize.getX()-1, platformSize.getY()-1), paddingColor);
        Imgproc.rectangle(rgbaInputFrame, new Point(0, 0), new Point(0, platformPaddings[BallTracker.PADDING_LEFT_INDEX]), paddingColor);
        */
    }

    @Override
    public boolean canDraw()
    {
        return ( frame != null );
    }

    private static Point getPointFromVector2Int( final Vector2Int v )
    {
        return new Point(v.getX(), v.getY());
    }
}
