package org.donald.zookeeper.set;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: SetDataSyncUsage
 * @Description: ZooKeeper API 更新节点数据内容，使用同步(sync)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-12 14:39
 */
@Slf4j
public class SetDataSyncUsage implements Watcher{
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;
    public static void main(String[] args) throws Exception {
        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new SetDataSyncUsage());
            connectedSemaphore.await();
            zk.create( path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
            log.info(new String(zk.getData( path, true, null ),ConfigConstant.CHAR_SET_NAME));
            Stat stat = zk.setData( path, "456".getBytes(), -1 );
            log.info("Czxid:{},Mzxid:{},Version:{}", new Object[]{stat.getCzxid(), stat.getMzxid(), stat.getVersion()});
            Stat stat2 = zk.setData( path, "789".getBytes(), stat.getVersion() );
            log.info("Czxid:{},Mzxid:{},Version:{}", new Object[]{stat2.getCzxid(), stat2.getMzxid(), stat2.getVersion()});
            zk.setData( path, "456".getBytes(), stat.getVersion() );
            Thread.sleep( Integer.MAX_VALUE );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            log.error("Error: {},{}",e.code(),e.getMessage());
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
            }
        }
    }
}
