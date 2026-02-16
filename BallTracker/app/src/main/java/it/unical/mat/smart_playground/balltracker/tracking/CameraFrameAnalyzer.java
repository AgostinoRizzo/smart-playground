package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by utente on 05/10/2020.
 */
public interface CameraFrameAnalyzer
{
    public void onCameraStarted( final int width, final int height );
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
    public void onPause();
    public List<Marker> getDetectedMarkers();
}
