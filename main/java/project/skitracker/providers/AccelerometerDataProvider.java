package project.skitracker.providers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import project.skitracker.MainActivity;
import project.skitracker.models.KalmanFilter;
import project.skitracker.settings.Properties;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AccelerometerDataProvider implements SensorEventListener
{
    private MainActivity sender;
    private SensorManager sensmgr;
    private KalmanFilter accfilterx, accfiltery, accfilterz;
    private double Q,R;
    private Sensor accelerometer;
    private static AccelerometerDataProvider instance = null;
    private volatile double filteredacceleration;
    private volatile double acceleration;
    private volatile double accx,accy,accz,accxf,accyf,acczf;

    private AccelerometerDataProvider(MainActivity sender)
    {
        initialize(sender);
    }

    public static AccelerometerDataProvider getInstance(MainActivity sender)
    {
        if (instance == null)
        {
            instance = new AccelerometerDataProvider(sender);
        }
        return instance;
    }

    private void initialize(MainActivity sender)
    {
        sensmgr = (SensorManager) sender.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensmgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Properties.isAccelerometerAvailable = (accelerometer != null);
        if (Properties.isAccelerometerAvailable) sensmgr.registerListener(this,accelerometer,Properties.ACCELEROMETER_UPDATES_DELAY);
        accfilterx = new KalmanFilter(Q,R);
        accfiltery = new KalmanFilter(Q,R);
        accfilterz = new KalmanFilter(Q,R);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        accx = sensorEvent.values[0];
        accy = sensorEvent.values[1];
        accz = sensorEvent.values[2];
        accxf = accfilterx.filter(accx);
        accyf = accfiltery.filter(accy);
        acczf = accfilterz.filter(accz);
        acceleration = sqrt(pow(accx,2) + pow(accy,2) + pow(accz,2));
        filteredacceleration = sqrt(pow(accxf,2) + pow(accyf,2) + pow(acczf,2));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    public double getFilteredAcceleration()
    {
        return filteredacceleration;
    }

    public double getRawAcceleration()
    {
        return acceleration;
    }
}
