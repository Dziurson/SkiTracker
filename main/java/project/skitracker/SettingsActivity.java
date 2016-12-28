package project.skitracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import project.skitracker.settings.Properties;

public class SettingsActivity extends AppCompatActivity
{
    private EditText update_interval_field;
    private EditText update_delay_field;
    private EditText sigma_value_field;
    private EditText ro_value_field;
    private Switch kalman_filtration_switch;
    private SeekBar update_interval_bar;
    private SeekBar update_delay_bar;
    private SeekBar sigma_value_bar;
    private SeekBar ro_value_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        enableListeners();
    }

    private void initialize()
    {
        update_interval_field = (EditText)findViewById(R.id.update_interval_field);
        update_delay_field = (EditText)findViewById(R.id.update_delay_field);
        sigma_value_field = (EditText)findViewById(R.id.sigma_value_field);
        ro_value_field = (EditText)findViewById(R.id.ro_value_field);
        kalman_filtration_switch = (Switch)findViewById(R.id.kalman_filtration_switch);
        update_interval_bar = (SeekBar)findViewById(R.id.update_interval_bar);
        update_delay_bar = (SeekBar)findViewById(R.id.update_delay_bar);
        sigma_value_bar = (SeekBar)findViewById(R.id.sigma_value_bar);
        ro_value_bar = (SeekBar)findViewById(R.id.ro_value_bar);
        update_interval_field.setText(((Integer)Properties.minDistanceBetweenGPSUpdates).toString());
        update_delay_field.setText(((Integer)Properties.minTimeBetweenGPSUpdates).toString());
        sigma_value_field.setText(((Double)Properties.gpsKalmanFilterQvalue).toString());
        ro_value_field.setText(((Double)Properties.gpsKalmanFilterRvalue).toString());
        sigma_value_bar.setProgress((int)(Properties.gpsKalmanFilterQvalue*100000));
        ro_value_bar.setProgress((int)(Properties.gpsKalmanFilterRvalue*10000));
        kalman_filtration_switch.setChecked(Properties.isFiltrationEnabled);
        update_interval_bar.setProgress(Properties.minDistanceBetweenGPSUpdates);
        update_delay_bar.setProgress(Properties.minTimeBetweenGPSUpdates/1000);
    }

    private void enableListeners()
    {

    }

}
