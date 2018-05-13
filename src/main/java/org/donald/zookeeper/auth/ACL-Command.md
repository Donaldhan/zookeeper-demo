
# ACL命令的使用

## 测试明文加密

```
[zk: localhost:2181(CONNECTED) 17] create /user donald   
Created /user  
[zk: localhost:2181(CONNECTED) 18] ls /user   
[]  
[zk: localhost:2181(CONNECTED) 19] get /user  
donald
cZxid = 0x21f8
ctime = Sun May 13 12:31:05 CST 2018
mZxid = 0x21f8
mtime = Sun May 13 12:31:05 CST 2018
pZxid = 0x21f8
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0  
```
***
## 默认访问控制权限为world方式，权限cdrwa
```
[zk: localhost:2181(CONNECTED) 20] getAcl /user   
'world,'anyone
: cdrwa
## 设置路径访问控制权限
[zk: localhost:2181(CONNECTED) 23] setAcl /user digest:jack:123456:cdrwa   
cZxid = 0x21f8
ctime = Sun May 13 12:31:05 CST 2018
mZxid = 0x21f8
mtime = Sun May 13 12:31:05 CST 2018
pZxid = 0x21f8
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
```
当前访问控制权限为digest方式，权限为cdrwa   
```
[zk: localhost:2181(CONNECTED) 24] getAcl /user   
'digest,'jack:123456
: cdrwa   
```
使用无ACL验证，直接访问将抛出验证失败
```
[zk: localhost:2181(CONNECTED) 25] get /user  
Authentication is not valid : /user 
``` 
 给"上下文"增加了一个认证用户，即对应刚才setAcl的设置
 ```
[zk: localhost:2181(CONNECTED) 26] addauth digest jack:123456  
[zk: localhost:2181(CONNECTED) 27] get /user
Authentication is not valid : /user
```
仍然验证失败，这是由于我们是使用明文加密的,在输入加密后，Zookeeper会对我们的密码进行加密，具体原因见：
揭开加密规则：
```java
class DigestAuthenticationProvider{
    //...
    public static  String generateDigest(String idPassword)
            throws NoSuchAlgorithmException {
        String parts[] = idPassword.split(":", 2);
        byte digest[] = MessageDigest.getInstance("SHA1").digest(
                idPassword.getBytes());
        return parts[0] + ":" + base64Encode(digest);
    }
    //...
}
```
删除/user
```
[zk: localhost:2181(CONNECTED) 1] delete /user
[zk: localhost:2181(CONNECTED) 2] ls /
[zookeeper]
```
最后 delete /test 成功了！原因是：根节点/默认是world:anyone:crdwa(即：全世界都能随便折腾)，
所以也就是说任何人，都能对根节点/进行读、写、创建子节点、管理acl、以及删除子节点（再次映证了ACL中的delete权限应该理解为对子节点的delete权限）

从上面可以看出使用setAcl，直接明文设置访问控制权限，使用明文获取访问权限会别拒绝

## 密文设置访问控制权限
首先获取密码的密文：123456的密码为fEqNCco3Yq9h5ZUglD3CZJT4lBs=

创建节点，并设置访问控制权限
```
[zk: localhost:2181(CONNECTED) 3]
[zk: localhost:2181(CONNECTED) 3] create /user donald
Created /user
[zk: localhost:2181(CONNECTED) 4] setAcl /user digest:jack:fEqNCco3Yq9h5ZUglD3CZJT4lBs=:cdrwa
cZxid = 0x21ff
ctime = Sun May 13 13:20:20 CST 2018
mZxid = 0x21ff
mtime = Sun May 13 13:20:20 CST 2018
pZxid = 0x21ff
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
[zk: localhost:2181(CONNECTED) 5] getAcl /user
'digest,'jack:fEqNCco3Yq9h5ZUglD3CZJT4lBs=
: cdrwa

```
添加会话访问权限

```
[zk: localhost:2181(CONNECTED) 6] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 8] addauth digest jack:fEqNCco3Yq9h5ZUglD3CZJT4lBs=
[zk: localhost:2181(CONNECTED) 9] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 10] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 11] get /user
Authentication is not valid : /user
```
仍然不能访问。

## 先声明认证用户，在设置访问权限
```
[zk: localhost:2181(CONNECTED) 14] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 16] create /user donald /
/ does not have the form scheme:id:perm
Acl is not valid : null
[zk: localhost:2181(CONNECTED) 17] create /user donald
Created /user
[zk: localhost:2181(CONNECTED) 18] setAcl /user digest:jack:123456:cdrwa
cZxid = 0x2202
ctime = Sun May 13 13:26:45 CST 2018
mZxid = 0x2202
mtime = Sun May 13 13:26:45 CST 2018
pZxid = 0x2202
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
[zk: localhost:2181(CONNECTED) 19] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 20] getAcl /user
'digest,'jack:123456
: cdrwa
[zk: localhost:2181(CONNECTED) 21] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 22] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 23]
```

仍然无法获取权限访问。

第二次尝试：
```
[zk: localhost:2181(CONNECTED) 28] create /user donald
Created /user
[zk: localhost:2181(CONNECTED) 29] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 30] setAcl /user digest:jack:123456:cdrwa
cZxid = 0x2205
ctime = Sun May 13 13:31:10 CST 2018
mZxid = 0x2205
mtime = Sun May 13 13:31:10 CST 2018
pZxid = 0x2205
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
[zk: localhost:2181(CONNECTED) 31] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 32] get

get      getAcl
[zk: localhost:2181(CONNECTED) 32] getAcl /user
'digest,'jack:123456
: cdrwa
[zk: localhost:2181(CONNECTED) 33] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 34] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 35]
```
仍然无法访问！！！
遗留问题。

再次测试：
```
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, user]
[zk: localhost:2181(CONNECTED) 1] addauth digest jack:123456
[zk: localhost:2181(CONNECTED) 2] get /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 3] getAcl /user
'digest,'jack:123456
: cdrwa
[zk: localhost:2181(CONNECTED) 4] create /user/email
[zk: localhost:2181(CONNECTED) 5] ls /user
Authentication is not valid : /user
[zk: localhost:2181(CONNECTED) 6] get /user/email
Node does not exist: /user/email
[zk: localhost:2181(CONNECTED) 7] create /user/email donald@gmail.com
Authentication is not valid : /user/email
[zk: localhost:2181(CONNECTED) 8]
```
无创建节点权限。

