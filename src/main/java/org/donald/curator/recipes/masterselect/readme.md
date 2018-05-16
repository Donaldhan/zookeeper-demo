## 分布式master选举实现模拟
同时启动MasterSelectDemo，MasterSelectDemo1，MasterSelectDemo2，在命令行监听选举节点curator_recipes_master_path，curator_recipes_master_path的子节点
在不断的变化，创建节点成功的成为master，我们可以观察到master路径的几点编号在不断的增长。
```
[zk: localhost:2181(CONNECTED) 46] ls /curator_recipes_master_path watch
[_c_ec6984d1-cebc-4791-8685-5f3289cf1b15-lock-0000000005, _c_a2378b2a-a9a7-4c60-ab7d-a78db61c8045-lock-0000000006, _c_f1ca79dd-c5f4-445d-8560-f4d59fbf4f74-lock-0000000007]
[zk: localhost:2181(CONNECTED) 47]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/curator_recipes_master_path
ls /curator_recipes_master_path watch
[_c_8436cd34-ebce-4ea7-90e1-2fcb7384bcb0-lock-0000000011, _c_d3cf94c7-b57a-4e9d-95eb-15fefa5e48f3-lock-0000000010, _c_f4296747-d993-4615-b400-42de4b6db9ef-lock-0000000012]
[zk: localhost:2181(CONNECTED) 48]

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/curator_recipes_master_path

[zk: localhost:2181(CONNECTED) 49] ls /curator_recipes_master_path watch
[_c_f828fd1f-ecf9-4ad1-a2a5-f829ece96f08-lock-0000000014, _c_f4296747-d993-4615-b400-42de4b6db9ef-lock-0000000012, _c_72692328-b16e-4486-99c7-847beddcb95e-lock-0000000013]
[zk: localhost:2181(CONNECTED) 50]
```