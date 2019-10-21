package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ourpalm.core.util.Cat;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;

public class MapTemplate {

	//分配出生点id
	private static final AtomicInteger redId = new AtomicInteger(1);
	private static final AtomicInteger buleId = new AtomicInteger(1);
	
	private int id;
	private String mapId;			//地图ID
	private int weather;			//天气
	private int gold;				//金币随机掉落金币上限数(军衔模式不产出)
	private int goldRat;			//金币产出概率
	private int randomGoldLimit;	//金币每次产出上限

	private int playerCount;		//歼灭战人头数
	private String name;
	
	private float offX;				//小地图偏移量
	private float offY;
	private float offWidth;
	private float offHeigh;
	
	private int reviveTime;  		//复活时间
	private int reviveGold;			//复活需要的金币数
	private int reviveBuffId;		//复活buffId;
	private int birthBuffId;		//出生buffId;
	
	private int readyTime;			//比赛准备时间
	private int overTime;			//比赛时长
	
	private float x;				//战旗位置
	private float y;
	private float z;
	private float radius; 			//战旗半径
	private int time;     			//占旗时间
	
	
	private List<MapBirthTemplate> birthList = new ArrayList<>();
	
	private List<int[]> teamABirth = new ArrayList<>();
	private List<int[]> teamBBirth = new ArrayList<>();
	
	
	public void init(){
		for(MapBirthTemplate template : birthList){
			if(template.getTeam() == 1){
				this.buildList(template, teamABirth);
				continue;
			}
			this.buildList(template, teamBBirth);
		}
	}
	
	
	private void buildList(MapBirthTemplate template, List<int[]> list){
		String[] strs = template.getBirth().split(Cat.comma);
		int[] locations = {
				Integer.parseInt(strs[0]), 
				Integer.parseInt(strs[1]),
				Integer.parseInt(strs[2]),
				template.getDir()};
		list.add(locations);
	}
	
	
	/** 分配出生点 */
	public void matchBirthLoaction(AbstractInstance tank){
		if(tank.getTeam() == TEAM.BLUE){
			int count = Math.abs(buleId.incrementAndGet());
			int index = count % teamABirth.size();
			int[] locations = teamABirth.get(index);
			Location location = Location.newBuilder()
					.setX(locations[0])
					.setY(locations[1])
					.setZ(locations[2])
					.setDir(locations[3])
					.build();
//			Location	 location = Location.newBuilder()
//					.setX(218+RandomUtil.randomInt(10))
//					.setY(35)
//					.setZ(123)
//					.setDir(31)
//					.build();
			tank.setBirthLocation(location);
			return ;
		}
		int count = Math.abs(redId.incrementAndGet());
		int index = count % teamBBirth.size();
		int[] locations = teamBBirth.get(index);
		Location location = Location.newBuilder()
				.setX(locations[0])
				.setY(locations[1])
				.setZ(locations[2])
				.setDir(locations[3])
				.build();
//		205,35,130
//		218,35,123
//		194,35,115
//		206,35,108
//		215,35,99
//		822,35,75
//		819,35,91

		
//		Location	 location = Location.newBuilder()
//				.setX(205+RandomUtil.randomInt(10))
//				.setY(35)
//				.setZ(130)
//				.setDir(31)
//				.build();
		tank.setBirthLocation(location);
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getReviveTime() {
		return reviveTime;
	}
	public void setReviveTime(int reviveTime) {
		this.reviveTime = reviveTime;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getReadyTime() {
		return readyTime;
	}
	public void setReadyTime(int readyTime) {
		this.readyTime = readyTime;
	}
	public int getOverTime() {
		return overTime;
	}
	public void setOverTime(int overTime) {
		this.overTime = overTime;
	}
	public int getReviveGold() {
		return reviveGold;
	}
	public void setReviveGold(int reviveGold) {
		this.reviveGold = reviveGold;
	}
	public int getReviveBuffId() {
		return reviveBuffId;
	}
	public void setReviveBuffId(int reviveBuffId) {
		this.reviveBuffId = reviveBuffId;
	}
	public int getBirthBuffId() {
		return birthBuffId;
	}
	public void setBirthBuffId(int birthBuffId) {
		this.birthBuffId = birthBuffId;
	}
	public int getPlayerCount() {
		return playerCount;
	}
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	public void setBirthList(List<MapBirthTemplate> birthList) {
		this.birthList = birthList;
	}
	public float getOffX() {
		return offX;
	}
	public void setOffX(float offX) {
		this.offX = offX;
	}
	public float getOffY() {
		return offY;
	}
	public void setOffY(float offY) {
		this.offY = offY;
	}
	public float getOffWidth() {
		return offWidth;
	}
	public void setOffWidth(float offWidth) {
		this.offWidth = offWidth;
	}
	public float getOffHeigh() {
		return offHeigh;
	}
	public void setOffHeigh(float offHeigh) {
		this.offHeigh = offHeigh;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getGoldRat() {
		return goldRat;
	}
	public void setGoldRat(int goldRat) {
		this.goldRat = goldRat;
	}
	public int getRandomGoldLimit() {
		return randomGoldLimit;
	}
	public void setRandomGoldLimit(int randomGoldLimit) {
		this.randomGoldLimit = randomGoldLimit;
	}
	public int getWeather() {
		return weather;
	}
	public void setWeather(int weather) {
		this.weather = weather;
	}
}
