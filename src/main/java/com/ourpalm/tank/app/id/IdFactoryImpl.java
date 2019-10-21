package com.ourpalm.tank.app.id;

import com.ourpalm.core.dao.redis.Client;
import com.ourpalm.core.util.Util;

public class IdFactoryImpl implements IdFactory{

	private final String initStr = "1000000000";
	
	private final String THIRTY_SIX_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
	private final String INT_KEY = "INT_ID";
	private final String STR_KEY = "STR_ID";
	
	private final String LOCK = "LOCK";
	private Client jedisClient;
	
	private Client jedis(){
		return jedisClient;
	}
	
	@Override
	public int nextInt() {
		return (int)jedis().incr(INT_KEY);
	}

	
	@Override
	public int currInt(){
		return Integer.parseInt(jedisClient.get(INT_KEY));
	}
	
	
	@Override
	public String nextStr() {
		jedis().lock(LOCK);
		try{
			String currStr = jedis().get(STR_KEY);
			if(Util.isEmpty(currStr)){
				currStr = initStr;
			}
			char[] cStr = currStr.toCharArray();
			char[] increase;
			for(int i = cStr.length - 1; i >= 0; i--){
				increase = increaseChar(cStr[i]);
				cStr[i] = increase[1];
				if('0' == increase[0]){
					break;
				}
			}
			currStr = String.valueOf(cStr);
			jedis().set(STR_KEY, currStr);
			return currStr;
		}finally{
			jedis().unlock(LOCK);
		}
	}
	
	private char[] increaseChar(char ch){
		if('z' == ch){
			return new char[]{'1','0'};
		}
		int pos = THIRTY_SIX_CHARS.indexOf(ch);
		return new char[]{'0',THIRTY_SIX_CHARS.charAt(pos + 1)};
	}

	public void setJedisClient(Client jedisClient) {
		this.jedisClient = jedisClient;
	}
}
