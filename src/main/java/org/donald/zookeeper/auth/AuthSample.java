package org.donald.zookeeper.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.donald.constant.ConfigConstant;

import java.io.IOException;
import java.util.Base64;

/**
 * @ClassName: AuthSample
 * @Description: 使用含权限信息的ZooKeeper会话创建数据节点,digest方式验证。
 * ZK的节点有5种操作权限：
 * CREATE、READ、WRITE、DELETE、ADMIN 也就是 增、删、改、查、管理权限，这5种权限简写为crwda(即：每个单词的首字符缩写)
 * 注：这5种权限中，delete是指对子节点的删除权限，其它4种权限指对自身节点的操作权限
 *
 * 身份的认证有4种方式：
 * world：默认方式，相当于全世界都能访问
 * auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)
 * digest：即用户名:密码这种方式认证，这也是业务系统中最常用的
 * ip：使用Ip地址认证
 *
 * 设置访问控制：
 * 方式一：（推荐）
 * 1）增加一个认证用户
 * addauth digest 用户名:密码明文
 * eg. addauth digest user1:password1
 * 2）设置权限
 * setAcl /path auth:用户名:密码明文:权限
 * eg. setAcl /test auth:user1:password1:cdrwa
 * 3）查看Acl设置
 * getAcl /path
 *
 * 方式二：
 * setAcl /path digest:用户名:密码密文:权限
 * 注：这里的加密规则是SHA1加密，然后base64编码。
 * @Author: Donaldhan
 * @Date: 2018-05-13 11:01
 */
@Slf4j
public class AuthSample implements Watcher {
    private final static String PATH = "/zk-book-auth_test";
    /**权限校验机制*/
    private static final String AUTH_SCHEMA = "digest";
    private static ZooKeeper zk1;
    private static ZooKeeper zk2;
    private static final String userName = "donald";
    private static final String password = Base64.getEncoder().encodeToString("123456".getBytes());
    public static void main(String[] args) throws InterruptedException {
        try {
            zk1 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthSample());
            //使用用户名加密码方式控制权限访问
            StringBuffer auth = new StringBuffer(userName).append(":").append(password);
            log.info("auth:{}",auth.toString());
            zk1.addAuthInfo(AUTH_SCHEMA, auth.toString().getBytes());
            zk1.create( PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL );
            //使用无权限访问节点,将抛出org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk-book-auth_test
            zk2 = new ZooKeeper(ConfigConstant.IP,ConfigConstant.SESSION_TIMEOUT, new AuthSample());
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
