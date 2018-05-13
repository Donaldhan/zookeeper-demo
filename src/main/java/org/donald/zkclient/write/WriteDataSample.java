package org.donald.zkclient.write;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: WriteDataSample
 * @Description: ZkClient更新节点数据
 * @Author: Donaldhan
 * @Date: 2018-05-13 20:12
 */
@Slf4j
public class WriteDataSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            String path = "/zk-book";
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            //创建临时节点
            zkClient.createEphemeral(path, new Integer(1));
            zkClient.writeData(path, new Integer(1));
            log.info("{} value:{}",path , zkClient.readData(path));
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
