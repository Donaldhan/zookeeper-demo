package org.donald.zookeeper.exists;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ExistsSyncUsage
 * @Description: ZooKeeper API 删除节点，使用同步(sync)接口。注意，如果重新注册监听器，必须在下一个事件操作之前，
 * 如果同时操作，或者前后，很有可能监听不到相关的事件。
 * @Author: Donaldhan
 * @Date: 2018-05-12 14:53
 */
@Slf4j
public class ExistsSyncUsage implements Watcher{
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;
    public static void main(String[] args) throws Exception {
        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new ExistsSyncUsage());
            connectedSemaphore.await();
            /**
             * 检查节点是否存在，默认注册一个Watcher监听接口，Watcher监听是一次性的，每次都要事件触发后，都要重新注册,
             * @param path
             *                the node path
             * @param watch
             *                whether need to watch this node,
             * 此节点为true时，默认注册exists(path, watch ? watchManager.defaultWatcher : null);
             * 默认watcher为ZKWatchManager的defaultWatcher，defaultWatcher默认是在构造Zk客户端时，传入的Watcher。
             * 具体见：
             * public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly)
             *  throws IOException
             *   LOG.info("Initiating client connection, connectString=" + connectString
             *           + " sessionTimeout=" + sessionTimeout + " watcher=" + watcher);
             *   watchManager.defaultWatcher = watcher;
             * }
             */
            zk.exists( path, true );
            zk.create( path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
            //为重新注册监听器，争取时间
            Thread.sleep(3000);
            zk.setData(path, "123".getBytes(), -1 );
            // 注册监听path+"/c1"
            zk.exists( path+"/c1", true );
            zk.create( path+"/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
            Thread.sleep( 3000 );
            zk.setData(path+"/c1", "456".getBytes(), -1 );
            Thread.sleep( 3000 );
            zk.delete( path+"/c1", -1 );
            zk.setData(path, "789".getBytes(), -1 );
            Thread.sleep( 3000 );
            zk.delete( path, -1 );
            Thread.sleep( Integer.MAX_VALUE );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
            if(null != zk){
                zk.close();
            }
        }
    }
   @Override
    public void process(WatchedEvent event) {
        try {
            if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                    connectedSemaphore.countDown();
                } else if (Watcher.Event.EventType.NodeCreated == event.getType()) {
                    log.info("Node({}) Created",event.getPath());
                    zk.exists( event.getPath(), true );
                } else if (Watcher.Event.EventType.NodeDeleted == event.getType()) {
                    log.info("Node({}) Deleted",event.getPath());
                    zk.exists( event.getPath(), true );
                } else if (Watcher.Event.EventType.NodeDataChanged == event.getType()) {
                    log.info("Node({}) DataChanged",event.getPath());
                    zk.exists( event.getPath(), true );
                }
                else if (Watcher.Event.EventType.NodeChildrenChanged == event.getType()) {
                    log.info("Node({}) NodeChildrenChanged",event.getPath());
                    zk.exists( event.getPath(), true );
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);

        }
    }
}
