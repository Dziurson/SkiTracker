package project.skitracker.models;

import java.util.ArrayList;
import static java.lang.Math.pow;

//Spline interpolation uses 4 points, returns spline between point 2 and 3 ie. for points [0 1 1 0] it will return spline between 1 and 1.
//Interpolating only ONE DIMENSION! It needs to be used once for Longtitude and once for latitude;
public class SplineInterpolation
{

    private ArrayList<Double> values;
    private static final int element_count = 10;
    private double m1,m2,d[],a[],step;
    public SplineInterpolation()
    {
        values = new ArrayList<>();
        for (int i = 0; i < 4; i++) values.add(0.0d);
        step = 1.0d/(element_count - 1);
        d = new double[3];
        a = new double[4];
    }

    public double getFirst()
    {
        return values.get(0);
    }

    public double getLast()
    {
        return values.get(3);
    }

    private double moveValues(double d)
    {
        double tmp = getFirst();
        values.set(0,values.get(1));
        values.set(1,values.get(2));
        values.set(2,getLast());
        values.set(3,d);
        return tmp;
    }

    private void AddValue(double d)
    {
        moveValues(d);
    }

    public ArrayList<Double> calculateNewSpline(double d)
    {
        AddValue(d);
        if ((getFirst() != 0.0d)) return calculate();
        else return null;
    }

    private ArrayList<Double> calculate()
    {
        for (int i = 0; i < 3; i++)
        {
            d[i] = values.get(i + 1) - values.get(i);
        }
        m1 = (10.0*d[1] - 8.0*d[0] - 2.0*d[2])/5.0;
        m2 = (8.0*d[2] - 10.0*d[1] + 2.0*d[0])/5.0;
        a[0] = values.get(1);
        a[1] = d[1] - (2.0*m1 + m2)/6.0;
        a[2] = m1/2.0;
        a[3] = (m2 - m1)/6.0;

        ArrayList<Double> ret_list = new ArrayList<>();
        for (int i = 0; i < element_count; i++)
        {
            ret_list.add(a[0] + a[1] * (step * i) + a[2] * pow(step * i,2) + a[3] * pow(step * i,3));
        }
        return ret_list;
    }
}
