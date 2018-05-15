package org.donald.common.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类TaskThreadFactory
 *
 * @author Donaldhan  on 2018/4/27
 **/
public class TaskThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final int threadPriority;
    /**
     *
     * @param namePrefix
     */
    public TaskThreadFactory(String namePrefix) {
        this(namePrefix, false, Thread.NORM_PRIORITY);
    }
    /**
     *
     * @param namePrefix
     * @param priority
     */
    public TaskThreadFactory(String namePrefix, int priority) {
        this(namePrefix, false, priority);
    }
    /**
     *
     * @param namePrefix
     * @param daemon
     * @param priority
     */
    public TaskThreadFactory(String namePrefix, boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.threadPriority = priority;
    }
    @Override
    public TaskThread newThread(Runnable r) {
        TaskThread t = new TaskThread(group, r, namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        t.setPriority(threadPriority);
        return t;
    }
}
