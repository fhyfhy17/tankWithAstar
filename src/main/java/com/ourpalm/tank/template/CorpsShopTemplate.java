package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;

public class CorpsShopTemplate {

	private int level;
	private int random1;
	private int random2;
	private int random3;
	private int random4;
	private int random5;
	private int random6;
	private int random7;
	private int random8;
	private int random9;
	private int random10;
	
	private List<Integer> randomGroups = new ArrayList<>();
	
	public void init(){
		this.buildGroups(random1);
		this.buildGroups(random2);
		this.buildGroups(random3);
		this.buildGroups(random4);
		this.buildGroups(random5);
		this.buildGroups(random6);
		this.buildGroups(random7);
		this.buildGroups(random8);
		this.buildGroups(random9);
		this.buildGroups(random10);
	}
	
	private void buildGroups(int random){
		if(random <= 0){
			return ;
		}
		if(!GameContext.getCorpsApp().hadGoodsGroupExist(random)){
			LogCore.startup.error("军团商城 level = {} 物品组ID = {} 没有找到对应的物品组", level, random);
			return ;
		}
		randomGroups.add(random);
	}
	
	public List<Integer> getRandomGroups(){
		return this.randomGroups;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getRandom1() {
		return random1;
	}
	public void setRandom1(int random1) {
		this.random1 = random1;
	}
	public int getRandom2() {
		return random2;
	}
	public void setRandom2(int random2) {
		this.random2 = random2;
	}
	public int getRandom3() {
		return random3;
	}
	public void setRandom3(int random3) {
		this.random3 = random3;
	}
	public int getRandom4() {
		return random4;
	}
	public void setRandom4(int random4) {
		this.random4 = random4;
	}
	public int getRandom5() {
		return random5;
	}
	public void setRandom5(int random5) {
		this.random5 = random5;
	}
	public int getRandom6() {
		return random6;
	}
	public void setRandom6(int random6) {
		this.random6 = random6;
	}
	public int getRandom7() {
		return random7;
	}
	public void setRandom7(int random7) {
		this.random7 = random7;
	}
	public int getRandom8() {
		return random8;
	}
	public void setRandom8(int random8) {
		this.random8 = random8;
	}
	public int getRandom9() {
		return random9;
	}
	public void setRandom9(int random9) {
		this.random9 = random9;
	}
	public int getRandom10() {
		return random10;
	}
	public void setRandom10(int random10) {
		this.random10 = random10;
	}
}
