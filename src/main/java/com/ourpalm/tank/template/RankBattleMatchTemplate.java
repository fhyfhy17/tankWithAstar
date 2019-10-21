package com.ourpalm.tank.template;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ourpalm.core.log.LogCore;

/**
 * 军衔排行赛季配置
 * @author wangkun
 *
 */
public class RankBattleMatchTemplate{
	
	private int id;				//序号
	private String tips;		//赛季结束说明
	private String endTime;		//赛季结束日期
	
	private Date endDate;	
	
	
	public void init(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			endDate = sdf.parse(endTime);
		}catch(Exception e){
			LogCore.startup.error("解析RankBattleMatchTemplate id = {} endTime = {} 时间格式错误", id, endTime);
		}
	}
	
	
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Date getEndDate(){
		return this.endDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
