package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.MatchDao;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;

public class MatchDaoImpl extends AbstractJedisDao implements MatchDao{
	private final static int MATCH_COUNT_EXPIRE = 300; //匹配人数统计key时效(秒)
	private final static String MATCH_APPLY_KEY = "MATCH_APPLY_KEY"; //报名匹配队列
	private final static String ROLE_BATTLE_KEY = "ROLE_BATTLE_";	//战场记录信息
	private final static String MATCH_TEAM_KEY = "MATCH_TEAM_KEY_"; //匹配队伍信息
	private final static String MATCH_COUNT_KEY = "MATCH_COUNT_KEY_"; //匹配人数计数

	@Override
	public void saveMatchIo(int ioId) {
		client.sadd(MATCH_APPLY_KEY, String.valueOf(ioId));
	}
	
	@Override
	public boolean hasMatchQueue(int ioId){
		return client.sismember(MATCH_APPLY_KEY, String.valueOf(ioId));
	}

	@Override
	public void removeIo(int ioId) {
		client.srem(MATCH_APPLY_KEY, String.valueOf(ioId));
		
		//删除匹配人数计数器
		for(WAR_TYPE type : WAR_TYPE.values()){
			client.srem(MATCH_COUNT_KEY + type.getNumber(), String.valueOf(ioId));
		}
	}

	@Override
	public RoleBattle getRoleBattle(int roleId) {
		return JSON.parseObject(client.get(ROLE_BATTLE_KEY + roleId), RoleBattle.class);
	}

	@Override
	public void saveRoleBattle(RoleBattle roleBattle) {
		client.set(ROLE_BATTLE_KEY + roleBattle.getRoleId(), JSON.toJSONString(roleBattle));
	}

	@Override
	public void removeRoleBattle(int roleId) {
		client.del(ROLE_BATTLE_KEY + roleId);
	}

	@Override
	public void saveMatchTeam(MatchTeam matchTeam) {
		String key = MATCH_TEAM_KEY + matchTeam.getTeamId();
		client.set(key, JSON.toJSONString(matchTeam));
	}

	@Override
	public void removeMatchTeam(String teamId) {
		if(Util.isEmpty(teamId)){
			return ;
		}
		client.del(MATCH_TEAM_KEY + teamId);
	}

	@Override
	public MatchTeam getMatchTeam(String teamId){
		if(Util.isEmpty(teamId)){
			return null;
		}
		
		String jsonStr = client.get(MATCH_TEAM_KEY + teamId);
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, MatchTeam.class);
	}

	@Override
	public void saveWarMatchCount(int warType, int ioId) {
		String key = MATCH_COUNT_KEY + warType;
		client.sadd(key, String.valueOf(ioId));
		client.expire(key, MATCH_COUNT_EXPIRE);
	}

	@Override
	public int getMatchCount(int warType) {
		String key = MATCH_COUNT_KEY + warType;
		client.expire(key, MATCH_COUNT_EXPIRE);
		return (int)client.scard(key);
	}
}
