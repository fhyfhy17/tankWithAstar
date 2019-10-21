package com.ourpalm.tank.template;

import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.KeySupport;

public class ActivityTemplate implements KeySupport<Integer> {

	private int id;			//活动id
	private int logicId;	//服务器内部逻辑id
	private String serviceId;		//渠道
	private String name;	//名称
	private int type;		//类型	1.时间活动，2常驻活动
	private int flag;		//标志。		1: 新； 2：超值；3：火爆
	private String beginDate;	//开始时间
	private String endDate;		//结束时间
	private String timeDesc;	//时间描述
	private String desc;		//活动描述
	
	private long beginTime;		
	private long endTime;
	private int sort;	//	排序
	
	
	public void init() {
		if(type == 2)
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
		if(type == 2)
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
