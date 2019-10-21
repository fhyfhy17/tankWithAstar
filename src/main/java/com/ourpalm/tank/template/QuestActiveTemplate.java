package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class QuestActiveTemplate implements Comparable<QuestActiveTemplate> {

	private int type;
	private int active;
	private String name;
	private int gold;
	private int iron;
	private int exp;
	private int diamond;
	
	private List<AttrUnit> attrList = new ArrayList<>();
	
	public void init(){
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.exp, exp, attrList);
		add(RoleAttr.iron, iron, attrList);
		add(RoleAttr.diamonds, diamond, attrList);
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
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
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
	@Override
	public int compareTo(QuestActiveTemplate o) {
		return this.active - o.getActive();
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
}
