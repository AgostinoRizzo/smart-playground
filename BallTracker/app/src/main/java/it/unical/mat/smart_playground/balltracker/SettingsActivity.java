package it.unical.mat.smart_playground.balltracker;

import it.unical.mat.smart_playground.balltracker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class SettingsActivity extends Activity implements View.OnClickListener
{
    private EditText minLocDeltaTextEdit;
    private EditText minOrientDeltaTextEdit;
    private TextView infoTextview;

    private Properties prop = new Properties();
    private boolean settingsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        minLocDeltaTextEdit = (EditText) findViewById(R.id.minLocDeltaTextEdit);
        minOrientDeltaTextEdit = (EditText) findViewById(R.id.minOrientDeltaTextEdit);
        infoTextview = (TextView) findViewById(R.id.infoTextview);

        final Button saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(this);

        loadSettings();
    }

    @Override
    public void onClick(View view)
    {
        if ( !settingsLoaded )
        {
            infoTextview.setText("Settings not loaded.");
            return;
        }
        saveSettings();
    }

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

            minLocDeltaTextEdit.setText(prop.getProperty("min_loc_delta"));
            minOrientDeltaTextEdit.setText(prop.getProperty("min_dir_delta"));

            settingsLoaded = true;
            infoTextview.setText("Settings loaded.");
        }
        catch ( IOException e ) { System.out.println(e.getMessage()); }
    }

    private void saveSettings()
    {
        try
        {
            prop.setProperty("min_loc_delta", minLocDeltaTextEdit.getText().toString());
            prop.setProperty("min_dir_delta", minOrientDeltaTextEdit.getText().toString());

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
