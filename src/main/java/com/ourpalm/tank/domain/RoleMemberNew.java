package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

public class RoleMemberNew {
	private String uniqueId;// 乘员唯一ID
	private int roleId;// 角色ID
	private int templateId;// 模板ID
	private int level;// 乘员等级
	private int exp;// 乘员经验
	private boolean active;// 是否上阵
	private int star;// 星级
	private List<Integer> attr = new ArrayList<>();// 拥有的属性列表

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public List<Integer> getAttr() {
		return attr;
	}

	public void setAttr(List<Integer> attr) {
		this.attr = attr;
	}

}
