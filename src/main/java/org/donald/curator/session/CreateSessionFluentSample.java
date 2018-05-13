package org.donald.curator.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: CreateSessionFluentSample
 * @Description: 使用Fluent风格的API接口来创建一个ZooKeeper客户端
 * @Author: Donaldhan
 * @Date: 2018-05-13 21:08
 */
@Slf4j
public class CreateSessionFluentSample {
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
                            .build();
            client.start();
            log.info("success connected...");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
