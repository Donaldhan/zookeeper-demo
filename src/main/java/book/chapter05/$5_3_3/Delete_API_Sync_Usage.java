package book.chapter05.$5_3_3;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ZooKeeper API 删除节点，使用同步(sync)接口。
public class Delete_API_Sync_Usage implements Watcher {
	private static Logger log = LoggerFactory.getLogger(Delete_API_Sync_Usage.class);
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;

    public static void main(String[] args) throws Exception {

    	String path = "/zk-book";
    	zk = new ZooKeeper("192.168.126.134:2181", 
				5000, //
				new Delete_API_Sync_Usage());
    	connectedSemaphore.await();

    	String pathx = zk.create( path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
    	log.info("======pathx:"+pathx);
    	zk.delete( path, -1 );
    	Thread.sleep(5000);
    }
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            if (EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}