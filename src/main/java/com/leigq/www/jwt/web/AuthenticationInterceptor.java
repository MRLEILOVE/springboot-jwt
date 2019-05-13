package com.leigq.www.jwt.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.annotation.PassToken;
import com.leigq.www.jwt.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 认证拦截器
 * <p>
 * 创建人：LeiGQ <br>
 * 创建时间：2019-05-11 14:17 <br>
 * <p>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注： <br>
 * </p>
 */
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.warn("JWT 认证拦截器-preHandle...");

        // // 从 http 请求头中取出 token
        String token = request.getHeader("token");

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取到方法
        Method method = ((HandlerMethod) handler).getMethod();

        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            return true;
        }

        // 剩余请求都需要登录
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("无效token，请重新登录!");
        }

        // 获取 token 中的 user id
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            // 根据 userId 去数据库查询用户，这里我省略就不去查询数据库了，使用模拟数据
            User user = User.builder().id(Long.valueOf(userId)).userName("admin").passWord("123456").build();
            if (Objects.isNull(user)) {
                throw new RuntimeException("用户不存在，请重新登录");
            }
            // 验证 token, 返回解码之后的 jwt
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(user.getPassWord())).build().verify(token);

            // 主题
            String subject = jwt.getSubject();
            // Claim中存放的内容是JWT自身的标准属性
            Map<String, Claim> claims = jwt.getClaims();
            // 自定义属性 loginName
            Claim claim = claims.get("loginName");
            System.out.println(claim.asString());
            // 用于说明该JWT发送给的用户"
            List<String> audience = jwt.getAudience();
            System.out.println(subject);
            System.out.println(audience.get(0));

        } catch (JWTDecodeException j) {
            throw new RuntimeException("401");
        }

        // 进行逻辑判断，如果ok就返回true，不行就返回false，返回false就不会处理改请求
        return true;
    }
}
