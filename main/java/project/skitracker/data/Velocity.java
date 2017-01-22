package project.skitracker.data;

import java.util.Date;

/**
 * Klasa odpowiedzialna za przechowywanie pary Prędkość - Czas.
 */
public class Velocity
{
    /**
     * Wartość prędkości
     */
    private double v;
    /**
     * Czas w ms, kiedy została zmierzona prędkość.
     */
    private long t;

    /**
     * Konstruktor przyjmujący prędkośći czas jako parametry
     * @param v Prędkość w m/s
     * @param t Czas, w którym została zmierzona prędkość
     */
    public Velocity(double v, long t)
    {
        this.v = v;
        this.t = t;
    }

    /**
     * Metoda zwraca prędkość w m/s
     * @return Prędkosć w m/s
     */
    public double getVelocity()
    {
        return v;
    }

    /**
     * Metoda zmienia wartość pola prędkości na wartość przekazaną jako parametr
     * @param v Nowa wartość prędkości.
     */
    public void setVelocity(double v)
    {
        this.v = v;
    }

    /**
     * Metoda zwraca prędkość w km/h
     * @return Prędkość w km/h
     */
    public double getVelocityInKph()
    {
        return v * 3.6;
    }

    /**
     * Metoda zwraca czas w którym została zmierzona predkość.
     * @return Czas w tórym została zmierzona prędkość.
     */
    public long getTime()
    {
        return this.t;
    }

    /**
     * Metoda zmienia wartość pola czasu na wartość przekazaną jako parametr
     * @param t Nowa wartość czasu ( w ms ).
     */
    public void setTime(long t)
    {
        this.t = t;
    }

    /**
     * Metoda zmienia wartość pola czasu na wartość przekazaną jako parametr
     * @param d Nowa wartość czasu ( obiekt Date )
     */
    public void setTime(Date d)
    {
        this.t = d.getTime();
    }

    /**
     * Zwraca dokładną datę, kiedy została zmierzona prędkość
     * @return Data, w której została zmierzona prędkość.
     */
    public Date getDate()
    {
        return new Date(this.t);
    }
}
