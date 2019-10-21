package com.ourpalm.tank.app.vip;

import java.util.Collection;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.VipInfo;
import com.ourpalm.tank.template.VipTemplate;
import com.ourpalm.tank.vo.result.Result;

public interface VipApp extends Service{
	
	void createUser(int roleId);
	
	Collection<VipTemplate> getTemplates();
	
	VipInfo getVipInfo(int roleId);
	
	VipTemplate getVipTemplate(int vipLvl);
	
	/**
	 * 
	 * @param roleId
	 * @param rmb 累积充值rmb
	 */
	void rechargeRmb(int roleId, int rmb);
	
	
	Result draw(int roleId, int vipLevel);
	
	/** 特权坦克全局经验加成百分比 */
	float getPrivilegeRoleTankExpPercent(int roleId);
	
	/** 特权银币加成百分比 */
	float getPrivilegeIronPercent(int roleId);
	
	/** 特权银币上限加成百分比 */
	float getPrivilegeIronMaxPercent(int roleId);
	
	/** 特权坦克经验加成百分比 */
	float getPrivilegeTankExpPercent(int roleId);
	
	/** 特权战场复活次数 */
	int getPrivilegeAliveCount(int roleId);
	
}
