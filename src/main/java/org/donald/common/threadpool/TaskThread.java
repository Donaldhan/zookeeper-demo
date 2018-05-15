package org.donald.common.threadpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 类TaskThread
 *
 * @author Donaldhan  on 2018/4/27
 **/
@Getter
@Slf4j
public class TaskThread extends Thread{
    private final long creationTime;

    public TaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, new WrapperRunnable(target), name);
        this.creationTime = System.currentTimeMillis();
    }

    public TaskThread(ThreadGroup group, Runnable target, String name,
                      long stackSize) {
        super(group, new WrapperRunnable(target), name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }
    private static class WrapperRunnable implements Runnable {
        private Runnable wrappedRunnable;
        WrapperRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }
        @Override
        public void run() {
            try{
                wrappedRunnable.run();
            }
            catch(Throwable t){
                log.error("{}线程执行异常!",Thread.currentThread().getName(), t);
                t.printStackTrace();
            }
        }
    }

}
