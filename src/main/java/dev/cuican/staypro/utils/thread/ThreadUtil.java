package dev.cuican.staypro.utils.thread;



import dev.cuican.staypro.module.pingbypass.util.Globals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Utility for Multithreading.
 */
public class ThreadUtil implements Globals
{
    public static final ThreadFactory FACTORY = newDaemonThreadFactoryBuilder()
                                  .setNameFormat("cuican-Thread-%d")
                                  .build();
    /**
     * @return a single thread {@link ScheduledExecutorService}, whose
     *         thread set ({@link Thread#setDaemon(boolean)}) to <tt>true</tt>.
     */
    public static ScheduledExecutorService newDaemonScheduledExecutor(
            String name)
    {
        ThreadFactoryBuilder factory = newDaemonThreadFactoryBuilder();
        factory.setNameFormat("cuican-" + name + "-%d");
        return Executors.newSingleThreadScheduledExecutor(factory.build());
    }

    public static ExecutorService newDaemonCachedThreadPool()
    {
        return Executors.newCachedThreadPool(FACTORY);
    }

    public static ExecutorService newFixedThreadPool(int size)
    {
        ThreadFactoryBuilder factory = newDaemonThreadFactoryBuilder();
        factory.setNameFormat("cuican-Fixed-%d");
        return Executors.newFixedThreadPool(Math.max(size, 1), factory.build());
    }

    public static ThreadFactoryBuilder newDaemonThreadFactoryBuilder()
    {
        ThreadFactoryBuilder factory = new ThreadFactoryBuilder();
        factory.setDaemon(true);
        return factory;
    }

}
