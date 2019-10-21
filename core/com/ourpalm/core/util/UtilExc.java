package com.ourpalm.core.util;

import java.util.Collection;
import java.util.Map;

public class UtilExc {
	
	public static void isEmpty(Collection<?> collection) {
		if(collection == null || collection.size() == 0){
			throw new NullPointerException("Collection is Null");
		}
	}
	
	public static void isEmpty(Map<?,?> map){
		if(map == null || map.size() == 0){
			throw new NullPointerException("Map is Null");
		}
	}
	
	
	public static void isEmpty(String str){
		if(str == null || str.length() <= 0){
			throw new NullPointerException("String is Null");
		}
	}

}
