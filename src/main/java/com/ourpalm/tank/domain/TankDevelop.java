package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 坦克研发
 * 
 * @author fhy
 *
 */
public class TankDevelop {
	private int roleId;// 角色ID
	private Map<Integer, Integer> developMap = new HashMap<>();// 研发列表
																// <tankTemplateId,flag>
																// flag 0未研发
																// 1可研发 2可购买
																// 3已购买

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public Map<Integer, Integer> getDevelopMap() {
		return developMap;
	}

	public void setDevelopMap(Map<Integer, Integer> developMap) {
		this.developMap = developMap;
	}

}
