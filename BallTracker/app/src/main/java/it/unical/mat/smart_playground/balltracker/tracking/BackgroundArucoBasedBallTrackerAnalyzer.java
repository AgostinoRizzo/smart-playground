package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by utente on 09/02/2022.
 */
public class BackgroundArucoBasedBallTrackerAnalyzer extends Thread implements CameraFrameAnalyzer
{
    private static BackgroundArucoBasedBallTrackerAnalyzer instance = null;
    private final Lock lock = new ReentrantLock();
    private final Condition inputFrameToAnalyzeAvailableCond = lock.newCondition();
    private final Condition detectedMarkersAvailableCond = lock.newCondition();
    private final ArucoBasedBallTrackerAnalyzer analyzer = new ArucoBasedBallTrackerAnalyzer();
    private Mat inputFrameToAnalyze = null;
    private List<Marker> detectedMarkers = null;

    public static BackgroundArucoBasedBallTrackerAnalyzer getInstance()
    {
        if ( instance == null )
            instance = new BackgroundArucoBasedBallTrackerAnalyzer();
        return instance;
    }

    private BackgroundArucoBasedBallTrackerAnalyzer() { setDaemon(true); start(); }

    @Override
    public void onCameraStarted(int width, int height)
    {
        lock.lock();
        analyzer.onCameraStarted(width, height);
        lock.unlock();
    }

    @Override
    public Mat analyzeFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        lock.lock();
        inputFrameToAnalyze = inputFrame.gray().clone();
        inputFrameToAnalyzeAvailableCond.signal();
        lock.unlock();
        return null;
    }

    @Override
    public void onPause() {}

    @Override
    public List<Marker> getDetectedMarkers()
    {
        try
        {
            lock.lock();
            return detectedMarkers;
        }
        finally { lock.unlock(); }
    }

    public void clearResult()
    {
        lock.lock();
        detectedMarkers = null;
        lock.unlock();
    }

    public List<Marker> waitForDetectedMarkers()
    {
        try
        {
            lock.lock();
            while ( detectedMarkers == null )
                try {  detectedMarkersAvailableCond.await(); }
                catch (InterruptedException e) { e.printStackTrace(); }
            return detectedMarkers;
        }
        finally { lock.unlock(); }
    }



    @Override
    public void run()
    {
        lock.lock();
        while (true)
        {
            while ( inputFrameToAnalyze == null )
                try { inputFrameToAnalyzeAvailableCond.await(); }
                catch (InterruptedException e) { e.printStackTrace(); }

            analyzer.analyzeFrame(inputFrameToAnalyze);
            final List<Marker> newDetectedMarkers = analyzer.getDetectedMarkers();

            if ( detectedMarkers == null )
                detectedMarkers = new ArrayList<>();

            if ( newDetectedMarkers != null )
            {
                if ( newDetectedMarkers.isEmpty() )
                {
                    final Marker m = Marker.searchMarker(detectedMarkers, Marker.GOLF_HOLE_ID);
                    if ( m != null )
                        detectedMarkers.remove(m);
                }
                else
                {
                    for (final Marker nm : newDetectedMarkers)
                    {
                        final Marker m = Marker.searchMarker(detectedMarkers, nm.getId());
                        if (m == null) detectedMarkers.add(nm);
                        else
                        {
                            detectedMarkers.remove(m);
                            detectedMarkers.add(nm);
                        }
                    }
                }
            }

            inputFrameToAnalyze = null;
            detectedMarkersAvailableCond.signalAll();
        }
    }
}
