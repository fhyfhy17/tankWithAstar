package com.ourpalm.tank.app.log;

import com.ourpalm.core.util.Util;

public class AttrChangeLog extends AbstractLog {
	
	/**日志ID*/
	private static final String logId = "10010";
	
	/**属性标识*/
	private String propKey;
	/**新属性值*/
	private String propValue;
	/**变更值*/
	private String rangeability;

	@Override
	public String getLogId() {
		return logId;
	}

	@Override
	public void createLog() {
		putLogData("propKey", Util.isEmpty(this.propKey) ? "" : this.propKey);  
		putLogData("propValue", Util.isEmpty(this.propValue) ? "" : this.propValue);  
		putLogData("rangeability", Util.isEmpty(this.rangeability) ? "" : this.rangeability);  
	}
	
	@Override
	public String getUrlParams(){
		return "";
	}

	public String getPropKey() {
		return propKey;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	public String getRangeability() {
		return rangeability;
	}

	public void setRangeability(String rangeability) {
		this.rangeability = rangeability;
	}
}
