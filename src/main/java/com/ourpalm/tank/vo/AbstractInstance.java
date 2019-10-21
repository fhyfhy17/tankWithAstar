package com.ourpalm.tank.vo;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.google.protobuf.GeneratedMessageLite;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.RoadManager;
import com.ourpalm.tank.app.map.ai.Ai;
import com.ourpalm.tank.app.map.ai.AiType;
import com.ourpalm.tank.app.map.ai.MoveToEnemyAi;
import com.ourpalm.tank.app.map.state.BeginStateMachine;
import com.ourpalm.tank.app.quest.BattleResultRecord;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_TYPE;
import com.ourpalm.tank.message.BATTLE_MSG.PropUnit;
import com.ourpalm.tank.message.BATTLE_MSG.STC_ATTR_SYN_MSG;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.template.TankAIPathTemplate;
import com.ourpalm.tank.template.TankAiActionTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.peshe.TankEffectPeshe;
import com.ourpalm.tank.vo.behaviortree.BehaviorTree;
import com.ourpalm.tank.vo.behaviortree.DataContext;

public abstract class AbstractInstance {

	protected Logger logger = LogCore.runtime;

	protected int id;            // 实例ID
	protected int roleId;        // 拥有者
	protected String roleName;   // 角色名称
	protected int level;         // 玩家等级
	protected int templateId;    // 模板ID
	protected int mapInstanceId; // 地图实例ID
	protected TEAM team;         // 阵营
	protected int memberId;      // 阵营成员序号（1-5）
	protected BodyType bodyType; // 实体类型
	protected long deathTime;    // 死亡时间
	protected double angle = -1; // 车身朝向角度
	protected double gunAngle = -1; // 炮塔朝向角度

	protected float x; // 位置坐标 x
	protected float y; // 位置坐标 y
	protected float z; // 位置坐标 z

	protected Location birthLocation; // 出生位置
	protected int rebirthGold;        // 复活需要金币数

	// ===== 客户端控制AI参数 已废弃
	protected int aiMain = 0; // 是否为AI主机 1:是
	protected int aiType = 0; // AI类型
	protected String aiStr = ""; // AI参数
	protected int aiInt = 0; // AI参数
	// ========== end

	protected Map<AiType, Ai> aiMap = new HashMap<>(); // AI行为库
	protected Ai currAi; // 当前AI

	protected RoadManager rm = new RoadManager(this);

	protected BehaviorTree<AbstractInstance> btTree;       // 行为树
	protected DataContext dataContext = new DataContext(); // 行为树的数据空间

	protected TankAiActionTemplate aiAction;   // AI行为
	protected TankAIPathTemplate aiPathPoint;  // AI行径类型
	protected boolean aiRunning = false;       // AI是否执行

	protected AbstractInstance attacker; // 攻击者
	protected AbstractInstance fireTarget;

	protected Map<AttrType, Float> attribute = new ConcurrentHashMap<AttrType, Float>(); // 坦克属性
	protected Map<AttrType, Float> changeAttrMap = new ConcurrentHashMap<>(); // 缓存属性变化
	protected Map<Integer, IBuff> buffMap = new ConcurrentHashMap<>(); // buff列表
	
	protected Map<Integer, Integer> aiParts = new HashMap<>();

	// 需要全体广播的属性变化
	protected static Set<AttrType> brodcastAttr = new HashSet<>();

	protected boolean isWeakTeam;// 是否在对战，排位赛中，是遭遇强敌

	protected boolean isEscape = false;// 是否逃跑过

	protected AiType lastAiType = null;

	protected boolean aiIsReduced;//AI是否已经降过战斗力了

	protected Map<AttrType, Float> aiChangeAttribute = new ConcurrentHashMap<AttrType, Float>(); // AI坦克改变的属性，用于新手赛

	protected int moveToEnemyLastTime = (int)(System.currentTimeMillis() / 1000);

	// 由于现在AI逻辑比较多，可能存在某种AI切换不了其他AI的情况，如果当某个AI持续了一分钟还不改变的话，则要对该AI做检测 isNeedCheckAIError 设置成 true 的时候，会打开检测逻辑
	protected long curAISustainBeginTime = 0;

	protected boolean isNeedCheckAIError = false;

	protected final static int BLUE_MOVE_TO_FLAG_RANDOM = 50;

	protected final static int RED_MOVE_TO_FLAG_RANDOM = 50;

	protected final static int MOVE_AND_FIRE_RANDOM = 50;
	/**组队中的好友*/
	protected int matchFriendId;
	/**组队中的军团成员*/
	protected int matchCorpMemberId;
	protected long lastViewRefreshTime;
	
	protected Cate cate;

	protected boolean usingFSM;

	protected long fightScore;//新加的  战斗分，用来排名和mvp用
	
	protected int fightRank;//新加的  战斗排名，用来排名和mvp用
	
	protected int battleGoods ;//在战斗是否使用穿甲弹
	
	public long getLastViewRefreshTime() {
		return lastViewRefreshTime;
	}

	public void setLastViewRefreshTime(long lastViewRefreshTime) {
		this.lastViewRefreshTime = lastViewRefreshTime;
	}

	static {
		brodcastAttr.add(AttrType.maxHp);
		brodcastAttr.add(AttrType.hp);
		brodcastAttr.add(AttrType.n_stab);
		brodcastAttr.add(AttrType.n_ArmorTurretFront);
		brodcastAttr.add(AttrType.n_ArmorTurretMiddle);
		brodcastAttr.add(AttrType.n_ArmorTurretbehind);
		brodcastAttr.add(AttrType.n_ArmorBodyFront);
		brodcastAttr.add(AttrType.n_ArmorBodyMiddle);
		brodcastAttr.add(AttrType.n_ArmorBodybehind);

	}

	public void init() {
	}

	public void update() {
		// 处理buff状态
		for (IBuff buff : this.buffMap.values()) {
			try {
				buff.update();
			} catch (Exception e) {
				LogCore.runtime.error("buff : " + buff.getId(), e);
			}
		}
	}


	public int getMatchFriendId() {
		return matchFriendId;
	}

	public void setMatchFriendId(int matchFriendId) {
		this.matchFriendId = matchFriendId;
	}

	public int getMatchCorpMemberId() {
		return matchCorpMemberId;
	}

	public void setMatchCorpMemberId(int matchCorpMemberId) {
		this.matchCorpMemberId = matchCorpMemberId;
	}


	// 添加buff
	public void putBuff(IBuff buff) {
		buffMap.put(buff.getId(), buff);
	}

	public IBuff getBuff(int id) {
		return this.buffMap.get(id);
	}

	public IBuff removeBuff(int id) {
		return this.buffMap.remove(id);
	}

	public void clearBuff() {
		// 删除所有buff
		for (IBuff buff : getBuffers()) {
			buff.clear();
			buff.update();
		}
		this.buffMap.clear();
	}

	/** 是否为机器人 */
	public boolean isRobot() {
		return roleId <= 0;
	}

	/** 同步属性变化 */
	public void synchChangeAttr() {
		if (Util.isEmpty(changeAttrMap)) {
			return;
		}

		// 向战场中广播属性变化(有可能自己是机器人先保证全局广播)
		this.brodcastChangeAttr();

		// 向自己广播属性变化
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect == null) {
			this.changeAttrMap.clear();
			return;
		}

		STC_ATTR_SYN_MSG.Builder msgBuilder = STC_ATTR_SYN_MSG.newBuilder();
		msgBuilder.setId(id);
		for (AttrType attr : changeAttrMap.keySet()) {
			float value = this.changeAttrMap.get(attr);
			PropUnit prop = PropUnit.newBuilder().setId(attr.getNumber()).setVal(value).build();
			msgBuilder.addProps(prop);
		}
		connect.sendMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_ATTR_SYN_VALUE, msgBuilder.build().toByteArray());

		this.changeAttrMap.clear();
	}

	/** 向客户端推送消息 */
	public void sendMsg(GeneratedMessageLite msg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(msg);
		}
	}

	public void sendMsg(int cmdType, int cmdId) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(cmdType, cmdId, null);
		}
	}

	public void clearChangeAttrMap() {
		this.changeAttrMap.clear();
	}

	/** 判断开炮是否冷却 */
	public boolean hadAndSetFireCoolTime() {
		return false;
	}

	public boolean hadFireCoolTime() {
		return false;
	}

	public void setLastFireTime(long fireTime) {
	};

	// 重置开炮冷却时间
	public void clearFireCoolTime() {
	}

	/** 检测坦克移动频率 */
	public boolean checkMoveTime() {
		return true;
	}

	/** 检测坦克移动位置 */
	public void checkMove(float x, float z) {
	}

	/** 清空移动错误记录 */
	public void clearClearMoveCount() {
	};

	/** 已命中冷却状态 */
	public boolean hadAndSetHitFireCoolTime() {
		return true;
	}

	/** 设置助攻 */
	public void putKiller(int roleId, long time) {
	}

	public Map<Integer, Long> getKillerMap() {
		return null;
	}

	public void removeKiller(int instanceId) {
	}

	/** 设置助攻数 */
	public int getHelpKill() {
		return 0;
	}

	public void setHelpKill(int helpKill) {
	}

	/** 协助数 */
	public int getAidKill() {
		return 0;
	}

	public void setAidKill(int aidKill) {
	}

	/** 协助伤害数 */
	public int getAidpAllDamage() {
		return 0;
	}

	public void setAidpAllDamage(int aidpAllDamage) {
	}

	/** 受到最后伤害 */
	public int getLastDamage() {
		return 0;
	}

	public void setLastDamage(int lastDamage) {
	}

	/** 累计输出伤害 */
	public void setHitAllDamage(int damage) {
	}

	/** 获取累计输出伤害 */
	public int getHitAllDamage() {
		return 0;
	}

	/** 累计承受伤害 */
	public void setReceiveAllDamage(int damage) {
	}

	/** 返回累计承受伤害 */
	public int getReceiveAllDamage() {
		return 0;
	}

	/** 设置死亡数 */
	public void setDeathCount(int deathCount) {
	};

	public int getDeathCount() {
		return 0;
	}

	/** 设置战斗力 */
	public float getMvpScore() {
		return 0f;
	}

	public void setMvpScore(float warScore) {
	}

	/** 设置一生击杀 */
	public int getLifeKillCount() {
		return 0;
	}

	public void setLifeKillCount(int lifeKillCount) {
	}

	/** 设置连杀 */
	public int getContinueKillCount() {
		return 0;
	}

	public void setContinueKillCount(int continueKillCount) {
	}

	/** 设置击杀数 */
	public int getKillCount() {
		return 0;
	}

	public void setKillCount(int killCount) {
	}

	/** 设置跳弹次数 */
	public int getDodgeCount() {
		return 0;
	}

	public void setDodgeCount(int dodgeCount) {
	}

	/** 设置未命中次数 */
	public int getMissCount() {
		return 0;
	}

	public void setMissCount(int missCount) {
	}

	/** 设置点灯次数 */
	public int getFindCount() {
		return 0;
	}

	public void setFindCount(int findCount) {
	}

	/** 设置击杀时间 */
	public long getKillTime() {
		return 0;
	}

	public void setKillTime(long killTime) {
	}

	/** 设置最大连杀数 */
	public int getMaxContinueKillCount() {
		return 0;
	}

	public void setMaxContinueKillCount(int maxContinueKillCount) {
	}

	/** 设置匹配分数 */
	public int getMatchScore() {
		return 0;
	}

	public void setMatchScore(int score) {
	}

	/** 语音扬声器序号 */
	public void setVoiceId(int voiceId) {
	}

	public int getVoiceId() {
		return 0;
	}

	/** 语音扬声器状态 */
	public void setVoiceState(int voiceState) {
	}

	public int getVoiceState() {
		return 0;
	}

	/** 判断目标是否在自己视野中 */
	public boolean view(AbstractInstance target) {
		return false;
	}

	public boolean viewSelf(AbstractInstance target) {
		return view(target);
	}

	/** 死亡是否不提供共享视野 */
	public boolean deathNotView() {
		return false;
	}

	/** 判断射程 */
	public boolean fireRange(AbstractInstance target) {
		return false;
	}

	// 判断俯仰角
	public boolean isFireElevation(AbstractInstance target) {
		return false;
	}

	// 判断是否有阻挡，有阻挡返回true
	public boolean hadBarrier(AbstractInstance target) {
		return true;
	}

	/** 任务记录器 */
	public BattleResultRecord getQuestRecord() {
		return null;
	}


	/** 广播属性变化 */
	private void brodcastChangeAttr() {
		if (Util.isEmpty(changeAttrMap)) {
			return;
		}
		boolean change = false;
		STC_ATTR_SYN_MSG.Builder msgBuilder = STC_ATTR_SYN_MSG.newBuilder();
		msgBuilder.setId(id);
		for (AttrType attr : changeAttrMap.keySet()) {
			if (!brodcastAttr.contains(attr)) {
				continue;
			}
			float value = this.changeAttrMap.get(attr);
			PropUnit prop = PropUnit.newBuilder().setId(attr.getNumber()).setVal(value).build();
			msgBuilder.addProps(prop);
			change = true;
		}

		// 向战场其他成员广播属性变化
		if (change) {
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
			if (mapInstance == null) {
				return;
			}
			mapInstance.brodcastMsg(roleId, BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_ATTR_SYN_VALUE, msgBuilder.build().toByteArray());
		}
	}

	public void set(AttrType attr, float value) {
		this.attribute.put(attr, value);
	}

	public Float get(AttrType attr) {
		Float value = this.attribute.get(attr);
		if (value == null) {
			return 0f;
		}
		return value;
	}

	public void syncCoordinate(AbstractInstance target, float x, float y, float z) {
		rm.syncCoordinate(target, x, y, z);
	}

	public void syncCoordinate(AbstractInstance target) {
		rm.syncCoordinate(target);
	}

	/** 记录属性改变，并通过 synchChangeAttr() 方法同步至客户端 */
	public void changeAttr(AttrType attr, float value) {
		this.set(attr, value);
		// 缓存已变化的属性
		this.changeAttrMap.put(attr, value);
	}

	public boolean isDeath() {
		int currHp = get(AttrType.hp).intValue();
		return currHp <= 0;
	}

	/** 是否为精英坦克 */
	public boolean isEliteTank() {
		return false;
	}

	/** 清空统计数据 */
	public void clearCount() {
	}

	// 放入道具
	public void putGoods(int goodsId, int count) {
	}

	// 删除道具
	public void removeGoods(int goodsId) {
	}

	// 返回物品数量
	public int getGoods(int goodsId) {
		return 0;
	}

	// 返回所有道具
	public Map<Integer, Integer> getAllGoods() {
		return null;
	}

	// 命中部位 0前,1侧,2后
	public TankEffectPeshe randomPeshe(int hitPart) {
		return null;
	}

	// 获取技能
	public Skill getSkill(int skillId) {
		return null;
	}

	// 添加技能
	public void putSkill(Skill skill) {
	}

	public Collection<Skill> getAllSkill() {
		return null;
	}

	// 设置战斗力
	public int getBattleScore() {
		return 0;
	}

	public void setBattleScore(int battleScore) {
	}
	
	public int getActualBattleScore() {
		return 0;
	}

	public void setActualBattleScore(int battleScore) {
	}

	// 设置军衔
	public int getTitleId() {
		return 0;
	}

	public void setTitleId(int titleId) {
	}

	// 协助总伤害
	public int getHelpAllDamage() {
		return 0;
	}

	public void setHelpAllDamage(int helpAllDamage) {
	}

	// 设置挂机
	public int getHandup() {
		return 0;
	}

	public void setHandup(int handup) {
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getMapInstanceId() {
		return mapInstanceId;
	}

	public void setMapInstanceId(int mapInstanceId) {
		this.mapInstanceId = mapInstanceId;
	}

	public TEAM getTeam() {
		return team;
	}

	public void setTeam(TEAM team) {
		this.team = team;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public Map<AttrType, Float> getAttribute() {
		return attribute;
	}

	public void setAttribute(Map<AttrType, Float> attribute) {
		this.attribute = attribute;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAiMain() {
		return aiMain;
	}

	public void setAiMain(int aiMain) {
		this.aiMain = aiMain;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Collection<IBuff> getBuffers() {
		return this.buffMap.values();
	}

	public Location getBirthLocation() {
		return birthLocation;
	}

	public void setBirthLocation(Location birthLocation) {
		this.birthLocation = birthLocation;
		this.x = birthLocation.getX();
		this.y = birthLocation.getY();
		this.z = birthLocation.getZ();
		this.angle = birthLocation.getDir();
		this.gunAngle = birthLocation.getDir();
		setGunAngle(birthLocation);
	}

	public int getAiType() {
		return aiType;
	}

	public void setAiType(int aiType) {
		this.aiType = aiType;
	}

	public String getAiStr() {
		return aiStr;
	}

	public void setAiStr(String aiStr) {
		this.aiStr = aiStr;
	}

	public int getAiInt() {
		return aiInt;
	}

	public void setAiInt(int aiInt) {
		this.aiInt = aiInt;
	}

	public int getRebirthGold() {
		return rebirthGold;
	}

	public void setRebirthGold(int rebirthGold) {
		this.rebirthGold = rebirthGold;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	/** 设置死亡时间 */
	public long getDeathTime() {
		return deathTime;
	}

	public void setDeathTime(long deathTime) {
		this.deathTime = deathTime;
	}

	public Ai getCurrAi() {
		return currAi;
	}

	private void setCurrAi(Ai currAi) {
		this.currAi = currAi;
	}

	public void putAi(Ai ai) {
		this.aiMap.put(ai.getType(), ai);
	}

	public Ai getAi(AiType type) {
		return aiMap.get(type);
	}

	public Collection<Ai> getAllAi() {
		return aiMap.values();
	}

	public TankAiActionTemplate getAiAction() {
		return aiAction;
	}

	public void setAiAction(TankAiActionTemplate aiAction) {
		this.aiAction = aiAction;
	}

	public TankAIPathTemplate getAiPathPoint() {
		return aiPathPoint;
	}

	public void setAiPathPoint(TankAIPathTemplate aiPathPoint) {
		this.aiPathPoint = aiPathPoint;
	}

	public boolean isAiRunning() {
		return aiRunning;
	}

	public void setAiRunning(boolean aiRunning) {
		this.aiRunning = aiRunning;
	}

	public boolean isWeakTeam() {
		return isWeakTeam;
	}

	public void setWeakTeam(boolean isWeakTeam) {
		this.isWeakTeam = isWeakTeam;
	}

	public AbstractInstance getAttacker() {
		return attacker;
	}

	public void setAttacker(AbstractInstance attacker) {
		this.attacker = attacker;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public boolean isEscape() {
		return isEscape;
	}

	public void setEscape(boolean isEscape) {
		this.isEscape = isEscape;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}



	public boolean isAiIsReduced() {
		return aiIsReduced;
	}

	public void setAiIsReduced(boolean aiIsReduced) {
		this.aiIsReduced = aiIsReduced;
	}




	public Map<AttrType, Float> getAiChangeAttribute() {
		return aiChangeAttribute;
	}

	public void setAiChangeAttribute(Map<AttrType, Float> aiChangeAttribute) {
		this.aiChangeAttribute = aiChangeAttribute;
	}

	public Map<INCOME_TYPE, List<IncomeInfo>> getIncomeMap() {
		return null;
	}

	public void setIncomeMap(Map<INCOME_TYPE, List<IncomeInfo>> incomeMap) {

	}

	public void aiTypeSwitch() {
		// 注意：
		// 以下各个逻辑分支，需要立即调用 return，避免选择优先级被代替
		// 每个逻辑分支判断条件不能带，判断是否当前状态是否 不等于要切换的状态，在 aiTypeSwitch（  type ） 函数里面会自动过滤
		/*
			比如：
			// 判断是否可以逃跑
			if( !isEscape && currAi.getType() != AiType.escape) {
				aiTypeSwitch(AiType.escape);
				return;
			}

			这样会导致该AI被pass，应该写成

			// 判断是否可以逃跑
			if( !isEscape ) {
				aiTypeSwitch(AiType.escape);
				return;
			}

			aiTypeSwitch(AiType.escape); 函数里面会自己做过滤

		 */

		Ai currAi = this.getCurrAi();
		// 是否空AI
		if (currAi == null) {
			this.moveAiSwitch();
			return;
		}

		// 如果当前是死亡AI，则停止一切判断
		if( getCurrAi().getType() == AiType.death ){
			return;
		}

		// 是否触发逃跑逻辑 或者 自杀逻辑
		if ( get(AttrType.hp) < get(AttrType.n_hpMax) * 1.0 * SysConfig.get(13) / 100) {
			// 判断是否可以逃跑，如果是站在旗内，则不能逃跑
			if( (!isEscape || this.currAi.getType() == AiType.escape) &&
					this.currAi.getType() != AiType.stayAndFire ) {
				aiTypeSwitch(AiType.escape);
				return;
			}
		}

		if ( getCurrAi().getType() == AiType.move ||
				getCurrAi().getType() == AiType.moveToFlag ||
				getCurrAi().getType() == AiType.fireAndMoveToEnemy ) {

			// 这些行走模式，如果坦克已经在圈子里面，则放弃该AI，改为停留在圈子里面，该模式高于其他模式
			if ( this.thinkNeedStayInFlag() ) {
				aiTypeSwitch(AiType.stayAndFire);
				return;
			}

			// 这些行走模式，当发现出现敌方坦克出现在圈内的时候，则要往圈内进攻前进
			if( this.thinkNeedFireToFlag() ){
				aiTypeSwitch(AiType.FireToFlag);
				return;
			}
		}
	}

	public void aiTypeSwitch(AiType aiType) {
		Ai ai = getAi(aiType);
		if (ai != null) {
			// 重置AI数据，好重新启动
			if( this.getCurrAi() != null ){
				if( aiType != this.getCurrAi().getType() ){
					//logger.debug( "{} Tank member id {}  AI {} to {}", this.getTeam().toString(), this.getMemberId(), this.getCurrAi().getType(), ai.getType());
					//System.out.println( this.getTeam().toString() + " Tank member id "+ this.getMemberId() +" AI " + this.getCurrAi().getType() + " to " + ai.getType() );
					ai.reset( this.getCurrAi().getTarget() );
					this.getCurrAi().clean();
					setLastAiType(this.getCurrAi().getType());
				}
			} else {
				ai.reset( null );
			}
			setCurrAi(ai);
		}
	}

	public void aiTypeSwitch(AiType aiType, AbstractInstance target) {
		Ai ai = getAi(aiType);
		if (ai != null) {
			// 重置AI数据，好重新启动
			if( this.getCurrAi() != null ){
				if( aiType != this.getCurrAi().getType() ){
					//logger.debug( "{} Tank member id {}  AI {} to {}", this.getTeam().toString(), this.getMemberId(), this.getCurrAi().getType(), ai.getType());
					//System.out.println( this.getTeam().toString() + " Tank member id "+ this.getMemberId() +" AI " + this.getCurrAi().getType() + " to " + ai.getType() );
					ai.reset( target );
					this.getCurrAi().clean();
					setLastAiType(this.getCurrAi().getType());
					this.setCurAISustainBeginTime(System.currentTimeMillis());
					this.setNeedCheckAIError(false);
				}
			} else {
				ai.reset( null );
			}
			setCurrAi(ai);
		}
	}

	public AiType getLastAiType() {
		return lastAiType;
	}

	public void setLastAiType(AiType lastAiType) {
		this.lastAiType = lastAiType;
	}

	public boolean isInFlag() {
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if (mapInstance != null && mapInstance instanceof SportMapInstance && mapInstance.getWarType() != WAR_TYPE.PERISH_VALUE) {
			SportMapInstance mapInstance2 = (SportMapInstance) mapInstance;
			MapTemplate mapTem = mapInstance2.getTemplate();

			float x = Math.abs(this.getX() - mapTem.getX());
			float z = Math.abs(this.getZ() - mapTem.getZ());

			int range = (int) Math.sqrt(x * x + z * z);
			if (mapTem.getRadius() > range) {
				return true;
			}
		}
		return false;
	}

	// 判断是否需要停留在旗子以内
	public boolean thinkNeedStayInFlag(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if (mapInstance != null && mapInstance instanceof SportMapInstance && mapInstance.getWarType() != WAR_TYPE.PERISH_VALUE) {
			SportMapInstance mapInstance2 = (SportMapInstance) mapInstance;
			MapTemplate mapTem = mapInstance2.getTemplate();

			float x = Math.abs(this.getX() - mapTem.getX());
			float z = Math.abs(this.getZ() - mapTem.getZ());

			int range = (int) Math.sqrt(x * x + z * z);
			if (mapTem.getRadius() - 3 > range) {
				return true;
			}
		}
		return false;
	}

	// 判断是否需要远离旗子
	public boolean thinkNeedKeepAwayFromFlag(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if (mapInstance != null && mapInstance instanceof SportMapInstance && mapInstance.getWarType() != WAR_TYPE.PERISH_VALUE) {
			SportMapInstance mapInstance2 = (SportMapInstance) mapInstance;
			MapTemplate mapTem = mapInstance2.getTemplate();

			float x = Math.abs(this.getX() - mapTem.getX());
			float z = Math.abs(this.getZ() - mapTem.getZ());

			int range = (int) Math.sqrt(x * x + z * z);
			if (mapTem.getRadius() + 10 > range) {
				return true;
			}
		}
		return false;
	}

	// 判断是否需要切换为向旗子方向进攻前进
	public boolean thinkNeedFireToFlag(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if( mapInstance.getStateMachine() instanceof BeginStateMachine ) {
			BeginStateMachine stateMachine = (BeginStateMachine) mapInstance.getStateMachine();
			if( stateMachine.getCurrHold() != BeginStateMachine.NO_HOLE ){
				if( stateMachine.getCurrHold() == BeginStateMachine.DOUBLE_HOLE ||
					( stateMachine.getCurrHold() == BeginStateMachine.BULE_HOLD && this.getTeam().getNumber() == TEAM.RED_VALUE ) ||
					( stateMachine.getCurrHold() == BeginStateMachine.RED_HOLD && this.getTeam().getNumber() == TEAM.BLUE_VALUE )){
					return true;
				}
			}
		}
		return false;
	}

	// 提供一种当前适合的行走AI
	public void moveAiSwitch(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if( mapInstance.getWarType() != WAR_TYPE.PERISH_VALUE ){
			int r = (int)(1+Math.random()*(100));
			if( this.thinkNeedFireToFlag() ){
				this.aiTypeSwitch(AiType.FireToFlag);
			} else if( (r < RED_MOVE_TO_FLAG_RANDOM && this.getTeam().getNumber() == TEAM.RED_VALUE) ||
					(r < BLUE_MOVE_TO_FLAG_RANDOM && this.getTeam().getNumber() == TEAM.BLUE_VALUE) ){
				this.aiTypeSwitch(AiType.moveToFlag);
			} else {
				this.aiTypeSwitch(AiType.move);
			}
		} else {
			this.aiTypeSwitch(AiType.move);
		}
	}

	// 判断是否需要切换为离开旗子
	public boolean thinkNeedFireOutFlag(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if( mapInstance.getStateMachine() instanceof BeginStateMachine ) {
			BeginStateMachine stateMachine = (BeginStateMachine) mapInstance.getStateMachine();
			if( stateMachine.getCurrHold() != BeginStateMachine.NO_HOLE ){
				if(( this.getTeam().getNumber() == TEAM.BLUE_VALUE && (stateMachine.getBlueHoldTime() * 100 / stateMachine.getHoldTime() > SysConfig.get(14) )) ||
					( this.getTeam().getNumber() == TEAM.RED_VALUE && (stateMachine.getRedHoldTime() * 100 / stateMachine.getHoldTime() > SysConfig.get(15) ))){
					return true;
				}
			}
		}
		return false;
	}

	// 判断是否需要靠近敌人，改函数在调用的情况是建立在，残血的时候，函数内部不做残血的判断
	public boolean thinkNeedMoveToEnemy( List<AbstractInstance> list ){
		if( this.currAi != null && this.currAi.getType() == AiType.fireAndMoveToEnemy ){
			list.add(this.currAi.getTarget());
			return true;
		}

		// 如果，该AI上一次执行还没超过3秒钟，不允许再次切换
		if( (int)(System.currentTimeMillis()/1000) - this.getMoveToEnemyLastTime() < 3 ){
			return false;
		}

		if( this.currAi != null && this.currAi.getType() == AiType.stayAndFire ){
			return false;
		}

		// 找出最近的玩家坦克
		AbstractInstance nearTank = this.findNearPlayerEnemyOfFire(null);
		if( nearTank != null ){
			// 判断坦克距离是否在需要靠近的范围内
			float range = Util.range(this.getX(), this.getZ(), nearTank.getX(), nearTank.getZ());
			if ( range > MoveToEnemyAi.MIN_RANGE + 10 ) {
				list.add(nearTank);
				return true;
			}
		}
		return false;
	}

	//寻找一个离自身最近的可攻击目标
	protected AbstractInstance findNearPlayerEnemyOfFire(AbstractInstance outside){
		float minRange = Integer.MAX_VALUE;
		AbstractInstance nearTank = null;
		//寻找离自身最近并在射程内的目标
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.isDeath()
					|| tank.getId() == this.getId()
					|| tank.getTeam() == this.getTeam()
					|| tank.isRobot() ){
				continue;
			}
			//排除现有目标外,另一个离自身最近的目标
			if(outside != null && tank.getId() == outside.getId()){
				continue;
			}
			float range = Util.range(this.getX(), this.getZ(), this.getX(), this.getZ());
			if(range < minRange){
				minRange = range;
				nearTank = tank;
			}
		}

		//判断是否在自己射程内,并进入视野
		if(this.get(AttrType.range) >= minRange && this.view(nearTank)){
			return nearTank;
		}
		return null;
	}

	// 提供一种当前适合的开火AI
	public void FireAiSwitch( AbstractInstance target ){
		int r = (int)(1+Math.random()*(100));
		if( (int)(System.currentTimeMillis()/1000) - this.getMoveToEnemyLastTime() > 3 && r < MOVE_AND_FIRE_RANDOM ){
			this.aiTypeSwitch(AiType.fireAndMoveToEnemy);
			this.setMoveToEnemyLastTime((int)(System.currentTimeMillis()/1000));
		} else {
			this.aiTypeSwitch(AiType.fire);
		}
	}

	public int getMoveToEnemyLastTime() {
		return moveToEnemyLastTime;
	}

	public void setMoveToEnemyLastTime(int moveToEnemyLastTime) {
		this.moveToEnemyLastTime = moveToEnemyLastTime;
	}

	public long getCurAISustainBeginTime() {
		return curAISustainBeginTime;
	}

	public void setCurAISustainBeginTime(long curAISustainBeginTime) {
		this.curAISustainBeginTime = curAISustainBeginTime;
	}

	public boolean isNeedCheckAIError() {
		return isNeedCheckAIError;
	}

	public void setNeedCheckAIError(boolean isNeedCheckAIError) {
		this.isNeedCheckAIError = isNeedCheckAIError;
	}

	public BehaviorTree<AbstractInstance> getBehaviorTree() {
		return btTree;
	}

	public void setBehaviorTree(BehaviorTree<AbstractInstance> btTree) {
		this.btTree = btTree;
	}

	public DataContext getBahaviorDataContext() {
		return dataContext;
	}

	// 暂时不调用该函数的检测，如果以后需要可以用该还是检测异常
	public void checkAIErrorStatus() {
		if( this.getCurrAi() == null ||
			this.getCurrAi().getType() == AiType.move ||
			this.getCurrAi().getType() == AiType.stayAndFire ){
			return ;
		}

		if( this.getCurAISustainBeginTime() != 0 ){
			// 如果某个AI时间持续了很长时间，则要对该AI做异常日志输出
			if( ( System.currentTimeMillis() - this.getCurAISustainBeginTime() ) > 60 * 1000 ){
				this.setNeedCheckAIError(true);
				logger.debug( "{} Tank member id {}  AI {} Sustain too long time {} s ", this.getTeam().toString(), this.getMemberId(), this.getCurrAi().getType(), ( System.currentTimeMillis() - this.getCurAISustainBeginTime() )/1000 );
			}
		}
	}

	public void performBehavior() {
		if (btTree != null) {
			btTree.eval(this);
		}
	}

	public RoadManager getRoadManager() {
		return rm;
	}

	public AbstractInstance getFireTarget() {
		return fireTarget;
	}

	public void setFireTarget(AbstractInstance fireTarget) {
		this.fireTarget = fireTarget;
	}
	public boolean isUsingFSM() {
		return usingFSM;
	}

	public void setUsingFSM(boolean usingFSM) {
		this.usingFSM = usingFSM;
	}

	public boolean isPreseason() {
		return false;
	}

	public double getGunAngle() {
		return gunAngle;
	}

	public void setGunAngle(double gunAngle) {
		this.gunAngle = gunAngle;
		if (this.gunAngle < 0) {
			this.gunAngle += 360;
		}
		this.gunAngle %= 360;
	}

	public void setGunAngle(Location loc) {
		float angle = loc.getDir();
		if (angle < 0) {
			angle += 360;
			angle = 360 - angle;
		}
		setGunAngle(angle);
	}

	public Cate getCate() {
		if (cate == null) {
			TankTemplate tt = getTankApp().getTankTemplate(templateId);
			if (tt != null) {
				cate = Cate.valueOf(tt.getTankType_i());
			}
		}
		return cate;
	}

	public boolean turrentTowardsWith(AbstractInstance target) {
		return false;
	}

	public Map<Integer, Integer> getAiParts() {
		return aiParts;
	}

	public void setAiParts(Map<Integer, Integer> aiParts) {
		this.aiParts = aiParts;
	}

	public long getFightScore() {
		return fightScore;
	}

	public void setFightScore(long fightScore) {
		this.fightScore = fightScore;
	}

	public int getFightRank() {
		return fightRank;
	}

	public void setFightRank(int fightRank) {
		this.fightRank = fightRank;
	}

	public int getBattleGoods() {
		return battleGoods;
	}

	public void setBattleGoods(int battleGoods) {
		this.battleGoods = battleGoods;
	}


	
}
