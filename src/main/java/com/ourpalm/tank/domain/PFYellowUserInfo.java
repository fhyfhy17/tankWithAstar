package com.ourpalm.tank.domain;

public class PFYellowUserInfo {

	private int is_yellow_vip; // 是否为黄钻用户（0：不是； 1：是）
	private int is_yellow_year_vip; // 是否为年费黄钻用户（0：不是； 1：是）
	private int yellow_vip_level;// 黄钻等级。目前最高级别为黄钻8级（如果是黄钻用户才返回此字段）
	private int is_yellow_high_vip; // 是否为豪华版黄钻用户（0：不是； 1：是）。
	private int yellow_vip_pay_way;// 用户的付费类型。
	
//	is_yellow_vip	是否为黄钻用户（0：不是； 1：是）
//	is_yellow_year_vip	是否为年费黄钻用户（0：不是； 1：是）
//	yellow_vip_level	黄钻等级。目前最高级别为黄钻8级（如果是黄钻用户才返回此字段）
//	is_yellow_high_vip	是否为豪华版黄钻用户（0：不是； 1：是）。
//	（当pf=qzone、pengyou或qplus时返回）
//	yellow_vip_pay_way	用户的付费类型。
//	0：非预付费用户（先开通业务后付费，一般指通过手机开通黄钻的用户）；
//	1：预付费用户（先付费后开通业务，一般指通过Q币Q点、财付通或网银付费开通黄钻的用户）。
	
	public int getIs_yellow_vip() {
		return is_yellow_vip;
	}
	public void setIs_yellow_vip(int is_yellow_vip) {
		this.is_yellow_vip = is_yellow_vip;
	}
	public int getIs_yellow_year_vip() {
		return is_yellow_year_vip;
	}
	public void setIs_yellow_year_vip(int is_yellow_year_vip) {
		this.is_yellow_year_vip = is_yellow_year_vip;
	}
	public int getYellow_vip_level() {
		return yellow_vip_level;
	}
	public void setYellow_vip_level(int yellow_vip_level) {
		this.yellow_vip_level = yellow_vip_level;
	}
	public int getIs_yellow_high_vip() {
		return is_yellow_high_vip;
	}
	public void setIs_yellow_high_vip(int is_yellow_high_vip) {
		this.is_yellow_high_vip = is_yellow_high_vip;
	}
	public int getYellow_vip_pay_way() {
		return yellow_vip_pay_way;
	}
	public void setYellow_vip_pay_way(int yellow_vip_pay_way) {
		this.yellow_vip_pay_way = yellow_vip_pay_way;
	}
	
}
