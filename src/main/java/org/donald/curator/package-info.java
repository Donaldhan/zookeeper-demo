/**
 *Curator是Netflix公司开源的一个Zookeeper客户端，与Zookeeper提供的原生客户端相比，Curator的抽象层次更高，简化了Zookeeper客户端编程。
 *Curator是对ZK的高阶封装. 与操作原生的Zookeeper相比, 它提供了对ZK的完美封装, 简化了对集群的连接, 错误的处理; 实现了一系列经典"模式", 比如分布式锁, Leader选举等.
 *
 * @see org.apache.curator.framework.CuratorFrameworkFactory
 * @see  org.apache.curator.RetryPolicy
 *
 * 异步接口
 * @see org.apache.curator.framework.api.Backgroundable
 * @see org.apache.curator.framework.api.BackgroundCallback
 * @see org.apache.curator.framework.api.CuratorEvent
 * @see org.apache.zookeeper.ClientCnxn.EventThread
 *
 * 监听器 ， NodeCache可以监听路径的创建，更新，删除事件操作。
 * @see org.apache.curator.framework.recipes.cache.NodeCache
 * @see org.apache.curator.framework.recipes.cache.NodeCacheListener
 *
 *  监听路径子节点的变化，具体相关事件见 PathChildrenCacheEvent，可以监控子节点的路径的创建，更新，删除事件，
 *  但与原生API一样，不能监控二级子节点的变化状态
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCache
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent
 *
 * 分布式master选举
 * @see org.apache.curator.framework.recipes.leader.LeaderSelector
 * @see org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter
 *
 */
package org.donald.curator;