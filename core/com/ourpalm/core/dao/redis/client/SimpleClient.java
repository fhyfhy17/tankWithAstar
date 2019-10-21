package com.ourpalm.core.dao.redis.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ourpalm.core.dao.redis.Client;
import com.ourpalm.core.util.Util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

public class SimpleClient implements Client {

	private static final Logger logger = LoggerFactory.getLogger(SimpleClient.class);
	private final static int KEY_EXPIRE_TIME = 5; // 分布式锁超时5秒自动解锁

	private int redis_pool_minIdle = 1;
	private int redis_pool_maxIdle = 20;
	private int redis_pool_maxTotal = 1000;
	private String ip;
	private int port;
	private String passwords;

	private JedisPool jedisPool;

	public void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMinIdle(redis_pool_minIdle);
		config.setMaxIdle(redis_pool_maxIdle);
		config.setMaxTotal(redis_pool_maxTotal);
		if (Util.isEmpty(passwords)) {
			jedisPool = new JedisPool(config, ip, port);
			return;
		}
		jedisPool = new JedisPool(config, ip, port, Protocol.DEFAULT_TIMEOUT, passwords);
	}

	@Override
	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 放入map
	 * 
	 * @param key
	 *            map对应的索引key
	 * @param entryKey
	 *            map中的key
	 * @param value
	 *            map中entryKey所对应的value
	 */
	@Override
	public long hset(String key, String entryKey, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hset(key, entryKey, value);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 批量放入map
	 * 
	 * @param key
	 *            map对应的索引key
	 * @param map
	 *            所存入的map
	 */
	@Override
	public String hmSet(String key, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmset(key, map);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 批量删除索引key所存储的map集合中对应entryKey
	 * 
	 * @param key
	 *            索引key
	 * @param entryKey
	 *            map集合的keys
	 */
	@Override
	public long hdel(String key, String... entryKey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hdel(key, entryKey);
		} finally {
			jedis.close();
		}
	}

	@Override
	public List<String> hmGet(String key, String... entryKey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hmget(key, entryKey);
		} finally {
			jedis.close();
		}
	}

	/**
	 * Set操作 添加一个set成员在对应的key上
	 */
	@Override
	public long sadd(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sadd(key, members);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 从SET中删除多个成员
	 */
	@Override
	public long srem(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srem(key, members);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incr(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long setnx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setnx(key, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.expire(key, seconds);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Set<String> keys(String param) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.keys(param);
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} finally {
			jedis.close();
		}
	}
	@Override
	public String hget(String key, String entryKey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, entryKey);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrangeWithScores(key, start, end);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long hincrBy(String key, String field, Integer value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hincrBy(key, field, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean sismember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, member);
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean hexiste(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hexists(key, field);
		} finally {
			jedis.close();
		}
	}

	@Override
	public List<String> lrange(String key, int start, int stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, stop);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long lpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpush(key, values);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long rpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpush(key, values);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lpop(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long lrem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key, 0, value);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hlen(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void lock(String key) {
		try {
			key += "_LOCK";
			do {
				long result = this.setnx(key, "lock");
				if (result == 1) {
					this.expire(key, KEY_EXPIRE_TIME);
					return;
				}
				Thread.sleep(5);
			} while (true);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void unlock(String key) {
		try {
			key += "_LOCK";
			this.del(key);
		} catch (Exception e) {
			logger.error("", e);
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

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}

	@Override
	public long zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Set<String> zrevrange(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long zrem(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrem(key, members);
		} finally {
			jedis.close();
		}
	}

	@Override
	public Long zrevrank(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrank(key, member);
		} finally {
			jedis.close();
		}
	}

	@Override
	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String srandmember(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.srandmember(key);
		} finally {
			jedis.close();
		}
	}
}
