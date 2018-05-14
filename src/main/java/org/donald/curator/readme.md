## 创建路径递归
```java
/**
     * Causes any parent nodes to get created if they haven't already been
     *
     * @return this
     */
    public ProtectACLCreateModePathAndBytesable<String> creatingParentsIfNeeded();
```
如果需要创建父节点，需要注意一个问题，创建的父节点是持久化的.

未关闭会话
```
[zk: localhost:2181(CONNECTED) 1] ls /
[zk-book, zookeeper]
[zk: localhost:2181(CONNECTED) 2] ls /zk-book
[c1]
[zk: localhost:2181(CONNECTED) 3] rmr /zk-book
```

关闭会话
```
[zk: localhost:2181(CONNECTED) 4] ls /
[zk-book, zookeeper]
[zk: localhost:2181(CONNECTED) 5] ls /zk-book
[]
[zk: localhost:2181(CONNECTED) 6]
```

验证了递归创建的父节点是持久化的.无论创建的路径是什么类型。


