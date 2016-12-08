package project.skitracker;

import android.Manifest;
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
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.round;

//Minimalna wersja: Android 4.4, dodane wsparcie dla Androida 6.0
//TODO: DODAC PROSBE O WLACZENIE GPS PRZY STARCIE APLIKACJI
//TODO: ODSWIEZANIE WIDOKU, ZAPIS DO PLIKU KML W OSOBNYM WATKU
//TODO: ADD STATUSBAR NOTIFICATION TO LET APPLICATION RUN IN BACKGROUND
//TODO: MAKE KMLFILEGENERATOR MORE SAFE!!!!!!!!!!!
//TODO: ADD SPLINE CUBIC INTERPOLATION
//TODO: USE KALMAN FILTRATION FOR ACCELEROMETER DATA
//TODO: KMLFILEPROVIDER extends FILEPROVIDER and ACCELERATIONFILEPROVIDER extends FILEPROVIDER
//TODO: CHECK IF THERE ARE ANY! MULTITHREAD CONFLICTS ( USE VOLATILE AND SYNCHRONIZED )
//TODO: IF TIME BETWEEN LOCATION UPDATES EXCEEDS (5-15s) SET KALMANFILTER X TO LOCATION VALUE (ALSO INTERPOLATION SHOULD BE STOPPED!!
public class MainActivity extends AppCompatActivity
{
    //Fields that represents movement data (velocity, acceleration, longitude, latitude).
    private TextView szerokosc_view, dlugosc_view, predkosc_view, acc_view;
    //Progressbars graphically shows velocity and acceleration
    private ProgressBar velocitybar,acc_bar;
    //Small button turns on/off recording
    private FloatingActionButton recordingbutton;
    //Two kml file providers - one for raw data, one for filtered data
    private KMLFileProvider kmlFileProvider, kmlFilteredFileGenerator;
    //This Object implements LocationListener and SensorListener Interfaces. Provides GPS and Accelerometer data.
    private GPSDataProvider movement_tracker;
    //Two data formatters, one for Longitude and Latitude, second for Velocity and Acceleration.
    private DecimalFormat coord_format, av_format;
    //Date provider - used for kml filename.
    private Calendar calendar;

    //This method is invoked while application is starting.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Set view for application (activity_main.xml).
        setContentView(R.layout.activity_main);
        Initialize();
        //TODO: Settings action, split into initialize and field.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recordingbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(checkForWriteExternalPermission())
                {
                    if (!Properties.isKmlFileOpened)
                    {
                        calendar = Calendar.getInstance();
                        String filename = calendar.getTime().toString() + calendar.getTimeInMillis() + ".kml";
                        String filenamefiltered = calendar.getTime().toString() + calendar.getTimeInMillis() + "filtered.kml";
                        if (kmlFileProvider.OpenFile(filename))
                        {
                            if(kmlFilteredFileGenerator.OpenFile(filenamefiltered)) Snackbar.make(view, "Rozpoczęto zapis.", Snackbar.LENGTH_LONG).show();
                            else
                            {
                                Snackbar.make(view, "Coś poszło nie tak.", Snackbar.LENGTH_LONG).show();
                                kmlFileProvider.CloseFile();
                            }
                        }
                        else Snackbar.make(view, "Coś poszło nie tak.", Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        kmlFileProvider.CloseFile();
                        kmlFilteredFileGenerator.CloseFile();
                        Snackbar.make(view, "Zakończono zapisywanie.", Snackbar.LENGTH_LONG).show();
                    }
                    changeFabStyle(Properties.isKmlFileOpened);
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
        if(b) recordingbutton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        else recordingbutton.setImageResource(android.R.drawable.ic_media_play);
    }

    //Initialize method is responsible for initialization of all MainActivity fields.
    private void Initialize()
    {
        kmlFileProvider = new KMLFileProvider(this);
        kmlFilteredFileGenerator = new KMLFileProvider(this);
        //Initializing fields with data from view.
        velocitybar = (ProgressBar) findViewById(R.id.velocitybar);
        acc_bar = (ProgressBar) findViewById(R.id.acceleration_bar);
        szerokosc_view = (TextView) findViewById(R.id.szerokosc_view_t);
        dlugosc_view = (TextView) findViewById(R.id.dlugosc_view_t);
        predkosc_view = (TextView) findViewById(R.id.predkosc_view_t);
        acc_view = (TextView) findViewById(R.id.acc_view_t);
        recordingbutton = (FloatingActionButton) findViewById(R.id.fab);
        coord_format = new DecimalFormat(Properties.COORDINATES_FORMAT);
        av_format = new DecimalFormat(Properties.ACCELERATION_AND_VELOCITY_FORMAT);
        movement_tracker = GPSDataProvider.getInstance(this);
    }

    //Function used to update all available fields in MainActivity.
    public synchronized void Update_Fields(double v, double a, double szer, double dlug)
    {
        Update_TextView(szerokosc_view,coord_format.format(szer).toString());
        Update_TextView(dlugosc_view,coord_format.format(dlug).toString());
        Update_TextView(predkosc_view,av_format.format(v*3.6).toString() + " km/h");
        Update_TextView(acc_view,av_format.format(a).toString() + " m/s^2");
        Update_ProgressBar(velocitybar,v*3.6);
        Update_ProgressBar(acc_bar,a*10+40);
        if (Properties.isKmlFileOpened) kmlFileProvider.AppendFile(dlug + "," + szer + " <!-- v = " + v + ", a = " + a + "-->\n");
    }
    public synchronized void SaveFilteredData(double v, double a, double szer, double dlug, long t)
    {
        if (Properties.isKmlFileOpened) kmlFilteredFileGenerator.AppendFile(dlug + "," + szer + " <!-- v = " + v + ", a = " + a + ", " + (new Date(t)).toString() + "-->\n");
    }

    // Function used to fill data from Accelerometer.
    public synchronized void Update_Acceleration_Field(double a)
    {
        String acc_text = av_format.format(a).toString() + " m/s^2";
        acc_view.setText(acc_text);
    }

    private void Update_TextView(TextView t, String s)
    {
        t.setText(s);
    }

    private void Update_ProgressBar(ProgressBar b, double v)
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
            return true;
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
                    recordingbutton.performClick();
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
