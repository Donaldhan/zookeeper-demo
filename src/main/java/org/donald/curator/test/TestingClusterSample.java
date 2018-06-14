package org.donald.curator.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;

/**
 * @ClassName: TestingClusterSample
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-06-14 16:28
 */
@Slf4j
public class TestingClusterSample {
    private static TestingCluster cluster;
    public static void main(String[] args) throws Exception {
        try {
            cluster = new TestingCluster(3);
            cluster.start();
            Thread.sleep(2000);

            TestingZooKeeperServer leader = null;
            for (TestingZooKeeperServer zs : cluster.getServers()) {
                log.info("getServerId：{}",zs.getInstanceSpec().getServerId() + "-");
                log.info("getServerState：{}",zs.getQuorumPeer().getServerState() + "-");
                log.info("getAbsolutePath：{}",zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
                if (zs.getQuorumPeer().getServerState().equals("leading")) {
                    leader = zs;
                }
            }
            leader.kill();
            log.info("--After leader kill:");
            for (TestingZooKeeperServer zs : cluster.getServers()) {
                log.info("getServerId：{}",zs.getInstanceSpec().getServerId() + "-");
                log.info("getServerState：{}",zs.getQuorumPeer().getServerState() + "-");
                log.info("getAbsolutePath：{}",zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.stop();
            }
        }
    }
}
