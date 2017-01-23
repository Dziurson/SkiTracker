package project.skitracker.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedziala za filtrację Kalmana. Parametry opisujące model znajdują się w klasie Properties.
 */
public class KalmanFilter
{
    private double x = 0;
    private double k;
    private double q;
    private double r;
    private double p = 0.008;

    /**
     * Konstruktor klasy, wywoływany przy rozpoczęciu filtracji, przyjmuje jako parametry sigma i ro
     * @param q
     * @param r
     */
    public KalmanFilter(double q, double r)
    {
        this.q = q;
        this.r = r;
    }

    /**
     * Metdda pozwalająca na filtrację pojedynczej wartości ciągu
     * @param d Wartość do przefiltrowania
     * @return
     */
    public double filterSingleValue(double d)
    {
        k = (p + q) / (p + q + r);
        p = r * (p + q) / (r + p + q);
        x = x + (d - x) * k;
        return x;
    }

    /**
     * Metoda pozwalająca na filtracje listy parametrów
     * @param list
     * @return
     */
    public ArrayList<Double> filterArrayOfValues(List<Double> list)
    {
        ArrayList<Double> ret_list = new ArrayList<>();
        for (double data : list)
        {
            ret_list.add(filterSingleValue(data));
        }
        return ret_list;
    }

    /**
     * Metoda ustalająca wartość początkowej
     * @param x Wartosc poczatkowa
     */
    public void setStartValue(double x)
    {
        this.x = x;
    }

    /**
     * Sprawdza czy wartość x jest równa 0, jeśli tak, zwraca true.
     * @return
     */
    public boolean isXEqualToZero()
    {
        return (x == 0);
    }
}
