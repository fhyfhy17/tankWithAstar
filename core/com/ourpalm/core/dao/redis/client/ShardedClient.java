package com.ourpalm.core.dao.redis.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;

import com.ourpalm.core.dao.redis.Client;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

public class ShardedClient implements Client{
	private static Logger logger = LogCore.runtime;
	private final static int KEY_EXPIRE_TIME = 5; //分布式锁超时5秒自动解锁
	
	private int redis_pool_minIdle = 1;
	private int redis_pool_maxIdle = 20;
	private int redis_pool_maxTotal = 20;
	
	private List<ShardedParam> shardedList = new ArrayList<>();
	private ShardedJedisPool jedisPool;
	
	
	public void init(){
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for(ShardedParam param : shardedList){
			JedisShardInfo shard = new JedisShardInfo(param.getIp(), param.getPort());
			String passwords = param.getPasswords();
			if(!Util.isEmpty(passwords)){
				shard.setPassword(param.getPasswords());
			}
			shards.add(shard);
		}
		
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMinIdle(redis_pool_minIdle);
		poolConfig.setMaxIdle(redis_pool_maxIdle);
		poolConfig.setMaxTotal(redis_pool_maxTotal);
		
		jedisPool = new ShardedJedisPool(poolConfig, shards);
	}
	
	
	@Override
	public void set(String key, String value) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		}finally{
			jedis.close();
		}
	}

	@Override
	public void del(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			jedis.del(key);
		}finally{
			jedis.close();
		}
	}
	
	
	/**
	 * 放入map
	 * @param key 		map对应的索引key
	 * @param entryKey 	map中的key
	 * @param value 	map中entryKey所对应的value
	 */
	@Override
	public long hset(String key, String entryKey, String value){
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hset(key, entryKey, value);
		}finally{
			jedis.close();
		}
	}
	
	
	/**
	 * 批量放入map
	 * @param key map对应的索引key
	 * @param map 所存入的map
	 */
	@Override
	public String hmSet(String key, Map<String, String> map){
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hmset(key, map);
		}finally{
			jedis.close();
		}
	}
	
	
	/**
	 * 批量删除索引key所存储的map集合中对应entryKey
	 * @param key 		索引key
	 * @param entryKey 	map集合的keys
	 */
	@Override
	public long hdel(String key, String... entryKey){
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hdel(key, entryKey);
		}finally{
			jedis.close();
		}
	}
	
	@Override
	public List<String> hmGet(String key, String... entryKey) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hmget(key, entryKey);
		}finally{
			jedis.close();
		}
	}
	
	/**
	 * Set操作
	 * 添加一个set成员在对应的key上
	 */
	@Override
	public long sadd(String key, String... members) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.sadd(key, members);
		}finally{
			jedis.close();
		}
	}
	
	
	/**
	 * 从SET中删除多个成员
	 */
	@Override
	public long srem(String key, String... members){
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.srem(key, members);
		}finally{
			jedis.close();
		}
	}
	
	
	@Override
	public long incr(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.incr(key);
		}finally{
			jedis.close();
		}
	}

	@Override
	public long setnx(String key, String value) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.setnx(key, value);
		}finally{
			jedis.close();
		}
	}
	

	@Override
	public void expire(String key, int seconds){
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			jedis.expire(key, seconds);
		}finally{
			jedis.close();
		}
	}

	
	@Override
	public String get(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.get(key);
		}finally{
			jedis.close();
		}
	}


	@Override
	public Set<String> keys(String param) {
		throw new RuntimeException("分片客户端不支持keys方法");
	}


	@Override
	public boolean exists(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		}finally{
			jedis.close();
		}
	}


	@Override
	public String hget(String key, String entryKey) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hget(key, entryKey);
		}finally{
			jedis.close();
		}
	}


	@Override
	public Map<String, String> hgetAll(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key);
		}finally{
			jedis.close();
		}
	}

	
	@Override
	public long hincrBy(String key, String field, Integer value) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hincrBy(key, field, value);
		}finally{
			jedis.close();
		}
	}


	@Override
	public Set<String> smembers(String key) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		}finally{
			jedis.close();
		}
	}


	@Override
	public boolean sismember(String key, String member) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.sismember(key, member);
		}finally{
			jedis.close();
		}
	}


	@Override
	public boolean hexiste(String key, String field) {
		ShardedJedis jedis = null;
		try{	
			jedis = jedisPool.getResource();
			return jedis.hexists(key, field);
		}finally{
			jedis.close();
		}
	}

	


	@Override
	public List<String> lrange(String key, int start, int stop) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, stop);
		}finally{
			jedis.close();
		}
	}


	@Override
	public long lpush(String key, String... values) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.lpush(key, values);
		}finally{
			jedis.close();
		}
	}
	
	@Override
	public long rpush(String key, String... values) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.rpush(key, values);
		}finally{
			jedis.close();
		}
	}
	
	@Override
	public String lpop(String key) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.lpop(key);
		}finally{
			jedis.close();
		}
	}
	
	@Override
	public long lrem(String key, String value){
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.lrem(key, 0, value);
		}finally{
			jedis.close();
		}
	}
	

	@Override
	public long hlen(String key) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.hlen(key);
		}finally{
			jedis.close();
		}
	}

	@Override
	public void lock(String key) {
		try{
			key += "_LOCK";
			do {
				long result = this.setnx(key,"lock");
				if(result == 1){
					this.expire(key, KEY_EXPIRE_TIME);
					return ;
				}
				Thread.sleep(5);
			}while(true);
		}catch(Exception e){
			logger.error("",e);
		}
	}


	@Override
	public void unlock(String key) {
		try{
			key += "_LOCK";
			this.del(key);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public void setRedis_pool_minIdle(int redis_pool_minIdle) {
		this.redis_pool_minIdle = redis_pool_minIdle;
	}
	public void setRedis_pool_maxIdle(int redis_pool_maxIdle) {
		this.redis_pool_maxIdle = redis_pool_maxIdle;
	}
	public void setRedis_pool_maxTotal(int redis_pool_maxTotal) {
		this.redis_pool_maxTotal = redis_pool_maxTotal;
	}
	public void setShardedList(List<ShardedParam> shardedList) {
		this.shardedList = shardedList;
	}


	@Override
	public long zadd(String key, double score, String member) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		}finally{
			jedis.close();
		}
	}
	
	@Override
	public Set<String> zrevrange(String key, int start, int end) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		}finally{
			jedis.close();
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
		ShardedJedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeWithScores(key, start, end);
		} finally {
			jedis.close();
		}
	}


	@Override
	public long zrem(String key, String... members) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.zrem(key, members);
		}finally{
			jedis.close();
		}
	}


	@Override
	public Long zrevrank(String key, String member) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.zrevrank(key, member);
		}finally{
			jedis.close();
		}
	}

	@Override
	public Double zscore(String key, String member) {
		ShardedJedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long scard(String key) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		}finally{
			jedis.close();
		}
	}


	@Override
	public String srandmember(String key) {
		ShardedJedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return jedis.srandmember(key);
		}finally{
			jedis.close();
		}
	}
}
