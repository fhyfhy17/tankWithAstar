package com.ourpalm.tank.app.match;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.message.MATCH_MSG.FriendItem;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_SCHEDULE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_REQ_FRIEND;
import com.ourpalm.tank.message.MATCH_MSG.TankTeamItem;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MatchParam;
import com.ourpalm.tank.vo.TeamIncomeTypeInfo;

public interface MatchApp extends Service{

	void matchNotifyEnter(MatchParam matchParam);
	
	/** 获取本地战斗信息 */
	RoleBattle getLocalRoleBattle(int roleId);
	void removeLocalRoleBattle(int roleId);
	void saveRoleBattle(RoleBattle roleBattle);
	void removeRoleBattle(int roleId);
	RoleBattle getRoleBattle(int roleId);
	
	/** 保存申请匹配的io */
	void saveMatchIo(int ioId);
	
	/** 战场匹配人数计数 */
	void cumWarMatchCount(int warType, int ioId);
	/** 返回战场匹配人数 */
	int warMatchCount(int warType);
	
	/** 判断io是否在匹配队列中 */
	boolean hasMatchQueue(int ioId);
	
	/** 删除申请匹配记录 */
	void removeIo(int ioId);
	
	/** 创建队伍 */
	MatchTeam createTeam(int roleId, int warType);
	
	/** 返回队伍坦克信息 */
	List<TankTeamItem> getTankTeamItems(String teamId);
	List<TankTeamItem> getTankTeamItems(MatchTeam matchTeam);
	
	/** 返回好友信息 */
	List<FriendItem> getFriendItems(int roleId);
	
	/** 加入队伍 */
	String addTeam(int roleId, String teamId,TEAM_REQ_FRIEND friendType,int reqRoleId);
	
	/** 更换所使用的坦克 */
	void teamChangeTank(int roleId, String teamId, int tankId);
	
	/** 出战准备 */
	void teamReady(int roleId, String teamId);
	
	/** 退出队伍 */
	void teamQuit(int roleId);
	
	/** 组队匹配申请 */
	void teamMatch(String teamId);
	
	/** 获取队伍信息 */
	MatchTeam getMatchTeam(String teamId);
	
	/** 保存队伍信息 */
	void saveMatchTeam(MatchTeam matchTeam);
	
	/** 军团成员信息 */
	List<FriendItem> getCorpsRoleItems(int roleId);
	/** 匹配等级 是否符合*/
	boolean ifLevelMate(int selfLevel,int otherLevel);
	/** 取得新手赛模板*/
	Map<Integer, PreseasonTemplate> getPreseasonTemplateMap();
	void loadTemplate();
	 Map<Integer, PreseasonTemplate> getPreseasonMap() ;
	 /**组队收益计算*/
	 TeamIncomeTypeInfo getTeamIncome(AbstractInstance tank);

	/** 进入战场进度 */
	void matchSchedule(int mapInstanceId, int roleId, int schedule, STC_MATCH_SCHEDULE_MSG.Builder builder);
}
