ZKClient使用注意事项：
## 创建持久化节点（递归）
```java
    /**
     * Create a persistent node.
     * 
     * @param path
     * @param createParents
     *            if true all parent dirs are created as well and no {@link ZkNodeExistsException} is thrown in case the
     *            path already exists
     * @throws ZkInterruptedException
     *             if operation was interrupted, or a required reconnection got interrupted
     * @throws IllegalArgumentException
     *             if called from anything except the ZooKeeper event thread
     * @throws ZkException
     *             if any ZooKeeper exception occurred
     * @throws RuntimeException
     *             if any other exception occurs
     */
    public void createPersistent(String path, boolean createParents) throws ZkInterruptedException, IllegalArgumentException, ZkException, RuntimeException {
        try {
            create(path, null, CreateMode.PERSISTENT);
        } catch (ZkNodeExistsException e) {
            if (!createParents) {
                throw e;
            }
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir, createParents);
            createPersistent(path, createParents);
        }
    }
```
从方法可以看出，当递归创建持久化路径的方法传入的createParents为true时，节点已经存在，不会抛出异常。

## 递归删除路径
```java
 public boolean deleteRecursive(String path) {
        List<String> children;
        try {
            children = getChildren(path, false);
        } catch (ZkNoNodeException e) {
            return true;
        }

        for (String subPath : children) {
            if (!deleteRecursive(path + "/" + subPath)) {
                return false;
            }
        }

        return delete(path);
    }
```

##  创建节点，并赋值
```java
//此方法，路径存在是，将会抛出org.I0Itec.zkclient.exception.ZkNodeExistsException: org.apache.zookeeper.KeeperException$NodeExistsException:
// KeeperErrorCode = NodeExists for /zk-book
//zkClient.createPersistent(path, "123");
  /**
     * Create a persistent node.
     * 
     * @param path
     * @param data
     * @throws ZkInterruptedException
     *             if operation was interrupted, or a required reconnection got interrupted
     * @throws IllegalArgumentException
     *             if called from anything except the ZooKeeper event thread
     * @throws ZkException
     *             if any ZooKeeper exception occurred
     * @throws RuntimeException
     *             if any other exception occurs
     */
    public void createPersistent(String path, Object data) throws ZkInterruptedException, IllegalArgumentException, ZkException, RuntimeException {
        create(path, data, CreateMode.PERSISTENT);
    }
```

## 读取节点数据
```java
//returnNullIfPathNotExists，控制zk异常时是否返回为null，但不抛出异常
 public <T extends Object> T readData(String path, boolean returnNullIfPathNotExists) {
        T data = null;
        try {
            data = (T) readData(path, null);
        } catch (ZkNoNodeException e) {
            if (!returnNullIfPathNotExists) {
                throw e;
            }
        }
        return data;
    }
```


