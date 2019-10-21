package com.ourpalm.tank.dao;

import java.util.Collection;
import java.util.Map;

public interface PackageDao {
	
	public Map<Integer, Integer> getAll(Integer roleId);
	
	public int getCount(int roleId, int goodsId);
	
	public void save(Integer roleId, Map<Integer, Integer> goodsMap);
	
	public void delete(Integer roleId, Collection<Integer> goodsIds);
	
	public void add(Integer roleId, Integer goodsId, int num);
}
