package org.donald.curator.recipes.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.common.threadpool.TaskExecutors;
import org.donald.constant.ConfigConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: RecipesLockSample
 * @Description: 测试使用分布式锁的情况下，生成订单id，可以避免产生重复id
 * @Author: Donaldhan
 * @Date: 2018-05-16 16:01
 */
@Slf4j
public class RecipesLockSample {
    private static CuratorFramework client;
    private static ExecutorService exec = null;
    public static void main(String[] args) {
        String lock_path = "/curator_recipes_lock_path";
        //模仿同时生成订单
        final CountDownLatch down = new CountDownLatch(1);
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(ConfigConstant.IP)
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();
            client.start();
            log.info("success connected...");
            //分布式锁
            final InterProcessMutex lock = new InterProcessMutex(client,lock_path);
            exec = TaskExecutors.newFixedThreadPool(30);
            for(int i = 0; i < 30; i++){
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //模仿同时生成订单
                            down.await();
                            //获取分布式锁
                            lock.acquire();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                            String orderNo = sdf.format(new Date());
                            log.info("{} 生成的订单号是 :{} ",Thread.currentThread().getName(), orderNo);

                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                        finally {
                            try {
                                lock.release();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            down.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (exec != null) {
                exec.shutdown();
            }
            /*  如果在会话执行的过程中，如果关闭会话，将抛出异常
            java.lang.IllegalStateException: instance must be started before calling this method
            if (client != null) {
                client.close();
            }*/
        }
    }
}
