package com.ourpalm.tank.domain;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;

public class UserAttr {
	private final static Logger logger = LogCore.runtime;
	private int roleId;
	private int exp;			//经验
	private int gold;			//金币
	private int diamonds;		//钻石
	private int iron;			//铁块
	private int honor;			//荣誉
	private int tankExp;		//坦克全局经验
	private transient Set<RoleAttr> changeAttr = new HashSet<>();


	public int get(RoleAttr type){
		switch(type){
			case exp		: return this.exp;
			case gold 		: return this.gold;
			case diamonds	: return this.diamonds;
			case iron		: return this.iron;
			case honor		: return this.honor;
			case tankExp	: return this.tankExp;
			default			: throw new IllegalArgumentException("属性类型不存在 type = " + type);
		}
	}

	public void set(RoleAttr type, int value){
		int old = get(type);
		switch(type){
			case exp		: this.exp 		= value; break;
			case gold 		: this.gold		= value; break;
			case diamonds	: this.diamonds = value; break;
			case iron		: this.iron		= value; break;
			case honor		: this.honor 	= value; break;
			case tankExp	: this.tankExp 	= value; break;
		default: throw new IllegalArgumentException("属性类型不存在 type = " + type);
		}
		changeAttr.add(type);

		if(logger.isDebugEnabled()){
			logger.debug("{} {}-->{}", type, old, get(type));
		}
	}

	public Set<RoleAttr> changeAttrs() {
		return this.changeAttr;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getDiamonds() {
		return diamonds;
	}

	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
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
}
