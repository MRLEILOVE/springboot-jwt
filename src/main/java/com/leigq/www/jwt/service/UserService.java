package com.leigq.www.jwt.service;

import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.JwtUtils;
import com.leigq.www.jwt.vo.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
	public LoginUser buildLoginUser(Long userId, String userName, HttpServletRequest request) {
		// 有效时间
		final long expiresIn = jwtProperties.getExpiresIn().getSeconds();

		// 过期时间
		Date expiresAt = jwtUtils.calculationExpiresAt(expiresIn);

		// 生成 token
		Map<String, String> customClaim = new HashMap<>(1);
		customClaim.put("ip", IpUtils.realIp(request));
		final String accessToken = jwtUtils.generate(customClaim, userId + "", expiresAt, userName);

		// 生成 refreshToken，用于当 token 过期时刷新 token
		final String refreshToken = jwtUtils.generate(customClaim, userId + "", jwtUtils.calculationExpiresAt(expiresIn * 2), userName);

		// 构建登录用户
		return LoginUser.builder()
				// 将 token Base64 编码
				.token(Base64.getEncoder().encodeToString(accessToken.getBytes(StandardCharsets.UTF_8)))
				.refreshToken(Base64.getEncoder().encodeToString(refreshToken.getBytes(StandardCharsets.UTF_8)))
				.expiresAt(expiresAt)
				.build();
	}
}
