package org.donald.curator.recipes.barrier;
import lombok.extern.slf4j.Slf4j;
import org.donald.common.threadpool.TaskExecutors;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: CyclicBarrierJdkSample
 * @Description: 基于jdk的屏障
 * @Author: Donaldhan
 * @Date: 2018-05-16 17:33
 */
@Slf4j
public class CyclicBarrierJdkSample {
    public static CyclicBarrier barrier = new CyclicBarrier( 3 );
    public static ExecutorService exec = null;
    public static void main( String[] args ) throws IOException, InterruptedException {
        try {
            exec = TaskExecutors.newFixedThreadPool( 3 );
            exec.submit( new Runner( "1号选手" ,barrier));
            exec.submit( new Runner( "2号选手" ,barrier));
            exec.submit( new Runner( "3号选手" ,barrier));
            exec.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (exec != null) {
                exec.shutdown();
            }
        }
    }
}
@Slf4j
class Runner implements Runnable {
    private String name;
    private  CyclicBarrier barrier;

    public Runner(String name, CyclicBarrier barrier) {
        this.name = name;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        log.info( name + " 准备好了." );
        try {
            CyclicBarrierJdkSample.barrier.await();
            log.info( name + " 起跑!" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}