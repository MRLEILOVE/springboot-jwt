package com.leigq.www.jwt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备平台
 *
 * @author leigq
 * @date 2021 -04-10 14:22:12
 */
@AllArgsConstructor
@Getter
public enum Platform {

	/**
	 * Pc platform.
	 */
	PC("PC"),

	/**
	 * 网页端
	 */
	H5("H5"),

	/**
	 * ANDROID 端
	 */
	ANDROID("Android"),

	/**
	 * IOS 端
	 */
	IOS("IOS"),


	/**
	 * 小程序
	 */
	APPLET("Applet"),
	;

	/**
	 * The Platform.
	 */
	private final String platform;

	@Override
	public String toString() {
		return platform;
	}
}
