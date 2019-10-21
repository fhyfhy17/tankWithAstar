package com.ourpalm.tank.app.title;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.app.tank.TankAppImpl.AiCondition;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.message.ARMY_TITLE_MSG.STC_DRAW_DAY_REWARD_MSG;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.template.ArmyTitleTemplate;
import com.ourpalm.tank.template.TankMatchAiTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

public interface RoleArmyTitleApp extends Service{
	
	/** 赛季更新 */
	void seasonMathUpdate();

	/** 军衔排行赛结果 */
	void rankBattleResult(int roleId, int camp, boolean hadWin, float blueHopeValue, float redHopeValue);
	
	/** 领取每日军衔奖励 */
	STC_DRAW_DAY_REWARD_MSG drawDayRaward(int roleId);
	
	void login(int roleId, boolean nextDay);
	
	/** 初始化军衔信息 */
	void createInit(int roleId, int titleId);
	
	/** 计算匹配分值 */
	MatchScore getTeamMatchScore(MatchTeam matchTeam);
	int getMatchScore(int roleId, int tankId, WAR_TYPE type);
	/** 随机返回下一个路径类型 */
	int nextAiPointType(int pointType);
	
	
	/** 构造AI坦克实例 */
	AbstractInstance buildNpcTankTest(int titleId, int camp, int index);
	
	/** 返回匹配地图配置 */
	TankMatchAiTemplate getTankMatchMapTemplate(int titleId);
	
	/** 返回当前赛季结束说明信息 */
	String getSeasonMatchContext();
	
	ArmyTitleTemplate getArmyTitleTemplate(int titleId);

	AbstractInstance buildNpcTankOld(int titleId, int averageScore, int preseasonId);

	AbstractInstance buildSpecificNpcTank(int titleId, int tankId, int preseasonId);

	AbstractInstance buildNpcTank(int titleId, AiCondition ac, int preseasonId);
}
