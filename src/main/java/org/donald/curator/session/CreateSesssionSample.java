package org.donald.curator.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: CreateSesssionSample
 * @Description: 使用curator来创建一个ZooKeeper客户端
 * @Author: Donaldhan
 * @Date: 2018-05-13 20:44
 */
@Slf4j
public class CreateSesssionSample {
    private static CuratorFramework client;

    public static void main(String[] args) {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.newClient(ConfigConstant.IP,
                            ConfigConstant.SESSION_TIMEOUT,
                            ConfigConstant.CONNETING_TIMEOUT,
                            retryPolicy);
            client.start();
            log.info("success connected...");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
