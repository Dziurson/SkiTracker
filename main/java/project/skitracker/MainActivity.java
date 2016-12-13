package project.skitracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import project.skitracker.providers.KMLFileProvider;
import project.skitracker.providers.GPSDataProvider;
import project.skitracker.settings.Properties;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Math.round;

//Minimalna wersja: Android 4.4, dodane wsparcie dla Androida 6.0
//TODO: DODAC PROSBE O WLACZENIE GPS PRZY STARCIE APLIKACJI
//TODO: ODSWIEZANIE WIDOKU, ZAPIS DO PLIKU KML W OSOBNYM WATKU
//TODO: LOCK ORIENTATION
//TODO: MAKE KMLFILEGENERATOR MORE SAFE!!!!!!!!!!!
//TODO: KMLFILEPROVIDER extends FILEPROVIDER and ACCELERATIONFILEPROVIDER extends FILEPROVIDER
//TODO: CHECK IF THERE ARE ANY! MULTITHREAD CONFLICTS ( USE VOLATILE AND SYNCHRONIZED )
//TODO: IF TIME BETWEEN LOCATION UPDATES EXCEEDS (5-15s) SET KALMANFILTER X TO LOCATION VALUE (ALSO INTERPOLATION SHOULD BE STOPPED!!
public class MainActivity extends AppCompatActivity
{
    //Fields that represents movement data (velocity, acceleration, longitude, latitude).
    private TextView szerokosc_textview, dlugosc_textview, predkosc_textview, acceleration_textview;

    //Progressbars graphically shows velocity and acceleration
    private ProgressBar velocity_bar, acceleration_bar;

    //Small button turns on/off recording
    private FloatingActionButton recording_button;

    //Two kml file providers - one for raw data, one for interpolated data
    private KMLFileProvider kml_raw_file_provider, kml_interpolated_file_generator;

    //This Object implements LocationListener and SensorListener Interfaces. Provides GPS and Accelerometer data.
    private GPSDataProvider movement_tracker;

    //Two data formatters, one for Longitude and Latitude, second for Velocity and Acceleration.
    private DecimalFormat coordinates_format, av_format;

    //Date provider - used for kml filename.
    private Calendar calendar;

    //This method is invoked while application is starting.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Set view for application (activity_main.xml).
        setContentView(R.layout.activity_main);
        initialize();
        //TODO: Settings action, split into initialize and field.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recording_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(checkForWriteExternalPermission())
                {
                    if (!Properties.is_kml_file_opened)
                    {
                        calendar = Calendar.getInstance();
                        String filename = calendar.getTime().toString() + calendar.getTimeInMillis() + ".kml";
                        String filenamefiltered = calendar.getTime().toString() + calendar.getTimeInMillis() + "filtered.kml";
                        if (kml_raw_file_provider.OpenFile(filename))
                        {
                            if(kml_interpolated_file_generator.OpenFile(filenamefiltered)) Snackbar.make(view, "Rozpoczęto zapis.", Snackbar.LENGTH_LONG).show();
                            else
                            {
                                Snackbar.make(view, "Coś poszło nie tak.", Snackbar.LENGTH_LONG).show();
                                kml_raw_file_provider.CloseFile();
                            }
                        }
                        else Snackbar.make(view, "Coś poszło nie tak.", Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        kml_raw_file_provider.CloseFile();
                        kml_interpolated_file_generator.CloseFile();
                        Snackbar.make(view, "Zakończono zapisywanie.", Snackbar.LENGTH_LONG).show();
                    }
                    changeFabStyle(Properties.is_kml_file_opened);
                }
            }
        });
    }

    //Runtime permission for writing in public directories handler. Returns true if permission is granted
    private boolean checkForWriteExternalPermission()
    {
        //Check if android version is lower than 6.0, if it is, permissions are granted on install and there is no need to requesting them on runtime.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) Properties.external_storage_write_permission_granted = true;
        //Check if Writing permission is granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //If android version is lower than Marshmallow and data flows here there is a permission error and instalation should be checked, else application requests for writing permission (handled by onRequestPermissionResult).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, Properties.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            else Properties.external_storage_write_permission_granted = false;
        }
        //If permission is already granted there is no need to request it.
        else Properties.external_storage_write_permission_granted = true;
        return Properties.external_storage_write_permission_granted;
    }

    //Simple method for changing Icon style while recording is on/off.
    private void changeFabStyle(boolean b)
    {
        if(b) recording_button.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        else recording_button.setImageResource(android.R.drawable.ic_media_play);
    }

    //initialize method is responsible for initialization of all MainActivity fields.
    private void initialize()
    {
        kml_raw_file_provider = new KMLFileProvider(this);
        kml_interpolated_file_generator = new KMLFileProvider(this);
        //Initializing fields with data from view.
        velocity_bar = (ProgressBar) findViewById(R.id.velocitybar);
        acceleration_bar = (ProgressBar) findViewById(R.id.acceleration_bar);
        szerokosc_textview = (TextView) findViewById(R.id.szerokosc_view_t);
        dlugosc_textview = (TextView) findViewById(R.id.dlugosc_view_t);
        predkosc_textview = (TextView) findViewById(R.id.predkosc_view_t);
        acceleration_textview = (TextView) findViewById(R.id.acc_view_t);
        recording_button = (FloatingActionButton) findViewById(R.id.fab);
        coordinates_format = new DecimalFormat(Properties.COORDINATES_FORMAT);
        av_format = new DecimalFormat(Properties.ACCELERATION_AND_VELOCITY_FORMAT);
        movement_tracker = GPSDataProvider.getInstance(this);
    }

    //Function used to update all available fields in MainActivity.
    public synchronized void updateTextViews(double v, double a, double szer, double dlug)
    {
        updateSingleTextView(szerokosc_textview, coordinates_format.format(szer).toString());
        updateSingleTextView(dlugosc_textview, coordinates_format.format(dlug).toString());
        updateSingleTextView(predkosc_textview,av_format.format(v*3.6).toString() + " km/h");
        updateSingleTextView(acceleration_textview,av_format.format(a).toString() + " m/s^2");
        updateProgressBar(velocity_bar,v*3.6);
        updateProgressBar(acceleration_bar,a*10+40);
        if (Properties.is_kml_file_opened) kml_raw_file_provider.AppendFile(dlug + "," + szer + " <!-- v = " + v + ", a = " + a + "-->\n");
    }

    //Saving array of strings to kml_interpolated_file_generator file
    public synchronized void saveLocationDataArrayToInterpolatedKmlFile(ArrayList<String> str)
    {
        if(Properties.is_kml_file_opened) kml_interpolated_file_generator.AppendAllCoordinates(str);
    }

    // Function used to fill data from Accelerometer.
    public synchronized void Update_Acceleration_Field(double a)
    {
        String acc_text = av_format.format(a).toString() + " m/s^2";
        acceleration_textview.setText(acc_text);
    }

    private void updateSingleTextView(TextView t, String s)
    {
        t.setText(s);
    }

    private void updateProgressBar(ProgressBar b, double v)
    {
        if (v > b.getMax()) b.setProgress(b.getMax());
        else b.setProgress((int)round(v));
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
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //Runtime Permission Provider
    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestcode,permissions,grantResults);
        switch(requestcode)
        {
            case Properties.MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_ON:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setGPSPermission(true);
                    movement_tracker.enableGPSRequests();
                }
                else setGPSPermission(false);
                break;
            case Properties.MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_OFF:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setGPSPermission(true);
                    movement_tracker.disableGPSRequests();
                }
                break;
            case Properties.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Properties.external_storage_write_permission_granted = true;
                    recording_button.performClick();
                }
                else Properties.external_storage_write_permission_granted = false;
                break;
        }
    }

    public boolean getGPSPermission()
    {
        return Properties.gps_permission_granted;
    }
    public void setGPSPermission(boolean b)
    {
        Properties.gps_permission_granted = b;
    }
}
