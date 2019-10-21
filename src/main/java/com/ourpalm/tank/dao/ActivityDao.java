package com.ourpalm.tank.dao;

import java.util.List;

import com.ourpalm.tank.domain.ActivityDailyGrabInfo;
import com.ourpalm.tank.domain.ActivityDailyInfo;
import com.ourpalm.tank.domain.ActivityKeepOnlineInfo;
import com.ourpalm.tank.domain.ActivityMonthCardInfo;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.domain.ActivitySevenLoginInfo;
import com.ourpalm.tank.domain.HallFirstPayInfo;
import com.ourpalm.tank.domain.RedMoneyInfo;
import com.ourpalm.tank.domain.RedPacketInfo;

public interface ActivityDao {

	
	void saveDailyInfo(int roleId, int logicId, ActivityDailyInfo info);
	ActivityDailyInfo getDailyInfo(int roleId, int logicId);
	void deleteDailyInfo(int roleId, int logicId);
	
	//月卡
	ActivityMonthCardInfo getMonthCardInfo(int roleId, int logicId);
	void saveMonthCardInfo(int roleId, int logicId, ActivityMonthCardInfo info);
	
	//红包
	void saveRedPacketInfo(int roleId, int logicId, RedPacketInfo info);
	RedPacketInfo getRedPacketInfo(int roleId, int logicId);
	
	//每日必秒
	void saveDailyGrabInfo(int roleId, int logicId, ActivityDailyGrabInfo info);
	ActivityDailyGrabInfo getDailyGrabInfo(int roleId, int logicId);
	void saveDailyGrabServerPacket(int count);
	int getDailyGrabServerPacket();
	
	//七日登录
	public void saveSevenLoginInfo(int roleId, ActivitySevenLoginInfo day);
	public ActivitySevenLoginInfo getSevenLoginInfo(int roleId);
	
	//月签到
	public void saveMonthLoginInfo(int roleId, ActivityMonthLoginInfo info);
	public ActivityMonthLoginInfo getMonthLoginInfo(int roleId); 
	
	//首充
	void saveFirstPayInfo(int roleId, int logicId, HallFirstPayInfo info);
	HallFirstPayInfo getFirstPayInfo(int roleId, int logicId);
	
	//挂机送经验
	void saveKeepOnlineInfo(int roleId, ActivityKeepOnlineInfo info);
	ActivityKeepOnlineInfo getKeepOnlineInfo(int roleId);
	
	//胜利红包
	void saveRedMoneyList(String uniqueId, List<RedMoneyInfo> infos);
	List<RedMoneyInfo> getRedMoneyList(String uniqueId);
	void updateRedMoneyList(String uniqueId, List<RedMoneyInfo> infos);
}
