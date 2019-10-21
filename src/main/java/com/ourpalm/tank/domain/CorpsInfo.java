package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ourpalm.tank.message.CORPS_MSG.RecruitType;

public class CorpsInfo {

	private int id;				//军团id
	private int areaId;			//分区id
	private int leaderId;		//团长角色id
	private String name;		//军团名称
	private String s1Name;		//s1级别名称
	private String s2Name;		//s2级别名称
	private String s3Name; 		//s3级别名称
	private String s4Name;		//s4级别名称
	private String s5Name;		//s5级别名称
	private int level = 1;		//军团等级
	private int playerLimit;	//人数上限;
	private int shopLevel = 1;		//商城等级
	private int active;			//军团活跃度
	private String dec = "";	//军团宣言

	private int recruitType = RecruitType.auto_VALUE;	//招募状态

	private Set<Integer> viceLeaders = new HashSet<Integer>(); //副团长列表

	//科技信息
	private Map<Integer, TechnologyInfo> techInfoMap = new HashMap<>();

	//踢人记录
	private Map<Integer, Long> driveRecords = new HashMap<>();

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getS1Name() {
		return s1Name;
	}
	public void setS1Name(String s1Name) {
		this.s1Name = s1Name;
	}
	public String getS2Name() {
		return s2Name;
	}
	public void setS2Name(String s2Name) {
		this.s2Name = s2Name;
	}
	public String getS3Name() {
		return s3Name;
	}
	public void setS3Name(String s3Name) {
		this.s3Name = s3Name;
	}
	public String getS4Name() {
		return s4Name;
	}
	public void setS4Name(String s4Name) {
		this.s4Name = s4Name;
	}
	public String getS5Name() {
		return s5Name;
	}
	public void setS5Name(String s5Name) {
		this.s5Name = s5Name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	public int getRecruitType() {
		return recruitType;
	}
	public void setRecruitType(int recruitType) {
		this.recruitType = recruitType;
	}
	public int getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(int leaderId) {
		this.leaderId = leaderId;
	}
	public Map<Integer, TechnologyInfo> getTechInfoMap() {
		return techInfoMap;
	}
	public void setTechInfoMap(Map<Integer, TechnologyInfo> techInfoMap) {
		this.techInfoMap = techInfoMap;
	}
	public int getPlayerLimit() {
		return playerLimit;
	}
	public void setPlayerLimit(int playerLimit) {
		this.playerLimit = playerLimit;
	}
	public int getShopLevel() {
		return shopLevel;
	}
	public void setShopLevel(int shopLevel) {
		this.shopLevel = shopLevel;
	}
	public Set<Integer> getViceLeaders() {
		return viceLeaders;
	}
	public void setViceLeaders(Set<Integer> viceLeaders) {
		this.viceLeaders = viceLeaders;
	}
	public Map<Integer, Long> getDriveRecords() {
		return driveRecords;
	}
	public void setDriveRecords(Map<Integer, Long> driveRecords) {
		this.driveRecords = driveRecords;
	}
}
