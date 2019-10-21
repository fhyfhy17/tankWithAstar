package com.ourpalm.core.dao.redis;

import java.util.Map;

public interface Write {
	
	void set(String key, String value);
	
	void del(String key);
	
	long incr(String key);
	
	long setnx(String key, String value);
	
	void expire(String key, int seconds);
	
	long hset(String key, String entryKey, String value);
	
	long hdel(String key, String... entryKey);
	
	long hincrBy(String key, String field, Integer value);
	
	String hmSet(String key, Map<String, String> map);
	
	long sadd(String key, String... value);
	
	long srem(String key, String... members);
	
	/**
	 * 添加多个值 到list头
	 * @param key
	 * @param values
	 * @return list长度
	 */
	long lpush(String key, String... values);
	
	/**
	 * 从表头开始删除列表中参数value相等元素
	 * @param key
	 * @param value
	 * @return	被移除元素数量
	 */
	long lrem(String key, String value);
	
	/**
	 * 添加多个值 到list尾
	 * @param key
	 * @param values
	 * @return list长度
	 */
	long rpush(String key, String... values);
	
	/**
	 * 移除并返回列表key的头元素
	 * @param key	key不存在，返回nil
	 * @return
	 */
	String lpop(String key);
	
	/**
	 * 添加有序集合
	 * @param key
	 * @param score	排序值
	 * @param member	排序成员
	 */
	long zadd(String key, double score, String member);
	
	/**
	 * 删除有序集合成员
	 * @param key
	 * @param members	成员
	 * @return
	 */
	long zrem(String key, String... members);
	
}
