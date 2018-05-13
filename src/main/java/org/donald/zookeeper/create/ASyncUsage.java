package org.donald.zookeeper.create;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ASyncUsage
 * @Description: ZooKeeper API创建节点，使用异步(async)接口。
 * @Author: Donaldhan
 * @Date: 2018-05-09 16:36
 */
@Slf4j
public class ASyncUsage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = null;
        try {
            zookeeper = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT,new ASyncUsage());
            connectedSemaphore.await();
            zookeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallback(), "I am context.");
            //需要注意的是，异步回调接口，当节点已经存在是，不是抛出异常，而是返回状态码
            zookeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallback(), "I am context.");

            zookeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                    new IStringCallback(), "I am context.");
            Thread.sleep( Integer.MAX_VALUE );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
/**
 * 创建节点异步回调接口,需要注意的是，异步回调接口，当节点已经存在是，不是抛出异常，而是返回状态码
 * @See org.apache.zookeeper.KeeperException
 *
 * Ok = 0;
 * ConnectionLoss = -4;
 * NodeExists = -110;
 * SessionExpired = -112;
 */
@Slf4j
class IStringCallback implements AsyncCallback.StringCallback{
    /**
     *  On success, rc is KeeperException.Code.OK.
     *
     * On failure, rc is set to the corresponding failure code in KeeperException.
     *
     *     KeeperException.Code.NODEEXISTS - The node on give path already exists for some API calls.
     *     KeeperException.Code.NONODE - The node on given path doesn't exists for some API calls.
     *     KeeperException.Code.NOCHILDRENFOREPHEMERALS - an ephemeral node cannot have children. There is discussion in community. It might be changed in the future.
     *
     * Parameters:
     *     rc - The return code or the result of the call.
     *     path - The path that we passed to asynchronous calls.
     *     ctx - Whatever context object that we passed to asynchronous calls. 路径节点对应的值
     *     name - The name of the Znode that was created. On success, name and path are usually equal, unless a sequential node has been created.
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        log.info("Create path result: [{}, {}, {}, real path name:{} ]" ,new Object[]{rc, path, ctx, name});
    }
}
