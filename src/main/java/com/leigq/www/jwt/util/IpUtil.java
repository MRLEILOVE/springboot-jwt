package com.leigq.www.jwt.util;

import javax.servlet.http.HttpServletRequest;

/**
 * IpUtil
 * 参考:http://www.ibloger.net/article/144.html
 *
 * @author leigq
 * @date 2021 -04-10 16:39:38
 */
public final class IpUtil {

	/**
	 * Instantiates a new Ip util.
	 */
	private IpUtil() {

	}

	/**
	 * 判断IP是否是内网地址
	 *
	 * @param ipAddress ip地址
	 * @return 是否是内网地址 boolean
	 */
	public static boolean isInnerIp(String ipAddress) {
		boolean isInnerIp;
		long ipNum = getIpNum(ipAddress);
		/*
		 * 私有IP：
		 * A类  10.0.0.0-10.255.255.255
		 * B类  172.16.0.0-172.31.255.255
		 * C类  192.168.0.0-192.168.255.255
		 * 当然，还有127这个网段是环回地址
		 * */
		long aBegin = getIpNum("10.0.0.0");
		long aEnd = getIpNum("10.255.255.255");

		long bBegin = getIpNum("172.16.0.0");
		long bEnd = getIpNum("172.31.255.255");

		long cBegin = getIpNum("192.168.0.0");
		long cEnd = getIpNum("192.168.255.255");
		isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
				|| "127.0.0.1".equals(ipAddress);
		return isInnerIp;
	}


	/**
	 * Gets real ip.
	 *
	 * @param request the request
	 * @return the real ip
	 */
	public static String realIp(HttpServletRequest request) {
		// 获取客户端ip地址
		String clientIp = request.getHeader("x-forwarded-for");

		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();
		}

		String[] clientIps = clientIp.split(",");
		if (clientIps.length <= 1) {
			return clientIp.trim();
		}

		// 判断是否来自CDN
		if (isFromCdn(request)) {
			return clientIps[clientIps.length - 2].trim();
		}

		return clientIps[clientIps.length - 1].trim();
	}

	/**
	 * Is from cdn boolean.
	 *
	 * @param request the request
	 * @return the boolean
	 */
	private static boolean isFromCdn(HttpServletRequest request) {
		String host = request.getHeader("host");
		return host.contains("www.189.cn") || host.contains("shouji.189.cn") || host.contains(
				"image2.chinatelecom-ec.com") || host.contains(
				"image1.chinatelecom-ec.com");
	}

	/**
	 * Gets ip num.
	 *
	 * @param ipAddress the ip address
	 * @return the ip num
	 */
	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
	}

	/**
	 * Is inner boolean.
	 *
	 * @param userIp the user ip
	 * @param begin  the begin
	 * @param end    the end
	 * @return the boolean
	 */
	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}
}
