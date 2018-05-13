/**
 * Apache 原生API,的缺点：
 *
 * 1.由于设置和返回的都是字节序列，所以自己去处理序列化。
 * 2,。同时事件注册是一次性的，如果需要持续监听一个节点，必须在监听器捕捉一个事件后，重新注册。
 * 3.无法创建父路径不存在的路径。
 * 4.删除操作，只能删除节点下没有子节点的路径。
 * 所有客户端的操作通过
 *
 * @see org.apache.zookeeper.ZooKeeper
 * @see org.apache.zookeeper.Watcher
 *
 *
 * 项目示例中用到很多的Thread.sleep(3000),主要的目的是，等待上一个操作执行完毕，
 * 如果不这样做，很可能，到的结果为空，比如我们创建一个节点，接着一个获取节点值的操作，
 * 很有可能，在我们获取节点值时，节点的值还没有赋值。
 * ZK的节点有5种操作权限：
 * CREATE、READ、WRITE、DELETE、ADMIN 也就是 增、删、改、查、管理权限，这5种权限简写为crwda(即：每个单词的首字符缩写)
 * 注：这5种权限中，delete是指对子节点的删除权限，其它4种权限指对自身节点的操作权限
 *
 * 身份的认证有4种方式：
 * world：默认方式，相当于全世界都能访问
 * auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)
 * digest：即用户名:密码这种方式认证，这也是业务系统中最常用的
 * ip：使用Ip地址认证
 *
 * 设置访问控制：
 * 方式一：（推荐）
 * 1）增加一个认证用户
 * addauth digest 用户名:密码明文
 * eg. addauth digest user1:password1
 * 2）设置权限
 * setAcl /path auth:用户名:密码明文:权限
 * eg. setAcl /test auth:user1:password1:cdrwa
 * 3）查看Acl设置
 * getAcl /path
 *
 * 方式二：
 * setAcl /path digest:用户名:密码密文:权限
 * 注：这里的加密规则是SHA1加密，然后base64编码。
 *
 */
package org.donald.zookeeper;