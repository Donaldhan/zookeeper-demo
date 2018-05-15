/**
 *Curator是Netflix公司开源的一个Zookeeper客户端，与Zookeeper提供的原生客户端相比，Curator的抽象层次更高，简化了Zookeeper客户端编程。
 *Curator是对ZK的高阶封装. 与操作原生的Zookeeper相比, 它提供了对ZK的完美封装, 简化了对集群的连接, 错误的处理; 实现了一系列经典"模式", 比如分布式锁, Leader选举等.
 *
 * @see org.apache.curator.framework.CuratorFrameworkFactory
 * @see  org.apache.curator.RetryPolicy
 * @see org.apache.curator.framework.api.Backgroundable
 * @see org.apache.curator.framework.api.BackgroundCallback
 * @see org.apache.curator.framework.api.CuratorEvent
 * @see org.apache.zookeeper.ClientCnxn.EventThread
 */
package org.donald.curator;