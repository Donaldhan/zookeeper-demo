package org.donald.zookeeper.get;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: GetDataSyncUsage
 * @Description: ZooKeeper API 获取节点数据内容，使用同步(sync)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-12 14:21
 */
@Slf4j
public class GetDataSyncUsage  implements Watcher{
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    //节点状态
    private static Stat stat = new Stat();
    public static void main(String[] args) throws Exception {
        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new GetDataSyncUsage());
            connectedSemaphore.await();
            zk.create( path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
            log.info(new String(zk.getData( path, true, stat ), ConfigConstant.CHAR_SET_NAME));
            log.info("Czxid:{},Mzxid:{},Version:{}", new Object[]{stat.getCzxid(), stat.getMzxid(), stat.getVersion()});
            zk.setData( path, "1234".getBytes(), -1 );
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
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                try {
                    log.info(new String(zk.getData( event.getPath(), true, stat )), ConfigConstant.CHAR_SET_NAME);
                    log.info("===Czxid:{},Mzxid:{},Version:{}", new Object[]{stat.getCzxid(), stat.getMzxid(), stat.getVersion()});
                } catch (Exception e) {}
            }
        }
    }
}
