package project.skitracker.models;

import java.util.ArrayList;

public class KalmanFilter
{
    private double x = 0;
    private double k;
    private double q;
    private double r;
    private double p = 0.008;

    public KalmanFilter(double q, double r)
    {
        this.q = q;
        this.r = r;
    }

    public double filterSingleValue(double d)
    {
        k = (p + q) / (p + q + r);
        p = r * (p + q) / (r + p + q);
        x = x + (d - x) * k;
        return x;
    }

    public ArrayList<Double> filterArrayOfValues(ArrayList<Double> list)
    {
        ArrayList<Double> ret_list = new ArrayList<Double>();
        for (double data : list)
        {
            ret_list.add(filterSingleValue(data));
        }
        return ret_list;
    }

    public void setStartValue(double x)
    {
        this.x = x;
    }

    public boolean isXEqualToZero()
    {
        return (x == 0);
    }
}
