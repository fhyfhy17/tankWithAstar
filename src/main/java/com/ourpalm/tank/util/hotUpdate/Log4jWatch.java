package com.ourpalm.tank.util.hotUpdate;

import org.apache.log4j.PropertyConfigurator;

public class Log4jWatch extends FileWatchdog {

	public Log4jWatch(String filename) {
		super(filename);
	}

	@SuppressWarnings("static-access")
	@Override
	protected void doOnChange() {
		logger.info("log4j文件更改");
		new PropertyConfigurator().configure("conf" + System.getProperty("file.separator") + "log4j.properties");
	}

}
