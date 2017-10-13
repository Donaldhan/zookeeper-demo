package book.chapter05.$5_3_1;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例，复用sessionId和
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {
	private static Logger log = LoggerFactory.getLogger(ZooKeeper_Constructor_Usage_With_SID_PASSWD.class);
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("192.168.126.134:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        connectedSemaphore.await();
        long sessionId = zookeeper.getSessionId();
        byte[] passwd  = zookeeper.getSessionPasswd();
        log.info("======sessionId:"+sessionId);
        log.info("======passwd:"+new String(passwd,"UTF-8"));
        //Use illegal sessionId and sessionPassWd
        zookeeper = new ZooKeeper("192.168.126.134:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				1l,//
				"test".getBytes());
        log.info("======State-unauth:"+zookeeper.getState());
        //Use correct sessionId and sessionPassWd
        zookeeper = new ZooKeeper("192.168.126.134:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				sessionId,//
				passwd);
        log.info("======State-auth:"+zookeeper.getState());
        Thread.sleep( Integer.MAX_VALUE );
    }
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}