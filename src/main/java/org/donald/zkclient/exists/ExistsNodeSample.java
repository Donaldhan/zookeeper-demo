package org.donald.zkclient.exists;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: ExistsNodeSample
 * @Description: ZkClient检测节点是否存在
 * @Author: Donaldhan
 * @Date: 2018-05-13 20:16
 */
@Slf4j
public class ExistsNodeSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            String path = "/zk-book";
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            log.info("Node {} exists {}", path ,zkClient.exists(path));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
