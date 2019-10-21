package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

public class VipShopGroupTemplate {

	private int vipLevel;
	private int group1Goods;
	private int group2Goods;
	private int group3Goods;
	private int group4Goods;
	private int group5Goods;
	private int group6Goods;
	private int group7Goods;
	private int group8Goods;
	
	private List<Integer> groups = new ArrayList<>();
	public void init() {
		groups.add(group1Goods);
		groups.add(group2Goods);
		groups.add(group3Goods);
		groups.add(group4Goods);
		groups.add(group5Goods);
		groups.add(group6Goods);
		groups.add(group7Goods);
		groups.add(group8Goods);
	}
	
	public List<Integer> getGroups() {
		return groups;
	}

	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	public int getGroup1Goods() {
		return group1Goods;
	}
	public void setGroup1Goods(int group1Goods) {
		this.group1Goods = group1Goods;
	}
	public int getGroup2Goods() {
		return group2Goods;
	}
	public void setGroup2Goods(int group2Goods) {
		this.group2Goods = group2Goods;
	}
	public int getGroup3Goods() {
		return group3Goods;
	}
	public void setGroup3Goods(int group3Goods) {
		this.group3Goods = group3Goods;
	}
	public int getGroup4Goods() {
		return group4Goods;
	}
	public void setGroup4Goods(int group4Goods) {
		this.group4Goods = group4Goods;
	}
	public int getGroup5Goods() {
		return group5Goods;
	}
	public void setGroup5Goods(int group5Goods) {
		this.group5Goods = group5Goods;
	}
	public int getGroup6Goods() {
		return group6Goods;
	}
	public void setGroup6Goods(int group6Goods) {
		this.group6Goods = group6Goods;
	}
	public int getGroup7Goods() {
		return group7Goods;
	}
	public void setGroup7Goods(int group7Goods) {
		this.group7Goods = group7Goods;
	}
	public int getGroup8Goods() {
		return group8Goods;
	}
	public void setGroup8Goods(int group8Goods) {
		this.group8Goods = group8Goods;
	}
	
}
