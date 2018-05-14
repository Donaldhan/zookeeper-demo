package org.donald.curator.delete;

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
 * @ClassName: DeleteDataSample
 * @Description: 使用Curator删除节点
 * @Author: Donaldhan
 * @Date: 2018-05-14 8:36
 */
@Slf4j
public class DeleteDataSample {
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
            //如果需要创建父节点，需要注意一个问题，创建的父节点是持久化的
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, "init".getBytes());
            Stat stat = new Stat();
            String value = new String(client.getData().storingStatIn(stat).forPath(path), ConfigConstant.CHAR_SET_NAME);
            log.info("{} value :{} , stat:{}", new Object[]{path, value, JSONObject.toJSONString(stat)});
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
