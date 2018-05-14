package org.donald.curator.create;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: CreateNodeSample
 * @Description: 使用Curator创建节点
 * @Author: Donaldhan
 * @Date: 2018-05-14 8:24
 */
@Slf4j
public class CreateNodeSample {
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
                log.info("success create:{}...", path);
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
