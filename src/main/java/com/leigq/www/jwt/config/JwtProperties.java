package com.leigq.www.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;

/**
 * JwtProperties
 *
 * @author leigq
 * @date 2021-04-10 13:49:40
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties implements Serializable {
	private static final long serialVersionUID = 6653274237419642531L;

	/**
	 * jwt 签名密钥
	 */
	private String signSecret;

	/**
	 * 发行者
	 */
	private String issuer;

	/**
	 * 前端放 jwt 的请求头的 key
	 */
	private String headerKey;

	/**
	 * 有效时间
	 */
	private Duration expiresIn;

}
