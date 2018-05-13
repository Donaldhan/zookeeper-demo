package org.donald.curator.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: CreateSessionWithNamespace
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-05-13 21:13
 */
@Slf4j
public class CreateSessionWithNamespace {
    private static CuratorFramework client;
    public static void main(String[] args) {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(ConfigConstant.IP)
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .namespace("base")
                            .build();
            client.start();
            log.info("success connected...");
            log.info("client namespace:{}",client.getNamespace());
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }

}
