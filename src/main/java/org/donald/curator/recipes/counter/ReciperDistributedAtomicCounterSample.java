package org.donald.curator.recipes.counter;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.donald.common.threadpool.TaskExecutors;
import org.donald.constant.ConfigConstant;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: ReciperDistributedAtomicCounterSample
 * @Description: 使用Curator实现分布式计数器
 * @Author: Donaldhan
 * @Date: 2018-05-16 16:39
 */
@Slf4j
public class ReciperDistributedAtomicCounterSample {
    private static CuratorFramework client;
    private static ExecutorService exec = null;
    public static void main(String[] args) {
        String distatomicint_path = "/curator_recipes_distatomicint_path";
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
            exec = TaskExecutors.newFixedThreadPool(30);
            DistributedAtomicInteger atomicInteger =
                    new DistributedAtomicInteger( client, distatomicint_path,
                            new RetryNTimes( 3, 1000 ) );
            for(int i = 0; i < 30; i++){
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            down.await();
                            AtomicValue<Integer> rc = atomicInteger.add(1);
                            log.info( "Result: {}, value pre: {}, post: {}",new Object[] {rc.succeeded(), rc.preValue(), rc.postValue()});
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
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
            /*  如果在会话执行的过程中，关闭会话，将抛出异常
            java.lang.IllegalStateException: instance must be started before calling this method
            if (client != null) {
                client.close();
            }*/
        }
    }
}
