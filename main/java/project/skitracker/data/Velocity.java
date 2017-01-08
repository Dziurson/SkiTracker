package project.skitracker.data;

import java.util.Date;

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

    public void setVelocity(double v)
    {
        this.v = v;
    }

    public double getVelocityInKph()
    {
        return v * 3.6;
    }

    public long getTime()
    {
        return this.t;
    }

    public void setTime(long t)
    {
        this.t = t;
    }

    public void setTime(Date d)
    {
        this.t = d.getTime();
    }

    public Date getDate()
    {
        return new Date(this.t);
    }
}
