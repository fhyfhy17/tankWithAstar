package com.ourpalm.tank.thread;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadUtil {

	public static ConcurrentHashMap<Integer, Thread> map = new ConcurrentHashMap<>();

	public static Thread getThread(int id) {
		return map.get(id);
	}

	public static void addThread(int id, Thread t) {
		map.put(id, t);
	}
}
