package com.ourpalm.tank.vo.behaviortree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataContext {

	private Map<String, Object> context;

	public DataContext() {
		context = new ConcurrentHashMap<>();
	}

	public void putInt(String key, int value) {
		context.put(key, value);
	}

	public void putLong(String key, long value) {
		context.put(key, value);
	}

	public void putString(String key, String value) {
		context.put(key, value);
	}

	public void putBoolean(String key, boolean value) {
		context.put(key, value);
	}

	public void putDouble(String key, double value) {
		context.put(key, value);
	}

	public void putBean(String key, Object value) {
		context.put(key, value);
	}

	public boolean exists(String key) {
		return context.containsKey(key);
	}

	public int getInt(String key) {
		if (context.containsKey(key)) {
			return (int) context.get(key);
		}
		return 0;
	}

	public long getLong(String key) {
		if (context.containsKey(key)) {
			return (long) context.get(key);
		}
		return 0;
	}

	public String getString(String key) {
		if (context.containsKey(key)) {
			return (String) context.get(key);
		}
		return null;
	}

	public boolean getBoolean(String key) {
		if (context.containsKey(key)) {
			return (boolean) context.get(key);
		}
		return false;
	}

	public double getDouble(String key) {
		if (context.containsKey(key)) {
			return (double) context.get(key);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String key, Class<T> cls) {
		if (context.containsKey(key)) {
			return (T) context.get(key);
		}
		return null;
	}

	public void remove(String key) {
		context.remove(key);
	}

	public void reset() {
		context.clear();
	}

	public void init() {

	}
}
