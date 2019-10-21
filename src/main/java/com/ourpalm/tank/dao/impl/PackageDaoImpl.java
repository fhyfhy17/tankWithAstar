package com.ourpalm.tank.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.PackageDao;

public class PackageDaoImpl extends AbstractJedisDao implements PackageDao {
	private final static String KEY = "ROLE_PACKAGE_";
	
	@Override
	public Map<Integer, Integer> getAll(Integer roleId) {
		Map<String, String> goodsMap = getClient().hgetAll(KEY + roleId);
		if(Util.isEmpty(goodsMap)){
			return new HashMap<Integer, Integer>();
		}

		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for(Map.Entry<String, String> entry : goodsMap.entrySet()){
			result.put(Integer.valueOf(entry.getKey()), Integer.valueOf(entry.getValue()));
		}
		return result;
	}
	

	@Override
	public int getCount(int roleId, int goodsId) {
		String result = getClient().hget(KEY + roleId, goodsId+"");
		return !Util.isEmpty(result) ? Integer.valueOf(result) : 0;
	}

	
	@Override
	public void save(Integer roleId, Map<Integer, Integer> goodsMap) {
		if(Util.isEmpty(goodsMap)){
			return;
		}

		Map<String, String> map = new HashMap<String, String>();
		for(Map.Entry<Integer, Integer> entry : goodsMap.entrySet()){
			map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		getClient().hmSet(KEY + roleId, map);
	}

	@Override
	public void delete(Integer roleId, Collection<Integer> goodsIds) {
		if(Util.isEmpty(goodsIds)){
			return;
		}
		
		String[] array = new String[goodsIds.size()];
		int index = 0;
		for(Integer id : goodsIds){
			array[index++] = id.toString();
		}
		getClient().hdel(KEY + roleId, array);
	}
	
	public void add(Integer roleId, Integer goodsId, int num) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		getClient().hincrBy(KEY + roleId, goodsId.toString(), num);
	}
}