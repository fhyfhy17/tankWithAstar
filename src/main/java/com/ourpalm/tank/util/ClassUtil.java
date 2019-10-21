package com.ourpalm.tank.util;

import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class ClassUtil {

	public static Object[] jsonParseToMethodParam(Method method, List<String> paramList) {
		int len = paramList.size();
		
		Class<?>[] types = method.getParameterTypes();
		Object[] args = new Object[types.length];
		for(int i = 0, l = types.length; i < l; i++) {
			String _v = null;
			if(i < len)
				_v = paramList.get(i);
			Object o = JSON.parseObject(_v, types[i]);
			args[i] = o;
		}
		
		return args;
	}
}
