package org.donald.curator.recipes;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.donald.common.threadpool.TaskExecutors;
import org.donald.constant.ConfigConstant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: PathChildrenCacheSample
 * @Description: 监控路径子节点变化,使用自定义线程池，执行监听事件
 * @Author: Donaldhan
 * @Date: 2018-05-16 9:37
 */
@Slf4j
public class PathChildrenCacheWithExecutorServiceSample {
    private static CuratorFramework client;
    private static ExecutorService exec = TaskExecutors.newFixedThreadPool(2);
    public static void main(String[] args) {
        String path = "/zk-book";
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
            log.info( "current thread name:{} ", Thread.currentThread().getName() );
            PathChildrenCache cache = new PathChildrenCache(client, path, true, false, exec);
            /**
             * NORMAL,
             * cache will _not_ be primed. i.e. it will start empty and you will receive
             * events for all nodes added, etc.
             */
            cache.start(PathChildrenCache.StartMode.NORMAL);
            cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client,
                                       PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            String currentPathValue =  new String(client.getData().storingStatIn(new Stat()).forPath(path),ConfigConstant.CHAR_SET_NAME);
                            log.info("CHILD_ADDED,{},parent node data: {}",event.getData().getPath(), currentPathValue);
                            log.info( "current thread name:{} ", Thread.currentThread().getName() );
                            break;
                        case CHILD_UPDATED:
                            log.info("CHILD_UPDATED,{},vaule:{}",event.getData().getPath(), new String(event.getData().getData(),ConfigConstant.CHAR_SET_NAME));
                            log.info( "current thread name:{} ", Thread.currentThread().getName() );
                            break;
                        case CHILD_REMOVED:
                            log.info("CHILD_REMOVED,{}",event.getData().getPath());
                            log.info( "current thread name:{} ", Thread.currentThread().getName() );
                            break;
                        default:
                            break;
                    }
                }
            });
            client.create().withMode(CreateMode.PERSISTENT).forPath(path,"init".getBytes());
            log.info("success create /zk-book...");
            Thread.sleep( 1000 );
            client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1");
            log.info("success create child c1...");
            Thread.sleep( 1000 );
            client.setData().forPath(path+"/c1","update".getBytes());
            log.info("success update child c1...");
            Thread.sleep( 1000 );
            client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1/d1");
            log.info("success create child c1/d1 ...");
            Thread.sleep( 1000 );
            client.delete().forPath(path+"/c1/d1");
            log.info("success delete child c1/d1 ...");
            Thread.sleep( 1000 );
            client.delete().forPath(path+"/c1");
            log.info("success delete child c1 ...");
            Thread.sleep( 1000 );
            client.delete().forPath(path);
            log.info("success delete child /zk-book...");
            Thread.sleep( 3000 );
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
