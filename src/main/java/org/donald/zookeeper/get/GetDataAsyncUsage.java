package org.donald.zookeeper.get;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: GetDataAsyncUsage
 * @Description:  ZooKeeper API 获取节点数据内容，使用异步(async)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-12 14:30
 */
public class GetDataAsyncUsage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;
    public static void main(String[] args) throws Exception {

        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new GetDataAsyncUsage());
            connectedSemaphore.await();
            zk.create( path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
            zk.getData( path, true, new IDataCallback(), null );
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
                    zk.getData( event.getPath(), true, new IDataCallback(), null );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

/**
 * 获取节点数据异步回调
 */
@Slf4j
class IDataCallback implements AsyncCallback.DataCallback{
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        log.info("rc:{},path:{} ,data:{}" ,new Object[]{rc,path,new String(data)});
        log.info("===Czxid:{},Mzxid:{},Version:{}", new Object[]{stat.getCzxid(), stat.getMzxid(), stat.getVersion()});
    }
}
