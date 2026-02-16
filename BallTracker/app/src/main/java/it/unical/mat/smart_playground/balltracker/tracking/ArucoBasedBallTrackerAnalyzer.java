package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.List;

import it.unical.mat.smart_playground.balltracker.view.OpenCVTrackingView;

/**
 * Created by utente on 05/10/2020.
 */
public class ArucoBasedBallTrackerAnalyzer implements CameraFrameAnalyzer
{
    private List<Marker> detectedMarkers = null;

    public ArucoBasedBallTrackerAnalyzer() {}

    @Override
    public void onCameraStarted(int width, int height) {}

    @Override
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        return analyzeFrame(inputFrame.gray());
    }

    public Mat analyzeFrame(Mat grayInputFrame)
    {
        detectedMarkers = ArucoMarkerDetector.getInstance().detectMarkers(grayInputFrame);
        return null;
    }

    @Override
    public void onPause() {}

    @Override
    public List<Marker> getDetectedMarkers()
    {
        return detectedMarkers;
    }
}
