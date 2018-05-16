/**
 * 此包是curator的提供经典使用案例类都在curator-recipes依赖中
 * <dependency>
 * 			<groupId>org.apache.curator</groupId>
 * 			<artifactId>curator-recipes</artifactId>
 * </dependency>
 *  NodeCache可以监听路径的创建，更新，删除事件操作。
 * @see org.apache.curator.framework.recipes.cache.NodeCache
 * @see org.apache.curator.framework.recipes.cache.NodeCacheListener
 *
 *  监听路径子节点的变化，具体相关事件见 PathChildrenCacheEvent，可以监控子节点的路径的创建，更新，删除事件，
 *  但与原生API一样，不能监控二级子节点的变化状态
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCache
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent
 *
 *
 */
package org.donald.curator.recipes.cache;