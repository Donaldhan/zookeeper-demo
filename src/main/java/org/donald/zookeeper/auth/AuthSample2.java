package org.donald.zookeeper.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.Base64;

/**
 * @ClassName: AuthSample2
 * @Description: 使用错误权限信息的ZooKeeper会话访问含权限信息的数据节点。
 * 当客户使用错误的访问控制权限，或没有权限，访问被权限控制的节点保护的节点时，
 * 将会抛出org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth 异常
 * @Author: Donaldhan
 * @Date: 2018-05-13 11:23
 */
@Slf4j
public class AuthSample2 implements  Watcher{
    private final static String PATH = "/zk-book-auth_test";
    /**权限校验机制*/
    private static final String AUTH_SCHEMA = "digest";
    private static ZooKeeper zk1;
    private static ZooKeeper zk2;
    private static ZooKeeper zk3;
    private static final String userName = "donald";
    private static final String password = Base64.getEncoder().encodeToString("123456".getBytes());
    public static void main(String[] args) throws Exception {
        try {
            zk1 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthSample2());
            //使用用户名加密码方式控制权限访问
            StringBuffer auth = new StringBuffer(userName).append(":").append(password);
            log.info("auth:{}",auth.toString());
            zk1.addAuthInfo(AUTH_SCHEMA, auth.toString().getBytes());
            zk1.create( PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );
            //等待节点创建成功
            Thread.sleep(3000);
            zk2 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthSample2());
            zk2.addAuthInfo(AUTH_SCHEMA, auth.toString().getBytes());
            //使用正确的用户名加密码方式控制权限访问
            log.info("{} value is :{}", PATH, new String(zk2.getData( PATH, false, null ),ConfigConstant.CHAR_SET_NAME));
            //等待客户端2，获取数据完毕
            Thread.sleep(3000);
            zk3 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthSample2());
            //使用错误的用户名加密码方式控制权限访问，org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk-book-auth_test
            log.info("{} value:{}", PATH, new String(zk3.getData( PATH, false, null ),ConfigConstant.CHAR_SET_NAME));
            Thread.sleep( Integer.MAX_VALUE );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(null != zk1){
                zk1.close();
            }
            if(null != zk2){
                zk2.close();
            }
            if(null != zk3){
                zk3.close();
            }
        }
    }
    @Override
    public void process(WatchedEvent event) {
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            if (Watcher.Event.EventType.None == event.getType() && null == event.getPath()) {
                log.info("is connected....");
            }
        }
    }
}
