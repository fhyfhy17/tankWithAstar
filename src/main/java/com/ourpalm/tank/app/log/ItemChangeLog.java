package com.ourpalm.tank.app.log;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.type.Operation;

public class ItemChangeLog extends AbstractLog{
	/**日志ID*/
	private static final String logId = "10009";
	
	/**物品变更类型*/
	private Operation operation;
	/**物品ID*/
	private String itemId;
	/**物品名*/
	private String itemName;
	/**物品数量*/
	private int itemCount;
	/**自定义字段*/
	private String custom;
	
	@Override
	public String getLogId() {
		return logId;
	}
	@Override
	public void createLog() {
		putLogData("updateType", String.valueOf(operation.getType()));  
		putLogData("itemId", Util.isEmpty(this.itemId) ? "" : this.itemId);  
		putLogData("itemName", Util.isEmpty(this.itemName) ? "" : this.itemName);  
		putLogData("itemCount", String.valueOf(this.itemCount));  
		putLogData("custom", Util.isEmpty(this.custom) ? "" : this.custom);  
	}
	
	@Override
	public String getUrlParams(){
		return "";
	}
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public String getCustom() {
		return custom;
	}
	public void setCustom(String custom) {
		this.custom = custom;
	}
}
