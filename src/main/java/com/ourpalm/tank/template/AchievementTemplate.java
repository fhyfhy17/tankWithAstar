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

public class AchievementTemplate implements KeySupport<Integer>{
	private int id_i;
	private int condition;
	private String param1;
	private String param2;
	private int group;
	private int limit_i;
	private String title_s;
	private int star_i;
	private int exp_i;
	private int gold_i;
	private int iron_i;
	private int diamond;
	private int goodsId1_i;	
	private int num1_i;
	private List<Set<Integer>> paramList = new ArrayList<>();
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	
	
	public void init(){
		put(goodsId1_i, num1_i, goodsMap);
		
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

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
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

	public int getStar_i() {
		return star_i;
	}

	public void setStar_i(int star_i) {
		this.star_i = star_i;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
}
