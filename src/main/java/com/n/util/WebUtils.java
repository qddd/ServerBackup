package com.n.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebUtils {

	public static Map<String, Object> getParameters(HttpServletRequest request) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();

		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();

			if (key.endsWith("[]")) {
				params.put(key.substring(0, key.length() - 2), request.getParameterValues(key));
			} else {
				params.put(key, request.getParameter(key));
			}
		}

		return params;
	}

	public static HashMap<String, Object> getParameters2(HttpServletRequest request) throws Exception {
		HashMap<String, Object> params = new HashMap<String, Object>();

		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();

			if (key.endsWith("[]")) {
				params.put(key.substring(0, key.length() - 2), request.getParameterValues(key));
			} else {
				params.put(key, request.getParameter(key));
			}
		}

		return params;
	}

	public static Map<String, Object> getSuccess(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", 1);
		result.put("message", message);
		result.put("data", null);
		return result;
	}

	public static String getSuccessJson(String message)
			throws JsonGenerationException, JsonMappingException, IOException {
		return getSuccessJson(message, null);
	}

	public static String getSuccessJson(String message, Object data)
			throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", 1);
		result.put("message", message);
		result.put("data", data);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(result);
	}

	public static Map<String, Object> getSuccess(String message, Object data) {
		Map<String, Object> result = getSuccess(message);
		result.put("data", data);
		return result;
	}

	public static Map<String, Object> getError(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", 0);
		result.put("message", message);
		return result;
	}

	public static String getErrorJson(String message) {
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("success", 0);
			result.put("message", message);
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> getError(String message, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", 0);
		result.put("message", message);
		result.put("data", data);
		return result;
	}

	 
	// 获取请求的IP地址
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	// 用于spring环境
	public static String getIP_spring() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return getIP(request);
	}

	 

	public static int browserType(HttpServletRequest request) {
		// android/(iphone/iPad)/PC/其他
		String userAgent = request.getHeader("user-agent");
		if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
			return 2;
		} else if (userAgent.contains("Android")) {
			return 1;
		} else if (userAgent.contains("BlackBerry") || userAgent.contains("Windows Phone")
				|| userAgent.contains("Symbian")) {
			return 4;
		} else {
			return 3;
		}
	}

	 
	


}
