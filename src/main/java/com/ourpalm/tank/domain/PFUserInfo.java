package com.ourpalm.tank.domain;

public class PFUserInfo {

	private int is_blue_vip; // 是否蓝钻， 1表示是，0表示不是
	private int is_blue_year_vip; // int 是否年费蓝钻；1表示是，0表示不是
	private int is_super_blue_vip;// 是否豪华蓝钻；1表示是，0表示不是
	private int is_expand_blue_vip;// 是否超级蓝钻；1表示是，0表示不是
	private int blue_vip_level;// 蓝钻等级
	private int is_have_growth;// 蓝钻是否具备成长值，1表示是，0表示不是
	private int is_mobile_blue_vip;// 是否手机蓝钻；1表示是，0表示不是
	
	private long server_time;// 服务器时间，用于比较蓝钻开通时间和到期时间，unix时间戳，单位为秒
	private long vip_reg_time;// 蓝钻开通时间，unix时间戳，单位为秒
	private long year_vip_reg_time;// 年费蓝钻开通时间，unix时间戳，单位为秒
	private long super_vip_reg_time;// 豪华蓝钻开通时间，unix时间戳，单位为秒
	private long expand_vip_reg_time;// 超级蓝钻开通时间，unix时间戳，单位为秒
	private long vip_valid_time;// 蓝钻到期时间，unix时间戳，单位为秒
	private long year_vip_valid_time;// 年费蓝钻到期时间，unix时间戳，单位为秒
	private long super_vip_valid_time;// 豪华蓝钻到期时间，unix时间戳，单位为秒
	private long expand_vip_valid_time;// 超级蓝钻到期时间，unix时间戳，单位为秒
	
	private String nickname = "";

	public int getIs_blue_vip() {
		return is_blue_vip;
	}

	public void setIs_blue_vip(int is_blue_vip) {
		this.is_blue_vip = is_blue_vip;
	}

	public int getIs_blue_year_vip() {
		return is_blue_year_vip;
	}

	public void setIs_blue_year_vip(int is_blue_year_vip) {
		this.is_blue_year_vip = is_blue_year_vip;
	}

	public int getIs_super_blue_vip() {
		return is_super_blue_vip;
	}

	public void setIs_super_blue_vip(int is_super_blue_vip) {
		this.is_super_blue_vip = is_super_blue_vip;
	}

	public int getIs_expand_blue_vip() {
		return is_expand_blue_vip;
	}

	public void setIs_expand_blue_vip(int is_expand_blue_vip) {
		this.is_expand_blue_vip = is_expand_blue_vip;
	}

	public int getBlue_vip_level() {
		return blue_vip_level;
	}

	public void setBlue_vip_level(int blue_vip_level) {
		this.blue_vip_level = blue_vip_level;
	}

	public int getIs_have_growth() {
		return is_have_growth;
	}

	public void setIs_have_growth(int is_have_growth) {
		this.is_have_growth = is_have_growth;
	}

	public int getIs_mobile_blue_vip() {
		return is_mobile_blue_vip;
	}

	public void setIs_mobile_blue_vip(int is_mobile_blue_vip) {
		this.is_mobile_blue_vip = is_mobile_blue_vip;
	}

	public long getServer_time() {
		return server_time;
	}

	public void setServer_time(long server_time) {
		this.server_time = server_time;
	}

	public long getVip_reg_time() {
		return vip_reg_time;
	}

	public void setVip_reg_time(long vip_reg_time) {
		this.vip_reg_time = vip_reg_time;
	}

	public long getYear_vip_reg_time() {
		return year_vip_reg_time;
	}

	public void setYear_vip_reg_time(long year_vip_reg_time) {
		this.year_vip_reg_time = year_vip_reg_time;
	}

	public long getSuper_vip_reg_time() {
		return super_vip_reg_time;
	}

	public void setSuper_vip_reg_time(long super_vip_reg_time) {
		this.super_vip_reg_time = super_vip_reg_time;
	}

	public long getExpand_vip_reg_time() {
		return expand_vip_reg_time;
	}

	public void setExpand_vip_reg_time(long expand_vip_reg_time) {
		this.expand_vip_reg_time = expand_vip_reg_time;
	}

	public long getVip_valid_time() {
		return vip_valid_time;
	}

	public void setVip_valid_time(long vip_valid_time) {
		this.vip_valid_time = vip_valid_time;
	}

	public long getYear_vip_valid_time() {
		return year_vip_valid_time;
	}

	public void setYear_vip_valid_time(long year_vip_valid_time) {
		this.year_vip_valid_time = year_vip_valid_time;
	}

	public long getSuper_vip_valid_time() {
		return super_vip_valid_time;
	}

	public void setSuper_vip_valid_time(long super_vip_valid_time) {
		this.super_vip_valid_time = super_vip_valid_time;
	}

	public long getExpand_vip_valid_time() {
		return expand_vip_valid_time;
	}

	public void setExpand_vip_valid_time(long expand_vip_valid_time) {
		this.expand_vip_valid_time = expand_vip_valid_time;
	}

	public int getIs_valid() {
		int curTime = (int)(System.currentTimeMillis() / 1000);
		if( vip_valid_time != 0 && curTime < vip_valid_time ) {
			return 1;
		} else if( year_vip_valid_time != 0 && curTime < year_vip_valid_time ){
			return 1;
		} else if( super_vip_valid_time != 0 && curTime < super_vip_valid_time ){
			return 1;
		} else if( expand_vip_valid_time != 0 && curTime < expand_vip_valid_time ){
			return 1;
		}
		return 0;
	}

	public void setIs_valid(int is_valid) {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	
	// is_blue_vip
	// int
	// 是否蓝钻， 1表示是，0表示不是
	// is_blue_year_vip
	// int 是否年费蓝钻；1表示是，0表示不是
	// is_super_blue_vip
	// int
	// 是否豪华蓝钻；1表示是，0表示不是
	// is_expand_blue_vip
	// int
	// 是否超级蓝钻；1表示是，0表示不是
	// blue_vip_level
	// int
	// 蓝钻等级
	// is_have_growth
	// int
	// 蓝钻是否具备成长值，1表示是，0表示不是
	// is_mobile_blue_vip
	// int
	// 是否手机蓝钻；1表示是，0表示不是
	// server_time
	// int
	// 服务器时间，用于比较蓝钻开通时间和到期时间，unix时间戳，单位为秒
	// vip_reg_time
	// int
	// 蓝钻开通时间，unix时间戳，单位为秒
	// year_vip_reg_time
	// int
	// 年费蓝钻开通时间，unix时间戳，单位为秒
	// super_vip_reg_time
	// int
	// 豪华蓝钻开通时间，unix时间戳，单位为秒
	// expand_vip_reg_time
	// int
	// 超级蓝钻开通时间，unix时间戳，单位为秒
	// vip_valid_time
	// int
	// 蓝钻到期时间，unix时间戳，单位为秒
	// year_vip_valid_time
	// int
	// 年费蓝钻到期时间，unix时间戳，单位为秒
	// super_vip_valid_time
	// int
	// 豪华蓝钻到期时间，unix时间戳，单位为秒
	// expand_vip_valid_time
	// int
	// 超级蓝钻到期时间，unix时间戳，单位为秒
	// =======================================

	// private int vip = 0;// 0不是VIP 1蓝钻 2 年费 3豪华4 超级
	// private int level;// 蓝钻等级
	// private int growth;// 蓝钻是否具备成长值，1表示是，0表示不是
	// private int isMobileBlueVip;// 是否手机蓝钻；1表示是，0表示不是
	// private long endTime;// 到期时间
	// private long startTime;// 开通时间
	// private int lastVipEndTime;// 上一个蓝钻到期的时间
	// private int lastEndVipType;// 上一个到期的VIP类型
	// private int isBeforeNotice;// 是否已经有过提前三天通知
	// private int isAfterNotice;// 是否已经有过结束三天通知

}
