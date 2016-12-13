package project.skitracker;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import project.skitracker.exceptions.NotImplementedException;

import java.util.ArrayList;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
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

    public void addLine(String filename) throws NotImplementedException
    {
        throw new NotImplementedException();
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
