package project.skitracker.data;

/**
 * Created by jakub on 05.12.2016.
 */
public class Velocity
{
    private double v;
    private long t;

    public Velocity(double v, long t)
    {
        this.v = v;
        this.t = t;
    }

    public double getVelocity()
    {
        return v;
    }

    public long getTime()
    {
        return t;
    }
}
