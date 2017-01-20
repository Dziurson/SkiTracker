package project.skitracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private FloatingActionButton load_button;
    private ListView file_list;
    private ArrayList<String> filenames;
    private ArrayAdapter<String> adapter;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initialize();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }

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

    public void addLine(ArrayList<String> location_points_list, String label) throws NotImplementedException
    {
        throw new NotImplementedException();
    }

    public void addLineFromStrings(ArrayList<String> location_latitude_list, ArrayList<String> location_longitude_list, String label) throws NotImplementedException
    {
        throw new NotImplementedException();
    }

    public void addLineFromDoubles(ArrayList<Double> location_latitude_list, ArrayList<Double> location_longitude_list, String label) throws NotImplementedException
    {
        throw new NotImplementedException();
    }

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
            bread.close();
            fread.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void moveCamera(double latitude, double longitude)
    {
        if (mMap != null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
    }

    public void addPlacemark(double latitude, double longitude, String label) throws NotImplementedException
    {
        if (mMap != null)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(label));
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
