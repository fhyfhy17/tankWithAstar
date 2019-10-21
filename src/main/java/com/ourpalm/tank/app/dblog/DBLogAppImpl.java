package com.ourpalm.tank.app.dblog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ourpalm.core.log.LogCore;

class LogList {
	
	private String tableName = "";
	private int saveLogTime = 0;
	
	private List<Record> logList = new ArrayList<Record>(DBLogAppImpl.LIST_SIZE);
	
	public void saveLog( boolean force ){
		if( logList.size() <= 0 ){
			return;
		}
		if( force || (logList.size() > DBLogAppImpl.SAVE_LOG_SIZE) || (saveLogTime > DBLogAppImpl.SAVE_LOG_TIME)){
			try {
				Db.batchSave(tableName, logList, logList.size());
			} catch (Exception e) {
				LogCore.runtime.error("保存日志数据失败！", e);
			}
			
			logList.clear();
			saveLogTime = 0;
		}
		saveLogTime++;
	}

	public boolean addLog( Record record ){
		if( record == null ) return false;
		
		logList.add(record);
		return true;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}

public class DBLogAppImpl implements DBLogApp{
	
	/**队列大小，防止日志服务器挂了导致内存溢出*/
	public static final int LIST_SIZE = 20000;		
	public static final int SAVE_LOG_SIZE = 50;		// 保存日志的条数
	public static final int SAVE_LOG_TIME = 10; //10 * 60; 	// 保存日志的定时时间
	
	public static final int DB_NUM = 1; // 20;
	public static final int USER_DB_NUM = 1; //10;
	public static final int MONEY_DB_NUM = 1; //10;
	
	public static enum DBLogType {

		userInc(1, "user_log_"),
		moneyInc(2, "money_log_"),	
		handleInc(3, "log_"),						
		;
		
		private int type;
		private String info;
		
		DBLogType(int type, String info){
			this.type = type;
			this.info = info;
		}
		
		public int type(){
			return this.type;
		}
		
		public String getInfo(){
			return this.info;
		}
	}
	
	/** 日志队列 */
	private Map<Integer, LogList> logMapQueue = new HashMap<>();
	private Map<Integer, LogList> logUserMapQueue = new HashMap<>();
	private Map<Integer, LogList> logMoneyMapQueue = new HashMap<>();
	
	public void initLogApp(){
		for( int i = 0; i < DB_NUM; i++ ){
			LogList list = new LogList();
			list.setTableName(DBLogType.handleInc.getInfo()+i);
			logMapQueue.put(i, list);
		}
		
		for( int i = 0; i < USER_DB_NUM; i++ ){
			LogList list = new LogList();
			list.setTableName(DBLogType.userInc.getInfo()+i);
			logUserMapQueue.put(i, list);
		}
		
		for( int i = 0; i < MONEY_DB_NUM; i++ ){
			LogList list = new LogList();
			list.setTableName(DBLogType.moneyInc.getInfo()+i);
			logMoneyMapQueue.put(i, list);
		}
	}
	
	/** 日志处理线程*/
	private LogProcessTask logProcessTask;
	
	private AtomicBoolean started = new AtomicBoolean(false);
	
	@Override
	public void start() {
		this.initLogApp();
		//启动日志处理任务
		if(started.compareAndSet(false,true)){
			logProcessTask = new LogProcessTask();
			logProcessTask.start();
		}
	}

	@Override
	public void stop() {
		//停止日志处理任务
		if(logProcessTask != null){
			logProcessTask.stopThread();
		}
	}

	public boolean offerLogToQueue(DBLog dbLog){
		int nType = dbLog.getTableType();
		int nIndex = dbLog.getDbIndex();
		if( nType == DBLogType.handleInc.type() ){
			if( logMapQueue.containsKey(nIndex) ){
				return logMapQueue.get(nIndex).addLog(dbLog.getRecord());
			}
		} else if( nType == DBLogType.userInc.type() ){
			if( logUserMapQueue.containsKey(nIndex) ){
				return logUserMapQueue.get(nIndex).addLog(dbLog.getRecord());
			}
		} else if( nType == DBLogType.moneyInc.type() ){
			if( logMoneyMapQueue.containsKey(nIndex) ){
				return logMoneyMapQueue.get(nIndex).addLog(dbLog.getRecord());
			}
		}
		return false;
	}
	
	private void saveLog( boolean force ){
		Iterator<Entry<Integer, LogList>> iter = logMapQueue.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, LogList>  entry = iter.next();
			LogList val = entry.getValue();
			val.saveLog(force);
		}
	}
	
	private void saveUserLog( boolean force ){
		Iterator<Entry<Integer, LogList>> iter = logUserMapQueue.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, LogList>  entry = iter.next();
			LogList val = entry.getValue();
			val.saveLog(force);
		}
	}
	
	private void saveMoneyLog( boolean force ){
		Iterator<Entry<Integer, LogList>> iter = logMoneyMapQueue.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, LogList>  entry = iter.next();
			LogList val = entry.getValue();
			val.saveLog(force);
		}
	}
	
	/**
	 * 日志处理线程
	 */
	private class LogProcessTask extends Thread{
		/**线程标识*/
		private volatile boolean isRuning = false;
		
		@Override
		public void run() {
			while(!isRuning){
				// 保存玩家操作日志
				try {
					saveLog(false);
				} catch (Exception e) {
					LogCore.runtime.error("保存日志数据失败！", e);
				}
				
				// 保存玩家账号日志
				try {
					saveUserLog(false);
				} catch (Exception e) {
					LogCore.runtime.error("保存日志数据失败！", e);
				}
				
				// 保存玩家消费日志
				try {
					saveMoneyLog(false);
				} catch (Exception e) {
					LogCore.runtime.error("保存日志数据失败！", e);
				}
				
				try {
					if( !isRuning ){
						Thread.sleep(1);
					}
				} catch (Exception e) {
					//LogCore.runtime.error("保存日志数据失败！", e);
				}
			}
		}
		/**
		 * 停止处理线程
		 */
		public void stopThread(){
			isRuning = true;
			this.interrupt();
			saveLog(true);
			saveUserLog(true);
			saveMoneyLog(true);
			LogCore.runtime.info("----------------- 保存日志数据结束！-----------------");
		}
	}


}

	