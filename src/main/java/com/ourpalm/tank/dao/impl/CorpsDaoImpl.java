package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.CorpsDao;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;

public class CorpsDaoImpl extends AbstractJedisDao implements CorpsDao{

	private static final String CORPS_TASK_KEY = "CORPS_TASK_KEY_";			//执行锁
	private static final String CORPS_KEY = "CORPS_"; 							//军团key
	private static final String CORPS_LIST_KEY = "CORPS_LIST_";					//军团列表key
	private static final String CORPS_ROLE_LIST_KEY = "CORPS_ROLE_LIST_";		//军团成员列表key
	private static final String CORPS_APPLY_LIST_KEY = "CORPS_APPLY_LIST_";		//军团申请加入列表
	private static final String CORPS_NAMES_KEY = "CORPS_NAMES_";				//军团名称
	private static final String CORPS_IDS_KEY = "CORPS_IDS_KEY";				//军团自增id
	private static final int pageSize = 6;										//军团每页显示条目数
	
	@Override
	public void init(){
		if(! client.exists(CORPS_IDS_KEY)){
			client.set(CORPS_IDS_KEY, "1000");
		}
	}
	
	@Override
	public void lock(int key){
		GameContext.getLock().lock(CORPS_TASK_KEY + key);
	}
	
	@Override
	public void unlock(int key){
		GameContext.getLock().unlock(CORPS_TASK_KEY + key);
	}
	
	@Override
	public int nextId(){
		return (int)client.incr(CORPS_IDS_KEY);
	}
	
	@Override
	public void createCorps(CorpsInfo corpsInfo){
		this.saveCorpsInfo(corpsInfo);
		//id加入军团列表
		String key = CORPS_LIST_KEY + corpsInfo.getAreaId();
		client.lpush(key, corpsInfo.getId()+"");
		
		//名字，ID加入到军团名称中
		client.hset(CORPS_NAMES_KEY + corpsInfo.getAreaId(), corpsInfo.getName(), corpsInfo.getId()+"");
	}
	
	
	@Override
	public void removeCorpsInfo(CorpsInfo corpsInfo){
		int id = corpsInfo.getId();
		//删除信息
		client.del(CORPS_KEY + id);
		
		//军团列表中删除ID
		String key = CORPS_LIST_KEY + corpsInfo.getAreaId();
		client.lrem(key, id+"");
		
		//删除名字
		client.hdel(CORPS_NAMES_KEY + corpsInfo.getAreaId(), corpsInfo.getName());
	}
	
	@Override
	public void removeAllCorpsRole(int corpsId){
		client.del(CORPS_ROLE_LIST_KEY + corpsId);
	}
	
	
	@Override
	public void saveCorpsInfo(CorpsInfo corpsInfo){
		String key = CORPS_KEY + corpsInfo.getId();
		client.set(key, JSON.toJSONString(corpsInfo));
	}
	
	
	@Override
	public CorpsInfo getCorpsInfo(int id){
		return this.getCorpsInfo(id+"");
	}
	
	@Override
	public CorpsInfo getCorpsInfo(int areaId, String name){
		String idStr = client.hget(CORPS_NAMES_KEY + areaId, name);
		if(Util.isEmpty(idStr)){
			return null;
		}
		return this.getCorpsInfo(idStr);
	}
	
	
	private CorpsInfo getCorpsInfo(String id){
		return JSON.parseObject(client.get(CORPS_KEY + id), CorpsInfo.class);
	}
	
	
	@Override
	public List<CorpsInfo> getCorpsInfoList(int areaId, int page){
		int start = page * pageSize;
		int end = start + pageSize;
		
		if(page < 0){
			start = 0;
			end = -1;
		}
		
		String key = CORPS_LIST_KEY + areaId;
		List<String> strList = client.lrange(key, start, end);
		if(Util.isEmpty(strList)){
			return new ArrayList<>();
		}
		
		List<CorpsInfo> list = new ArrayList<>();
		for(String id : strList){
			if(Util.isEmpty(id)){
				continue;
			}
			CorpsInfo info = this.getCorpsInfo(id);
			if(info != null){
				list.add(info);
			}
		}
		
		return list;
	}
	
	
	@Override
	public List<CorpsInfo> getAllCorpsInfo(int areaId){
		return this.getCorpsInfoList(areaId, -1);
	}
	
	
	
	@Override
	public void saveCorpsRole(CorpsRole corpsRole){
		client.hset(CORPS_ROLE_LIST_KEY + corpsRole.getCorpsId(), corpsRole.getRoleId()+"", JSON.toJSONString(corpsRole));
	}
	
	@Override
	public void removeCorpsRole(int corpsId, int roleId){
		client.hdel(CORPS_ROLE_LIST_KEY + corpsId, roleId + "");
	}
	
	
	@Override
	public CorpsRole getCorpsRole(int corpsId, int roleId){
		return JSON.parseObject(client.hget(CORPS_ROLE_LIST_KEY + corpsId, String.valueOf(roleId)), CorpsRole.class);
	}
	
	
	@Override
	public List<CorpsRole> getCorpsRoleList(int corpsId){
		Map<String, String> allMap = client.hgetAll(CORPS_ROLE_LIST_KEY + corpsId);
		List<CorpsRole> list = new ArrayList<>();
		if(Util.isEmpty(allMap)){
			return list;
		}
		for(String jsonStr : allMap.values()){
			CorpsRole corpsRole = JSON.parseObject(jsonStr, CorpsRole.class);
			if(corpsRole != null){
				list.add(corpsRole);
			}
		}
		return list;
	}
	
	
	@Override
	public int getCorpsRoleSize(int corpsId){
		return (int)client.hlen(CORPS_ROLE_LIST_KEY + corpsId); 
	}
	
	@Override
	public Set<String> getCorpsApplyList(int corpsId){
		return client.smembers(CORPS_APPLY_LIST_KEY + corpsId);
	}
	
	@Override
	public boolean hasExistCorpsApply(int corpsId, int roleId){
		return client.sismember(CORPS_APPLY_LIST_KEY + corpsId, roleId+"");
	}
	
	@Override
	public void removeCorpsApply(int corpsId, int roleId){
		client.srem(CORPS_APPLY_LIST_KEY + corpsId, roleId+"");
	}
	
	@Override
	public void addCorpsApply(int corpsId, int roleId){
		client.sadd(CORPS_APPLY_LIST_KEY + corpsId, roleId+"");
	}
	
	@Override
	public boolean hasExistCorpsName(int areaId, String name){
		return client.hexiste(CORPS_NAMES_KEY + areaId, name);
	}
}
