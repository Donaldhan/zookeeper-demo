package book.chapter05.$5_3_1;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例
public class ZooKeeper_Constructor_Usage_Simple implements Watcher {
	private static Logger log = LoggerFactory.getLogger(ZooKeeper_Constructor_Usage_Simple.class);
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    
    public static void main(String[] args) throws Exception{
        
        ZooKeeper zookeeper = new ZooKeeper("192.168.126.134:2181", 
        									5000, //
        									new ZooKeeper_Constructor_Usage_Simple());
        System.out.println(zookeeper.getState());
        log.info("======State:"+zookeeper.getState());
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {}
        System.out.println("=========ZooKeeper session established.");
        log.info("======State:"+zookeeper.getState());
    }
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}