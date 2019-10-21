package com.ourpalm.tank.template;

import com.ourpalm.core.util.Cat;
import com.ourpalm.tank.message.MATCH_MSG.Location;

public class BattleTeachTankTemplate {

	private int id;			//坦克实例ID
	private int tankId;
	private String birth;
	private int dir;
	private int team;
	
	private Location location;
	
	public void init(){
		location = buildLocation(birth, dir);
	}
	
	private Location buildLocation(String location, int dir){
		String[] strs = location.split(Cat.comma);
		Location location3D = Location.newBuilder()
				.setDir(dir)
				.setX(Float.valueOf(strs[0]))
				.setY(Float.valueOf(strs[1]))
				.setZ(Float.valueOf(strs[2]))
				.build();
		return location3D;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
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
	public Location getLocation() {
		return location;
	}
}
