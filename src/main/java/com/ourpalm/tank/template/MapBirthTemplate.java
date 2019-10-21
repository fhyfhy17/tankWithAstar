package com.ourpalm.tank.template;

public class MapBirthTemplate {
	
	private int id;				//地图序号
	private String birth;		//出生点
	private int dir;			//朝向
	private int team;			//队伍
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public int getDir() {
		return dir;
	}
	public void setDir(int dir) {
		this.dir = dir;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
}
