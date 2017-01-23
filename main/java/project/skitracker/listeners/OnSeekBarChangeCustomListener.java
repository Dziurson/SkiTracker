package project.skitracker.listeners;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import project.skitracker.SettingsActivity;
import project.skitracker.settings.Properties;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * System obsługi zdarzeń przy zmianie paska postepu w menu ustawień.
 */
public class OnSeekBarChangeCustomListener implements SeekBar.OnSeekBarChangeListener
{
    private TextView field;
    private Type typ;
    private double coeff;
    private SettingsActivity settingsActivity;
    private Field prop;

    /**
     * Konstruktor listenera
     * @param field Dowolne pole dziedziczące po TextView, które jest modyfikowane podczas zmieniania wartości paska
     * @param fieldname Nazwa pola w klasie Propeties które ma zostać zmodyfikowane
     * @param typ Typ pola w plasie propeties, które ma zostać zmodyfikowane
     * @param coeff Współczynnik wartości (np jezeli operujemy na danych rzędu 10^-3 - 10^-4, współczynnik powinien wynoscć 10^-4
     * @param settingsActivity Aktywność ustawienia, w której znajdują się pola.
     */
    public OnSeekBarChangeCustomListener(TextView field, String fieldname, Type typ, double coeff, SettingsActivity settingsActivity)
    {
        this.settingsActivity = settingsActivity;
        this.field = field;
        this.typ = typ;
        try
        {
            prop = Properties.class.getDeclaredField(fieldname);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        this.coeff = coeff;
    }

    /**
     * Przy zamianie postepu aktualizowane jest pole Textview
     */
    @Override
    @SuppressWarnings("all")
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {
        field.setText(((Double) ((double) seekBar.getProgress() / coeff)).toString());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    /**
     * Przy zakonczeniu modyfikacji paska, zmieniana jest wartość pola w klasie Properties
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        try
        {
            if (prop != null)
            {
                if (typ == Double.TYPE) prop.set(null, ((double) seekBar.getProgress() / coeff));
                if (typ == Integer.TYPE) prop.set(null, (int) (seekBar.getProgress() / coeff));
                Toast.makeText(settingsActivity, "Wartość " + prop.getName() + ": " + prop.get(null), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(settingsActivity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
