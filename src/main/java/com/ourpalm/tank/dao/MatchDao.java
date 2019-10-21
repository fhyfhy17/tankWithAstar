package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleBattle;

public interface MatchDao {

	/** 保存申请匹配的io */
	void saveMatchIo(int ioId);
	
	/** 判断io是否在匹配队列中 */
	boolean hasMatchQueue(int ioId);
	
	/** 删除申请匹配记录 */
	void removeIo(int ioId);
	
	/** 保存战场匹配io用于计数 */
	void saveWarMatchCount(int warType, int ioId);
	int getMatchCount(int warType);
	
	/** 获取战场记录 */
	RoleBattle getRoleBattle(int roleId);
	
	/** 保存战场记录 */
	void saveRoleBattle(RoleBattle roleBattle);
	
	/** 删除战场记录 */
	void removeRoleBattle(int roleId);
	
	/** 保存匹配队伍 */
	void saveMatchTeam(MatchTeam matchTeam);
	
	/** 删除匹配队伍 */
	void removeMatchTeam(String teamId);
	
	/** 获取匹配队伍 */
	MatchTeam getMatchTeam(String teamId);
}
