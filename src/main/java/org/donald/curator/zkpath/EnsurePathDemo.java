package org.donald.curator.zkpath;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: EnsurePathDemo
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-06-14 16:17
 */
@Slf4j
public class EnsurePathDemo {
    private final static String path = "/zk-book/c1";
    private static CuratorFramework client;
    public static void main(String[] args) throws Exception {
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
            client.usingNamespace( "zk-book" );
            /*
             * 确保路径创建时，其父路径存在
             */
            EnsurePath ensurePath = new EnsurePath(path);
            ensurePath.ensure(client.getZookeeperClient());

            EnsurePath ensurePath2 = client.newNamespaceAwareEnsurePath("/c1");
            ensurePath2.ensure(client.getZookeeperClient());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
