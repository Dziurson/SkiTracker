package project.skitracker.listeners;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import project.skitracker.SettingsActivity;
import project.skitracker.settings.Properties;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * System obsługi zdarzeń przy zmianie wartości pól aktywności Setting
 */
public class OnFocusChangeCustomListener implements View.OnFocusChangeListener
{
    private SeekBar bar;
    private Field prop;
    private Type typ;
    private SettingsActivity settingsActivity;
    private TextView field;
    private double coeff;

    /**
     * Konstruktor listenera
     * @param field Dowolne pole dziedziczące po TextView, do którego zostaje przypięty Listener
     * @param bar Pasek, którego wartość ma ulegać zmianie po wpisaniu wartości do pola TextView
     * @param fieldname Nazwa pola w klasie Propeties które ma zostać zmodyfikowane
     * @param typ Typ pola w plasie propeties, które ma zostać zmodyfikowane
     * @param coeff Współczynnik wartości (np jezeli operujemy na danych rzędu 10^-3 - 10^-4, współczynnik powinien wynoscć 10^-4
     * @param settingsActivity Aktywność ustawienia, w której znajdują się pola.
     */
    public OnFocusChangeCustomListener(TextView field, SeekBar bar, String fieldname, Type typ, double coeff, SettingsActivity settingsActivity)
    {
        try
        {
            prop = Properties.class.getField(fieldname);
        }
        catch (NoSuchFieldException e)
        {
            Toast.makeText(settingsActivity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        this.bar = bar;
        this.typ = typ;
        this.coeff = coeff;
        this.settingsActivity = settingsActivity;
        this.field = field;
    }

    /**
     * Metoda wywoływana podczas zakończenia wprowadzania zmian
     * @param view aktualny widok
     * @param has_focus aktualna wartość czy użytkownik zakończył wprowadzanie zmian
     */
    @Override
    public void onFocusChange(View view, boolean has_focus)
    {
        if ((prop != null) && (!has_focus))
        {
            String field_value = field.getText().toString();
            if (field_value.matches("([0-9]*[1-9]+[0-9]*)|([0-9]+(\\.|,)[0-9]*[1-9]+[0-9]*)"))
            {
                try
                {
                    if (typ == Integer.TYPE)
                    {
                        String[] parts = field_value.split("\\.|,");
                        prop.set(null, (int) ((double) Integer.parseInt(parts[0])));
                        bar.setProgress((int) ((int) prop.get(null) * coeff));
                    }
                    if (typ == Double.TYPE)
                    {
                        field_value = field_value.replace(',', '.');
                        prop.set(null, (Double.parseDouble(field_value)));
                        bar.setProgress((int) ((double) prop.get(null) * coeff));
                    }
                    Toast.makeText(settingsActivity, "Wartość " + prop.getName() + ": " + prop.get(null), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(settingsActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(settingsActivity, "Niepoprawna liczba", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
