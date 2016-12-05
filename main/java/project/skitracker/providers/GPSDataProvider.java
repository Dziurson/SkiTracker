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
import project.skitracker.settings.Properties;
import project.skitracker.data.Velocity;
import project.skitracker.models.KalmanFilter;

import static java.lang.Math.abs;

/*
    GPSDataProvider, actually using only GPS signal.
    This class can only be instantioned once - Singleton design pattern used.
    It run as a service listening to GPS updates.
*/

public class GPSDataProvider implements LocationListener
{
    private static GPSDataProvider instance = null;
    private MainActivity sender;
    @Deprecated
    private KalmanFilter gpsKalmanLongitudeFilter;
    @Deprecated
    private KalmanFilter gpsKalmanLatitudeFilter;
    private LocationManager location_manager;

    private Location rawLocationData;
    private Location rawLocationDataPrev;
    private Location filteredLocation;
    private Location filteredLocationPrev;
    private Velocity velocity;
    private Velocity velocity_prev;
    private Velocity filteredVelocity;
    private Velocity filteredVelocityPrev;

    private double acceleration = 0;

    private GPSDataProvider(MainActivity sender)
    {
        this.sender = sender;
        location_manager = (LocationManager) this.sender.getSystemService(Context.LOCATION_SERVICE);
        gpsKalmanLongitudeFilter = new KalmanFilter(Properties.gpsKalmanFilterQvalue,Properties.gpsKalmanFilterRvalue);
        gpsKalmanLatitudeFilter = new KalmanFilter(Properties.gpsKalmanFilterQvalue,Properties.gpsKalmanFilterRvalue);
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
            if (gpsKalmanLongitudeFilter.isXEqualToZero())
            {
                gpsKalmanLongitudeFilter.setStartValue(location.getLongitude());
            }
            if (gpsKalmanLatitudeFilter.isXEqualToZero())
            {
                gpsKalmanLatitudeFilter.setStartValue(location.getLatitude());
            }

            //Storing raw and filtered data to test.
            rawLocationDataPrev = this.rawLocationData;
            filteredLocationPrev = filteredLocation;
            this.rawLocationData = location;
            filteredLocation = new Location(location);
            filteredLocation.setLatitude(gpsKalmanLatitudeFilter.filter(location.getLatitude()));
            filteredLocation.setLongitude(gpsKalmanLongitudeFilter.filter(location.getLongitude()));

            //Velocity is calculated using Location time and distance between points using ds/dt
            velocity_prev = velocity;
            filteredVelocityPrev = filteredVelocity;
            velocity = new Velocity(getVelocityFromGPS(), location.getTime());
            filteredVelocity = new Velocity(getFilteredVelocityFromGPS(),location.getTime());

            //If accelerometer is not available and two previous locations are not null, acceleration is calculate using dV/dt
            if ((velocity != null) && (velocity_prev != null) && ((!Properties.isAccelerometerAvailable) || (!Properties.isAccelerometerEnabled)))
            {
                acceleration = (velocity.getVelocity() - velocity_prev.getVelocity()) / ((velocity.getTime() - velocity_prev.getTime()) / 1000);
            }
            sender.Update_Fields(velocity.getVelocity(), getAcceleration(), this.rawLocationData.getLatitude(), this.rawLocationData.getLongitude());
            sender.SaveFilteredData(filteredVelocity.getVelocity(),getAcceleration(),filteredLocation.getLatitude(),filteredLocation.getLongitude(),filteredLocation.getTime());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    } //TODO: Dodac wlaczanie/wylaczanie nadajnika

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
            if (sender.getGPSPermission()) location_manager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
    }

    public double getVelocityFromGPS()
    {
        //TODO: Change for filter data, odleglosc ze wzorku
        if((rawLocationData != null) && (rawLocationDataPrev != null))
        {
            return abs(rawLocationData.distanceTo(rawLocationDataPrev))/((rawLocationData.getTime() - rawLocationDataPrev.getTime())/1000);
        }
        else return 0;
    }
    public double getFilteredVelocityFromGPS()
    {
        if((filteredLocation != null) && (filteredLocationPrev != null))
        {
            return abs(filteredLocation.distanceTo(filteredLocationPrev))/((filteredLocation.getTime() - filteredLocationPrev.getTime())/1000);
        }
        else return 0;
    }

    public double getAcceleration()
    {
        return acceleration;
    }
}
