package org.donald.curator.recipes.barrier;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: DistributedDoubleBarrierSample
 * @Description: 分布式锁， 控制同时进入，同时退出
 * @Author: Donaldhan
 * @Date: 2018-05-17 8:37
 */
@Slf4j
public class DistributedDoubleBarrierSample1 {
    private static CuratorFramework client = null;
    private static DistributedDoubleBarrier barrier;
    public static void main(String[] args) {
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
            barrier  = new DistributedDoubleBarrier(client, ConfigConstant.BARRIER_PATH, 3);
            log.info("{} 号barrier设置", DistributedDoubleBarrierSample1.class.getSimpleName());
            //等待所有分布式屏障节点到达屏障
            Thread.sleep(6000);
            barrier.enter();
            log.info("{} 启动...", DistributedDoubleBarrierSample1.class.getSimpleName());
            //等待所有分布式屏障节点到达屏障
            Thread.sleep(6000);
            barrier.leave();
            log.info( "退出..." );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          /*  if (client != null) {
                client.close();
            }*/
        }
    }
}
