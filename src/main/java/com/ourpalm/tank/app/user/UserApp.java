package com.ourpalm.tank.app.user;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.template.UserLevelTemplate;
import com.ourpalm.tank.vo.result.BattleRewardResult;

public interface UserApp extends Service{

	/** 新建账号 */
	void createUser(int areaId, String uid, String roleName, int country);
	
	/** 加载账号信息 */
	RoleAccount getRoleAccount(int areaId, String uid);
	
	/** 获取账号信息 */
	RoleAccount getRoleAccount(int roleId);
	void saveRoleAccount(RoleAccount account);
	
	/** 获取角色链接信息 */
	RoleConnect getRoleConnect(int roleId);
	void saveRoleConnect(RoleConnect roleConnect);
	void removeRoleConnect(int roleId);
	
	/** 登录初始化角色 */
	void loginInit(int roleId, boolean online);
	
	UserLevelTemplate getLevelTemplate(int level);
	
	UserInitTemplate getUserInitTemplate();
	
	/** 检测角色名是否存在 */
	boolean roleNameHadExist(String roleName);
	
	/** 删除角色名与角色ID的绑定关系 */
	void delNameRoleIdLink(String roleName);
	
	/** 添加角色id和角色名绑定关系 */
	void addNameRoleIdLink(int roleId, String roleName);
	
	/** 返回角色id */
	int getRoleId(String roleName);
	
	/**根据ID得到名字*/
	String getNameByRoleId(int roleId);
	//给角色加，战斗结束银币奖励
	BattleRewardResult addBattleEndIronReward(int roleId, int iron);
	
	/** 下线处理 */
	void offline(int roleId, boolean offline);
	
	/** 
	 * 军团ID单独存储
	 * 军团的加入、解散、剔除等于修改他人数据
	 * 保证不影响到其他属性
	 * */
	int getCorpsId(int roleId);
	void saveCorpsId(int roleId, int corpsId);
	
	PFUserInfo getPfUserInfo(int roleId);
	String getPfUserInfoStr(int roleId);
	void savePfUserInfo(int roleId, PFUserInfo pfUserInfo);
	PFUserInfo getRefreshedPf(int roleId);
	
	PFYellowUserInfo getPfYellowUserInfo(int roleId);
	String getPfYellowUserInfoStr(int roleId);
	void savePfYellowUserInfo(int roleId, PFYellowUserInfo pfYellowUserInfo);
	PFYellowUserInfo getRefreshedPfYellow(int roleId);
	
}
