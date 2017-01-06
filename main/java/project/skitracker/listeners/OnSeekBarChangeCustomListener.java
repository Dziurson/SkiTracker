package project.skitracker.listeners;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import project.skitracker.SettingsActivity;
import project.skitracker.settings.Properties;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class OnSeekBarChangeCustomListener implements SeekBar.OnSeekBarChangeListener
{
    private TextView field;
    private Type typ;
    private double coeff;
    private SettingsActivity settingsActivity;
    private Field prop;

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

    @Override
    @SuppressWarnings("all")
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {
        field.setText(((Double)((double)seekBar.getProgress()/coeff)).toString());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        try
        {
            if(prop != null)
            {
                if (typ == Double.TYPE) prop.set(null,((double)seekBar.getProgress()/coeff));
                if (typ == Integer.TYPE) prop.set(null, (int)(seekBar.getProgress()/coeff));
                Toast.makeText(settingsActivity,"Wartość " + prop.getName() + ": " + prop.get(null),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
           Toast.makeText(settingsActivity,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
