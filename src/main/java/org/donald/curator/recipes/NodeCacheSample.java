package org.donald.curator.recipes;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.donald.constant.ConfigConstant;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: NodeCacheSample
 * @Description: 节点监听器，类似于原生Watcher
 * @Author: Donaldhan
 * @Date: 2018-05-16 8:24
 */
@Slf4j
public class NodeCacheSample {
    private static CuratorFramework client;
    public static void main(String[] args) {
        String path = "/zk-book/nodecache";
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
            final NodeCache cache = new NodeCache(client,path,false);
            cache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws UnsupportedEncodingException {
                    log.info("Node data update, new data: {}" , new String(cache.getCurrentData().getData(), ConfigConstant.CHAR_SET_NAME));
                }
            });
            //不要忘记启动cache
            cache.start(true);
            //如果需要创建父节点，需要注意一个问题，创建的父节点是持久化的
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, "init".getBytes());
            log.info("success create:{}...", path);
            client.setData().forPath( path, "update".getBytes() );
            log.info("success update:{}...", path);
            Thread.sleep( 3000 );
            client.delete().deletingChildrenIfNeeded().forPath( path );
            log.info("success delete:{}...", path);
            Thread.sleep(3000);
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
