package com.ourpalm.tank.template;

import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.KeySupport;

public class ActivityDailyTemplate implements KeySupport<Integer>{

	private int id;
	private String serviceId;
	private String name;
	private int type;		//1 :条目活动，2：图片活动
	private int flag;
	private String beginDate;
	private String endDate;
	private String timeDesc;
	private String desc;
	private int logicId;
	
	private long beginTime;
	private long endTime;
	
	private int sort;
	
	public void init() {
		if(type == 5)
			return;
		
		beginTime = DateUtil.convertYYYYMMDDStr2Millis(beginDate);
		endTime = DateUtil.convertYYYYMMDDStr2Millis(endDate);
		
		if(endTime <= beginTime)
			throw new IllegalArgumentException("初始化活动表，结束时间配置错误：" + endDate);
	}
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
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
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


	public int getFlag() {
		return flag;
	}


	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getServiceId() {
		return serviceId;
	}


	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


	public String getTimeDesc() {
		return timeDesc;
	}


	public void setTimeDesc(String timeDesc) {
		this.timeDesc = timeDesc;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public boolean isOpen() {
		if(type == 5)
			return true;
		
		long cur = System.currentTimeMillis();
		return beginTime <= cur && endTime > cur;
	}


	public int getLogicId() {
		return logicId;
	}


	public void setLogicId(int logicId) {
		this.logicId = logicId;
	}


	@Override
	public Integer getKey() {
		return id;
	}
	
	
}
