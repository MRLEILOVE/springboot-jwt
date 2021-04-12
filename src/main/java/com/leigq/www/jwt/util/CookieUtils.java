package com.leigq.www.jwt.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Cookie Utils
 *
 * @author leigq
 * @date 2021-04-12 09:28:52
 */
public class CookieUtils {

	public static final int COOKIE_MAX_AGE = 7 * 24 * 3600;
	public static final int COOKIE_HALF_HOUR = 30 * 60;

	/**
	 * 根据Cookie名称得到Cookie对象，不存在该对象则返回Null
	 *
	 * @param request the request
	 * @param name    the name
	 * @return cookie
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (Objects.isNull(cookies) || cookies.length == 0) {
			return null;
		}
		Cookie cookie = null;
		for (Cookie c : cookies) {
			if (name.equals(c.getName())) {
				cookie = c;
				break;
			}
		}
		return cookie;
	}


	/**
	 * 根据Cookie名称直接得到Cookie值
	 *
	 * @param request the request
	 * @param name    the name
	 * @return cookie value
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			return cookie.getValue();
		}
		return null;
	}

	/**
	 * 移除cookie
	 *
	 * @param request  the request
	 * @param response the response
	 * @param name     这个是名称，不是值
	 */
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		if (null == name) {
			return;
		}
		Cookie cookie = getCookie(request, name);
		if (null != cookie) {
			cookie.setPath("/");
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	/**
	 * 添加一条新的Cookie，可以指定过期时间(单位：秒)
	 *
	 * @param response the response
	 * @param name     the name
	 * @param value    the value
	 * @param maxValue the max value
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int maxValue) {
		final Cookie cookie = generate(name, value, maxValue);
		if (cookie == null) {
			return;
		}
		response.addCookie(cookie);
	}


	/**
	 * 添加一条新的Cookie，默认30分钟过期时间
	 *
	 * @param response the response
	 * @param name     the name
	 * @param value    the value
	 */
	public static void setCookie(HttpServletResponse response, String name, String value) {
		setCookie(response, name, value, COOKIE_HALF_HOUR);
	}


	/**
	 * 添加一条新的Cookie，安全
	 *
	 * @param response the response
	 * @param name     the name
	 * @param value    the value
	 */
	public static void setSecurityCookie(HttpServletResponse response, String name, String value, int maxValue) {
		final Cookie cookie = generate(name, value, maxValue);
		if (cookie == null) {
			return;
		}
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		response.addCookie(cookie);
	}

	/**
	 * Generate cookie.
	 *
	 * @param name     the name
	 * @param value    the value
	 * @param maxValue the max value
	 * @return the cookie
	 */
	public static Cookie generate(String name, String value, int maxValue) {
		if (Objects.isNull(name) || name.trim().length() == 0) {
			return null;
		}
		Cookie cookie = new Cookie(name, value == null ? "" : value);
		cookie.setPath("/");
		cookie.setMaxAge(maxValue != 0 ? maxValue : COOKIE_HALF_HOUR);
		return cookie;
	}
}
