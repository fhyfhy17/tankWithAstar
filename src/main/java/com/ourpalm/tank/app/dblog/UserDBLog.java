package com.ourpalm.tank.app.dblog;

public class UserDBLog extends DBLog {
	private static final String LOG_ID_KEY = "log_id";
	private static final String LOG_SUB_ID_KEY = "log_sub_id";
	private static final String LOG_ACTION_TIME = "action_time";
	
	public void initLog( int roleId, int logId, int logSubId ){
		this.setLogId(logId);
		this.setLogSubId(logSubId);
		this.setActionTime(System.currentTimeMillis()/1000);
		this.setDbIndex(roleId % DBLogAppImpl.USER_DB_NUM);
		this.setTableType(DBLogAppImpl.DBLogType.userInc.type());
		
		this.getRecord().set( LOG_ID_KEY, logId );
		this.getRecord().set( LOG_SUB_ID_KEY, logSubId );
		this.getRecord().set( LOG_ACTION_TIME, this.getActionTime() );
		
	}
}
