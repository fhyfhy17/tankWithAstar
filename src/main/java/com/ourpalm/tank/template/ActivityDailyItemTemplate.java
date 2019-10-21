package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class ActivityDailyItemTemplate implements Comparable<ActivityDailyItemTemplate>{

	private int id;
	private String name;
	private int toPage;
	private String desc;
	private int gold;
	private int iron;
	private int exp;
	private int honor;
	private int goods1;
	private int num1;
	private int goods2;
	private int num2;
	private int goods3;
	private int num3;
	private int goods4;
	private int num4;
	
	private Map<Integer, Integer> goodsMap = new LinkedHashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	public void init() {
		putGoods(goods1, num1, goodsMap);
		putGoods(goods2, num2, goodsMap);
		putGoods(goods3, num3, goodsMap);
		putGoods(goods4, num4, goodsMap);
		
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.exp, exp, attrList);
		add(RoleAttr.iron, iron, attrList);
		add(RoleAttr.honor, honor, attrList);
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
	
	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
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

	public int getGoods4() {
		return goods4;
	}

	public void setGoods4(int goods4) {
		this.goods4 = goods4;
	}

	public int getNum4() {
		return num4;
	}

	public void setNum4(int num4) {
		this.num4 = num4;
	}

	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}

	public List<AttrUnit> getAttrList() {
		return attrList;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getToPage() {
		return toPage;
	}

	public void setToPage(int toPage) {
		this.toPage = toPage;
	}

	@Override
	public int compareTo(ActivityDailyItemTemplate o) {
		return id >= o.getId() ? 1 : -1;
	}
	
}
