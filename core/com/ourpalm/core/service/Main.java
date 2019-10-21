package com.ourpalm.core.service;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant._MappingKit;
import com.ourpalm.tank.http.HttpThread;
import com.ourpalm.tank.thread.ThreadId;
import com.ourpalm.tank.thread.ThreadUtil;
import com.ourpalm.tank.thread.roleThread.RoleThread;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.hotUpdate.Log4jWatch;
import com.ourpalm.tank.util.hotUpdate.MapXLSWatch;
import com.ourpalm.tank.util.hotUpdate.SystemconfigXLSWatch;
import com.ourpalm.tank.util.hotUpdate.TankXLSWatch;

public class Main {

	private static final Logger logger = LogCore.startup;

	private ClassPathXmlApplicationContext context;

	private AtomicBoolean started = new AtomicBoolean(false);

	ActiveRecordPlugin arp;
	int num = 0;

	void startServer() throws Exception {
		logger.info("begin start server ...");
		this.initConf();
		this.initWatch();
		ServerHolder holder = (ServerHolder) context.getBean("serverHolder");

		SysConfig.load();
		startJfinal();
		// 如果没启动监听服务,不再启动
		Map<String, Service> serverMap = holder.getServerMap();
		if (Util.isEmpty(serverMap)) {
			logger.error("not configure the ServerHolder's Server,start failure!");
			return;
		}

		// 启动功能模块
		Map<String, Service> initMap = holder.getInitServiceMap();
		if (!Util.isEmpty(initMap)) {
			for (Iterator<Map.Entry<String, Service>> initIt = initMap.entrySet().iterator(); initIt.hasNext();) {
				Map.Entry<String, Service> entry = initIt.next();
				entry.getValue().start();
				logger.info("init res: " + entry.getKey() + " success");
			}
		}

		// 启动server
		for (Iterator<Map.Entry<String, Service>> serverIt = serverMap.entrySet().iterator(); serverIt.hasNext();) {
			final Map.Entry<String, Service> entry = serverIt.next();
			logger.info(entry.getKey() + " begin to start");
			final Service service = entry.getValue();

			new Thread(new Runnable() {
				public void run() {
					try {
						service.start();
					} catch (Exception ex) {
						logger.error(entry.getKey() + " start failure,system shutdown", ex);
						System.exit(0);
					}
				}
			}).start();
		}

		// 启动server后服务
		Map<String, Service> startedMap = holder.getStartedServiceMap();
		if (!Util.isEmpty(startedMap)) {
			for (Iterator<Map.Entry<String, Service>> initIt = startedMap.entrySet().iterator(); initIt.hasNext();) {
				Map.Entry<String, Service> entry = initIt.next();
				entry.getValue().start();
				logger.info("init res: " + entry.getKey() + " success");
			}
		}

		HttpThread httpThread = new HttpThread();
		httpThread.start();
		ThreadUtil.addThread(ThreadId.HTTP, httpThread);

		RoleThread roleThread = new RoleThread();
		roleThread.start();
		ThreadUtil.addThread(ThreadId.ROLE, roleThread);

		
		logger.info("server start finish ! ");
	}

	public static void main(String[] args) throws Exception {
		new Main().startServer();
	}

	public void startJfinal() {
		DruidPlugin druidPlugin = new DruidPlugin(GameContext.getMysql_jdbcUrl(), GameContext.getMysql_user(), GameContext.getMysql_password().trim());
		druidPlugin.start();

		// 配置ActiveRecord插件
		arp = new ActiveRecordPlugin(druidPlugin);
		// 忽略大小写
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		arp.setDevMode(GameContext.isMysql_devMode());
		arp.setShowSql(GameContext.isMysql_devMode());
		// 配置Entity和数据库表的对应关系
		_MappingKit.mapping(arp);
		arp.start();
	}

	private void initWatch() {
		TankXLSWatch tankXLSWatch = new TankXLSWatch("resources" + System.getProperty("file.separator") + "tank.xls");
		tankXLSWatch.setDelay(10000);// 10秒
		tankXLSWatch.start();

		MapXLSWatch mapXLSWatch = new MapXLSWatch("resources" + System.getProperty("file.separator") + "map.xls");
		mapXLSWatch.setDelay(10000);// 10秒
		mapXLSWatch.start();

		SystemconfigXLSWatch systemconfigXLSWatch = new SystemconfigXLSWatch("resources" + System.getProperty("file.separator") + "sysconfig.xls");
		systemconfigXLSWatch.setDelay(10000);// 10秒
		systemconfigXLSWatch.start();

		Log4jWatch log4jWatch = new Log4jWatch("conf" + System.getProperty("file.separator") + "log4j.properties");
		log4jWatch.setDelay(10000);// 10秒
		log4jWatch.start();
	}

	private void initConf() {
		try {
			if (started.compareAndSet(false, true)) {
				String mainContextFilePath = "spring.conf.dir";
				try {
					java.util.ResourceBundle rb = ResourceBundle.getBundle("configure");
					mainContextFilePath = rb.getString("spring.conf.dir");
				} catch (Throwable e) {
				}
				// 多路径用;分隔
				String[] paths = mainContextFilePath.split(";");
				String[] load = new String[paths.length];
				int i = 0;
				for (String path : paths) {
					load[i] = path + "/*.xml";
					i++;
				}
				context = new ClassPathXmlApplicationContext(load);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void serviceStop() {

		ServerHolder holder = (ServerHolder) context.getBean("serverHolder");
		// 如果没启动监听服务,不再启动
		Map<String, Service> serverMap = holder.getServerMap();
		if (Util.isEmpty(serverMap)) {
			logger.error("not configure the ServerHolder's Server,start failure!");
			return;
		}
		// 关闭功能模块
		Map<String, Service> initMap = holder.getInitServiceMap();
		if (!Util.isEmpty(initMap)) {
			for (Iterator<Map.Entry<String, Service>> initIt = initMap.entrySet().iterator(); initIt.hasNext();) {
				Map.Entry<String, Service> entry = initIt.next();
				entry.getValue().stop();
				logger.info("stop res: " + entry.getKey() + " success");
			}
		}
		// 关闭server
		for (Iterator<Map.Entry<String, Service>> serverIt = serverMap.entrySet().iterator(); serverIt.hasNext();) {
			final Map.Entry<String, Service> entry = serverIt.next();
			logger.info(entry.getKey() + " begin to stop");
			final Service service = entry.getValue();
			service.stop();
		}
		// 关闭server后服务
		Map<String, Service> startedMap = holder.getStartedServiceMap();
		if (!Util.isEmpty(startedMap)) {
			for (Iterator<Map.Entry<String, Service>> initIt = startedMap.entrySet().iterator(); initIt.hasNext();) {
				Map.Entry<String, Service> entry = initIt.next();
				entry.getValue().stop();
				logger.info("stop  res: " + entry.getKey() + " success");
			}
		}

		System.out.println("关闭数据库");
		arp.stop();
		System.out.println("服务器关闭成功");
	}

	public void stop2(Main main) {
		// 关闭服务
		main.serviceStop();
	}

	public void destroy2() throws Exception {
		System.out.println("execute destroy method！");
		System.exit(0);

	}

}
