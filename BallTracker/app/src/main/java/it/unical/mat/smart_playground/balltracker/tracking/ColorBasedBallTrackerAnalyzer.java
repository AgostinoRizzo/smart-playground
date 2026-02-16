package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.Tracking;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.smart_playground.balltracker.view.OpenCVTrackingView;

/**
 * Created by utente on 08/02/2022.
 */
public class ColorBasedBallTrackerAnalyzer implements CameraFrameAnalyzer
{
    private static final int H_GREEN = 60;
    private static final int LOW_SV  = 100;
    private static final int HIGH_SV = 255;

    private Mat mat1, mat2;
    private List<Marker> detectedMarkers = new ArrayList<>();
    private Double[][] ballMarkerCornersCoords = new Double[4][2];

    public ColorBasedBallTrackerAnalyzer() {}

    @Override
    public void onCameraStarted(int width, int height)
    {
        mat1 = new Mat(width, height, CvType.CV_16UC4);
        mat2 = new Mat(width, height, CvType.CV_16UC4);
    }

    @Override
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Imgproc.cvtColor(inputFrame.rgba(), mat1, Imgproc.COLOR_BGR2HSV);

        final int colorDetectionSensitivity = BallTracker.getTrackingSettings().getColorDetectionSensitivity();
        final Scalar scalarLow  = new Scalar(H_GREEN - colorDetectionSensitivity, LOW_SV, LOW_SV);
        final Scalar scalarHigh = new Scalar(H_GREEN + colorDetectionSensitivity, HIGH_SV, HIGH_SV);
        Core.inRange(mat1, scalarLow, scalarHigh, mat2);

        final List<MatOfPoint> contours = new ArrayList<>();
        final Mat hierarchy = new Mat();
        Imgproc.findContours(mat2, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        detectedMarkers.clear();

        if ( !contours.isEmpty() )
        {
            Rect maxRect = null;
            double maxArea = -1.0, currArea;

            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
            {
                final MatOfPoint matOfPoint = contours.get(idx);
                final Rect rect = Imgproc.boundingRect(matOfPoint);
                //Imgproc.rectangle(inputFrame.rgba(), rect.tl(), rect.br(), new Scalar(0, 0, 255));

                currArea = rect.area();
                if ( maxRect == null || currArea > maxArea )
                {
                    maxRect = rect;
                    maxArea = currArea;
                }
            }

            final TrackingSettings trackingSettings = BallTracker.getTrackingSettings();
            if ( maxRect != null && maxArea >= trackingSettings.getMinBallDetectArea() )
            {
                final Point tl = maxRect.tl();
                final Point br = maxRect.br();

                ballMarkerCornersCoords[0][Marker.X] = ballMarkerCornersCoords[1][Marker.X] =
                ballMarkerCornersCoords[2][Marker.X] = ballMarkerCornersCoords[3][Marker.X] = (tl.x + br.x) / 2;
                ballMarkerCornersCoords[0][Marker.Y] = ballMarkerCornersCoords[1][Marker.Y] =
                ballMarkerCornersCoords[2][Marker.Y] = ballMarkerCornersCoords[3][Marker.Y] = (tl.y + br.y) / 2;

                detectedMarkers.add(new StaticallyOrientedMarker(ballMarkerCornersCoords, Marker.BALL_MARKER_ID));
            }
        }

        return null;
    }

    @Override
    public void onPause()
    {
        BallTracker.getInstance().getBallTrackingCommunicator().sendUnknownBallTrackingStatus();
    }

    @Override
    public List<Marker> getDetectedMarkers()
    {
        return detectedMarkers;
    }
}
