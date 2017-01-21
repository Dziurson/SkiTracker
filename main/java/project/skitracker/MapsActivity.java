package project.skitracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import project.skitracker.exceptions.NotImplementedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Klasa odpowiadająca za wyświetlanie zapisanych tras w mapach Google.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    /**
     * Obiekt map Google
     */
    private GoogleMap mMap;
    /**
     * Pole odpowiadające za przycisk otwierania plików.
     */
    private FloatingActionButton load_button;
    /**
     * Pole odpowiadające za wyświetlanie listy plików z widoku.
     */
    private ListView file_list;
    /**
     * Lista zawierająca nazwy plików z rozszerzeniem .kml
     */
    private ArrayList<String> filenames;
    /**
     * Adapter używany do aktualizacji wyświetlanej listy plików
     */
    private ArrayAdapter<String> adapter;
    /**
     * Scieżka do katalogu, w którym znajdują się pliki .kml.
     */
    private File path;

    /**
     * Funkcja która zostaje wywoływana przy starcie aktywności.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initialize();
        adapter.notifyDataSetChanged();
    }

    /**
     * Funckja oczekująca na dostępność map Google
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }

    /**
     * Funkcja inicjalizująca pola klasy, oraz dodająca podstawowe EventListenery do elementów widoku.
     */
    private void initialize()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        load_button = (FloatingActionButton) findViewById(R.id.floating_load_button);
        filenames = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.custom_textview, filenames);
        file_list = (ListView) findViewById(R.id.listoffiles);
        file_list.setAdapter(adapter);
        file_list.setVisibility(View.GONE);
        load_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                File[] files = path.listFiles();
                filenames.clear();
                for(File f : files)
                {
                    if (f.getName().matches(".*\\.kml$"))
                        filenames.add(f.getName());
                }
                adapter.notifyDataSetChanged();
                file_list.setVisibility(View.VISIBLE);
                load_button.setVisibility(View.GONE);
            }
        });
        file_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String item_name = (String) file_list.getItemAtPosition(i);
                addLine(item_name);
                file_list.setVisibility(View.GONE);
                load_button.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Funkcja dodająca trasę na mapy google, znajdującą się w pliku kml o podanej jako parametr nazwie.
     * @param filename Nazwa pliku .kml z trasą.
     */
    public void addLine(String filename)
    {
        try
        {
            File kmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            FileReader fread = new FileReader(kmlFile);
            BufferedReader bread = new BufferedReader(fread);
            List<LatLng> point_list = new LinkedList<>();
            String line = bread.readLine();
            boolean check = false;
            while (line != null)
            {
                line = line.replaceAll("[\\t| ]*", "").replaceAll("<!--.*>", "");
                if (line.equalsIgnoreCase("</coordinates>")) check = false;
                if (check)
                {
                    String[] parts = line.split(",");
                    if ((parts[0] != null) && (parts[1] != null))
                    {
                        point_list.add(new LatLng(Double.parseDouble(parts[1]), (Double.parseDouble(parts[0]))));
                    }
                }
                if (line.equalsIgnoreCase("<coordinates>")) check = true;
                line = bread.readLine();
            }
            PolylineOptions points = new PolylineOptions().addAll(point_list).width(5).color(Color.RED).visible(true);
            mMap.addMarker(new MarkerOptions().position(point_list.get(1)).title("START"));
            mMap.addPolyline(points);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point_list.get(1)));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            bread.close();
            fread.close();

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Funkcja dodająca znacznik na mapie
     * @param latitude Szerokość geograficzna
     * @param longitude Długość geograficzna
     * @param label Etykieta znacznika
     * @throws NotImplementedException
     */
    public void addPlacemark(double latitude, double longitude, String label) throws NotImplementedException
    {
        if (mMap != null)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(label));
        }
    }

    /**
     * Funkcja czyszcząca wszystkie trasy i znaczniki z mapy.
     */
    public void clearMap()
    {
        if (mMap != null)
        {
            mMap.clear();
        }
    }
}
