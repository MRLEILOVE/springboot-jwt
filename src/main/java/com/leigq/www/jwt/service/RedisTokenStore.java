package com.leigq.www.jwt.service;

import com.leigq.www.jwt.bean.RedisCacheUser;
import com.leigq.www.jwt.constant.RedisCacheKey;
import com.leigq.www.jwt.enums.Platform;
import com.leigq.www.jwt.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RedisTokenStore
 *
 * @author leigq
 * @date 2021-04-12 12:42:26
 */
@Slf4j
@Service
public class RedisTokenStore {

	@Autowired
	private RedisUtils redisUtils;


	/**
	 * Save boolean.
	 *
	 * @param cacheUser the cache user
	 * @param scope     the scope
	 * @param expiresIn the expires in
	 * @return the boolean
	 */
	public boolean save(RedisCacheUser cacheUser, Platform scope, int expiresIn) {
		return redisUtils.string.set(String.format(RedisCacheKey.CACHE_USER_KEY_FORMAT, cacheUser.getUserId(), scope.getPlatform()), cacheUser, expiresIn);
	}


	/**
	 * Save boolean.
	 *
	 * @param userId the user id
	 * @param scope  the scope
	 * @return the boolean
	 */
	public RedisCacheUser get(long userId, Platform scope) {
		return redisUtils.string.get(String.format(RedisCacheKey.CACHE_USER_KEY_FORMAT, userId, scope.getPlatform()));
	}

}
