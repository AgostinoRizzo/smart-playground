package it.unical.mat.smart_playground.balltracker;

import it.unical.mat.smart_playground.balltracker.tracking.BallTracker;
import it.unical.mat.smart_playground.balltracker.tracking.CameraFrameAnalyzer;
import it.unical.mat.smart_playground.balltracker.tracking.ComposedBallTrackerAnalyzer;
import it.unical.mat.smart_playground.balltracker.tracking.TrackingSettings;
import it.unical.mat.smart_playground.balltracker.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    static final String PROPERTIES_FILENAME = "config.properties";

    private static MainActivity instance = null;

    private CameraBridgeViewBase mOpenCvCameraView;
    private final CameraFrameAnalyzer cameraFrameAnalyzer = ComposedBallTrackerAnalyzer.getInstance();

    //private final Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250);
    //private final DetectorParameters parameters = DetectorParameters.create();

    public static MainActivity getInstance()
    {
        return instance;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if ( item.getItemId() == R.id.settingsMenu )
        {
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return false;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        instance = this;
        loadProperies();

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setMaxFrameSize(800, 600);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        //mOpenCvCameraView.setCameraPermissionGranted();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        //cameraFrameAnalyzer.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height)
    {
        Log.i(TAG, "called onCameraViewStarted");
        cameraFrameAnalyzer.onCameraStarted(width, height);
    }

    public void onCameraViewStopped() {
        Log.i(TAG, "called onCameraViewStopped");
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        return cameraFrameAnalyzer.analyzeFrame(inputFrame);
    }

    protected void loadProperies()
    {
        InputStream istream = null;
        try
        {
            final File file = new File(getCacheDir(), MainActivity.PROPERTIES_FILENAME);
            istream  = new FileInputStream(file);
        }
        catch ( Exception e ) { istream = null; }

        try
        {
            if (istream == null)
                istream = getBaseContext().getAssets().open(PROPERTIES_FILENAME);

            final Properties prop = new Properties();
            prop.load(istream);

            try
            {
                final float minLocDelta = Float.parseFloat(prop.getProperty("min_loc_delta"));
                final short minDirDelta = Short.parseShort(prop.getProperty("min_dir_delta"));
                final long  arucoDetectDelta = Long.parseLong(prop.getProperty("aruco_detect_delta"));
                final int   minBallDetectDelta = Integer.parseInt(prop.getProperty("min_ball_detect_area"));
                final int   colorDetectionSensitivity = Integer.parseInt(prop.getProperty("color_detect_sensitivity"));

                final String userColorBoosterProp = prop.getProperty("use_color_booster");
                final boolean useColorBooster = userColorBoosterProp != null && userColorBoosterProp.equals("set");

                final TrackingSettings settings = new TrackingSettings( minLocDelta, minDirDelta, arucoDetectDelta, minBallDetectDelta, colorDetectionSensitivity, useColorBooster );
                BallTracker.updateTrackingSettings(settings);
            }
            catch ( NumberFormatException nfe ) {}
            finally { istream.close(); }
        }
        catch ( IOException e ) { System.out.println(e.getMessage()); }
    }
}
