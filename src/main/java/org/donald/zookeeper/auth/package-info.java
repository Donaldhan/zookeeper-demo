/**
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
 */
package org.donald.zookeeper.auth;