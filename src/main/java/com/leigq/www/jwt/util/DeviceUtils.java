package com.leigq.www.jwt.util;

import com.leigq.www.jwt.enums.Platform;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取设备类型
 *
 * @author leigq
 */
public class DeviceUtils {

	public static Platform platform(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		if (userAgent.contains(Platform.ANDROID.getPlatform())) {
			return Platform.ANDROID;
		} else if (userAgent.contains("Safari")) {
			return Platform.IOS;
		} else {
			return Platform.PC;
		}
	}

}
