package org.donald.zookeeper.construction;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.donald.constant.ConfigConstant;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: UsageSimpleWithSidPasswd
 * @Description:Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例，复用sessionId和
 * @Author: Donaldhan
 * @Date: 2018-05-09 14:42
 */
@Slf4j
public class UsageSimpleWithSidPasswd implements Watcher{
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT, new UsageSimpleWithSidPasswd());
        connectedSemaphore.await();
        //获取会话id和会话密码
        long sessionId = zookeeper.getSessionId();
        byte[] passwd  = zookeeper.getSessionPasswd();
        log.info("======sessionId:{}",sessionId);
        log.info("======passwd:{}",new String(passwd,ConfigConstant.CHAR_SET_NAME));
        //使用非法会话id和会话密码
        zookeeper = new ZooKeeper(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT, new UsageSimpleWithSidPasswd(), 12L,
                "test".getBytes());
        log.info("======State-unauth:{}", zookeeper.getState());
        //使用合法会话id和会话密码
        zookeeper = new ZooKeeper(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT, new UsageSimpleWithSidPasswd(),
                sessionId, passwd);
        log.info("======State-auth:{}", zookeeper.getState());
        Thread.sleep( Integer.MAX_VALUE );
    }
    @Override
    public void process(WatchedEvent event) {
        log.info("Receive watched event:{}", event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
