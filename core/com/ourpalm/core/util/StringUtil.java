package com.ourpalm.core.util;

import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isNumber(String content) {
		if (null == content || content.trim().length() == 0)
			return false;
		content = content.trim();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}

	public static boolean isCharacter(String content) {
		if (null == content || content.trim().length() == 0)
			return false;
		content = content.trim().toLowerCase();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
				return false;
			}
		}
		return true;
	}

	public static String buildLogOrigin(String name, String... params) {
		StringBuilder sb = new StringBuilder(name);
		for (String p : params) {
			sb.append("_").append(p);
		}
		return sb.toString();
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[\\d]+$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}
		s = s.trim();
		if (s.isEmpty() || s.equals("null"))
			return true;
		return false;
	}

	public static boolean isNotEmpty(String s) {
		return s != null && !s.isEmpty();
	}

}
