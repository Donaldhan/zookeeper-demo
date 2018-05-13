package org.donald.zookeeper.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.Base64;

/**
 * @ClassName: AuthDeleteSample
 * @Description: 删除节点的权限控制
 * @Author: Donaldhan
 * @Date: 2018-05-13 11:43
 */
@Slf4j
public class AuthDeleteSample implements Watcher {
    final static String PATH  = "/zk-book-auth_test";
    final static String PATH2 = "/zk-book-auth_test/child";
    /**权限校验机制*/
    private static final String AUTH_SCHEMA = "digest";
    private static ZooKeeper zk1;
    private static ZooKeeper zk2;
    private static final String userName = "donald";
    private static final String password = Base64.getEncoder().encodeToString("123456".getBytes());
    public static void main(String[] args) throws InterruptedException {

        try {
            zk1 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthDeleteSample());
            //使用用户名加密码方式控制权限访问
            StringBuffer auth = new StringBuffer(userName).append(":").append(password);
            log.info("auth:{}",auth.toString());
            zk1.addAuthInfo(AUTH_SCHEMA, auth.toString().getBytes());
            zk1.create( PATH, "p".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT );
            zk1.create( PATH2, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );
            //删除失败
            try {
                zk2 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthDeleteSample());
                zk2.delete( PATH2, -1 );
            } catch ( Exception e ) {
                log.error( "删除节点失败!" ,e);
            }
            log.info( "成功删除节点：{}" , PATH );
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
