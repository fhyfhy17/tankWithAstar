package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class ExtraIncomeTextTemplate implements KeySupport<Integer> {
	private int type;
	private String mainText;
	private String subText1;
	private String subText2;

	@Override
	public Integer getKey() {
		return this.type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public String getSubText1() {
		return subText1;
	}

	public void setSubText1(String subText1) {
		this.subText1 = subText1;
	}

	public String getSubText2() {
		return subText2;
	}

	public void setSubText2(String subText2) {
		this.subText2 = subText2;
	}

}
