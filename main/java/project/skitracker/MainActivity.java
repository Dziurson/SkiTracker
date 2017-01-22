package project.skitracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import project.skitracker.providers.GPSDataProvider;
import project.skitracker.providers.KMLFileProvider;
import project.skitracker.settings.Properties;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Math.round;

/**
 * Główna klasa aplikacji odpowiadająca za wyświetlanie parametrów, wywoływanie kluczowych obiektów oraz do uruchamiania innych aktywności.
 * Najniższa obsługiwana wersja OS: Android 4.4, posiada wsparcie dla dynamicznych uprawnień Android 6.0+.
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * Obiekt udostepniający kolejne aktualizacje GPS, i wywołujący aktualizację widku.
     */
    GPSDataProvider movement_tracker;
    /**
     * Pola odpowiadające miejscom do wyświetlania aktualnych parametrów z widoku.
     */
    private TextView szerokosc_textview, dlugosc_textview, predkosc_textview, acceleration_textview;
    /**
     * Pola odpowiadające paskom postępu prędkości i przyspieszenia z widoku.
     */
    private ProgressBar velocity_bar, acceleration_bar;
    /**
     * Pole odpowiadające przyciskowi do zapisywania z widoku.
     */
    private FloatingActionButton recording_button;
    /**
     * Obiekty odpowiadające za zapis do plików .kml.
     */
    private KMLFileProvider kml_raw_file_provider, kml_interpolated_file_generator;
    /**
     * Formattery służące do poprawnego wyświetlania wyników, z zadaną dokładnością.
     */
    private DecimalFormat coordinates_format, av_format;
    /**
     * Aktualny czas używany przy tworzeniu nazwy plików .kml.
     */
    private Calendar calendar;

    /**
     * Funkcja która zostaje wywoływana przy starcie aplikacji (po jej wyłączeniu)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * Funkcja wywoływana przy starcie aktywności
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        //Przypisanie widoku do aktywności
        setContentView(R.layout.activity_main);
        //Inicjalizacja pól
        initialize();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Dodanie EventListenera do przycisku zapisywania
        recording_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (checkForWriteExternalPermission())
                {
                    if (!Properties.is_kml_file_opened)
                    {
                        /*
                            Jeśli pliki kml nie są otwarte to następuje próba utworzenia nowych plików
                            na podstawie aktualnego czasu. W razie niepowodzenia użytkownik zostaje o tym
                            powiadomiony poprzez pojawiającą się na ekranie informację.

                            Po poprawnym utworzeniu plików następuje rozpoczęcie zapisu do plików
                            oraz zmiana wyglądu przycisku nagrywania.
                         */
                        calendar = Calendar.getInstance();
                        String filename = calendar.getTime().toString() + ".kml";
                        String filename_filtered = calendar.getTime().toString() + "filtered.kml";
                        if (kml_raw_file_provider.openFile(filename))
                        {
                            if (kml_interpolated_file_generator.openFile(filename_filtered))
                                Snackbar.make(view, getResources().getString(R.string.file_writing_started), Snackbar.LENGTH_LONG).show();
                            else
                            {
                                Snackbar.make(view, getResources().getString(R.string.file_error_message), Snackbar.LENGTH_LONG).show();
                                kml_raw_file_provider.closeFile();
                            }
                        }
                        else
                            Snackbar.make(view, getResources().getString(R.string.file_error_message), Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        kml_raw_file_provider.closeFile();
                        kml_interpolated_file_generator.closeFile();
                        Snackbar.make(view, getResources().getString(R.string.file_writing_ended), Snackbar.LENGTH_LONG).show();
                    }
                    changeFabStyle(Properties.is_kml_file_opened);
                }
            }
        });
    }

    /**
     * Funkcja używana do sprawdzenia czy aplikacja posiada uprawnienia do zapisu w pamięci telefonu.
     * @return Zwraca wartość true jeśli uprawnienia są przyznane. Jeśli aplikacja nie posiada uprawnień zwracana jest wartość false.
     */
    private boolean checkForWriteExternalPermission()
    {
        //Check if android version is lower than 6.0, if it is, permissions are granted on install and there is no need to requesting them on runtime.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) Properties.external_storage_write_permission_granted = true;
        //Check if Writing permission is granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //If android version is lower than Marshmallow and data flows here there is a permission error and instalation should be checked, else application requests for writing permission (handled by onRequestPermissionResult).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Properties.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            else Properties.external_storage_write_permission_granted = false;
        }
        //If permission is already granted there is no need to request it.
        else Properties.external_storage_write_permission_granted = true;
        return Properties.external_storage_write_permission_granted;
    }

    /**
     * Funkcja zmieniajaca wygląd przycisku w prawym dolnym rogu ekranu, w zależości od tego czy zapis do pliku jest włączony czy nie
     * @param b Jesli przekazany zostanie parametr true, przycisk zmieni wygląd na strzałkę, jeśli false, na krzyżyk.
     */
    private void changeFabStyle(boolean b)
    {
        if (b) recording_button.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        else recording_button.setImageResource(android.R.drawable.ic_media_play);
    }

    /**
     * Funkcja inicjalizująca wartości pól, na podstawie elementów pobranych z widoku, oraz inicjalizacja podstawowych obiektów
     */
    private void initialize()
    {
        kml_raw_file_provider = new KMLFileProvider(this);
        kml_interpolated_file_generator = new KMLFileProvider(this);
        velocity_bar = (ProgressBar) findViewById(R.id.velocitybar);
        acceleration_bar = (ProgressBar) findViewById(R.id.acceleration_bar);
        szerokosc_textview = (TextView) findViewById(R.id.szerokosc_view_t);
        dlugosc_textview = (TextView) findViewById(R.id.dlugosc_view_t);
        predkosc_textview = (TextView) findViewById(R.id.predkosc_view_t);
        acceleration_textview = (TextView) findViewById(R.id.acc_view_t);
        recording_button = (FloatingActionButton) findViewById(R.id.fab);
        coordinates_format = new DecimalFormat(getResources().getString(R.string.coordinates_format));
        av_format = new DecimalFormat(getResources().getString(R.string.acceleration_and_velocity_format));
        movement_tracker = GPSDataProvider.getInstance(this);
        movement_tracker.setNewMainActivity(this);
    }
    /**
     * Funkcja, używana do aktualizacji wartości pól w MainActivity
     * @param v Nowa wartość prędkości w m/s
     * @param a Nowa wartość przyspieszenia w m/s^2
     * @param szer Nowa wartość szerokości geograficznej
     * @param dlug Nowa wartość długości geograficznej
     */
    public synchronized void updateTextViews(double v, double a, double szer, double dlug)
    {
        updateSingleTextView(szerokosc_textview, coordinates_format.format(szer).toString());
        updateSingleTextView(dlugosc_textview, coordinates_format.format(dlug).toString());
        updateSingleTextView(predkosc_textview, av_format.format(v * 3.6).toString() + " km/h");
        updateSingleTextView(acceleration_textview, av_format.format(a).toString() + " m/s^2");
        updateProgressBar(velocity_bar, v * 3.6);
        updateProgressBar(acceleration_bar, a * 10 + 40);
        if (Properties.is_kml_file_opened)
            kml_raw_file_provider.addCoordinates(dlug + "," + szer + " <!-- v = " + v * 3.6 + ", a = " + a + "-->");
    }

    /**
     * Funkcja używana do zapisywania tablicy typu String do pliku .kml z interpolowanymi wartościami współrzędnych geograficznych.
     * @param str Tablica stringów w postaci "Double,Double"
     */
    public synchronized void saveLocationDataArrayToInterpolatedKmlFile(ArrayList<String> str)
    {
        if (Properties.is_kml_file_opened) kml_interpolated_file_generator.addAllCoordinates(str);
    }

    /**
     * Funkcja używana do aktualizacji wartości przyspieszenia
     * @param a wartość przyspieszenia w m/s^2
     */
    @Deprecated
    public synchronized void Update_Acceleration_Field(double a)
    {
        String acc_text = av_format.format(a).toString() + " m/s^2";
        acceleration_textview.setText(acc_text);
    }

    /**
     * Funkcja używana do aktualizacji pola t wartością s
     * @param t Pole, które ma być zaktualizowane
     * @param s String z wartością jaka ma znaleźć się w polu
     */
    private void updateSingleTextView(TextView t, String s)
    {
        t.setText(s);
    }

    /**
     * Funkcja używana do aktualizacji pasków postępu (graficzne przedstawienie prędkości i przyspieszenia)
     * @param b ProgressBar który ma być zaktualizowany
     * @param v Wartość, która ma być przypisana do ProgressBar
     */
    private void updateProgressBar(ProgressBar b, double v)
    {
        if (v > b.getMax()) b.setProgress(b.getMax());
        else b.setProgress((int) round(v));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Funkcja, która otwiera menu, umożliwia przejście do menu ustawień i map.
     * @param item Obiekt z listy, który został wybrany
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open_map)
        {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Funkcja odpowiadająca za przydzielanie uprawnien w aplikacji kiedy zajdzie taka potrzeba - Android 6.0+
     * @param requestcode Indywidualny kod dla przekazanej listy uprawnień
     * @param permissions Lista uprawnień, które mają zostać przydzielone
     * @param grantResults Lista wynikowa (czy użytkownik przydzielił wymagane uprawnienia)
     */
    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestcode, permissions, grantResults);
        switch (requestcode)
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

    /**
     * Funkcja sprawdza czy przydzielone zostały uprawnienia GPS
     * @return Zwraca wartość czy przydzielone zostały uprawnienia do aktualizacji GPS
     */
    public boolean getGPSPermission()
    {
        return Properties.gps_permission_granted;
    }

    /**
     * Funkcja ustawia uprawnienia GPS
     * @param b Zależy od tego, czy użytkownik przydzielił uprawnienia GPS
     */
    public void setGPSPermission(boolean b)
    {
        Properties.gps_permission_granted = b;
    }
}
