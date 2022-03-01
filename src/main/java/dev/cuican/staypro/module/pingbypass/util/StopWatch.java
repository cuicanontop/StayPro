package dev.cuican.staypro.module.pingbypass.util;

public class StopWatch
{
    private long time;

    public boolean passed(double ms)
    {
        return System.nanoTime() - time >= ms * 1000000;
    }

    public boolean passed(long ms)
    {
        return System.nanoTime() - time >= ms * 1000000;
    }

    public StopWatch reset()
    {
        time = System.nanoTime();
        return this;
    }
    public void setTime(long ns)
    {
        time = ns;
    }
    public long getTime()
    {
        return (System.nanoTime() - time) / 1000000;
    }

}
