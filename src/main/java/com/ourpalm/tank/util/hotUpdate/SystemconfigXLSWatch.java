package com.ourpalm.tank.util.hotUpdate;

import com.ourpalm.tank.util.SysConfig;

public class SystemconfigXLSWatch extends FileWatchdog {

	public SystemconfigXLSWatch(String filename) {
		super(filename);
	}

	@Override
	protected void doOnChange() {
		logger.info("sysconfig.xls 文件更改");
		SysConfig.load();
	}

}
