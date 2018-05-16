package org.donald.curator.recipes.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: NodeCacheNotExistsSample
 * @Description: 测试监听节点不存在的情况
 * @Author: Donaldhan
 * @Date: 2018-05-16 14:42
 */
@Slf4j
public class NodeCacheNotExistsSample {
    private static CuratorFramework client;
    public static void main(String[] args) {
        String path = "/curator_nodecache_sample";
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(ConfigConstant.IP)
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();
            log.info("success connected...");
            client.start();
            final NodeCache cache = new NodeCache(client, path, false);
            cache.start(true);
            cache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() {
                    log.info("Node data update, new data: {}", new String(cache.getCurrentData().getData()));
                }
            });
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, "init".getBytes());
            Thread.sleep(6000);
            //使用过后，不要忘了关闭节点缓存
            cache.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
