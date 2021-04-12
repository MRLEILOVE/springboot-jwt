package com.leigq.www.jwt.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Redis 缓存用户信息
 *
 * @author leigq
 * @date 2021 -04-12 13:37:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisCacheUser implements Serializable {

	/**
	 * The constant serialVersionUID.
	 */
	private static final long serialVersionUID = -9022759272290204997L;

	/**
	 * The User id.
	 */
	private Long userId;

	/**
	 * The User name.
	 */
	private String userName;

	/**
	 * The Mobile.
	 */
	private String mobile;

	/**
	 * The Token.
	 */
	private String token;
}
