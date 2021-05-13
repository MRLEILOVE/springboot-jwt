package com.leigq.www.jwt.bean;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.enums.Platform;
import com.leigq.www.jwt.service.RedisTokenStore;
import com.leigq.www.jwt.util.*;

import java.util.Date;
import java.util.Map;

/**
 * 用户信息上下文
 *
 * @author leigq
 * @date 2021 -05-13 10:30:35
 */
public class UserContext {

    /**
     * The constant JWT_PROPERTIES.
     */
    private static final JwtProperties JWT_PROPERTIES = SpringContextHolder.getBean(JwtProperties.class);

    /**
     * The constant REDIS_TOKEN_STORE.
     */
    private static final RedisTokenStore REDIS_TOKEN_STORE = SpringContextHolder.getBean(RedisTokenStore.class);


    /**
     * Generate token string.
     *
     * @param customClaim 自定义负载
     * @param userId      用户id
     * @param expiresAt   过期时间
     * @param audience    接受 jwt 的一方，类似用户名
     * @return the string
     * @see JwtUtils#generate(Map, String, Date, String...) JwtUtils#generate(Map, String, Date, String...)
     */
    public static String genToken(Map<String, String> customClaim, Long userId, Date expiresAt, String... audience) {
        // 生成 jwt
        return JwtUtils.generate(customClaim, userId + "", expiresAt, audience);
    }


    /**
     * 获取用户 token 信息
     *
     * @return the user
     */
    public static String getToken() {
        // 从 Cookie 中取出 token
        return CookieUtils.getCookieValue(WebUtils.getRequest(), JWT_PROPERTIES.getTokenCookieName());
    }


    /**
     * 获取用户 refreshToken 信息
     *
     * @return the user
     */
    public static String getRefreshToken() {
        // 从 Cookie 中取出 refreshToken
        return CookieUtils.getCookieValue(WebUtils.getRequest(), JWT_PROPERTIES.getRefreshTokenCookieName());
    }

    /**
     * 获取 Redis 缓存中的用户id
     *
     * @return the user
     * @throws JWTVerificationException the jwt verification exception
     */
    public static Long getCacheUserId() throws JWTVerificationException {
        return getCacheUser().getUserId();
    }

    /**
     * 获取 Redis 缓存中的用户信息
     *
     * @return the user
     * @throws JWTVerificationException the jwt verification exception
     */
    public static CacheLoginUser getCacheUser() throws JWTVerificationException {
        // Subject 中存的 用户id
        final String userId = JwtUtils.subject(getToken());
        return REDIS_TOKEN_STORE.get(Long.parseLong(userId), getPlatform());
    }


    /**
     * 获取 Redis 缓存中的用户信息
     *
     * @return the user
     * @throws JWTVerificationException the jwt verification exception
     */
    public static Boolean delCacheUser() throws JWTVerificationException {
        // Subject 中存的 用户id
        final String userId = JwtUtils.subject(getToken());
        return delCacheUser(Long.parseLong(userId));
    }


    /**
     * 获取 Redis 缓存中的用户信息
     *
     * @param userId the user id
     * @return the user
     */
    public static Boolean delCacheUser(Long userId) {
        return REDIS_TOKEN_STORE.del(userId, getPlatform());
    }


    /**
     * Gets platform.
     *
     * @return the platform
     */
    private static Platform getPlatform() {
        return DeviceUtils.platform(WebUtils.getRequest());
    }
}
