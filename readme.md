
## 基础概念

- [JSON Web Token 入门教程 - 阮一峰](http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html)

- [JSON Web Token的使用](https://www.cnblogs.com/dinglinyong/p/6611151.html)


![img](https://img-blog.csdn.net/20170729233023711?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhvdWt1bjEwMDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


jwt验证流程如下：

1. 用户使用用户名密码来请求服务器
2. 服务器进行验证用户的信息
3. 服务器通过验证发送给用户一个token
4. 客户端存储token，并在每次请求时附送上这个token值
5. 服务端验证token值，并返回数据


## 适用场景

1. 用于向Web应用传递一些非敏感信息。例如完成加好友、下订单的操作等等。
2. 用于设计用户认证和授权系统。
3. 实现Web应用的单点登录。



## SpringBoot + JWT 实战


我参考这篇文章[SpringBoot集成JWT实现token验证](https://www.jianshu.com/p/e88d3f8151db)实现的

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

主要实现了以下功能：

1. 用户登录成功后生成 token、refreshToken，保存至浏览器 Cookie 中，且 Cookie 为 HttpOnly、Secure的，将基础用户信息和 token 保存至 Redis 缓存中，最后返回用户基础信息和 token 过期时间给前端。
2. 后端使用拦截器拦截需要登录访问的接口，不需要前端手动传 token，后端自己从 Cookie 中取，具体流程如下：

    1. 自定义了一个 @PassToken 注解，除了 @PassToken 注解的接口全部会进行拦截
    2. 从 Cookie 中获取 token，如果 token 为空，则返回 “登录失效”
    3. 解析 token，如果解析异常，则 “登录失效”
    4. 将 Cookie 中的 token 和 Redis 缓存中的 token 进行对比，如果不想等则 “登录失效”


3. 同种设备只能同时登录一个账号，如：俩个Android手机登录一个账号的话，只能有一个在线，但是允许Android、IOS同时在线。
4. 提供刷新 token 的接口给前端调用，前端每次请求之前都需要根据 token 的过期时间去判断 token 是否过期，如果已经过期，则请求刷新 token 的接口，该接口不需要提供参数，后端自己从 Cookie 中拿 refreshToken，具体流程如下：

    1. 后端从 Cookie 中获取 token，看是不是真的过期了
    2. 后端从 Cookie 中获取 refreshToken，如果 refreshToken 为空，则跳转登录，让用户重新登录
    3. 如果 refreshToken 不为空，就开始解析 refreshToken，从 refreshToken 中可以解析出 userId，userName 属性。
    4. 重新生成新的token、refreshToken，之后就和登录成功逻辑一样了

源码：https://github.com/MRLEILOVE/springboot-jwt