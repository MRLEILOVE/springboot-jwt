package com.leigq.www.jwt.service;

import com.leigq.www.jwt.bean.CacheLoginUser;
import com.leigq.www.jwt.bean.UserContext;
import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.entity.User;
import com.leigq.www.jwt.util.CookieUtils;
import com.leigq.www.jwt.util.DeviceUtils;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.WebUtils;
import com.leigq.www.jwt.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 *
 * @author leigq
 * @date 2021-04-10 16:01:54
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtProperties jwtProperties;
	private final RedisTokenStore redisTokenStore;


	/**
	 * Build login user login user.
	 *
	 * @param user     the user
	 * @return the login user
	 */
	public LoginUser buildLoginUser(User user) {
        final HttpServletRequest request = WebUtils.getRequest();
        final HttpServletResponse response = WebUtils.getResponse();

        // 有效时间
		final long expiresIn = jwtProperties.getExpiresIn().getSeconds();

		// 过期时间
		Date expiresAt = this.calculationExpiresAt(expiresIn);

		// 用户 ip
		Map<String, String> customClaim = new HashMap<>(1);
		customClaim.put("ip", IpUtils.realIp(request));

		// 生成 token
        final String accessToken = UserContext.genToken(customClaim, user.getId(), expiresAt, user.getUserName());

		// 生成 refreshToken，用于当 token 过期时刷新 token
		final String refreshToken = UserContext.genToken(customClaim, user.getId(), this.calculationExpiresAt(expiresIn * 2), user.getUserName());

		// 将 token 放入 cookie 中, 防止 XSS 攻击
		CookieUtils.setSecurityCookie(response, jwtProperties.getTokenCookieName(), accessToken, (int) expiresIn);

		// 将 refreshToken 放入 cookie 中, 防止 XSS 攻击
		CookieUtils.setSecurityCookie(response, jwtProperties.getRefreshTokenCookieName(), refreshToken, (int) expiresIn * 2);

		// 将 token 存入缓存
		CacheLoginUser cacheUser = CacheLoginUser.builder()
				.token(accessToken)
				.userId(user.getId())
				.mobile(user.getMobile())
				.userName(user.getUserName())
				.build();
		redisTokenStore.save(cacheUser, DeviceUtils.platform(request), (int) expiresIn);

		// 构建登录用户
		return LoginUser.builder()
				.userId(user.getId())
				.mobile(user.getMobile())
				.tokenExpiresAt(expiresAt)
				.build();
	}

    /**
     * 计算过期时间
     *
     * @param expiresIn 时间，单位：秒，过期时间 = 当前时间 + expiresIn
     * @return the date
     */
    private Date calculationExpiresAt(long expiresIn) {
        return new Date(System.currentTimeMillis() + expiresIn * 1000);
    }
}
