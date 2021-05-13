package com.leigq.www.jwt.web;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.annotation.PassToken;
import com.leigq.www.jwt.bean.CacheLoginUser;
import com.leigq.www.jwt.bean.UserContext;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
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
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

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

		// 获取用户 token
        final String token = UserContext.getToken();

		// 剩余请求都需要登录
		if (StringUtils.isBlank(token)) {
			throw new ServiceException("登录失效，请重新登录!");
		}

		try {
			// 获取缓存用户，在这里可以增加自己项目的业务，比如：判断用户是否被禁用
			final CacheLoginUser cacheUser = UserContext.getCacheUser();
			if (Objects.isNull(cacheUser)) {
				throw new ServiceException("登录失效，请重新登录!");
			}

			// Cookie 中的 token 和缓存中的 token 比较
			if (!cacheUser.getToken().equals(token)) {
				throw new ServiceException("登录失效，请重新登录!");
			}

			// 解码JWT
            final DecodedJWT decodedJwt = JwtUtils.parse(token);

            // 获取 token 中的 audience (用户名)
            log.info("userName = {}", decodedJwt.getAudience().get(0));

			// Claim中存放的内容是JWT自身的标准属性
			Map<String, Claim> claims = decodedJwt.getClaims();

			// 自定义属性
			String ip = claims.get("ip").asString();
			log.info("ip = {}", ip);

			// 当前请求用户的ip和 jwt中的是否一致
			if (!IpUtils.realIp(request).equals(ip)) {
				throw new ServiceException("登录失效，请重新登录!");
			}
		} catch (JWTVerificationException ex) {
			log.error("jwt 解析异常：", ex);
			// 全局异常捕获
			throw new ServiceException("登录失效，请重新登录");
		}

		// 进行逻辑判断，如果ok就返回true，不行就返回false，返回false就不会处理改请求
		return true;
	}
}
