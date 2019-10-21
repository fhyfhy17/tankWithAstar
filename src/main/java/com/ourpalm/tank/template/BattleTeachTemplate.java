package com.ourpalm.tank.template;

import com.ourpalm.core.util.Cat;
import com.ourpalm.tank.message.MATCH_MSG.Location;

public class BattleTeachTemplate {

	private String id;				//地图mapId
	private int tankInstanceId;		//坦克实例ID
	private int tankId;
	private String birthplayer;
	private int dirplayer;
	private int team;
	private String mine;
	private int time;
	private int holdTime;
	
	private Location playerBirth ;
	private Location mineLocation ;
	
	
	public void init(){
		playerBirth = this.buildLocation(birthplayer, dirplayer);
		mineLocation = this.buildLocation(mine, 0);
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


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTankInstanceId() {
		return tankInstanceId;
	}
	public void setTankInstanceId(int tankInstanceId) {
		this.tankInstanceId = tankInstanceId;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public String getBirthplayer() {
		return birthplayer;
	}
	public void setBirthplayer(String birthplayer) {
		this.birthplayer = birthplayer;
	}
	public int getDirplayer() {
		return dirplayer;
	}
	public void setDirplayer(int dirplayer) {
		this.dirplayer = dirplayer;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public String getMine() {
		return mine;
	}
	public void setMine(String mine) {
		this.mine = mine;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getHoldTime() {
		return holdTime;
	}
	public void setHoldTime(int holdTime) {
		this.holdTime = holdTime;
	}
	public Location getPlayerBirth() {
		return playerBirth;
	}
	public void setPlayerBirth(Location playerBirth) {
		this.playerBirth = playerBirth;
	}
	public Location getMineLocation() {
		return mineLocation;
	}
	public void setMineLocation(Location mineLocation) {
		this.mineLocation = mineLocation;
	}
}
