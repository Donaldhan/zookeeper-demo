package org.donald.curator.set;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: SetDataSample
 * @Description: 使用Curator更新数据内容
 * @Author: Donaldhan
 * @Date: 2018-05-14 9:19
 */
@Slf4j
public class SetDataSample {
    private static CuratorFramework client;
    public static void main(String[] args) {
        String path = "/zk-book/c1";
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(ConfigConstant.BASE_SLEEP_TIMES, ConfigConstant.MAX_RETRIES);
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(ConfigConstant.IP)
                            .sessionTimeoutMs(ConfigConstant.SESSION_TIMEOUT)
                            .connectionTimeoutMs(ConfigConstant.CONNETING_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();
            log.info("success connected...");
            client.start();
            //当前节点不存在时，将抛出org.apache.zookeeper.KeeperException$NoNodeException: KeeperErrorCode = NoNode for /zk-book/c1
//            client.delete().deletingChildrenIfNeeded().forPath( path );
            //如果需要创建父节点，需要注意一个问题，创建的父节点是持久化的
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, "init".getBytes());
            Stat stat = new Stat();
            String value = new String(client.getData().storingStatIn(stat).forPath(path), ConfigConstant.CHAR_SET_NAME);
            log.info("{} value :{} , stat:{}", new Object[]{path, value, JSONObject.toJSONString(stat)});
            log.info("Success set node for : {}, new version: {}", path ,
                     client.setData().withVersion(stat.getVersion()).forPath(path,value.getBytes()).getVersion());
            //待旧的版本更新，将抛出org.apache.zookeeper.KeeperException$BadVersionException: KeeperErrorCode = BadVersion for /zk-book/c1
            client.setData().withVersion(stat.getVersion()).forPath(path,value.getBytes());
            //如果需要删除子节点
            client.delete().deletingChildrenIfNeeded()
                    .withVersion(stat.getVersion()).forPath(path);
            log.info("success delete :{}, stat :{}...",path, JSONObject.toJSONString(stat));
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
