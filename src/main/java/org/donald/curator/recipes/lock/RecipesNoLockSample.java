package org.donald.curator.recipes.lock;

import lombok.extern.slf4j.Slf4j;
import org.donald.common.threadpool.TaskExecutors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: RecipesNoLockSample
 * @Description: 测试没有分布式锁的情况下，生成订单id，容易产生重复id
 * @Author: Donaldhan
 * @Date: 2018-05-16 15:56
 */
@Slf4j
public class RecipesNoLockSample {
    private static ExecutorService exec = null;
    public static void main(String[] args) {
        final CountDownLatch down = new CountDownLatch(1);
        exec = TaskExecutors.newFixedThreadPool(10);
        try {
            for(int i = 0; i < 10; i++){
                exec.submit(new Runnable() {
                    @Override
                    public void run(){
                        try{
                            //模仿同时生成订单
                            down.await();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                            String orderNo = sdf.format(new Date());
                            log.info("{} 生成的订单号是 :{} ",Thread.currentThread().getName(), orderNo);
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            down.countDown();
            if (exec != null) {
                exec.shutdown();
            }
        }

    }
}
