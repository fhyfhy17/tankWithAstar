package com.ourpalm.tank.util.hotUpdate;

import java.io.File;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;

// 使用一个线程来监视log4j.properties文件的更改
public abstract class FileWatchdog extends Thread {

	Logger logger = LogCore.runtime;
	/**
	 * 默认检查文件更新的间隔时间
	 */
	static final public long DEFAULT_DELAY = 60000;
	/**
	 * 文件名
	 */
	protected String filename;

	protected long delay = DEFAULT_DELAY;

	File file;
	long lastModif = 0;
	boolean warnedAlready = false;
	boolean interrupted = false;

	public FileWatchdog(String filename) {
		super("FileWatchdog " + filename);
		this.filename = filename;
		file = new File(filename);
		setDaemon(true);
		checkAndConfigure();
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	// 当文件发生更改时,做的操作
	abstract protected void doOnChange();

	protected void checkAndConfigure() {
		boolean fileExists;
		try {
			fileExists = file.exists();
		} catch (SecurityException e) {
			logger.warn("没权限,	 file:[" + filename + "].");
			interrupted = true;
			return;
		}

		if (fileExists) {
			// 文件最后被更改的时间
			long l = file.lastModified(); // this can also throw a
											// SecurityException
			if (l > lastModif) { // however, if we reached this point this
				lastModif = l; // is very unlikely.
				doOnChange();
				warnedAlready = false;
			}
		} else {
			if (!warnedAlready) {
				logger.debug("[" + filename + "] does not exist.");
				warnedAlready = true;
			}
		}
	}

	public void run() {
		while (!interrupted) {
			try {
				Thread.sleep(delay);

			} catch (InterruptedException e) {
				logger.error("热部署报错", e);
			}
			// 检查并且配置文件
			checkAndConfigure();
		}
	}
}
