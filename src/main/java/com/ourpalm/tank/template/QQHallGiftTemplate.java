package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.KeySupport;

/**
 * QQ大厅奖励物品表
 * 
 * @author Administrator
 *
 */
public class QQHallGiftTemplate implements KeySupport<Integer> {

	private int id; // id
	private int type;// 礼品类型
	private String itemId;// 物品ID
	private int roleLevel;// 角色等级
	private List<int[]> itemList = new ArrayList<>();

	public void init() {
		if (!"".equals(itemId)) {
			String[] s = itemId.split(";");
		
			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[2];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				itemList.add(a);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public List<int[]> getItemList() {
		return itemList;
	}

	public void setItemList(List<int[]> itemList) {
		this.itemList = itemList;
	}

	public int getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}
