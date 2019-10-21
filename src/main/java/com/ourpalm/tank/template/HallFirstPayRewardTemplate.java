package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class HallFirstPayRewardTemplate {

	private int showDay;
	private int gold;
	private int iron;
	private int diamond;
	private int honor;
	private int exp;
	private int tankExp;
	private int tankId;
	
	private int goods;
	private int num;
	
	private Map<Integer, Integer> goodsMap = new LinkedHashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	
	public void init() {
		putGoods(goods, num, goodsMap);
		
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.exp, exp, attrList);
		add(RoleAttr.iron, iron, attrList);
		add(RoleAttr.honor, honor, attrList);
		add(RoleAttr.diamonds, diamond, attrList);
		add(RoleAttr.tankExp, tankExp, attrList);
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
			throw new NullPointerException("初始化战力比拼表失败，goodsId = " + goodsId);
		
		if(map.containsKey(goodsId)){
			num += map.get(goodsId);
		}
		map.put(goodsId, num);
	}
	
	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}

	public List<AttrUnit> getAttrList() {
		return attrList;
	}

	public int getShowDay() {
		return showDay;
	}

	public void setShowDay(int showDay) {
		this.showDay = showDay;
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
	public int getDiamond() {
		return diamond;
	}
	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
	public int getHonor() {
		return honor;
	}
	public void setHonor(int honor) {
		this.honor = honor;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getTankExp() {
		return tankExp;
	}
	public void setTankExp(int tankExp) {
		this.tankExp = tankExp;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
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
	
}
