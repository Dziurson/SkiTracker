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

/**
 * Klasa umożliwiająca korzystanie z akcelerometru w aplikacji. W wypadku braku
 * żyroskopu w urządzeniu wyniki nie mogą być poprawnie zmierzone,
 * dlatego nie zaleca się korzystania z tej klasy. Klasa jest singletonem
 * (tylko jeden akcelerometr w telefonie).
 */
@Deprecated
public class AccelerometerDataProvider implements SensorEventListener
{
    private static AccelerometerDataProvider instance = null;
    /**
     * Obiekt głównej aktywności, którego metody są wykonywane w wypadku aktualizacji przyspieszenia
     */
    private MainActivity sender;
    /**
     * Obiekt odpowiadający za dostęp do sensorów urządenia
     */
    private SensorManager sensmgr;
    /**
     * Obiekty umożliwiające filtrację kalmana we wszystkich trzech wymiarach
     */
    private KalmanFilter accfilterx, accfiltery, accfilterz;
    /**
     * Obiekt odpowiadający za śledzenie zmian danych z akcelerometru
     */
    private Sensor accelerometer;
    /**
     * Filtrowane przyspieszenie
     */
    private volatile double filteredacceleration;
    /**
     * Niefiltrowane przyspieszenie
     */
    private volatile double acceleration;
    /**
     * Przyspieszenia we wszystkich trzech wymiarach (filtrowane i niefiltrowane)
     */
    private volatile double accx, accy, accz, accxf, accyf, acczf;

    /**
     * Kostruktor klasy, przyjmujący jako parametr aktywność, posiadająca metody UpdateAccelerometer
     * @param sender Aktywność do której mają być przekazywane komunikaty
     */
    private AccelerometerDataProvider(MainActivity sender)
    {
        initialize(sender);
    }

    /**
     * Implementacja singletonu, zwraca instancję obiektu
     * @param sender Aktywność do której mają być przekazywane komunikaty
     * @return Instancja obiektu AccelerometerDataProvider
     */
    public static AccelerometerDataProvider getInstance(MainActivity sender)
    {
        if (instance == null)
        {
            instance = new AccelerometerDataProvider(sender);
        }
        return instance;
    }

    /**
     * Inicjalizacja wartości pól klasy
     * @param sender Aktywność do której mają być przekazywane komunikaty
     */
    private void initialize(MainActivity sender)
    {
        sensmgr = (SensorManager) sender.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensmgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Properties.isAccelerometerAvailable = (accelerometer != null);
        if (Properties.isAccelerometerAvailable)
            sensmgr.registerListener(this, accelerometer, Properties.ACCELEROMETER_UPDATES_DELAY);
        accfilterx = new KalmanFilter(0.0001, 0.01);
        accfiltery = new KalmanFilter(0.0001, 0.01);
        accfilterz = new KalmanFilter(0.0001, 0.01);
    }

    /**
     * Metoda wykonywana, kiedy stan akcelerometru ulegnie zmianie
     * @param sensorEvent Zdarzenie akcelerometru
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        accx = sensorEvent.values[0];
        accy = sensorEvent.values[1];
        accz = sensorEvent.values[2];
        accxf = accfilterx.filterSingleValue(accx);
        accyf = accfiltery.filterSingleValue(accy);
        acczf = accfilterz.filterSingleValue(accz);
        acceleration = sqrt(pow(accx, 2) + pow(accy, 2) + pow(accz, 2)) - Properties.GRAVITY_VALUE;
        filteredacceleration = sqrt(pow(accxf, 2) + pow(accyf, 2) + pow(acczf, 2)) - Properties.GRAVITY_VALUE;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    /**
     * Metoda zracająca filtrowane przyspieszenie
     * @return filtrowane przyspieszenie
     */
    public double getFilteredAcceleration()
    {
        return filteredacceleration;
    }

    /**
     * Metoda zwracająca przyspieszenie
     * @return przyspieszenie
     */
    public double getRawAcceleration()
    {
        return acceleration;
    }
}
