package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class VipTemplate {
	private int level;
	private int limit;
	private int gold;
	private int iron;
	private int honor;
	private int tankExp;
	private int goods1;
	private int num1;
	private int goods2;
	private int num2;
	private int goods3;
	private int num3;
	
	private float privilegeRoleTankExp;
	private float privilegeIron;
	private float privilegeIronMax;
	private float privilegeTankExp;
	private int privilegeAlive;
	
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	public void init() {
		putGoods(goods1, num1, goodsMap);
		putGoods(goods2, num2, goodsMap);
		putGoods(goods3, num3, goodsMap);
		
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.honor, honor, attrList);
		add(RoleAttr.iron, iron, attrList);
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
			throw new NullPointerException("初始化vip表失败，goodsId = " + goodsId);
		
		if(map.containsKey(goodsId)){
			num += map.get(goodsId);
		}
		map.put(goodsId, num);
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
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
	public int getHonor() {
		return honor;
	}
	public void setHonor(int honor) {
		this.honor = honor;
	}
	public int getTankExp() {
		return tankExp;
	}
	public void setTankExp(int tankExp) {
		this.tankExp = tankExp;
	}
	public int getGoods1() {
		return goods1;
	}
	public void setGoods1(int goods1) {
		this.goods1 = goods1;
	}
	public int getNum1() {
		return num1;
	}
	public void setNum1(int num1) {
		this.num1 = num1;
	}
	public int getGoods2() {
		return goods2;
	}
	public void setGoods2(int goods2) {
		this.goods2 = goods2;
	}
	public int getNum2() {
		return num2;
	}
	public void setNum2(int num2) {
		this.num2 = num2;
	}
	public int getGoods3() {
		return goods3;
	}
	public void setGoods3(int goods3) {
		this.goods3 = goods3;
	}
	public int getNum3() {
		return num3;
	}
	public void setNum3(int num3) {
		this.num3 = num3;
	}

	public float getPrivilegeIron() {
		return privilegeIron;
	}

	public void setPrivilegeIron(float privilegeIron) {
		this.privilegeIron = privilegeIron;
	}

	public float getPrivilegeIronMax() {
		return privilegeIronMax;
	}

	public void setPrivilegeIronMax(float privilegeIronMax) {
		this.privilegeIronMax = privilegeIronMax;
	}

	public float getPrivilegeTankExp() {
		return privilegeTankExp;
	}

	public void setPrivilegeTankExp(float privilegeTankExp) {
		this.privilegeTankExp = privilegeTankExp;
	}

	public int getPrivilegeAlive() {
		return privilegeAlive;
	}

	public void setPrivilegeAlive(int privilegeAlive) {
		this.privilegeAlive = privilegeAlive;
	}

	public float getPrivilegeRoleTankExp() {
		return privilegeRoleTankExp;
	}

	public void setPrivilegeRoleTankExp(float privilegeRoleTankExp) {
		this.privilegeRoleTankExp = privilegeRoleTankExp;
	}

	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}

	public List<AttrUnit> getAttrList() {
		return attrList;
	}
	
}
