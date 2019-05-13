@[TOC]
## 入门教程

#### JSON Web Token 入门教程 - 阮一峰
[JSON Web Token 入门教程 - 阮一峰](http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html)

#### 补充
- ##### JWT认证流程：

![img](https://img-blog.csdn.net/20170729233023711?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhvdWt1bjEwMDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
	1、用户使用用户名密码来请求服务器 
	2、服务器进行验证用户的信息 
	3、服务器通过验证发送给用户一个token 
	4、客户端存储token，并在每次请求时附送上这个token值 
	5、服务端验证token值，并返回数据

- ##### JWT实现认证的原理

	服务器在生成一个JWT之后会将这个JWT会以`Authorization : Bearer JWT` 键值对的形式存放在cookies里面发送到客户端机器，在客户端再次访问收到JWT保护的资源URL链接的时候，服务器会获取到cookies中存放的JWT信息，首先将Header进行反编码获取到加密的算法，在通过存放在服务器上的密匙对`Header.Payload` 这个字符串进行加密，比对JWT中的Signature和实际加密出来的结果是否一致，如果一致那么说明该JWT是合法有效的，认证成功，否则认证失败。

- ##### 适用场景

	1、用于向Web应用传递一些非敏感信息。例如完成加好友、下订单的操作等等。
	2、用于设计用户认证和授权系统。
	3、实现Web应用的单点登录。

- ##### JWT长什么样
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511170109596.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511170120638.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)


- ##### 八幅漫画理解使用JSON Web Token设计单点登录系统
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511133619454.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511133637715.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511133649193.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511133704754.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)
- ##### 和Session方式存储id的差异
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190511133836435.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODQ1Mzk0,size_16,color_FFFFFF,t_70)

## SpringBoot + JWT
我参考下面的文章实现了

- [SpringBoot集成JWT实现token验证](https://www.jianshu.com/p/e88d3f8151db)
- [Spring Boot整合JWT实现用户认证](https://blog.csdn.net/ltl112358/article/details/79507148)

我的环境：

`SpringBoot 2.0.4.RELEASE`
`java-jwt 3.4.1`

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.4.1</version>
</dependency>
```

源码：

> ## 参考
> https://blog.csdn.net/qiuyinthree/article/details/80811937
> https://www.cnblogs.com/dinglinyong/p/6611151.html