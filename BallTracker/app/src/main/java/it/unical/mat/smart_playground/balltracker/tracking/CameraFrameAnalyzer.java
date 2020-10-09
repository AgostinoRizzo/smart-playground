package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;

/**
 * Created by utente on 05/10/2020.
 */
public interface CameraFrameAnalyzer
{
    public void analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}
