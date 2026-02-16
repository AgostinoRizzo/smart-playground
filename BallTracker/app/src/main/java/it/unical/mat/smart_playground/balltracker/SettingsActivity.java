package it.unical.mat.smart_playground.balltracker;

import it.unical.mat.smart_playground.balltracker.tracking.UDPBallTrackingCommunicator;
import it.unical.mat.smart_playground.balltracker.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SettingsActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
    private static final int BALL_DETECT_AREA_UPPER_BOUND = 5000;
    private static final int COLOR_DETECTION_SENSITIVITY_UPPER_BOUND = 50;

    private EditText maxFpsTextEdit;
    private EditText minLocDeltaTextEdit;
    private EditText minOrientDeltaTextEdit;
    private EditText arucoDetectionDeltaTextEdit;

    private TextView minBallDetectionAreaTextView;
    private SeekBar  minBallDetectionAreaSeekBar;

    private TextView colorDetectionSensitivityTextView;
    private SeekBar  colorDetectionSensitivitySeekBar;

    private CheckBox useColorBoosterCheckBox;

    private TextView destAddressNamesTextView;

    private Button saveSettingsButton;
    private ImageButton resetSettingsButton;

    private TextView infoTextview;

    private Properties prop = new Properties();
    private boolean settingsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        maxFpsTextEdit = (EditText) findViewById(R.id.maxFpsTextEdit);
        minLocDeltaTextEdit = (EditText) findViewById(R.id.minLocDeltaTextEdit);
        minOrientDeltaTextEdit = (EditText) findViewById(R.id.minOrientDeltaTextEdit);
        arucoDetectionDeltaTextEdit = (EditText) findViewById(R.id.arucoDetectionDeltaTextEdit);

        minBallDetectionAreaTextView = (TextView) findViewById(R.id.minBallDetectionAreaTextView);
        minBallDetectionAreaSeekBar = (SeekBar) findViewById(R.id.minBallDetectionAreaSeekBar);

        colorDetectionSensitivityTextView = (TextView) findViewById(R.id.colorDetectionSensitivityTextView);
        colorDetectionSensitivitySeekBar = (SeekBar) findViewById(R.id.colorDetectionSensitivitySeekBar);

        useColorBoosterCheckBox = (CheckBox) findViewById(R.id.useColorBoosterCheckBox);

        destAddressNamesTextView = (TextView) findViewById(R.id.destAddressNamesTextView);

        infoTextview = (TextView) findViewById(R.id.infoTextview);

        updateMinBallDetectionAreaTextView(minBallDetectionAreaSeekBar.getProgress());
        minBallDetectionAreaSeekBar.setOnSeekBarChangeListener(this);

        updateColorDetectionSensitivityTextView(colorDetectionSensitivitySeekBar.getProgress());
        colorDetectionSensitivitySeekBar.setOnSeekBarChangeListener(this);

        saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(this);

        resetSettingsButton = (ImageButton) findViewById(R.id.resetSettingsButton);
        resetSettingsButton.setOnClickListener(this);

        loadSettings();
        showDestAddrNames();
    }

    @Override
    public void onClick(View view)
    {
        if ( view == saveSettingsButton ) onSaveSettings();
        else if ( view == resetSettingsButton ) onResetSettings();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if ( seekBar == minBallDetectionAreaSeekBar )
            updateMinBallDetectionAreaTextView(progress);
        else if ( seekBar == colorDetectionSensitivitySeekBar )
            updateColorDetectionSensitivityTextView(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void updateMinBallDetectionAreaTextView( final int seekBarProgress )
    { minBallDetectionAreaTextView.setText(Integer.toString(getMinBallDetectionAreaFromProgress(seekBarProgress))); }

    private void updateColorDetectionSensitivityTextView(final int seekBarProgress)
    { colorDetectionSensitivityTextView.setText(Integer.toString(getColorDetectionSensitivityFromProgress(seekBarProgress))); }

    public void onSaveSettings()
    {
        if (!settingsLoaded)
        {
            infoTextview.setText("Settings not loaded.");
            return;
        }
        saveSettings();
    }

    public void onResetSettings()
    {
        maxFpsTextEdit.setText("20");
        minLocDeltaTextEdit.setText("0.0001");
        minOrientDeltaTextEdit.setText("1");
        arucoDetectionDeltaTextEdit.setText("200");

        updateMinBallDetectionAreaTextView(40);
        minBallDetectionAreaSeekBar.setProgress(40);

        updateColorDetectionSensitivityTextView(40);
        colorDetectionSensitivitySeekBar.setProgress(40);

        useColorBoosterCheckBox.setChecked(true);
    }

    private void showDestAddrNames()
    {
        final StringBuilder text = new StringBuilder("Dst addr names: ");
        for ( int i=0; i<UDPBallTrackingCommunicator.DESTINATION_ADDRESS_NAMES.length; ++i )
            text.append( (i > 0 ? ", " : "") + UDPBallTrackingCommunicator.DESTINATION_ADDRESS_NAMES[i]);
        destAddressNamesTextView.setText(text.toString());
    }

    public static int getMinBallDetectionAreaFromProgress( final int progress )
    { return (int) (BALL_DETECT_AREA_UPPER_BOUND * (progress/100.0)); }

    public static int getColorDetectionSensitivityFromProgress( final int progress )
    { return (int) (COLOR_DETECTION_SENSITIVITY_UPPER_BOUND * (progress/100.0)); }

    private void loadSettings()
    {
        InputStream istream = null;
        try
        {
            final File file = new File(getCacheDir(), MainActivity.PROPERTIES_FILENAME);
            istream = new FileInputStream(file);
        }
        catch ( FileNotFoundException e ) { istream = null; }

        try
        {
            if ( istream == null )
                istream = getBaseContext().getAssets().open(MainActivity.PROPERTIES_FILENAME);
            prop.load(istream);
            istream.close();

            maxFpsTextEdit.setText(prop.getProperty("max_fps"));
            minLocDeltaTextEdit.setText(prop.getProperty("min_loc_delta"));
            minOrientDeltaTextEdit.setText(prop.getProperty("min_dir_delta"));
            arucoDetectionDeltaTextEdit.setText(prop.getProperty("aruco_detect_delta"));

            try
            {
                final int minBallDetectAreaProgress = Integer.parseInt(prop.getProperty("min_ball_detect_area"));
                updateMinBallDetectionAreaTextView(minBallDetectAreaProgress);
                minBallDetectionAreaSeekBar.setProgress(minBallDetectAreaProgress);
            }
            catch ( NumberFormatException nfe ) {}

            try
            {
                final int colorDetectionSensitivity = Integer.parseInt(prop.getProperty("color_detect_sensitivity"));
                updateColorDetectionSensitivityTextView(colorDetectionSensitivity);
                colorDetectionSensitivitySeekBar.setProgress(colorDetectionSensitivity);
            }
            catch ( NumberFormatException nfe ) {}

            final String userColorBoosterProp = prop.getProperty("use_color_booster");
            useColorBoosterCheckBox.setChecked(userColorBoosterProp != null && userColorBoosterProp.equals("set"));

            settingsLoaded = true;
            infoTextview.setText("Settings loaded.");
        }
        catch ( IOException e ) { System.out.println(e.getMessage()); }
    }

    private void saveSettings()
    {
        try
        {
            prop.setProperty("max_fps", maxFpsTextEdit.getText().toString());
            prop.setProperty("min_loc_delta", minLocDeltaTextEdit.getText().toString());
            prop.setProperty("min_dir_delta", minOrientDeltaTextEdit.getText().toString());
            prop.setProperty("aruco_detect_delta", arucoDetectionDeltaTextEdit.getText().toString());
            prop.setProperty("min_ball_detect_area", Integer.toString(minBallDetectionAreaSeekBar.getProgress()));
            prop.setProperty("color_detect_sensitivity", Integer.toString(colorDetectionSensitivitySeekBar.getProgress()));

            prop.setProperty("use_color_booster", useColorBoosterCheckBox.isChecked() ? "set" : "notset");

            final File file = new File(getCacheDir(), MainActivity.PROPERTIES_FILENAME);
            final FileWriter fw = new FileWriter(file.getAbsoluteFile());
            prop.store(fw,"");
            fw.close();

            MainActivity.getInstance().loadProperies();
            infoTextview.setText("Settings saved.");
        }
        catch ( IOException e ) { System.out.println(e.getMessage()); }
    }
}
