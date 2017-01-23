package project.skitracker.models;

import java.util.ArrayList;

import static java.lang.Math.pow;

/**
 * Klasa odpowiedzialna za dostarczenie punktów interpolacji, uzyskanych zmodyfikowaną metodą interpolacji
 * funkcjami sklejanymi 3 stopnia.
 */
public class SplineInterpolation
{
    /**
     * Ilość punktów które mają zostać wygenerowane przez interpolację w jednym kroku
     */
    private static final int element_count = 10;
    /**
     * Wartości czterech interpolowanych punktów
     */
    private ArrayList<Double> values;
    /**
     * Kolejno: pochodna w pierszym punkcie, pochodna w drugim punkcie, wektor wspolczynnikow d, wektor wspolczynnikow a, krok interpolacji
     */
    private double m1, m2, d[], a[], step;

    /**
     * Konstruktor klasy wypełnia początkowe wektory zerami.
     */
    public SplineInterpolation()
    {
        values = new ArrayList<>();
        for (int i = 0; i < 4; i++) values.add(0.0d);
        step = 1.0d / (element_count - 1);
        d = new double[3];
        a = new double[4];
    }

    /**
     * Zwraca ostatnio dodana wartosc
     * @return
     */
    public double getFirst()
    {
        return values.get(0);
    }

    /**
     * Zwraca czwartą z kolei dodana wartość
     * @return
     */
    public double getLast()
    {
        return values.get(3);
    }

    /**
     * Po dodaniu wartości wywolywana jest ta metoda - wartości są kolejkowane, pierwsza jest
     * zdejmowana z kolejki, kolejne są przesuwane, a nowa dodawana na koniec kolejki.
     * @param d Wartość do dodania
     * @return
     */
    private double moveValues(double d)
    {
        double tmp = getFirst();
        values.set(0, values.get(1));
        values.set(1, values.get(2));
        values.set(2, getLast());
        values.set(3, d);
        return tmp;
    }

    /**
     * Dodaje wartość do zbioru wartości
     * @param d Wartość do dodania
     */
    private void AddValue(double d)
    {
        moveValues(d);
    }

    /**
     * Metoda oblicza punkty interpolacji po dodaniu do niej nowej wartości
     * @param d Wartość do dodania
     * @return
     */
    public ArrayList<Double> calculateNewSpline(double d)
    {
        AddValue(d);
        if ((getFirst() != 0.0d)) return calculate();
        else return null;
    }

    /**
     * Metoda służąca do wykonania obliczeń. Pochodne zostały policzone analitycznie i podstawione do wzoru.
     * @return Lista wartości wygenerowanych przez interpolację.
     */
    private ArrayList<Double> calculate()
    {
        for (int i = 0; i < 3; i++)
        {
            d[i] = values.get(i + 1) - values.get(i);
        }
        m1 = (10.0 * d[1] - 8.0 * d[0] - 2.0 * d[2]) / 5.0;
        m2 = (8.0 * d[2] - 10.0 * d[1] + 2.0 * d[0]) / 5.0;
        a[0] = values.get(1);
        a[1] = d[1] - (2.0 * m1 + m2) / 6.0;
        a[2] = m1 / 2.0;
        a[3] = (m2 - m1) / 6.0;

        ArrayList<Double> ret_list = new ArrayList<>();
        for (int i = 0; i < element_count; i++)
        {
            ret_list.add(a[0] + a[1] * (step * i) + a[2] * pow(step * i, 2) + a[3] * pow(step * i, 3));
        }
        return ret_list;
    }
}
