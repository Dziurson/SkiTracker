package project.skitracker.settings;

import android.hardware.SensorManager;

public class Properties
{
    public static boolean isListeningToGPSEnabled = true;
    public static boolean isListeningToNetworkEnabled = true;
    public static boolean isAccelerometerAvailable = false;
    public static boolean isAccelerometerEnabled = false;
    public static boolean isFirstGPSUpdate = true;
    public static boolean isFiltrationEnabled = true;
    public volatile static boolean is_kml_file_opened = false;
    public static int minDistanceBetweenGPSUpdates = 5;
    public static int minTimeBetweenGPSUpdates = 1000;
    public static double gpsKalmanFilterQvalue = 0.0001;
    public static double gpsKalmanFilterRvalue = 0.001;
    public static boolean gps_permission_granted = false;
    public static boolean external_storage_write_permission_granted = false;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_ON = 100;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_OFF = 101;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    public static final int ACCELEROMETER_UPDATES_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    public static final double MINIMAL_ACCELERATION_CHANGE = 0.1;
    public static final double MINIMAL_ACCELERATION = 0.1;
    public static final double GRAVITY_VALUE = 9.81;
}
