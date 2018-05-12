package org.donald.zookeeper.delete;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: SyncDeleteUsage
 * @Description: ZooKeeper API 删除节点，使用同步(sync)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-09 17:01
 */
@Slf4j
public class SyncDeleteUsage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = null;
        try {
            String path = "/zk-book";
            zookeeper = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new SyncDeleteUsage());
            connectedSemaphore.await();
            String pathx = zookeeper.create( path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
            log.info("======pathx:{}",pathx);
            /*
             * 如果节点不存在，或节点有子节点，或节点版本不对，则抛出KeeperException
             * @param path
             *                the path of the node to be deleted.
             * @param version
             *                the expected node version.
             */
            zookeeper.delete( path, -1 );
            Thread.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        finally {
            if(null != zookeeper){
                zookeeper.close();
            }
        }
    }
    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
           if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
