package org.donald.zookeeper.get;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: GetChildrenSyncUsage
 * @Description: ZooKeeper API 获取子节点列表，使用同步(sync)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-09 17:39
 */
@Slf4j
public class GetChildrenSyncUsage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static  ZooKeeper zk = null;
    public static void main(String[] args) throws Exception{
        try {
            String path = "/zk-book";
            zk = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new GetChildrenSyncUsage());
            connectedSemaphore.await();
            zk.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.create(path+"/c1", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            List<String> childrenList = zk.getChildren(path, true);
            log.info("{} children:{}",path,JSONObject.toJSONString(childrenList));
            zk.create(path+"/c2", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
            } else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                try {
                    log.info("ReGet Child:{}",zk.getChildren(event.getPath(),true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
