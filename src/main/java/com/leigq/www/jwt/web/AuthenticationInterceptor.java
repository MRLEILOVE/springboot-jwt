package com.leigq.www.jwt.web;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.annotation.PassToken;
import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.entity.User;
import com.leigq.www.jwt.util.CookieUtils;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 认证拦截器
 *
 * @author leigq
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	private final JwtProperties jwtProperties;
	private final JwtUtils jwtUtils;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// 如果不是映射到方法直接通过
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		// 获取到方法
		Method method = ((HandlerMethod) handler).getMethod();

		// 检查是否有 PassToken 注解，有则跳过认证
		if (method.isAnnotationPresent(PassToken.class)) {
			return true;
		}

		// 从 Cookie 中取出 accessToken
		final String accessToken = CookieUtils.getCookieValue(request, jwtProperties.getTokenCookieName());

		// 剩余请求都需要登录
		if (StringUtils.isBlank(accessToken)) {
			throw new ServiceException("无效token，请重新登录!");
		}

		try {
			// 解析 jwt
			final DecodedJWT decodedJwt = jwtUtils.parse(new String(Base64Utils.decodeFromString(accessToken)));

			// 获取 accessToken 中的 userId
			String userId = decodedJwt.getSubject();

			// 获取 accessToken 中的 audience (用户名)
			log.info("userName = {}", decodedJwt.getAudience().get(0));

			// 根据 userId 去数据库查询用户，这里我省略就不去查询数据库了，使用模拟数据，在这里可以增加自己项目的业务，比如：判断用户是否被禁用
			User user = User.builder().id(Long.valueOf(userId)).userName("admin").passWord("123456").build();
			if (Objects.isNull(user)) {
				throw new ServiceException("用户不存在，请重新登录");
			}

			// Claim中存放的内容是JWT自身的标准属性
			Map<String, Claim> claims = decodedJwt.getClaims();

			// 自定义属性
			String ip = claims.get("ip").asString();
			log.info("ip = {}", ip);

			// 当前请求用户的ip和 jwt中的是否一致
			if (!IpUtils.realIp(request).equals(ip)) {
				throw new ServiceException("无效token，请重新登录!");
			}
		} catch (JWTVerificationException ex) {
			log.error("jwt 解析异常：", ex);
			// 全局异常捕获
			throw new ServiceException("用户登录失效，请重新登录");
		}

		// 进行逻辑判断，如果ok就返回true，不行就返回false，返回false就不会处理改请求
		return true;
	}
}
