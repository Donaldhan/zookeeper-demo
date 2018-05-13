package org.donald.zkclient.delete;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: DeleteNodeSample
 * @Description: ZkClient删除节点数据
 * @Author: Donaldhan
 * @Date: 2018-05-13 19:43
 */
@Slf4j
public class DeleteNodeSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            String path = "/zk-book";
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            log.info("success connected ...");
            //此方法，路径存在是，将会抛出org.I0Itec.zkclient.exception.ZkNodeExistsException: org.apache.zookeeper.KeeperException$NodeExistsException:
            // KeeperErrorCode = NodeExists for /zk-book
            zkClient.createPersistent(path, "123");
            zkClient.createPersistent(path+"/c1", "456");
            /*
             * 如果路径有子路径，则递归删除子路径
             */
            zkClient.deleteRecursive(path);
            log.info("success delete:{} ...",path);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
