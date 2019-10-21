package com.ourpalm.tank.app.log;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.type.Operation;

public interface GameLogApp extends Service{

	/**
	 * 发送充值日志
	 * worldid 逻辑服ID
	 * domain 渠道
	 * opopenid 账户ID
	 * userip 用户IP
	 * modifyfee 上报单位为Q分（100Q分 = 10Q点 = 1Q币）
	 */
	void sendPayLog(int worldid, String domain, String userid, String userip, int modifyfee);
	/**
	 * 发送物品变更日志
	 * @param roleId 角色ID
	 * @param itemChangeEnum 物品变更类型
	 * @param itemId 物品ID
	 * @param itemName 物品名
	 * @param itemCount 物品数量
	 * @param custom 自定义字段（可为空）
	 */
	void sendItemChangeLog(int roleId, Operation operation, String itemId, String itemName, int itemCount, String custom);
	
	/**
	 * 发送属性变更日志
	 * @param roleId 角色ID
	 * @param propKey 属性标识
	 * @param propValue 新属性值
	 * @param rangeability 变更值
	 */
	void sendAttrChangeLog(int roleId,String propKey,String propValue,String rangeability);
	
	/**
	 * 发送登录日志
	 * worldid 逻辑服ID
	 * domain 渠道
	 * opopenid 账户ID
	 * userip 用户IP
	 * level 角色等级
	 */
	void sendLoginLog(int worldid, String domain, String opopenid, String userip, int level);

	/**
	 * 发送退出日志
	 * worldid 逻辑服ID
	 * domain 渠道
	 * opopenid 账户ID
	 * userip 用户IP
	 * onlinetime 登录在线时长
	 */
	void sendQuitLog(int worldid, String domain, String opopenid, String userip, int onlinetime);
	
	/**
	 * 发送注册日志
	 * worldid 逻辑服ID
	 * domain 渠道
	 * opopenid 账户ID
	 * userip 用户IP
	 */
	void sendRegisterLog(int worldid, String domain, String userid, String userip);
	
	/**
	 * 发送消费日志
	 * worldid 逻辑服ID
	 * domain 渠道
	 * opopenid 账户ID
	 * userip 用户IP
	 * modifyfee 上报单位为Q分（100Q分 = 10Q点 = 1Q币）
	 */
	void sendConsumeLog(int worldid, String domain, String userid, String userip, int modifyfee);	
	
}
