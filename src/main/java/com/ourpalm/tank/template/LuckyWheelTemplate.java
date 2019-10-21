package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 幸福转盘
 * 
 * @author Administrator
 *
 */
public class LuckyWheelTemplate implements KeySupport<Integer> {

	private int id; // id
	private int rate;// 免费概率
	private String item;
	private int[] itemi = new int[2];
	private int diamondRate;// 钻石概率

	public void init() {
		if (!"".equals(item)) {
			String[] s = item.split(",");
			itemi[0] = Integer.parseInt(s[0]);
			itemi[1] = Integer.parseInt(s[1]);
			// for (int i = 0; i < s.length; i++) {
			// String[] ss = s[i].split(",");
			// int[] a = new int[2];
			// a[0] = Integer.parseInt(ss[0]);
			// a[1] = Integer.parseInt(ss[1]);
			// itemList.add(a);
			// }

		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int[] getItemi() {
		return itemi;
	}

	public void setItemi(int[] itemi) {
		this.itemi = itemi;
	}

	@Override
	public Integer getKey() {
		return id;
	}

	public int getDiamondRate() {
		return diamondRate;
	}

	public void setDiamondRate(int diamondRate) {
		this.diamondRate = diamondRate;
	}

}
