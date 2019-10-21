package com.ourpalm.tank.app.tank;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.app.tank.TankAppImpl.AiCondition;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.TANK_MSG.Freeze_Info;
import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;
import com.ourpalm.tank.message.TANK_MSG.STC_BUYPARK_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_CLEAR_FREEZE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_ONE_KEY_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_OPEN_GROOVE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_OPEN_GROOVE_SHOW_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_BUY_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_LIST_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_UP_DOWN_MSG;
import com.ourpalm.tank.message.TANK_MSG.TankItem;
import com.ourpalm.tank.template.AiConfig;
import com.ourpalm.tank.template.BuildTemplate;
import com.ourpalm.tank.template.GoldTankTemplate;
import com.ourpalm.tank.template.TankAiTemplate;
import com.ourpalm.tank.template.TankAiTestTemplate;
import com.ourpalm.tank.template.TankCoefficientTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.TankBuyResult;
import com.ourpalm.tank.vo.result.ValueResult;

public interface TankApp extends Service{

	void login(int roleId, boolean nextDay);

	/** 创建RoleTank对象 */
	RoleTank createRoleTank(int roleId, int tankId,UserInitTemplate initTemplate);
	RoleTank getRoleTank(int roleId, int tankId);
	void saveRoleTank(RoleTank roleTank);
	Collection<RoleTank> getAllRoleTank(int roleId);

	/** 创建坦克实例对象 */
	AbstractInstance createTankInstance(int roleId, int tankId);
	/** 创建机器人 */
	AbstractInstance createNpcTank(int tankId, int preseasonId);

	/** 创建教学机器人 */
	AbstractInstance createTeachTank(int instanceId, int tankId);

	/** 创建建筑物 */
	AbstractInstance createBuildTank(int buildId, BodyType type);

	/** 获取坦克实例对象 */
	AbstractInstance getInstance(int instanceId);
	AbstractInstance getTankInstanceByRoleId(int roleId);

	/** 删除坦克实例 */
	void removeTankInstance(int id);

	/** 返回坦克配置模板对象 */
	TankTemplate getTankTemplate(int tankId);

	BuildTemplate getBuildTemplate(int buildId);

	TankAiTemplate getTankAiTemplate2(int score);

//	TankAiGroupTemplate randomTankAiGroupTemplate(int group);

	void offline(int roleId);

	/** 购买坦克 */
	TankBuyResult tankBuy(int roleId, int tankId,STC_TANK_BUY_MSG.Builder builder);

	/** 添加坦克 */
	RoleTank tankAdd(int roleId, int tankId, String origin);

	/** 属性计算 */
	void reCalcAttr(AbstractInstance tank, Map<AttrType, Float> attrMap);
	void reCalcAttr(AbstractInstance tank);

	/** 计算坦克战斗力  */
	int calcAllBattleScore(RoleTank tank);
	int calcAllBattleScore(int roleId, int tankId);
	int calcAllBattleScore(AbstractInstance tank);
	
	int calcShowBattleScore(AbstractInstance tank);

	/***
	 * 计算自身坦克和火炮配件的攻击力
	 */
	int calcTankAndPart2Atk(AbstractInstance tank);

	//更换出战坦克
	Result changeMainTank(int roleId, int tankId);

	boolean goodsIsEnough(int roleId, Map<Integer, Integer> consumeMap);

	TankItem buildTankItem(RoleTank tank);

	void tankPush(RoleTank tank);

	ValueResult<Map<Integer, Integer>> translateTankExp(int roleId, List<Integer> tankIds, int translateExp, int type);

	TankBuyResult tankGoldBuy(int roleId, int tankId, int moneyType);

	Collection<GoldTankTemplate> getGoldTankTemplates();

	/** 计算坦克匹配分 */
	int calcAllMatchScore(int roleId, int tankId);
	int calcAllMatchScore(RoleTank tank);


	/** 获取坦克+成员所有属性 */
	Map<AttrType, Float> getAllTankMemberAttr(int roleId, int tankId);

	//发送心跳包
	void runBroadcastHeartbeatThread();
	void broadcastHeartbeat();

	TankAiTestTemplate getTankAiTestTemplate(int camp, int index);
	void hotLoadTankTemplate();
	Map<AttrType, Float> tankBornAttr(AbstractInstance tank);

	/** 创建NPC坦克实例对象 */
	AbstractInstance createTankInstanceByPreseasonId(int preseasonId, int instanceId);
	/**坦克战斗力系数Template*/
	TankCoefficientTemplate getTankCoefficientTemplate();

	/** 购买车位 */
	Result buyPark(STC_BUYPARK_MSG.Builder builder, int roleId);

	/** 强化 */
	Result strengthen(STC_STRENGTHEN_MSG.Builder builder, int roleId, int tankTemplateId, PART_INDEX index, int type, int upLevel);

	/** 开槽信息 */
	void openGrooveShow(STC_OPEN_GROOVE_SHOW_MSG.Builder builder, int roleId, int tankId);

	/** 开槽 */
	Result openGroove(STC_OPEN_GROOVE_MSG.Builder builder, int type, int tankId, int roleId);

	/** 清除CD */
	Result clearFreeze(STC_CLEAR_FREEZE_MSG.Builder builder, int roleId);

	/** 获取 一键强化金币数 */
	void getOneKeyAllGold(int roleId, STC_ONE_KEY_STRENGTHEN_MSG.Builder builder, int upLevel, int tankId, PART_INDEX partIndex);

	/** 装卸配件 */
	Result upDownPart(STC_UP_DOWN_MSG.Builder builder, int tankId, PART_INDEX partIndex, int roleId, int type);
	/**冷却体*/
	Freeze_Info buildFreezeInfo(int roleId) ;

	/** 每日清除 消除冷却次数 */
	void refreshFreezeNum(int roleId, boolean isNextDay);

	AiConfig getAiConfig();
	/**研发*/
	 Result developTank(int roleId, int tankTemplateId, STC_TANK_DEVELOP_MSG.Builder builder);
	 /**研发树*/
	 void getTankTree(int roleId, STC_TANK_DEVELOP_LIST_MSG.Builder builder) ;
	 /**取得坦克属性*/
	 Map<AttrType, Float> getTankRawAttr(int tankTemplateId);
	 /**根据坦克原生属性，加上百分比计算所得值*/
	 Map<AttrType, Float> tankRawAddAttr(Map<AttrType, Float> percentMap, int tankTemplateId) ;

	TankTemplate getTankAiTemplate(int score, int minLevel, int maxLevel);

	AbstractInstance createAiTank(AiCondition ac, int preseasonId);
}

