package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * Created by utente on 05/10/2020.
 */
public interface CameraFrameAnalyzer
{
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}
