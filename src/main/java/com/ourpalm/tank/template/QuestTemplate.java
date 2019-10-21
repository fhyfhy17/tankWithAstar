package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.KeySupport;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class QuestTemplate implements KeySupport<Integer>{
	private int id_i;
	private int condition;
	private String param1;
	private String param2;
	private int limit_i;
	private String title_s;

	private int exp_i;
	private int gold_i;
	private int iron_i;
	private int diamond;
	private int active;
	
	private int goodsId1_i;	
	private int num1_i;
	private int goodsId2_i;
	private int num2_i;
	private int goodsId3_i;
	private int num3_i;
	private int goodsId4_i;
	private int num4_i;
	private int goodsId5_i;
	private int num5_i;
	private List<Set<Integer>> paramList = new ArrayList<>();
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	
	public void init(){
		put(goodsId1_i, num1_i, goodsMap);
		put(goodsId2_i, num2_i, goodsMap);
		put(goodsId3_i, num3_i, goodsMap);
		put(goodsId4_i, num4_i, goodsMap);
		put(goodsId5_i, num5_i, goodsMap);
		
		add(RoleAttr.gold, gold_i, attrList);
		add(RoleAttr.exp, exp_i, attrList);
		add(RoleAttr.iron, iron_i, attrList);
		add(RoleAttr.diamonds, diamond, attrList);

		paramList.add(split(param1));
		paramList.add(split(param2));
	}
	
	private void put(int key, int value, Map<Integer, Integer> map){
		if(key <= 0 || value <= 0){
			return;
		}
		
		if(map.containsKey(key)){
			value += map.get(key);
		}
		map.put(key, value);
	}
	
	private Set<Integer> split(String param){
		if(Util.isEmpty(param) || "0".equalsIgnoreCase(param)){
			return new HashSet<>();
		}
		
		Set<Integer> result = new HashSet<>();
		try{
			String[] params = param.split(Cat.comma);
			for(String paramStr : params){
				result.add(Integer.valueOf(paramStr));
			}
			return result;
		}catch(Exception e){
			String errorStr = String.format("任务条件错误, questId=%d, param=%s", id_i, param);
			LogCore.startup.error(errorStr, e);
			throw new IllegalArgumentException(errorStr, e);
		}
	}
	
	
	
	private void add(RoleAttr type, int value, List<AttrUnit> list){
		if(value <= 0){
			return;
		}
		list.add(AttrUnit.build(type, Operation.add, value));
	}
	
	
	public List<AttrUnit> getAttrList() {
		return new ArrayList<AttrUnit>(attrList);
	}
	
	public Map<Integer, Integer> getGoodsMap(){
		return new HashMap<>(this.goodsMap);
	}
	
	public boolean isMatched(int conditionType, int... params) {
		if (this.condition != conditionType) {
			return false;
		}
		int index = 0;
		for(Set<Integer> paramSet : paramList){
			if(Util.isEmpty(paramSet)){
				index++;
				continue;
			}
			
			//任务有限定条件，但是限定条件列表又不包括传入的参数条件
			if(index >= params.length || !paramSet.contains(params[index++])) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Integer getKey(){
		return this.id_i;
	}

	public int getId_i() {
		return id_i;
	}

	public void setId_i(int id_i) {
		this.id_i = id_i;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public int getLimit_i() {
		return limit_i;
	}

	public void setLimit_i(int limit_i) {
		this.limit_i = limit_i;
	}

	public int getExp_i() {
		return exp_i;
	}

	public void setExp_i(int exp_i) {
		this.exp_i = exp_i;
	}

	public int getGold_i() {
		return gold_i;
	}

	public void setGold_i(int gold_i) {
		this.gold_i = gold_i;
	}

	public int getGoodsId1_i() {
		return goodsId1_i;
	}

	public void setGoodsId1_i(int goodsId1_i) {
		this.goodsId1_i = goodsId1_i;
	}

	public int getNum1_i() {
		return num1_i;
	}

	public void setNum1_i(int num1_i) {
		this.num1_i = num1_i;
	}

	public int getGoodsId2_i() {
		return goodsId2_i;
	}

	public void setGoodsId2_i(int goodsId2_i) {
		this.goodsId2_i = goodsId2_i;
	}

	public int getNum2_i() {
		return num2_i;
	}

	public void setNum2_i(int num2_i) {
		this.num2_i = num2_i;
	}

	public int getGoodsId3_i() {
		return goodsId3_i;
	}

	public void setGoodsId3_i(int goodsId3_i) {
		this.goodsId3_i = goodsId3_i;
	}

	public int getNum3_i() {
		return num3_i;
	}

	public void setNum3_i(int num3_i) {
		this.num3_i = num3_i;
	}

	public int getGoodsId4_i() {
		return goodsId4_i;
	}

	public void setGoodsId4_i(int goodsId4_i) {
		this.goodsId4_i = goodsId4_i;
	}

	public int getNum4_i() {
		return num4_i;
	}

	public void setNum4_i(int num4_i) {
		this.num4_i = num4_i;
	}

	public int getGoodsId5_i() {
		return goodsId5_i;
	}

	public void setGoodsId5_i(int goodsId5_i) {
		this.goodsId5_i = goodsId5_i;
	}

	public int getNum5_i() {
		return num5_i;
	}

	public void setNum5_i(int num5_i) {
		this.num5_i = num5_i;
	}

	public int getIron_i() {
		return iron_i;
	}

	public void setIron_i(int iron_i) {
		this.iron_i = iron_i;
	}

	public String getTitle_s() {
		return title_s;
	}

	public void setTitle_s(String title_s) {
		this.title_s = title_s;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
	
}