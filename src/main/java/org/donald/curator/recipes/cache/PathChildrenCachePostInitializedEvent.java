package org.donald.curator.recipes.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: PathChildrenCachePostInitializedEvent
 * @Description: 测试正常模式节点缓存初始化
 * @Author: Donaldhan
 * @Date: 2018-05-16 14:52
 */
@Slf4j
public class PathChildrenCachePostInitializedEvent {
    private static CuratorFramework client;
    public static void main(String[] args) {
        String path = "/zk-book/c1";
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
            PathChildrenCache cache = new PathChildrenCache(client, path, true);
            /**
             *  NORMAL,
             * cache will _not_ be primed. i.e. it will start empty and you will receive
             * events for all nodes added, etc.
             * 此模式下，节点路径监听器缓存不会初始化
             */

            cache.start(PathChildrenCache.StartMode.NORMAL);
            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client,
                                       PathChildrenCacheEvent event) {
                    switch (event.getType()) {
                        case INITIALIZED:
                            log.info("INITIALIZED,{}", event.getData().getPath());
                            break;
                        default:
                            break;
                    }
                }
            });
            Thread.sleep(6000);
            //关闭节点监听
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
