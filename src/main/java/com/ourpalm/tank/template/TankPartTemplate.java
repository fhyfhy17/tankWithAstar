package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class TankPartTemplate extends AbstractAttribute{
	
	private int icon_i;			//icon
	private int quality_i;		//品质
	private int nextId_i;		//升级下一级配件ID
	
	private int iron_i;			//铁块
	private int goods1_i;
	private int num1_i;
	private int upExp;
	
	
	public Map<Integer, Integer> consumeMap = new HashMap<>();
	
	
	public void init(){
		super.init();
		this.buildMap(goods1_i, num1_i);
	}
	private void buildMap(int goodsId, int num){
		if(goodsId <= 0 || num <= 0){
			return ;
		}
		if(consumeMap.containsKey(goodsId)){
			int count = consumeMap.get(goodsId) + num;
			consumeMap.put(goodsId, count);
			return ;
		}
		consumeMap.put(goodsId, num);
	}
	
	public Map<Integer, Integer> getConsumeMap(){
		return new HashMap<>(consumeMap);
	}
	
	public int getIcon_i() {
		return icon_i;
	}
	public void setIcon_i(int icon_i) {
		this.icon_i = icon_i;
	}
	public int getQuality_i() {
		return quality_i;
	}
	public void setQuality_i(int quality_i) {
		this.quality_i = quality_i;
	}
	public int getNextId_i() {
		return nextId_i;
	}
	public void setNextId_i(int nextId_i) {
		this.nextId_i = nextId_i;
	}
	public int getGoods1_i() {
		return goods1_i;
	}
	public void setGoods1_i(int goods1_i) {
		this.goods1_i = goods1_i;
	}
	public int getNum1_i() {
		return num1_i;
	}
	public void setNum1_i(int num1_i) {
		this.num1_i = num1_i;
	}
	public int getIron_i() {
		return iron_i;
	}
	public void setIron_i(int iron_i) {
		this.iron_i = iron_i;
	}

	public int getUpExp() {
		return upExp;
	}

	public void setUpExp(int upExp) {
		this.upExp = upExp;
	}

	
	
}
