/**
 *ZkClient是由Datameer的工程师开发的开源客户端，对Zookeeper的原生API进行了包装。
 * 相对于原生api优势：
 * 1. 实现了超时重连、Watcher反复注册等功能。
 * 2. 添加序列化支持。
 * 3. 同时可以递归创建和删除路径。
 * 主要操作委托给一下几个类：
 * @see org.I0Itec.zkclient.ZkClient
 * {@link org.I0Itec.zkclient.IZkConnection}
 * 类似原生API的
 * @see org.apache.zookeeper.ZooKeeper
 *
 * @see org.I0Itec.zkclient.IZkDataListener
 * 类似原生API的
 * @see org.apache.zookeeper.Watcher
 *
 * @see org.I0Itec.zkclient.serializ.ZkSerializer 序列化
 * {@link org.I0Itec.zkclient.serializ.ZkSerializer}
 */
package org.donald.zkclient;