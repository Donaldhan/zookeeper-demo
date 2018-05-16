package org.donald.curator.recipes.masterselect;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: MasterSelectDemo
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-05-16 15:17
 */
@Slf4j
public class MasterSelectDemo1 {
    private static CuratorFramework client;
    public static void main(String[] args) {
        String master_path = ConfigConstant.MASTER_SELECTOR_PATH;
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
            LeaderSelector selector = new LeaderSelector(client,
                    master_path,
                    new LeaderSelectorListenerAdapter() {
                        @Override
                        public void takeLeadership(CuratorFramework client) throws Exception {
                            log.info("成为Master角色");
                            //成为master后，可以执行相关的业务逻辑，然后释放Master权利
                            Thread.sleep( 6000 );
                            log.info( "完成Master操作，释放Master权利" );
                        }
                    });
            selector.autoRequeue();
            selector.start();
            Thread.sleep( Integer.MAX_VALUE );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
