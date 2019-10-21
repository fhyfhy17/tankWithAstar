package com.ourpalm.tank.domain;

import java.util.Date;

/**
 * 排位赛赛季对象
 * 
 * @author wangkun
 *
 */
public class SeasonMatch {

	private int id;				//赛季ID
	private Date beginTime;		//赛季开始时间
	private Date endTime;		//赛季结束时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
