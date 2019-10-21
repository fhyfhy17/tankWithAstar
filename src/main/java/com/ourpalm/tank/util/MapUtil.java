package com.ourpalm.tank.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.util.Util;

public class MapUtil {

	public static void put(Map<Integer, Integer> map, int key, int value){
		if (map.containsKey(key)) 
			value += map.get(key);
		map.put(key, value);
	}
	
	public static Map<Integer, Integer> transformToIntMap(Map<String, String> source) {
		if (Util.isEmpty(source)) {
			return new HashMap<>();
		}
		
		Map<Integer, Integer> result = new HashMap<>();
		for (Map.Entry<String, String> entry : source.entrySet()) {
			result.put(parseInt(entry.getKey()), parseInt(entry.getValue()));
		}
		return result;
	}
	
	private static Integer parseInt(String str){
		return str == null ? null : Integer.valueOf(str);
	}
	
	public static Map<String, String> transformToStringMap(Map<Integer, Integer> source) {
		if (Util.isEmpty(source)) {
			return new HashMap<>();
		}
		
		Map<String, String> result = new HashMap<>();
		for (Map.Entry<Integer, Integer> entry : source.entrySet()) {
			result.put(parseStr(entry.getKey()), parseStr(entry.getValue()));
		}
		return result;
	}
	
	private static String parseStr(Integer value){
		return value == null ? null : value.toString();
	}
	
	public static Map<Integer, Integer> arrayListToMap(List<int[]> list) {
		Map<Integer, Integer> map = new HashMap<>();
		for (int[] is : list) {
			map.put(is[0], is[1]);
		}
		return map;
	}
}
