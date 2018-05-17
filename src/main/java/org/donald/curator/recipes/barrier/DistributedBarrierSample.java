package org.donald.curator.recipes.barrier;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.common.threadpool.TaskExecutors;
import org.donald.constant.ConfigConstant;

import java.util.concurrent.ExecutorService;

/**
 * @ClassName: DistributedBarrierSample
 * @Description: 使用Curator实现分布式Barrier，实际在分布式环境中使用，待所有应用到达屏障时，移除屏障。
 * @Author: Donaldhan
 * @Date: 2018-05-16 17:39
 */
@Slf4j
public class DistributedBarrierSample {
    /**
     * 布式Barrier
     */
    private static DistributedBarrier barrier;
    private static ExecutorService exec = null;
    public static void main(String[] args) throws Exception {
        try {
            exec = TaskExecutors.newFixedThreadPool(5);
            for (int i = 0; i < 5; i++) {
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
                        CuratorFramework client =
                                CuratorFrameworkFactory.builder()
                                        .connectString(ConfigConstant.IP)
                                        .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                                        .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                                        .retryPolicy(retryPolicy)
                                        .build();
                        client.start();
                        barrier = new DistributedBarrier(client, ConfigConstant.BARRIER_PATH);
                        log.info("{} 号barrier设置", Thread.currentThread().getName());
                        try {
                            barrier.setBarrier();
                            barrier.waitOnBarrier();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        log.info("{} 启动...", Thread.currentThread().getName());
                    }
                });
            }
            //等待所有分布式屏障节点到达屏障，以免在所有节点到达前，移除屏障
            Thread.sleep( 6000 );
            barrier.removeBarrier();
            log.info("移除分布式屏障...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (exec != null) {
                exec.shutdown();
            }
        }
    }
}
