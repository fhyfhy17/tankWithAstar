package com.ourpalm.tank.app.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.type.Operation;

public class GameLogAppImpl implements GameLogApp {

	/** 队列大小，防止日志服务器挂了导致内存溢出 */
	private int queueSize = 20000;

	/** 日志队列 */
	private BlockingQueue<AbstractLog> logQueue = new LinkedBlockingQueue<AbstractLog>(queueSize);

	/** 日志处理线程 */
	private LogProcessTask logProcessTask;

	private AtomicBoolean started = new AtomicBoolean(false);

	@Override
	public void start() {
		if (!GameContext.isReportNeed()) {
			return;
		}
		// 启动日志处理任务
		if (started.compareAndSet(false, true)) {
			logProcessTask = new LogProcessTask();
			logProcessTask.start();
		}
	}

	@Override
	public void stop() {
		// 停止日志处理任务
		if (logProcessTask != null) {
			logProcessTask.stopThread();
		}
	}

	@Override
	public void sendRegisterLog(int worldid, String domain, String userid, String userip) {
		try {
			RegisterLog registerLog = new RegisterLog();
			registerLog.setWorldid(worldid);
			registerLog.setDomain(domain);
			registerLog.setOpuid(userid);
			registerLog.setOpopenid(userid);
			registerLog.setUserip(userip);
			// 添加日志数据到队列
			offerLogToQueue(registerLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendLoginLog(int worldid, String domain, String userid, String userip, int level) {
		try {
			LoginLog loginLog = new LoginLog();
			loginLog.setWorldid(worldid);
			loginLog.setDomain(domain);
			loginLog.setOpuid(userid);
			loginLog.setOpopenid(userid);
			loginLog.setUserip(userip);
			loginLog.setLevel(level);
			// 添加日志数据到队列
			offerLogToQueue(loginLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendQuitLog(int worldid, String domain, String userid, String userip, int onlinetime) {
		try {
			QuitLog quitLog = new QuitLog();
			quitLog.setWorldid(worldid);
			quitLog.setDomain(domain);
			quitLog.setOpuid(userid);
			quitLog.setOpopenid(userid);
			quitLog.setUserip(userip);
			quitLog.setOnlinetime(onlinetime);
			// 添加日志数据到队列
			offerLogToQueue(quitLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendPayLog(int worldid, String domain, String userid, String userip, int modifyfee) {
		try {
			PayLog payLog = new PayLog();
			payLog.setWorldid(worldid);
			payLog.setDomain(domain);
			payLog.setOpuid(userid);
			payLog.setOpopenid(userid);
			payLog.setUserip(userip);
			payLog.setModifyfee(modifyfee);
			// 添加日志数据到队列
			offerLogToQueue(payLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendItemChangeLog(int roleId, Operation operation, String itemId, String itemName, int itemCount, String custom) {
		try {
			ItemChangeLog itemChangeLog = new ItemChangeLog();
			itemChangeLog.setRoleId(roleId);
			itemChangeLog.setOperation(operation);
			itemChangeLog.setItemId(itemId);
			itemChangeLog.setItemName(itemName);
			itemChangeLog.setItemCount(itemCount);
			itemChangeLog.setCustom(custom);
			// 添加日志数据到队列
			offerLogToQueue(itemChangeLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendAttrChangeLog(int roleId, String propKey, String propValue, String rangeability) {
		try {
			AttrChangeLog attrChangeLog = new AttrChangeLog();
			attrChangeLog.setRoleId(roleId);
			attrChangeLog.setPropKey(propKey);
			attrChangeLog.setPropValue(propValue);
			attrChangeLog.setRangeability(rangeability);
			// 添加日志数据到队列
			offerLogToQueue(attrChangeLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	@Override
	public void sendConsumeLog(int worldid, String domain, String userid, String userip, int modifyfee) {
		try {
			ConsumeLog consumeLog = new ConsumeLog();
			consumeLog.setWorldid(worldid);
			consumeLog.setDomain(domain);
			consumeLog.setOpuid(userid);
			consumeLog.setOpopenid(userid);
			consumeLog.setUserip(userip);
			consumeLog.setModifyfee(modifyfee);
			// 添加日志数据到队列
			offerLogToQueue(consumeLog);
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	/**
	 * 添加日志到队列
	 * 
	 * @param abstractLog
	 * @return
	 */
	private boolean offerLogToQueue(AbstractLog abstractLog) {
		if (!GameContext.isReportNeed()) {
			return false;
		}
		return logQueue.offer(abstractLog);
	}

	/**
	 * 日志处理线程
	 */
	private class LogProcessTask extends Thread {
		/** 线程标识 */
		private volatile boolean isRuning = false;

		@Override
		public void run() {
			while (!isRuning) {
				try {
					AbstractLog abstractLog;
					if (!logQueue.isEmpty()) {
						abstractLog = logQueue.take();
						abstractLog.sendLog();
					}
					Thread.sleep(1);
				} catch (Exception e) {
					LogCore.runtime.error("发送日志数据失败！", e);
				}
			}
		}

		/**
		 * 停止处理线程
		 */
		public void stopThread() {
			isRuning = true;
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.interrupt();
		}
	}
}
