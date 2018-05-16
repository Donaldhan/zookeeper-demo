package org.donald.curator.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.donald.common.threadpool.TaskExecutors;
import org.donald.constant.ConfigConstant;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: CreateNodeBackgroundSample
 * @Description: 使用Curator的异步接口
 * @Author: Donaldhan
 * @Date: 2018-05-15 8:30
 */
@Slf4j
public class CreateNodeBackgroundSample {
    private static final String path = "/zk-book";
    private static CountDownLatch semaphore = new CountDownLatch(2);
    private static ExecutorService exec = null;
    private static CuratorFramework client;

    public static void main(String[] args) {
        try {
            exec = TaskExecutors.newFixedThreadPool(2);
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
            log.info("Main thread: {}", Thread.currentThread().getName());
            // 此处传入了自定义的Executor，初始化路径值不是很靠谱，测试发现没有值。
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws UnsupportedEncodingException {
                    log.info("event[code: {}, type:{}", new Object[]{event.getResultCode(),
                            event.getType()});
                    /* 注意此时stat 和 data为null
                    String value =  null == event.getData() ?"":new String(event.getData(), ConfigConstant.CHAR_SET_NAME);
                    log.info("event[code: {}, type:{}, path:{}, data:{}, version  ", new Object[]{event.getResultCode(),
                            event.getType(), event.getPath(),value , event.getStat().getVersion()});*/
                    log.info("Thread of processResult: {}", Thread.currentThread().getName());
                    semaphore.countDown();
                }
            }, exec).forPath(path, "init".getBytes());
            Thread.sleep(3000);
            // 此处没有传入自定义的Executor,使用curator的EventThread
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws UnsupportedEncodingException {
                    log.info("event[code: {}, type:{}", new Object[]{event.getResultCode(),
                            event.getType()});
                  /*  String value =  null == event.getData() ?"":new String(event.getData(), ConfigConstant.CHAR_SET_NAME);
                    log.info("event[code: {}, type:{}, path:{}, data:{}, version  ", new Object[]{event.getResultCode(),
                            event.getType(), event.getPath(),value , event.getStat().getVersion()});*/
                    log.info("Thread of processResult: {}", Thread.currentThread().getName());
                    semaphore.countDown();
                }
            }).forPath(path, "init1".getBytes());
            semaphore.await();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (exec != null) {
                exec.shutdown();
            }
            //关闭会话删除临时节点。
           if (client != null) {
                client.close();
            }
        }
    }
}
