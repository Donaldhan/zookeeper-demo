package book.chapter05.$5_4_1;
import org.I0Itec.zkclient.ZkClient;

// 使用ZkClient创建节点
public class Create_Node_Sample {

    public static void main(String[] args) throws Exception {
    	ZkClient zkClient = new ZkClient("192.168.126.134:2181", 50000);
        String path = "/zk-book/c1";
        zkClient.createPersistent(path, true);
    }
}