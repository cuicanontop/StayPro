package dev.cuican.staypro.module.pingbypass.util;




import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadUtil implements Globals
{
    public static void schedule(Runnable runnable)
    {
        mc.addScheduledTask(runnable);
    }

    public static void scheduleNext(Runnable runnable)
    {
        ((IMinecraft) mc).scheduleNext(runnable);
    }

    public static ScheduledExecutorService newSingleThreadDaemonExecutor()
    {
        return Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    }

    public static void run(Runnable runnable)
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(runnable);
        executor.shutdown();
    }

}
