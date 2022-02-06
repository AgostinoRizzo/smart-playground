package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by utente on 05/10/2020.
 */
public abstract class SyncCameraFrameAnalyzer implements CameraFrameAnalyzer
{
    private final Lock lock = new ReentrantLock();

    @Override
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        lock.lock();
        try { return syncAnalyzeFrame(inputFrame); }
        finally { lock.unlock(); }
    }

    @Override
    public void onPause()
    {
        lock.lock();
        syncOnPause();
        lock.unlock();
    }

    protected abstract Mat syncAnalyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
    protected abstract void syncOnPause();
}
