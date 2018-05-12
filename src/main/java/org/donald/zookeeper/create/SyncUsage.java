package org.donald.zookeeper.create;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: SyncUsage
 * @Description: ZooKeeper API创建节点，使用同步(sync)接口
 * @Author: Donaldhan
 * @Date: 2018-05-09 15:53
 */
@Slf4j
public class SyncUsage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new SyncUsage());
            connectedSemaphore.await();
            String path1 = zookeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.EPHEMERAL);
            log.info("Success create znode path1: {}" , path1);
       /* 同步情况下，如果节点已经存在，则抛出如下异常：
       Exception in thread "main" org.apache.zookeeper.KeeperException$NodeExistsException: KeeperErrorCode = NodeExists for /zk-test-ephemeral-
                at org.apache.zookeeper.KeeperException.create(KeeperException.java:119)
        at org.apache.zookeeper.KeeperException.create(KeeperException.java:51)
        at org.apache.zookeeper.ZooKeeper.create(ZooKeeper.java:783)
        at org.donald.zookeeper.create.SyncUsage.main(SyncUsage.java:21)
        */
            String path2 = zookeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("Success create znode path2: {}" , path2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
            if(null != zookeeper){
                zookeeper.close();
            }
        }
    }
    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
