package com.ourpalm.core.dao.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Tuple;

public interface Read {

	String get(String key);
	
	Set<String> keys(String param);
	
	boolean exists(String key);
	
	String hget(String key, String entryKey);
	
	Map<String, String> hgetAll(String key);
	
	List<String> hmGet(String key, String... entryKey);
	
	Set<String> smembers(String key);
	
	boolean sismember(String key, String member);
	
	/** 返回set集合的数量 */
	long scard(String key);
	
	boolean hexiste(String key, String field);
	
	/**
	 * 返回list列表区间值
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	List<String> lrange(String key, int start, int stop);
	
	long hlen(String key);
	
	/**
	 * 获取排序集合列表（从大到小）
	 * @param key
	 * @param start	开始索引
	 * @param end	结束索引
	 * @return
	 */
	Set<String> zrevrange(String key, int start, int end);
	
	/**
	 * 获取排名（从大到小）
	 * @param key
	 * @param member
	 * @return
	 */
	Long zrevrank(String key, String member);
	
	/**
	 * set集合，随机
	 * @param key
	 * @return
	 */
	String srandmember(String key);
	
	/**
	 * 排序 同时返回键和值 （由高到低）
	 * 
	 * @param key
	 * @param start 
	 * @param end
	 * @return
	 */
	Set<Tuple> zrevrangeWithScores(String key, int start, int end);
	
	Double zscore(String key,String member);
}
