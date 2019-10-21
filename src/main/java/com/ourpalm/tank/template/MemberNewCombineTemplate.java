package com.ourpalm.tank.template;

import java.util.HashSet;
import java.util.Set;

import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.KeySupport;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.vo.CombineNumIdInfo;

/**
 * 新成员属性组合
 * 
 * @author fhy
 *
 */
public class MemberNewCombineTemplate implements KeySupport<Integer> {

	private int id;
	private String name;
	private String members;
	private String all;
	private String half;
	private String littleHalf;

	private CombineNumIdInfo allInfo = new CombineNumIdInfo(); // 全套
	private CombineNumIdInfo halfInfo = new CombineNumIdInfo();// 半套
	private CombineNumIdInfo littleHalfInfo = new CombineNumIdInfo();// 小半套

	private Set<Integer> combineSet = new HashSet<>();

	public void init() {
		if (!Util.isEmpty(members)) {
			String[] s = members.split(Cat.comma);
			for (int i = 0; i < s.length; i++) {
				combineSet.add(Integer.parseInt(s[i]));
			}
		}
		if (!Util.isEmpty(all)) {
			allInfo.setNum(Integer.parseInt(all.split(Cat.comma)[0]));
			allInfo.setId(Integer.parseInt(all.split(Cat.comma)[1]));
		}
		if (!Util.isEmpty(half)) {
			halfInfo.setNum(Integer.parseInt(half.split(Cat.comma)[0]));
			halfInfo.setId(Integer.parseInt(all.split(Cat.comma)[1]));
		}
		if (!Util.isEmpty(littleHalf)) {
			littleHalfInfo.setNum(Integer.parseInt(littleHalf.split(Cat.comma)[0]));
			littleHalfInfo.setId(Integer.parseInt(all.split(Cat.comma)[1]));
		}
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

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getAll() {
		return all;
	}

	public void setAll(String all) {
		this.all = all;
	}

	public String getHalf() {
		return half;
	}

	public void setHalf(String half) {
		this.half = half;
	}

	public String getLittleHalf() {
		return littleHalf;
	}

	public void setLittleHalf(String littleHalf) {
		this.littleHalf = littleHalf;
	}

	@Override
	public Integer getKey() {
		return id;
	}

	public Set<Integer> getCombineSet() {
		return combineSet;
	}

	public void setCombineSet(Set<Integer> combineSet) {
		this.combineSet = combineSet;
	}

	public CombineNumIdInfo getAllInfo() {
		return allInfo;
	}

	public void setAllInfo(CombineNumIdInfo allInfo) {
		this.allInfo = allInfo;
	}

	public CombineNumIdInfo getHalfInfo() {
		return halfInfo;
	}

	public void setHalfInfo(CombineNumIdInfo halfInfo) {
		this.halfInfo = halfInfo;
	}

	public CombineNumIdInfo getLittleHalfInfo() {
		return littleHalfInfo;
	}

	public void setLittleHalfInfo(CombineNumIdInfo littleHalfInfo) {
		this.littleHalfInfo = littleHalfInfo;
	}

}