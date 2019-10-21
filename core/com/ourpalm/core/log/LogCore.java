package com.ourpalm.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCore {

	//服务启动日志
	public final static Logger startup = LoggerFactory.getLogger("startup");
	
	//服务器运行日志
	public final static Logger runtime = LoggerFactory.getLogger("runtime");
	
	//性能统计
	public final static Logger system = LoggerFactory.getLogger("sys");
	
	//支付统计
	public final static Logger pay = LoggerFactory.getLogger("pay");
	
	//排行榜
	public final static Logger rank = LoggerFactory.getLogger("rank");
	
	//绑定tank1支付
	public final static Logger tank1Pay = LoggerFactory.getLogger("Tank1Pay");
	
	//邮件
	public final static Logger mail = LoggerFactory.getLogger("Mail");
	
}
