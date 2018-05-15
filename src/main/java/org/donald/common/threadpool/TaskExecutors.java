package org.donald.common.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 类TaskExecutors
 *
 * @author Donaldhan  on 2018/4/27
 **/
@Slf4j
public class TaskExecutors  extends ThreadPoolExecutor {
    /**
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    public TaskExecutors(int corePoolSize, int maximumPoolSize,
                         long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    public static ExecutorService newSingleThreadPool() {
        return new TaskExecutors(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * @param taskCount 任务线程数
     * @return
     */
    public static ExecutorService newSingleThreadPoolWithLimitTask(int taskCount) {
        return new TaskExecutors(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(taskCount));
    }
    /**
     *
     * @param nThreads 工作线程数
     * @return
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new TaskExecutors(nThreads, nThreads, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * @param nThreads 工作线程数
     * @param taskCount 任务线程数
     * @return
     */
    public static ExecutorService newFixedThreadPoolWithLimitTask(int nThreads, int taskCount) {
        return new TaskExecutors(nThreads, nThreads, 0L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(taskCount));
    }
    /**
     * 捕捉线程执行异常
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                Object result = ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null){
            log.error("线程池执行线程异常",t);
        }
    }

}
