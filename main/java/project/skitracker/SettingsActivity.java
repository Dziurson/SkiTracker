package project.skitracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import project.skitracker.listeners.OnFocusChangeCustomListener;
import project.skitracker.listeners.OnSeekBarChangeCustomListener;
import project.skitracker.settings.Properties;

/**
 * Klasa odpowiadająca za dostosowywanie parametrów aplikacji
 */
public class SettingsActivity extends AppCompatActivity
{
    /**
     * Pole odpowiadające miejscu na wpisanie dystansu między aktualizacjami.
     */
    private EditText update_interval_field;
    /**
     * Pole odpowiadające miejscu na wpisanie czasu między aktualizacjami.
     */
    private EditText update_delay_field;
    /**
     * Pole odpowiadające miejscu na wpisanie wartości sigma filtracji Kalmana.
     */
    private EditText sigma_value_field;
    /**
     * Pole odpowiadające miejscu na wpisanie wartosci ro filtracji Kalmana.
     */
    private EditText ro_value_field;
    /**
     * Pole odpowiadające przełącznikowi Filtracji kalmana (on/off)
     */
    private Switch kalman_filtration_switch;
    /**
     * Pole odpowiadające paskowi do wyboru dystansu między aktualizacjami
     */
    private SeekBar update_interval_bar;
    /**
     * Pole odpowiadające paskowi do wyboru czasu między aktualizacjami
     */
    private SeekBar update_delay_bar;
    /**
     * Pole odpowiadające paskowi do wyboru wartości sigma filtracji Kalmana
     */
    private SeekBar sigma_value_bar;
    /**
     * Pole odpowiadające paskowi do wyboru wartości ro filtracji Kalmana
     */
    private SeekBar ro_value_bar;

    /**
     * Funcka uruchamiana przy otwarciu aktywności, inicjalizująca podstawowe parametry.
     * @param savedInstanceState
     */
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

    /**
     * Funkcja inicjalizująca pola klasy wartościami uzyskanym na podstawie widoku
     */
    private void initialize()
    {
        update_interval_field = (EditText) findViewById(R.id.update_interval_field);
        update_delay_field = (EditText) findViewById(R.id.update_delay_field);
        sigma_value_field = (EditText) findViewById(R.id.sigma_value_field);
        ro_value_field = (EditText) findViewById(R.id.ro_value_field);
        kalman_filtration_switch = (Switch) findViewById(R.id.kalman_filtration_switch);
        update_interval_bar = (SeekBar) findViewById(R.id.update_interval_bar);
        update_delay_bar = (SeekBar) findViewById(R.id.update_delay_bar);
        sigma_value_bar = (SeekBar) findViewById(R.id.sigma_value_bar);
        ro_value_bar = (SeekBar) findViewById(R.id.ro_value_bar);
        update_interval_field.setText(((Integer) Properties.minDistanceBetweenGPSUpdates).toString());
        update_delay_field.setText(((Integer) Properties.minTimeBetweenGPSUpdates).toString());
        sigma_value_field.setText(((Double) Properties.gpsKalmanFilterQvalue).toString());
        ro_value_field.setText(((Double) Properties.gpsKalmanFilterRvalue).toString());
        sigma_value_bar.setProgress((int) (Properties.gpsKalmanFilterQvalue * 10000));
        ro_value_bar.setProgress((int) (Properties.gpsKalmanFilterRvalue * 1000));
        kalman_filtration_switch.setChecked(Properties.isFiltrationEnabled);
        update_interval_bar.setProgress(Properties.minDistanceBetweenGPSUpdates);
        update_delay_bar.setProgress(Properties.minTimeBetweenGPSUpdates / 1000);
    }

    /**
     * Funkcja aktywująca EventListenery dla pól i pasków, w wyniku czego zostają zmieniane wartości w klasie Propeties.
     */
    private void enableListeners()
    {
        update_interval_bar.setOnSeekBarChangeListener(new OnSeekBarChangeCustomListener(update_interval_field, "minDistanceBetweenGPSUpdates", Integer.TYPE, 1, this));
        update_delay_bar.setOnSeekBarChangeListener(new OnSeekBarChangeCustomListener(update_delay_field, "minTimeBetweenGPSUpdates", Integer.TYPE, 0.001d, this));
        sigma_value_bar.setOnSeekBarChangeListener(new OnSeekBarChangeCustomListener(sigma_value_field, "gpsKalmanFilterQvalue", Double.TYPE, 10000d, this));
        ro_value_bar.setOnSeekBarChangeListener(new OnSeekBarChangeCustomListener(ro_value_field, "gpsKalmanFilterRvalue", Double.TYPE, 1000d, this));
        update_interval_field.setOnFocusChangeListener(new OnFocusChangeCustomListener(update_interval_field, update_interval_bar, "minDistanceBetweenGPSUpdates", Integer.TYPE, 1, this));
        update_delay_field.setOnFocusChangeListener(new OnFocusChangeCustomListener(update_delay_field, update_delay_bar, "minTimeBetweenGPSUpdates", Integer.TYPE, 0.001d, this));
        sigma_value_field.setOnFocusChangeListener(new OnFocusChangeCustomListener(sigma_value_field, sigma_value_bar, "gpsKalmanFilterQvalue", Double.TYPE, 10000d, this));
        ro_value_field.setOnFocusChangeListener(new OnFocusChangeCustomListener(ro_value_field, ro_value_bar, "gpsKalmanFilterRvalue", Double.TYPE, 1000d, this));

        kalman_filtration_switch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Properties.isFiltrationEnabled = kalman_filtration_switch.isChecked();
            }
        });
    }
}