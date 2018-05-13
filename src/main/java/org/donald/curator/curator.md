ZooKeeper本身自带一个Java客户端，但使用这个客户端繁琐而且容易出错。客户端的使用者需要做大量的手动维护性工作。比如：

连接问题

    初始化连接：ZooKeeper客户端与服务器进行握手，这需要花一些时间。如果握手未完成，任何要与服务器端同步执行的方法(如，create()，getData()等)都会抛出异常。
    Failover：如果ZooKeeper客户端与服务器连接断开，它会failover到集群中另外一台服务器。然后，这个过程会使客户端退回到”初始化连接”的模式。
    Session过期：有些边际情况可以导致ZooKeeper session过期。客户端需要监视这个状态，关闭并重建ZooKeeper客户端实例。

恢复问题

    当在Server创建顺序节点(sequential ZNode)时，有可能出现这种情况：节点成功创建了，但server在将节点名返回给客户端之前崩溃了。
    ZooKeeper客户端可能会抛出几个可恢复的异常，使用者需要捕捉这些异常并做重试操作。

Recipe方面

    标准的ZooKeeper recipe(如锁，选leader等)只是得到最低程序的描述，要正确地编写出来比较困难。
    有一些重要的边界情况在recipe描述里没有提到。例如，锁recipe的描述中，没有说到如何处理服务器成功创建了顺序(Sequential)/临时(Ephemeral)节点，但在向客户端返回结点名之前就崩溃的情况。如果没有得到正确处理，可能会导致死锁。
    某些使用场景下，必须要注意可能出现的连接问题。例如，选leader过程要监视连接的稳定性。如果连接到的服务器崩溃了，leader就不能假定自己继续为leader，除非已经成功failover到另外的服务器。

上述问题(和其它类似的问题)必须由每个ZooKeeper使用者来处理。问题解决方案既不容易编写，也不是显而易见的，需要消耗相当多的时间。而Curator处理了所有的问题。
Curator是什么

Curator n ˈkyoor͝ˌātər:，展品或者其它收藏品的看守者，管理员，ZooKeeper的Keeper。它由3个相关的项目组成：

    curator-client - ZooKeeper自带客户端的替代者，它负责处理低层次的维护工作，并提供某些有用的小功能
    curator-framework - Curator Framework大大地简化ZooKeeper使用的高层次API。它在ZooKeeper客户端之上添加了很多功能，并处理了与ZooKeeper集群连接管理和重试操作的复杂性。
    curator-recipes - ZooKeeper某些通用recipe的实现。它是基于Curator Framework之上实现的。

Curator专注于锁，选Leader等这些recipe。大部分对ZooKeeper感兴趣的人不需要关心连接管理等细节。他们想要的只是简单的使用这些recipe。Curator就是以此作为目标。

Curator通过以下方式处理了使用ZooKeeper的复杂度：

    重试机制：Curator支持可插拔式的(pluggable)重试机制。所有会产生可恢复异常的ZooKeeper操作都会在配置好的重试策略下得到重试。Curator自带了几个标准的重试策略(如二元指数后退策略)。
    连接状态监视：Curator不断监视ZooKeeper连接的状态，Curator用户可以监听连接状态变化并相应的作出回应。
    ZooKeeper客户端实例管理：Curator通过标准的ZooKeeper类实例来管理与ZooKeeper集群的实际连接。然而，这些实例是管理在内部(尽管你若需要也可以访问)，在需要的时候被重新创建。因此，Curator提供了对ZooKeeper集群的可靠处理(不像ZooKeeper自带的实现)。
    正确，可靠的recipe：Curator实现了大部分重要的ZooKeeper recipe(还有一些附加的recipe)。它们的实现使用了ZooKeeper的最佳实践，处理了所有已知的边界情况(像前面所说的)。
    Curator专注于那些让你的代码更强健，因为你完全专心于你感兴趣的ZooKeeper功能，而不用担心怎么正确完成那些的维护性工作。

ZooKeeper在Netflix

ZooKeeper/Curator在Netflix得到了广泛的使用。使用情景有：

    InterProcessMutex在各种顺序ID生成器中被用来保证值的唯一性
    Cassandra备份
    TrackID服务
    我们的Chukwa收集器使用LeaderSelector来做各种维护性的任务
    我们用了一些第三方的服务，但它们只允许有限数目的并发用户。InterProcessSemphore被用来处理这个。
    各种Cache
