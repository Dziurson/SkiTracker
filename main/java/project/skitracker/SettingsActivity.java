package project.skitracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import project.skitracker.settings.Properties;

public class SettingsActivity extends AppCompatActivity
{
    private MainActivity sender;
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
        sigma_value_bar.setProgress((int)(Properties.gpsKalmanFilterQvalue*10000));
        ro_value_bar.setProgress((int)(Properties.gpsKalmanFilterRvalue*1000));
        kalman_filtration_switch.setChecked(Properties.isFiltrationEnabled);
        update_interval_bar.setProgress(Properties.minDistanceBetweenGPSUpdates);
        update_delay_bar.setProgress(Properties.minTimeBetweenGPSUpdates/1000);
    }

    private void enableListeners()
    {
        update_interval_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                update_interval_field.setText(((Integer)update_interval_bar.getProgress()).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Properties.minDistanceBetweenGPSUpdates = update_interval_bar.getProgress();
            }
        });

        update_delay_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                update_delay_field.setText(((Integer)(Properties.minTimeBetweenGPSUpdates/1000)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Properties.minTimeBetweenGPSUpdates = update_delay_bar.getProgress()*1000;
            }
        });

        sigma_value_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                sigma_value_field.setText(((Double)(Properties.gpsKalmanFilterQvalue*10000)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Properties.gpsKalmanFilterQvalue = ((double)seekBar.getProgress())/10000d;
            }
        });

        ro_value_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                ro_value_field.setText(((Double)(Properties.gpsKalmanFilterRvalue*1000)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Properties.gpsKalmanFilterRvalue = ((double)seekBar.getProgress())/1000d;
            }
        });

        kalman_filtration_switch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Properties.isFiltrationEnabled = kalman_filtration_switch.isChecked();
            }
        });

        update_interval_field.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean has_focus)
            {
                if(!has_focus)
                {
                    String field_value = update_interval_field.getText().toString();
                    if(field_value.matches("([0-9]*[1-9]+[0-9]*)|([0-9]+(\\.|,)[0-9]*[1-9]+[0-9]*)"))
                    {
                        Properties.minDistanceBetweenGPSUpdates = (int)(Double.parseDouble(field_value));
                    }
                    else
                    {
                        Properties.minDistanceBetweenGPSUpdates = 5;
                    }
                    update_interval_bar.setProgress(Properties.minDistanceBetweenGPSUpdates);
                }
            }
        });

        update_delay_field.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean has_focus)
            {
                if(!has_focus)
                {
                    String field_value = update_delay_field.getText().toString();
                    if(field_value.matches("([0-9]*[1-9]+[0-9]*)|([0-9]+(\\.|,)[0-9]*[1-9]+[0-9]*)"))
                    {
                        Properties.minTimeBetweenGPSUpdates = (int)(Double.parseDouble(field_value)*1000);
                    }
                    else
                    {
                        Properties.minTimeBetweenGPSUpdates = 1000;
                    }
                    update_delay_bar.setProgress(Properties.minTimeBetweenGPSUpdates/1000);
                }
            }
        });

        sigma_value_field.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean has_focus)
            {
                if(!has_focus)
                {
                    String field_value = sigma_value_field.getText().toString();
                    if(field_value.matches("([0-9]*[1-9]+[0-9]*)|([0-9]+(\\.|,)[0-9]*[1-9]+[0-9]*)"))
                    {
                        Properties.gpsKalmanFilterQvalue = Double.parseDouble(field_value)/10000d;
                    }
                    else
                    {
                        Properties.gpsKalmanFilterQvalue = 0.0001;
                    }
                }
            }
        });

        ro_value_field.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean has_focus)
            {
                if(!has_focus)
                {
                    String field_value = ro_value_field.getText().toString();
                    if(field_value.matches("([0-9]*[1-9]+[0-9]*)|([0-9]+(\\.|,)[0-9]*[1-9]+[0-9]*)"))
                    {
                        Properties.gpsKalmanFilterRvalue = Double.parseDouble(field_value)/1000d;
                    }
                    else
                    {
                        Properties.gpsKalmanFilterRvalue = 0.01;
                    }
                }
            }
        });
    }

}
