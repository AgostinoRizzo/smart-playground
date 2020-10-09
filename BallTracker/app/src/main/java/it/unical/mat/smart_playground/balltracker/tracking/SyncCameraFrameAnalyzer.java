package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by utente on 05/10/2020.
 */
public abstract class SyncCameraFrameAnalyzer implements CameraFrameAnalyzer
{
    private final Lock lock = new ReentrantLock();

    @Override
    public void analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        lock.lock();
        try { syncAnalyzeFrame(inputFrame); }
        finally { lock.unlock(); }
    }

    protected abstract void syncAnalyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}
