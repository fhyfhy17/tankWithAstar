package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 幸福转盘
 * 
 * @author Administrator
 *
 */
public class LuckyWheelDiamondTemplate implements KeySupport<Integer> {

	private int id; // id
	private int diamond;//

	public void init() {
		// if (!"".equals(itemId)) {
		// String[] s = itemId.split(";");
		//
		// for (int i = 0; i < s.length; i++) {
		// String[] ss = s[i].split(",");
		// int[] a = new int[2];
		// a[0] = Integer.parseInt(ss[0]);
		// a[1] = Integer.parseInt(ss[1]);
		// itemList.add(a);
		// }
		//
		// }
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}
