package org.donald.zookeeper.get;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: GetChildrenAsyncUsage
 * @Description: ZooKeeper API 获取子节点列表，使用异步(ASync)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-12 13:58
 */
@Slf4j
public class GetChildrenAsyncUsage implements Watcher{
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    public static void main(String[] args) throws Exception{
        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new GetChildrenAsyncUsage());
            connectedSemaphore.await();
            zk.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep( 6000);
            zk.create(path+"/c1", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            //异步获取路径孩子节点
            zk.getChildren(path, true, new IChildren2Callback(), null);
            Thread.sleep( 6000);
            zk.create(path+"/c2", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            Thread.sleep( 6000);
            //每次path节点的字节点改变将处罚NodeChildrenChanged事件
            zk.create(path+"/c3", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
            } else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                try {
                    log.info("ReGet Child:{}",zk.getChildren(event.getPath(),true));
                } catch (Exception e) {}
            }
        }
    }
}

/**
 * 异步回调接口
 */
@Slf4j
class IChildren2Callback implements AsyncCallback.Children2Callback{
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        log.info("Get Children znode result: [response code:{}, param path:{}, ctx:{}, " +
                "children list: {}, stat:{} ",new Object[]{rc,path,ctx,children,stat} );
    }
}
