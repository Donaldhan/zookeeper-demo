package org.donald.zkclient.read;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

/**
 * @ClassName: ReadDataSample
 * @Description: ZkClient获取节点数据
 * @Author: Donaldhan
 * @Date: 2018-05-13 20:03
 */
@Slf4j
public class ReadDataSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            String path = "/zk-book";
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            zkClient.createEphemeral(path, "123");
            zkClient.subscribeDataChanges(path, new IZkDataListener() {
                @Override
                public void handleDataDeleted(String dataPath) {
                    log.info("Node:{} deleted", dataPath);
                }
                @Override
                public void handleDataChange(String dataPath, Object data) {
                    log.info("Node:{} changed, new data: {}", dataPath, data);
                }
            });
            log.info("{} value:{}",path , zkClient.readData(path));
            zkClient.writeData(path,"456");
            Thread.sleep(1000);
            zkClient.delete(path);
            Thread.sleep( Integer.MAX_VALUE );
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
