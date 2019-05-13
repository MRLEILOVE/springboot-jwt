package com.leigq.www.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.entity.User;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWTUtils
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 17:07 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Component
public class JWTUtils {

    /**
     * 密钥
     */
    private static final String secret = "secret";

    /**
     * 发行者
     */
    private static final String Issuer = "leigq";

    /**
     * 签名的主题
     */
    private static final String Subject = "Subject";

    public String create(User user, Integer expireSeconds) {
        // TODO 改成使用数据库测试数据，结合Redis缓存用户信息？？？？
        return JWT.create()
                /*设置头部信息 Header，可以不设置，使用默认值*/
//                .withHeader()
                /*设置 载荷 Payload*/
                .withClaim("loginName", "lijunkui")
                .withIssuer(Issuer)//签名是有谁生成 例如 服务器
                .withSubject(Subject)//签名的主题
                //.withNotBefore(new Date())//定义在什么时间之前，该jwt都是不可用的.
                .withAudience(user.getId() + "")//签名的观众 也可以理解谁接受签名的
                .withIssuedAt(new Date()) //生成签名的时间
                .withExpiresAt(new Date(System.currentTimeMillis() + expireSeconds * 1000))//签名过期的时间
                /*签名 Signature */
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(Issuer)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
