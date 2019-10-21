package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;

public interface UserDAO {

	/** 获取账号信息 */
	RoleAccount getRoleAccount(int areaId, String uid);
	RoleAccount getRoleAccount(int roleId);//可以删除
	
	/** 保存账号信息 */
	void createRoleAccount(RoleAccount account);
	void saveRoleAccount(RoleAccount account);
	
	/** 获取角色链接信息 */
	RoleConnect getRoleConnect(int roleId);
	
	/** 保存角色链接信息 */
	void saveRoleConnect(RoleConnect roleConnect);
	void removeRoleConnect(int roleId);
	
	/** 检测角色名是否存在 */
	boolean roleNameHadExist(String roleName);
	
	/** 返回角色id */
	int getRoleId(String roleName);
	
	/** 删除角色名与角色ID的绑定关系 */
	void delNameRoleIdLink(String roleName);
	
	/** 添加角色名与角色ID的绑定关系 */
	void addNameRoleIdLink(String roleName, int roleId);
	
	/** 获取保存军团ID */
	void saveCorpsId(int roleId, int corpsId);
	int getCorpsId(int roleId);
	
	/** 通过平台获取的一些平台蓝钻信息  */
	PFUserInfo getPfUserInfo(int roleId);
	PFUserInfo getPfUserUpdateInfo(int roleId);
	void savePfUserInfo(int roleId, PFUserInfo pfUserInfo);
	
	/** 通过平台获取的一些平台黄钻信息  */
	PFYellowUserInfo getPfYellowUserInfo(int roleId);
	PFYellowUserInfo getPfYellowUserUpdateInfo(int roleId);
	void savePfYellowUserInfo(int roleId, PFYellowUserInfo pfYellowUserInfo);
}
