package project.skitracker.providers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import project.skitracker.MainActivity;
import project.skitracker.models.SplineInterpolation;
import project.skitracker.settings.Properties;
import project.skitracker.data.Velocity;
import project.skitracker.models.KalmanFilter;
import java.util.ArrayList;
import static java.lang.Math.abs;

/*
    GPSDataProvider, using only GPS signal.
    This class can only be created once, as it is working as a LocationListener service.
    This service is listening to GPS updates.
*/

public class GPSDataProvider implements LocationListener
{
    private static GPSDataProvider instance = null;
    private MainActivity sender;
    private KalmanFilter longitude_filtration;
    private KalmanFilter latitude_filtration;
    private LocationManager location_manager;
    private SplineInterpolation longitude_interpolation;
    private SplineInterpolation latitude_interpolation;
    private Location location_data;
    private Location location_data_prev;
    private Velocity velocity;
    private Velocity velocity_prev;
    private double acceleration = 0;
    private double current_time;
    private double current_interval;

    private GPSDataProvider(MainActivity sender)
    {
        this.sender = sender;
        longitude_interpolation = new SplineInterpolation();
        latitude_interpolation = new SplineInterpolation();
        location_manager = (LocationManager) this.sender.getSystemService(Context.LOCATION_SERVICE);
        longitude_filtration = new KalmanFilter(Properties.gpsKalmanFilterQvalue,Properties.gpsKalmanFilterRvalue);
        latitude_filtration = new KalmanFilter(Properties.gpsKalmanFilterQvalue,Properties.gpsKalmanFilterRvalue);
        current_time = Properties.minTimeBetweenGPSUpdates;
        current_interval = Properties.minDistanceBetweenGPSUpdates;
        enableGPSRequests();
    }

    public static GPSDataProvider getInstance(MainActivity sender)
    {
        if (instance == null) instance = new GPSDataProvider(sender);
        return instance;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        synchronized (this)
        {
            //Storing two location data points.
            location_data_prev = this.location_data;
            this.location_data = location;

            //Velocity is calculated using Location time and distance between points using ds/dt
            velocity_prev = velocity;
            velocity = new Velocity(calculateVelocity(), location.getTime());

            //If accelerometer is not available and two previous locations are not null, acceleration is calculate using dV/dt
            if ((velocity != null) && (velocity_prev != null) && ((!Properties.isAccelerometerAvailable) || (!Properties.isAccelerometerEnabled)))
            {
                acceleration = calculateAcceleration();
            }

            ArrayList<Double> longitude_list = longitude_interpolation.calculateNewSpline(this.location_data.getLongitude());
            ArrayList<Double> latitude_list = latitude_interpolation.calculateNewSpline(this.location_data.getLatitude());
            ArrayList<String> location_list = new ArrayList<>();

            if ((longitude_list != null) && (latitude_list != null))
            {
                for(int i = 0; i < latitude_list.size(); i++)
                {
                    location_list.add(longitude_list.get(i).toString() + "," + latitude_list.get(i).toString());
                }
            }

            if(!Properties.isFiltrationEnabled) sender.saveLocationDataArrayToInterpolatedKmlFile(location_list);
            else
            {
                if ((longitude_list != null) && (latitude_list != null))
                {
                    if (latitude_filtration.isXEqualToZero())
                    {
                        latitude_filtration.setStartValue(latitude_list.get(0));
                    }
                    if (longitude_filtration.isXEqualToZero())
                    {
                        longitude_filtration.setStartValue(longitude_list.get(0));
                    }
                    ArrayList<Double> filtered_longitude_list = longitude_filtration.filterArrayOfValues(longitude_list);
                    ArrayList<Double> filtered_latitude_list = latitude_filtration.filterArrayOfValues(latitude_list);
                    ArrayList<String> filtered_location_list = new ArrayList<String>();
                    for (int i = 0; i < filtered_longitude_list.size(); i++)
                    {
                        filtered_location_list.add(filtered_longitude_list.get(i).toString() + "," + filtered_latitude_list.get(i).toString());
                    }
                    sender.saveLocationDataArrayToInterpolatedKmlFile(filtered_location_list);
                }
            }
            sender.updateTextViews(getVelocity(), getAcceleration(), getLatitude(), getLongitude());
            if (checkForValuesChange())
            {
                disableGPSRequests();
                enableGPSRequests();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }

    @Override
    public void onProviderEnabled(String s)
    {

    }

    @Override
    public void onProviderDisabled(String s)
    {

    }

    public void enableGPSRequests()
    {
        if (!(location_manager == null))
        {
            // If android version is less than Android 6.0, permissions are granted on-install.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) sender.setGPSPermission(true);
            // Check if permissions are enabled
            if (ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // Request for Runtime permission. This method invokes onRequestPermissionResult in MainActivity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ActivityCompat.requestPermissions(sender, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Properties.MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_ON);
                // If something went wrong in permissions go here
                else sender.setGPSPermission(false);
            }
            // If permissions have been granted its possible to invoke method.
            else sender.setGPSPermission(true);
            if (sender.getGPSPermission()) location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Properties.minTimeBetweenGPSUpdates, Properties.minDistanceBetweenGPSUpdates, this);
        }
    }

    public void disableGPSRequests()
    {
        if (!(location_manager == null))
        {
            // If android version is less than Android 6.0, permissions are granted on-install.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) sender.setGPSPermission(true);
            // Check if permissions are enabled
            if (ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(sender, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // Request for Runtime permission. This method invokes onRequestPermissionResult in MainActivity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ActivityCompat.requestPermissions(sender, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Properties.MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_OFF);
                    // If something went wrong in permissions go here
                else sender.setGPSPermission(false);
            }
            // If permissions have been granted its possible to invoke method.
            else sender.setGPSPermission(true);
            if (sender.getGPSPermission()) location_manager.removeUpdates(this);
        }
    }

    private double calculateVelocity()
    {
        if((location_data != null) && (location_data_prev != null))
        {
            return abs(location_data.distanceTo(location_data_prev))/((location_data.getTime() - location_data_prev.getTime())/1000);
        }
        else return 0;
    }

    private double calculateAcceleration()
    {
        return (velocity.getVelocity() - velocity_prev.getVelocity()) / ((velocity.getTime() - velocity_prev.getTime()) / 1000);
    }

    public double getVelocityInKPH()
    {
        return velocity.getVelocityInKph();
    }
    public double getVelocity()
    {
        return velocity.getVelocity();
    }
    public double getAcceleration()
    {
        return acceleration;
    }
    public double getLongitude()
    {
        return this.location_data.getLongitude();
    }
    public double getLatitude()
    {
        return this.location_data.getLatitude();
    }

    private boolean checkForValuesChange()
    {
        return !(current_interval == Properties.minDistanceBetweenGPSUpdates) || (current_time == Properties.minTimeBetweenGPSUpdates);
    }
}
