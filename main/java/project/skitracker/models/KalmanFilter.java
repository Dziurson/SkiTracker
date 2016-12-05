package project.skitracker.models;

public class KalmanFilter
{
    private double x = 0;
    private double k;
    private double q;
    private double r;
    private double p = 1;

    public KalmanFilter(double q, double r)
    {
        this.q = q;
        this.r = r;
    }
    public double filter(double d)
    {
        k = (p + q) / (p + q + r);
        p = r * (p + q) / (r + p + q);
        x = x + (d - x) * k;
        return x;
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
