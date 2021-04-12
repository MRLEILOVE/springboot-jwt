package com.leigq.www.jwt.service;

import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.util.CookieUtils;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.JwtUtils;
import com.leigq.www.jwt.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
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

	private final JwtUtils jwtUtils;
	private final JwtProperties jwtProperties;

	/**
	 * Build login user login user.
	 *
	 * @param userId   the user id
	 * @param userName the user name
	 * @return the login user
	 */
	@SneakyThrows
	public LoginUser buildLoginUser(Long userId, String userName, HttpServletRequest request, HttpServletResponse response) {
		// 有效时间
		final long expiresIn = jwtProperties.getExpiresIn().getSeconds();

		// 过期时间
		Date expiresAt = jwtUtils.calculationExpiresAt(expiresIn);

		// 用户 ip
		Map<String, String> customClaim = new HashMap<>(1);
		customClaim.put("ip", IpUtils.realIp(request));

		// 生成 token
		final String accessToken = jwtUtils.generate(customClaim, userId + "", expiresAt, userName);

		// 生成 refreshToken，用于当 token 过期时刷新 token
		final String refreshToken = jwtUtils.generate(customClaim, userId + "", jwtUtils.calculationExpiresAt(expiresIn * 2), userName);

		final String tokenBase64 = Base64Utils.encodeToString(accessToken.getBytes(StandardCharsets.UTF_8));
		final String refreshTokenBase64 = Base64Utils.encodeToString(refreshToken.getBytes(StandardCharsets.UTF_8));

		// 构建登录用户
		final LoginUser loginUser = LoginUser.builder()
				.tokenExpiresAt(expiresAt)
				.build();

		// 将 token 放入 cookie 中, 防止 XSS 攻击
		CookieUtils.setSecurityCookie(response, jwtProperties.getTokenCookieName(), tokenBase64, (int) expiresIn);

		// 将 refreshToken 放入 cookie 中, 防止 XSS 攻击
		CookieUtils.setSecurityCookie(response, jwtProperties.getRefreshTokenCookieName(), refreshTokenBase64, (int) expiresIn * 2);

		return loginUser;
	}
}
