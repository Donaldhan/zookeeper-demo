package org.donald.zkclient.create;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: CreateNodeSample
 * @Description: 使用ZkClient创建节点，注意：当递归创建持久化路径的方法传入的createParents为true时，节点已经存在，不会抛出异常。
 * @Author: Donaldhan
 * @Date: 2018-05-13 19:28
 */
@Slf4j
public class CreateNodeSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            log.info("success connected ...");
            String path = "/zk-book/c1";
            //如果父节点不存在，可以创建父节点
            zkClient.createPersistent(path, true);
            log.info("success create:{} ...",path);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
