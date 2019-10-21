package com.ourpalm.tank.domain;

/**
 * 战役记录
 * 
 * @author wangkun
 *
 */
public class RoleCamp {

	private int roleId;		//角色ID
	private int id;			//战场id
	private int star;		//获得星级
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
}
