package com.zakimak.utils;

import javax.servlet.http.HttpServletRequest;

public class Utils {

	public static String getIPAddress(HttpServletRequest request) {

		String ipAddress = request.getHeader("X-Forwarded-For");

		if (ipAddress == null) {

			if (ipAddress == null || ipAddress.length() == 0
					|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0
					|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0
					|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ipAddress == null || ipAddress.length() == 0
					|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ipAddress == null || ipAddress.length() == 0
					|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
			}

		}

		if (ipAddress == null) {
			ipAddress = "Could not resolve IP";
		} else {
			ipAddress = ipAddress.split(",")[0];
		}

		return ipAddress;
	}
}
