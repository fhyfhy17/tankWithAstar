package com.ourpalm.core.dao.redis;

public interface Client extends Read, Write{

	void lock(String key);
	
	void unlock(String key);
}
