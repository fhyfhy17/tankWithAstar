package com.ourpalm.tank.app.dblog;

import com.jfinal.plugin.activerecord.Record;

public abstract class DBLog  {
	
	// 日志主ID
	private int logId;
	
	// 日志子ID
	private int logSubId;
	
	private long actionTime;

	private int dbIndex;
	
	private int tableType;
	
	private Record record = new Record();
	
	/**
	 * 添加日志数据到日志集合
	 * @param key 
	 * @param value
	 */
	
	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getLogSubId() {
		return logSubId;
	}

	public void setLogSubId(int logSubId) {
		this.logSubId = logSubId;
	}

	public long getActionTime() {
		return actionTime;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

	public int getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	public int getTableType() {
		return tableType;
	}

	public void setTableType(int tableType) {
		this.tableType = tableType;
	}
	
	public abstract void initLog( int roleId, int logId, int logSubId );

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}
	
}
