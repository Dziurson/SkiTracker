package project.skitracker;

import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import project.skitracker.exceptions.NotImplementedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private FloatingActionButton load_button;
    private ArrayList<String> filenames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initialize();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        addLine("test.kml");
    }

    private void initialize()
    {
        load_button = (FloatingActionButton) findViewById(R.id.floating_load_button);
        load_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: Make scrolling listview of saved kml files.
                Snackbar.make(view,"TODO: Show list of kml filse",Snackbar.LENGTH_LONG);
            }
        });
    }

    public void addLine(ArrayList<String> location_points_list, String label) throws NotImplementedException
    {
        throw new NotImplementedException();
    }

    public void addLineFromStrings(ArrayList<String> location_latitude_list, ArrayList<String> location_longitude_list, String label) throws NotImplementedException
    {
        throw new NotImplementedException();
    }

    public void addLineFromDoubles(ArrayList<Double> location_latitude_list, ArrayList<Double> location_longitude_list, String label) throws  NotImplementedException
    {
        throw new NotImplementedException();
    }

    public void addLine(String filename)
    {
        //throw new NotImplementedException();
        try
        {
            File kmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            FileReader fread = new FileReader(kmlFile);
            BufferedReader bread = new BufferedReader(fread);
            List<LatLng> point_list = new LinkedList<>();
            String line = bread.readLine();
            boolean check = false;
            while(line != null)
            {
                line = line.replaceAll("[\\t| ]*","").replaceAll("<!--.*>","");
                if (line.equalsIgnoreCase("</coordinates>")) check = false;
                if (check)
                {
                    String[] parts = line.split(",");
                    if((parts[0] != null) && (parts[1] != null))
                    {
                        point_list.add(new LatLng(Double.parseDouble(parts[1]),(Double.parseDouble(parts[0]))));
                    }
                }
                if (line.equalsIgnoreCase("<coordinates>")) check = true;
                line = bread.readLine();
            }
            PolylineOptions points = new PolylineOptions().addAll(point_list).width(5).color(Color.RED).visible(true);
            mMap.addMarker(new MarkerOptions().position(point_list.get(1)).title("START"));
            mMap.addPolyline(points);
            bread.close();
            fread.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void moveCamera(double latitude, double longitude)
    {
        if (mMap != null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
        }
    }

    public void addPlacemark(double latitude, double longitude, String label) throws NotImplementedException
    {
        if (mMap != null)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(label));
        }
    }

    public void clearMap()
    {
        if (mMap != null)
        {
            mMap.clear();
        }
    }
}
