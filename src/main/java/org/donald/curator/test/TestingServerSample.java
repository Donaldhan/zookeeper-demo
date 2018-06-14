package org.donald.curator.test;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.donald.constant.ConfigConstant;

import java.io.File;

/**
 * @ClassName: TestingServerSample
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-06-14 16:29
 */
@Slf4j
public class TestingServerSample {
    private final static String path = "/zookeeper";
    private static CuratorFramework client;
    private  static TestingServer server;
    public static void main(String[] args) throws Exception {
        try {
            server = new TestingServer(ConfigConstant.PORT,new File("/home/admin/zk-demo-data"));
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(server.getConnectString())
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();

            client.start();
            log.info("success connected...");
            log.info("forPath:{}",client.getChildren().forPath(path).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.close();
            }
        }
    }
}
