package com.ourpalm.tank.template;

public class CampaignMapTankTemplate {

	private int id;
	private int itemId;
	private int type;
	private String name;
	private int team;
	private float birthDir;
	private int aiType;
	private String aiStr;
	private int aiInt;
	private String location;
	private int loopTime;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public int getAiType() {
		return aiType;
	}
	public void setAiType(int aiType) {
		this.aiType = aiType;
	}
	public String getAiStr() {
		return aiStr;
	}
	public void setAiStr(String aiStr) {
		this.aiStr = aiStr;
	}
	public int getAiInt() {
		return aiInt;
	}
	public void setAiInt(int aiInt) {
		this.aiInt = aiInt;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getLoopTime() {
		return loopTime;
	}
	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}
	public float getBirthDir() {
		return birthDir;
	}
	public void setBirthDir(float birthDir) {
		this.birthDir = birthDir;
	}
}
