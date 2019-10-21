package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;

public class CampaignMapTemplate {

	private int id;
	private int warId;			//战役ID(战区)
	private int gateId;			//战场ID
	private int hard;			//难度
	private int level;			//所需等级
	private int gateUnlockId;	//战场解锁id
	private int unlockId;		//解锁Id
	private String mapId;
	private float dir;			//出生朝向
	private String location;
	private int fuel;			//消耗燃油
	private int readyTime;
	private int overTime;
	private int deathCount;		//死亡次数
	private int reviveGold;		//复活所需金币数
	private int reviveTime; 	//复活间隔时间
	
	private int passType;		//通关类型
	private String passParam1;	//通关参数1
	private String passParam2;	//通关参数2
	private String passParam3;  //通关参数3
	
	private int star1;
	private int starParam1;
	private int star2;
	private int starParam2;
	
	private int firon;
	private int fgoodsId1;
	private int fnum1;
	private int fgoodsId2;
	private int fnum2;
	private int fgoodsId3;
	private int fnum3;
	
	private int iron;
	private int goodsId1;
	private int num1;
	private int goodsId2;
	private int num2;
	private int goodsId3;
	private int num3;
	
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	private Map<Integer, Integer> firstGoodsMap = new HashMap<>();
	
	public void init(){
		this.buildGoodsMap(goodsMap, goodsId1, num1);
		this.buildGoodsMap(goodsMap, goodsId2, num2);
		this.buildGoodsMap(goodsMap, goodsId3, num3);
		
		this.buildGoodsMap(firstGoodsMap, fgoodsId1, fnum1);
		this.buildGoodsMap(firstGoodsMap, fgoodsId2, fnum2);
		this.buildGoodsMap(firstGoodsMap, fgoodsId3, fnum3);
	}
	
	private void buildGoodsMap(Map<Integer, Integer> goodsMap, Integer goodsId, Integer num){
		if(goodsId <= 0 || num <= 0){
			return ;
		}
		//验证物品是否存在
		GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(template == null){
			LogCore.startup.error("goodsId = " + goodsId + "此物品不存在...");
			return ;
		}
		Integer count = goodsMap.get(goodsId);
		if(count != null){
			num = count + num;
		}
		goodsMap.put(goodsId, num);
	}
	
	public Map<Integer, Integer> getGoodsMap(){
		return new HashMap<>(this.goodsMap);
	}
	
	public Map<Integer, Integer> getFirstGoodsMap(){
		return new HashMap<>(this.firstGoodsMap);
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
	public int getFuel() {
		return fuel;
	}
	public void setFuel(int fuel) {
		this.fuel = fuel;
	}
	public int getPassType() {
		return passType;
	}
	public void setPassType(int passType) {
		this.passType = passType;
	}
	public String getPassParam1() {
		return passParam1;
	}
	public void setPassParam1(String passParam1) {
		this.passParam1 = passParam1;
	}
	public String getPassParam2() {
		return passParam2;
	}
	public void setPassParam2(String passParam2) {
		this.passParam2 = passParam2;
	}
	public int getStar1() {
		return star1;
	}
	public void setStar1(int star1) {
		this.star1 = star1;
	}
	public int getStar2() {
		return star2;
	}
	public void setStar2(int star2) {
		this.star2 = star2;
	}
	public int getStarParam1() {
		return starParam1;
	}
	public void setStarParam1(int starParam1) {
		this.starParam1 = starParam1;
	}
	public int getStarParam2() {
		return starParam2;
	}
	public void setStarParam2(int starParam2) {
		this.starParam2 = starParam2;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getGoodsId1() {
		return goodsId1;
	}
	public void setGoodsId1(int goodsId1) {
		this.goodsId1 = goodsId1;
	}
	public int getNum1() {
		return num1;
	}
	public void setNum1(int num1) {
		this.num1 = num1;
	}
	public int getGoodsId2() {
		return goodsId2;
	}
	public void setGoodsId2(int goodsId2) {
		this.goodsId2 = goodsId2;
	}
	public int getNum2() {
		return num2;
	}
	public void setNum2(int num2) {
		this.num2 = num2;
	}
	public int getGoodsId3() {
		return goodsId3;
	}
	public void setGoodsId3(int goodsId3) {
		this.goodsId3 = goodsId3;
	}
	public int getNum3() {
		return num3;
	}
	public void setNum3(int num3) {
		this.num3 = num3;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	public int getDeathCount() {
		return deathCount;
	}
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}
	public int getReviveGold() {
		return reviveGold;
	}
	public void setReviveGold(int reviveGold) {
		this.reviveGold = reviveGold;
	}
	public int getReviveTime() {
		return reviveTime;
	}
	public void setReviveTime(int reviveTime) {
		this.reviveTime = reviveTime;
	}
	public String getPassParam3() {
		return passParam3;
	}
	public void setPassParam3(String passParam3) {
		this.passParam3 = passParam3;
	}
	public int getGateId() {
		return gateId;
	}
	public void setGateId(int gateId) {
		this.gateId = gateId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getGateUnlockId() {
		return gateUnlockId;
	}
	public void setGateUnlockId(int gateUnlockId) {
		this.gateUnlockId = gateUnlockId;
	}
	public int getUnlockId() {
		return unlockId;
	}
	public void setUnlockId(int unlockId) {
		this.unlockId = unlockId;
	}
	public int getFiron() {
		return firon;
	}
	public void setFiron(int firon) {
		this.firon = firon;
	}
	public int getFgoodsId1() {
		return fgoodsId1;
	}
	public void setFgoodsId1(int fgoodsId1) {
		this.fgoodsId1 = fgoodsId1;
	}
	public int getFnum1() {
		return fnum1;
	}
	public void setFnum1(int fnum1) {
		this.fnum1 = fnum1;
	}
	public int getFgoodsId2() {
		return fgoodsId2;
	}
	public void setFgoodsId2(int fgoodsId2) {
		this.fgoodsId2 = fgoodsId2;
	}
	public int getFnum2() {
		return fnum2;
	}
	public void setFnum2(int fnum2) {
		this.fnum2 = fnum2;
	}
	public int getFgoodsId3() {
		return fgoodsId3;
	}
	public void setFgoodsId3(int fgoodsId3) {
		this.fgoodsId3 = fgoodsId3;
	}
	public int getFnum3() {
		return fnum3;
	}
	public void setFnum3(int fnum3) {
		this.fnum3 = fnum3;
	}
	public int getWarId() {
		return warId;
	}
	public void setWarId(int warId) {
		this.warId = warId;
	}
	public int getHard() {
		return hard;
	}
	public void setHard(int hard) {
		this.hard = hard;
	}
	public float getDir() {
		return dir;
	}
	public void setDir(float dir) {
		this.dir = dir;
	}
}
