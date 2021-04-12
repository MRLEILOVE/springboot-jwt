package com.leigq.www.jwt.constant;

/**
 * Redis Cache Key
 *
 * @author leigq
 * @date 2021-04-12 12:30:32
 */
public interface RedisCacheKey {

	/**
	 * 第一个 %s = userId
	 * 第二个 %s = Scope
	 */
	String CACHE_USER_KEY_FORMAT = "login_user:%s:%s";

}
