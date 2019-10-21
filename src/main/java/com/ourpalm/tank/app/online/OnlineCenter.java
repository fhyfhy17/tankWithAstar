package com.ourpalm.tank.app.online;

import java.util.Collection;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.vo.result.LoginResult;

public interface OnlineCenter extends Service{

	void register(RoleConnect connect);
	
	LoginResult login(int areaId, String uid, int ioId);
	
	/**
	 * 内部登录回调
	 * @param connect
	 */
	void sysLogin(RoleConnect connect, boolean hasLogin);
	
	/**
	 * 
	 * @param ioId
	 * @param offline 是否下线处理
	 */
	void offline(int ioId, boolean offline);
	void offlineByRoleId(int roleId, boolean offline);
	
	RoleConnect getRoleConnect(int ioId);
	RoleConnect getRoleConnectByRoleId(int roleId);
	
	boolean hadOnline(int roleId);
	
	/** 返回所有在线连接 */
	Collection<RoleConnect> getAllRoleConnect();

	/** 验证多点登录 */
	void checkMultiLogin(int roleId, int ioId);
}
