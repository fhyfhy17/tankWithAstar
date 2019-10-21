package com.ourpalm.tank.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.vo.MatchTank;

/**
 * 匹配队伍
 * @author wangkun
 *
 */
public class MatchTeam {

	private String teamId;		//队伍id
	private int leaderRoleId;	//队长角色id
	private int warType;		//战场类型
	private boolean hadBegin;	//比赛是否开始
	private int limitMin;		//限制最小军衔
	private int limitMax;		//限制最大军衔
	private String limitDesc = "";	//限制说明
	private Map<Integer, MatchTank> teamMap = new HashMap<>();	//队伍信息
	
	/** 添加队伍 */
	public void putMatchTank(MatchTank matchTank){
		teamMap.put(matchTank.getRoleId(), matchTank);
	}
	
	/** 删除队伍信息 */
	public void removeMatchTank(int roleId){
		teamMap.remove(roleId);
	}
	
	/** 队伍是否为空 */
	public boolean teamIsEmpty(){
		return teamMap.isEmpty();
	}
	
	/** 随机一位新队长 */
	public void randomTeamLeader(){
		if(teamIsEmpty()){
			this.leaderRoleId = 0;
			return ;
		}
		for(MatchTank matchTank : teamMap.values()){
			this.leaderRoleId = matchTank.getRoleId();
			return ;
		}
	}
	
	/** 返回所用队员 */
	public Collection<MatchTank> allTeamTank(){
		return teamMap.values();
	}
	
	/** 队伍是否已满 */
	public boolean hadFull(){
		return teamMap.size() >= 2;
	}
	
	/** 进入匹配 */
	public void enterMatch(){
		this.hadBegin = true;
		for(MatchTank tank : teamMap.values()){
			tank.setHadReady(false);
		}
	}
	
	
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public int getLeaderRoleId() {
		return leaderRoleId;
	}
	public void setLeaderRoleId(int leaderRoleId) {
		this.leaderRoleId = leaderRoleId;
	}
	public Map<Integer, MatchTank> getTeamMap() {
		return teamMap;
	}
	public void setTeamMap(Map<Integer, MatchTank> teamMap) {
		this.teamMap = teamMap;
	}
	public int getWarType() {
		return warType;
	}
	public void setWarType(int warType) {
		this.warType = warType;
	}
	public boolean isHadBegin() {
		return hadBegin;
	}
	public void setHadBegin(boolean hadBegin) {
		this.hadBegin = hadBegin;
	}
	public int getLimitMin() {
		return limitMin;
	}
	public void setLimitMin(int limitMin) {
		this.limitMin = limitMin;
	}
	public int getLimitMax() {
		return limitMax;
	}
	public void setLimitMax(int limitMax) {
		this.limitMax = limitMax;
	}
	public String getLimitDesc() {
		return limitDesc;
	}
	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}
}
