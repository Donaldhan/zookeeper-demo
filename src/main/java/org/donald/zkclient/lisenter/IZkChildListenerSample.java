package org.donald.zkclient.lisenter;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.donald.constant.ConfigConstant;

import java.util.List;

/**
 * @ClassName: IZkChildListenerSample
 * @Description: ZkClient获取子节点列表。
 * @Author: Donaldhan
 * @Date: 2018-05-13 19:51
 */
@Slf4j
public class IZkChildListenerSample {
    private static ZkClient zkClient;
    public static void main(String[] args) {
        try {
            String path = "/zk-book";
            zkClient = new ZkClient(ConfigConstant.IP, ConfigConstant.SESSION_TIMEOUT);
            log.info("success connected ...");
            zkClient.subscribeChildChanges(path, new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) {
                    log.info( " {}'s child changed, currentChilds:{}", parentPath, currentChilds);
                }
            });
            zkClient.createPersistent(path);
            //等待创建成功
            Thread.sleep( 1000 );
           log.info("{} child:{}", path, zkClient.getChildren(path));
            Thread.sleep( 1000 );
            zkClient.createPersistent(path+"/c1");
            Thread.sleep( 1000 );
            zkClient.createPersistent(path+"/c2");
            Thread.sleep( 1000 );
            zkClient.delete(path+"/c1");
            Thread.sleep( 1000 );
            zkClient.delete(path+"/c2");
            Thread.sleep( 1000 );
            zkClient.delete(path);
            Thread.sleep( Integer.MAX_VALUE );
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
