package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class ActivityRedPacketDrawTemplate{
	private int id;
	private int gold;
	private int iron;
	private int tankExp;
	private int diamond;
	private int goods;
	private int num;
	
	private List<AttrUnit> attrList = new ArrayList<>();
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	public void init() {
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.iron, iron, attrList);
		add(RoleAttr.tankExp, tankExp, attrList);
		add(RoleAttr.diamonds, diamond, attrList);
		
		putGoods(goods, num, goodsMap);
	}
	
	private void add(RoleAttr type, int value, List<AttrUnit> list){
		if(value <= 0){
			return;
		}
		
		list.add(AttrUnit.build(type, Operation.add, value));
	}
	
	private void putGoods(int goodsId, int num, Map<Integer, Integer> map){
		if(goodsId <= 0 || num <= 0){
			return;
		}
		
		GoodsBaseTemplate t = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(t == null)
			throw new NullPointerException("初始化红包奖励表失败，goodsId = " + goodsId);
		
		if(map.containsKey(goodsId)){
			num += map.get(goodsId);
		}
		map.put(goodsId, num);
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getTankExp() {
		return tankExp;
	}
	public void setTankExp(int tankExp) {
		this.tankExp = tankExp;
	}
	public int getGoods() {
		return goods;
	}
	public void setGoods(int goods) {
		this.goods = goods;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

	public List<AttrUnit> getAttrList() {
		return attrList;
	}

	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}

}
