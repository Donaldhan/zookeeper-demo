package org.donald.curator.zkpath;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: ZKPathsSample
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-06-14 15:57
 */
@Slf4j
public class ZKPathsSample {
    private final static String path = "/curator_zkpath_sample";
    private static CuratorFramework client;

    public static void main(String[] args) throws Exception {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(ConfigConstant.IP)
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();
            client.start();
            log.info("success connected...");
            //应该命令空间到给定的路径
            log.info("fixForNamespace:{}",ZKPaths.fixForNamespace(path,"sub"));
            /***
             * 以下操作，实际不创建节点，getNodeFromPath，getPathAndNode，即这些操作，节点可以不存在
             */
            //在给定路径下生成，子路径
            log.info("makePath:{}",ZKPaths.makePath(path, "sub"));
            //从全路径获取节点的name
            log.info( "getNodeFromPath:{}",ZKPaths.getNodeFromPath( "/curator_zkpath_sample/sub1" ) );
            //获取全路径的路径和节点
            ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode( "/curator_zkpath_sample/sub1" );
            log.info("getPathAndNode path:{}",pathAndNode.getPath());
            log.info("getPathAndNode node:{}",pathAndNode.getNode());

            ZooKeeper zookeeper = client.getZookeeperClient().getZooKeeper();
            String dir1 = path + "/child1";
            String dir2 = path + "/child2";
            //确保所有的创建路径时，父路径存在
            ZKPaths.mkdirs(zookeeper, dir1);
            ZKPaths.mkdirs(zookeeper, dir2);

            log.info("getSortedChildren:{}",ZKPaths.getSortedChildren( zookeeper, path ));
            ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(), path, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
