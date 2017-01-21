package project.skitracker.settings;

import android.hardware.SensorManager;

/**
 * Klasa przechowująca wartości dostępne w całej aplikacji.
 */
public class Properties
{
    /**
     * Identyfikator dla włączania lokalizacji GPS, używany przy dynamicznych uprawnieniach
     */
    public static final int MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_ON = 100;
    /**
     * Identyfikator dla wyłączania lokalizacji GPS, używany przy dynamicznych uprawnieniach
     */
    public static final int MY_PERMISSIONS_REQUEST_COARSE_AND_FINE_LOCATION_TURN_OFF = 101;
    /**
     * Identyfikator dla zapisu do pliku używany przy dynamicznych uprawnieniach
     */
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 200;
    /**
     * Częstotliwość aktualzacji akcelerometru
     */
    public static final int ACCELEROMETER_UPDATES_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    /**
     * Minimalna zmiana przyspieszenia akcelerometru
     */
    public static final double MINIMAL_ACCELERATION_CHANGE = 0.1;
    /**
     * Minimalna wartość przyspieszenia akcelerometru
     */
    public static final double MINIMAL_ACCELERATION = 0.1;
    /**
     * Stała grawitacji
     */
    public static final double GRAVITY_VALUE = 9.81;
    /**
     * Czy aktualizacje GPS są aktywne
     */
    public static boolean isListeningToGPSEnabled = true;
    /**
     * Czy aktualizacje Sieci są aktywne
     */
    public static boolean isListeningToNetworkEnabled = true;
    /**
     * Czy akcelerometr jest dostępny
     */
    public static boolean isAccelerometerAvailable = false;
    /**
     * Czy akcelerometr jest włączony
     */
    public static boolean isAccelerometerEnabled = false;
    /**
     * Czy filtracja jest włączona
     */
    public static boolean isFiltrationEnabled = true;
    /**
     * Czy plik kml jest otwarty
     */
    public volatile static boolean is_kml_file_opened = false;
    /**
     * Minimalny dystans między aktualizacjami GPS (w m)
     */
    public static int minDistanceBetweenGPSUpdates = 5;
    /**
     * Minimalny czas między aktualizacjami GPS (w ms).
     */
    public static int minTimeBetweenGPSUpdates = 1000;
    /**
     * Wartość sigma filtracji Kalmana
     */
    public static double gpsKalmanFilterQvalue = 0.0001;
    /**
     * Wartość ro filtracji Kalmana
     */
    public static double gpsKalmanFilterRvalue = 0.001;
    /**
     * Czy przyznane są uprawnienia GPS
     */
    public static boolean gps_permission_granted = false;
    /**
     * Czy przyznane są uprawnienia do zapisu w pamięci telefonu
     */
    public static boolean external_storage_write_permission_granted = false;
}
