package com.leigq.www.jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.leigq.www.jwt.annotation.PassToken;
import com.leigq.www.jwt.entity.User;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 14:39 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 登录方法 @PassToken 注解，不会被拦截器拦截
     * <br>创建人： leiGQ
     * <br>创建时间： 2019-05-11 14:44
     * <p>
     * 修改人： <br>
     * 修改时间： <br>
     * 修改备注： <br>
     * </p>
     * <br>
     */
    @PostMapping("/login")
    @PassToken
    public User login(String userName, String passWord) {
        // 根据 userName 去数据库查询用户，这里我省略就不去查询数据库了，使用模拟数据
        User user = User.builder().id(1L).userName("admin").passWord("123456").build();

        if (!user.checkUserName(userName)) {
            throw new RuntimeException("用户名不存在！");
        }

        if (!user.checkPwd(passWord)) {
            throw new RuntimeException("密码错误！");
        }

        // 生成 token
        final String token = JWT.create()
                // 签名的观众 也可以理解谁接受签名的
                .withAudience(user.getId() + "")
                // 这里是以用户密码作为密钥
                .sign(Algorithm.HMAC256(user.getPassWord()));

        // 将 token 放入 user 中，返回给前端
        user.setToken(token);
        return user;
    }

    /**
     * 验证方法，没有 @PassToken 注解，会被拦截器拦截
     * <br>创建人： leiGQ
     * <br>创建时间： 2019-05-11 14:44
     * <p>
     * 修改人： <br>
     * 修改时间： <br>
     * 修改备注： <br>
     * </p>
     * <br>
     */
    @GetMapping("/getMessage")
    public String getMessage() {
        return "你已通过验证";
    }

}
