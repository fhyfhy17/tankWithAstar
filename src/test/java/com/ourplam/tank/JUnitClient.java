package com.ourplam.tank;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ourpalm.core.service.ServerHolder;
import com.ourpalm.core.service.Service;
import com.ourpalm.core.util.Util;

import junit.framework.TestCase;

public class JUnitClient extends TestCase{

private ClassPathXmlApplicationContext context;
	
	public void setUp() throws Exception {
		init();
		
		ServerHolder holder = (ServerHolder)context.getBean("serverHolder");
		
		// 如果没启动监听服务,不再启动
		Map<String, Service> serverMap = holder.getServerMap();
		if(Util.isEmpty(serverMap)){
			return ;
		}
		
		// 启动功能模块
		Map<String, Service> initMap = holder.getInitServiceMap();
		if(!Util.isEmpty(initMap)){
			for(Iterator<Map.Entry<String,Service>> initIt = initMap.entrySet().iterator();initIt.hasNext();){
				Map.Entry<String,Service> entry = initIt.next();
	            entry.getValue().start();
			}
		}
	}
	
	
	private void init(){
		try {
				String mainContextFilePath="spring.conf.dir";
			 	try{
			 		java.util.ResourceBundle rb = ResourceBundle.getBundle("configure");
			 		mainContextFilePath = rb.getString("spring.conf.dir");
			 	}
			 	catch(Throwable e){}
			 	//多路径用;分隔
			 	String[] paths = mainContextFilePath.split(";");
			 	String[] load = new String[paths.length];
			 	int i = 0 ;
			 	for(String path : paths){
			 		load[i] = path + "/*.xml";
			 		i++ ;
			 	}
			 	context = new ClassPathXmlApplicationContext(load);
		} catch (Exception e) {
		}
	}
	
	
}
