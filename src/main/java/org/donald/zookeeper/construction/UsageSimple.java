package org.donald.zookeeper.construction;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.donald.constant.ConfigConstant;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: UsageSimple
 * @Description: Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例
 * @Author: Donaldhan
 * @Date: 2018-05-09 9:43
 */
@Slf4j
public class UsageSimple implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT, new UsageSimple());
        log.info("======State:"+zookeeper.getState());
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            log.error("中断异常",e);

        }
        log.info("======ZooKeeper session established State:{}", zookeeper.getState());
    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
