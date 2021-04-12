package com.leigq.www.jwt.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.annotation.PassToken;
import com.leigq.www.jwt.bean.Response;
import com.leigq.www.jwt.config.JwtProperties;
import com.leigq.www.jwt.entity.User;
import com.leigq.www.jwt.service.UserService;
import com.leigq.www.jwt.util.CookieUtils;
import com.leigq.www.jwt.util.IpUtils;
import com.leigq.www.jwt.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户 Controller
 * <br/>
 * jwt验证流程如下：
 * <P>1. 首先，前端通过Web表单将自己的用户名和密码发送到后端的接口。这一过程一般是一个HTTP POST请求。建议的方式是通过SSL加密的传输(https协议) ，从而避免敏感信息被嗅探。</P>
 * <p>2. 后端核对用户名和密码成功后，将用户的id等其他信息作为JWT Payload (负载) ，将其与头部分别进行Base64编码拼接后签名。形成的JWT就是一个形同111. zzz . xxx的字符串。</p>
 * <p>3. 后端将JWT字符串作为登录成功的返回结果返回给前端。前端可以将返回的结果保存在localstorage或sessionStorage上，退出登录时前端删除保存的JWT即可。</p>
 * <p>4. 前端在每次请求时将JWT放入HTTP Header中的Authorization位。 (解决XSS和XSRF问题)</p>
 * <p>5. 后端检查是否存在，如存在验证JWT的有效性。例如，检查签名是否正确;检查Token是否过期。(拦截器可实现)</p>
 * <p>6. 验证通过后后端使用JWT中包含的用户信息进行其他逻辑操作，返回相应结果。</p>
 * <p>
 *
 * @author leigq
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final JwtUtils jwtUtils;
	private final JwtProperties jwtProperties;

	public UserController(UserService userService, JwtUtils jwtUtils, JwtProperties jwtProperties) {
		this.userService = userService;
		this.jwtUtils = jwtUtils;
		this.jwtProperties = jwtProperties;
	}

	/**
	 * 登录方法 @PassToken 注解，不会被拦截器拦截
	 *
	 * @param userName the user name
	 * @param passWord the pass word
	 * @return the user
	 */
	@PassToken
	@RequestMapping("/login")
	public Response login(String userName, String passWord, HttpServletRequest request, HttpServletResponse response) {
		// 根据 userName 去数据库查询用户，这里我省略就不去查询数据库了，使用模拟数据
		User user = User.builder().id(10010L).userName("admin").passWord("123456").mobile("11111111111").build();

		if (!user.checkUserName(userName) || !user.checkPwd(passWord)) {
			return Response.fail("用户名或密码错误！");
		}

		return Response.success(userService.buildLoginUser(user, request, response));
	}


	/**
	 * 刷新 token @PassToken 注解，不会被拦截器拦截
	 *
	 * @return the user
	 */
	@PassToken
	@RequestMapping("/token/refresh")
	public Response refreshToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 获取 token
			final String token = CookieUtils.getCookieValue(request, jwtProperties.getTokenCookieName());
			if (StringUtils.isNotBlank(token)) {
				return Response.fail("token还未失效，无需刷新");
			}

			// 获取 refreshToken
			final String refreshToken = CookieUtils.getCookieValue(request, jwtProperties.getRefreshTokenCookieName());

			if (StringUtils.isBlank(refreshToken)) {
				return Response.fail("登录失效，请重新登录");
			}

			// 解析 refreshToken
			final DecodedJWT decodedJwt = jwtUtils.parse(new String(Base64Utils.decodeFromString(refreshToken)));

			// 获取用户id
			final String userId = decodedJwt.getSubject();

			// 获取 userName
			final String userName = decodedJwt.getAudience().get(0);

			// 获取ip
			final String ip = decodedJwt.getClaims( ).get("ip").asString();

			if (!IpUtils.realIp(request).equals(ip)) {
				return Response.fail("refreshToken无效，请重新登录");
			}
			return Response.success(userService.buildLoginUser(new User(Long.parseLong(userId), userName, "11111111111", ""), request, response));
		} catch (JWTVerificationException e) {
			log.error("refreshToken解析失败:", e);
			return Response.fail("refreshToken解析失败");
		}
	}

	/**
	 * 验证方法，没有 @PassToken 注解，会被拦截器拦截
	 *
	 * @return the message
	 */
	@GetMapping("/getMessage")
	public Response getMessage() {
		return Response.success();
	}

	/**
	 * Gets ip.
	 *
	 * @param request the request
	 * @return the ip
	 */
	@PassToken
	@GetMapping("/ip")
	public String getIp(HttpServletRequest request) {
		final String realIp = IpUtils.realIp(request);
		log.info("realIp = {}", realIp);
		return realIp;
	}

}
