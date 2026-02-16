package it.unical.mat.smart_playground.motioncontroller;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener
{
    private TextView orientationTextView;
    private TextView stepsTextView;

    private SensorManager sensorManager;
    private MotionManager motionManager;

    private Sensor orientationSensor;
    private Sensor accelSensor;

    private final StepCounter stepCounter = new StepCounter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orientationTextView = (TextView) findViewById(R.id.orientationTextView);
        stepsTextView = (TextView) findViewById(R.id.stepsTextView);
        System.out.println("ON CREATE 1");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        motionManager = MotionManager.getInstance();
        System.out.println("ON CREATE 2");

        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync)
        {
            System.out.println("SYNC");
            motionManager.onOrientationSync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
        motionManager.setUnknownOrientation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, orientationSensor, sensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelSensor, sensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        synchronized (this)
        {
            final int sensorType = sensorEvent.sensor.getType();
            if ( sensorType == Sensor.TYPE_ORIENTATION )
            {
                // get orientation around the z-axis
                final float orientationDegrees = Math.round(sensorEvent.values[0]);
                motionManager.updateOrientation(orientationDegrees);
                orientationTextView.setText(Float.toString(orientationDegrees) + "°");
            }
            else if ( sensorType == Sensor.TYPE_ACCELEROMETER )
            {
                final int currentSteps = stepCounter.getSteps();
                final int newSteps = stepCounter.onAccelValues(sensorEvent.values);
                if ( newSteps != currentSteps )
                {
                    motionManager.updateSteps(newSteps);
                    stepsTextView.setText(Integer.toString(newSteps) + " steps");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }
}
