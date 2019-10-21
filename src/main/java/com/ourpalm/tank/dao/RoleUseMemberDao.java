package com.ourpalm.tank.dao;

import java.util.Map;

public interface RoleUseMemberDao {

	/** 获取正在使用的成员(key=成员类型) */
	public Map<Integer, String> getAll(int roleId);
	
	/** 保存使用的成员实例id */
	public void save(int roleId, Integer type, String instanceId);
	
	/** 根据成员类型返回对应的成员实例ID */
	String get(int roleId, Integer type);
}
