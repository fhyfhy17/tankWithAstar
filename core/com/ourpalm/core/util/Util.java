package com.ourpalm.core.util;

import java.util.Collection;
import java.util.Map;

public class Util {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(String str) {
		return str == null || "null".equals(str) || str.isEmpty();
	}

	/** 返回两坐标点之间的距离 */
	public static float range(float x1, float y1, float x2, float y2) {
		float x = Math.abs(x1 - x2);
		float y = Math.abs(y1 - y2);
		return (float) Math.sqrt(x * x + y * y);
	}

}
