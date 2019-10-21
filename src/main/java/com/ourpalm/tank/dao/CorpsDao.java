package com.ourpalm.tank.dao;

import java.util.List;
import java.util.Set;

import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;

public interface CorpsDao {
	
	void init();
	
	/** 创建军团id */
	int nextId();
	
	void lock(int key);
	void unlock(int key);

	/** 创建军团 */
	void createCorps(CorpsInfo corpsInfo);
	
	/** 保存军团 */
	void saveCorpsInfo(CorpsInfo corpsInfo);
	
	/** 获取军团信息 */
	CorpsInfo getCorpsInfo(int id);
	CorpsInfo getCorpsInfo(int areaId, String name);
	
	/** 分页获取军团信息 */
	List<CorpsInfo> getCorpsInfoList(int areaId, int page);
	List<CorpsInfo> getAllCorpsInfo(int areaId);
	
	/** 保存军团成员信息 */
	void saveCorpsRole(CorpsRole corpsRole);
	
	/** 返回军团成员信息 */
	CorpsRole getCorpsRole(int corpsId, int roleId);
	
	/** 返回军团所有成员列表 */
	List<CorpsRole> getCorpsRoleList(int corpsId);
	
	/** 返回当前军团人数 */
	int getCorpsRoleSize(int corpsId);
	
	/** 返回申请加入军团的人员列表 */
	Set<String> getCorpsApplyList(int corpsId);
	
	/** 判断是否在此军团的审批队列中 */
	boolean hasExistCorpsApply(int corpsId, int roleId);
	
	/** 从军团的审批队列中删除 */
	void removeCorpsApply(int corpsId, int roleId);
	
	/** 添加到军团的审批队列中 */
	void addCorpsApply(int corpsId, int roleId);
	
	/** 判断军团名称是否重复 */
	boolean hasExistCorpsName(int areaId, String name);
	
	/** 删除军团信息 */
	void removeCorpsInfo(CorpsInfo corpsInfo);
	
	/** 删除军团所有成员 */
	void removeAllCorpsRole(int corpsId);
	
	/** 删除军团成员 */
	void removeCorpsRole(int corpsId, int roleId);
}
