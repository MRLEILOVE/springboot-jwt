package com.leigq.www.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leigq.www.jwt.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * Jwt utils.
 *
 * @author leigq
 */
@RequiredArgsConstructor
@Component
public final class JwtUtils {

	/**
	 * The Jwt properties.
	 */
	private final JwtProperties jwtProperties;


	/**
	 * 生成 jwt
	 *
	 * @param subject   主题
	 * @param expiresAt 过期时间
	 * @param audience  接受签名的受众
	 * @return the string
	 */
	public String generate(String subject, Date expiresAt, String... audience) {
		return generate(null, subject, expiresAt, audience);
	}


	/**
	 * 生成 jwt
	 *
	 * @param customClaim 自定义负载
	 * @param subject     签名的对象，jwt所面向的用户，类似用户id
	 * @param expiresAt   过期时间
	 * @param audience    接受 jwt 的一方，类似用户名
	 * @return the string
	 */
	public String generate(Map<String, String> customClaim, String subject, Date expiresAt, String... audience) {

		final JWTCreator.Builder builder = JWT.create();

		/* 设置头部信息 Header，可以不设置，使用默认值 */
		builder.withHeader(new HashMap<>());

		/* 设置 Payload(载荷) */
		// 设置jti(JWT ID，编号)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重复攻击。
		builder.withJWTId(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes()));

		// 签名是有谁生成 例如 服务器
		builder.withIssuer(jwtProperties.getIssuer());

		// 签名的对象，jwt所面向的用户，类似用户id
		builder.withSubject(subject);

		// 生效时间，（定义在什么时间之前，该jwt都是不可用的）
		builder.withNotBefore(new Date());

		// 接受 jwt 的一方，类似用户名
		builder.withAudience(audience);

		// 签发时间
		builder.withIssuedAt(new Date());

		// 签名过期的时间
		builder.withExpiresAt(expiresAt);

		// 自定义声明
		if (!CollectionUtils.isEmpty(customClaim)) {
			customClaim.forEach(builder::withClaim);
		}

		/* Signature（签名）*/
		return builder.sign(Algorithm.HMAC256(jwtProperties.getSignSecret()));
	}


	/**
	 * 解析 jwt
	 *
	 * @param token the token
	 * @return 解析后的 jwt，包含头、负载、签名
	 * @throws JWTVerificationException the jwt verification exception
	 */
	public DecodedJWT parse(String token) throws JWTVerificationException {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtProperties.getSignSecret()))
				.withIssuer(jwtProperties.getIssuer())
				.build();
		return verifier.verify(token);
	}

	/**
	 * 获取 jwt 的 Subject
	 *
	 * @param token the token
	 * @return the string
	 */
	public String subject(String token) {
		return this.parse(token).getSubject();
	}

	/**
	 * 获取 jwt 的 Audience
	 *
	 * @param token the token
	 * @return the list
	 */
	public List<String> audience(String token) {
		return this.parse(token).getAudience();
	}


	/**
	 * 计算过期时间
	 *
	 * @param expiresIn 时间，单位：秒，过期时间 = 当前时间 + expiresIn
	 * @return the date
	 */
	public Date calculationExpiresAt(long expiresIn) {
		return new Date(System.currentTimeMillis() + expiresIn * 1000);
	}
}
