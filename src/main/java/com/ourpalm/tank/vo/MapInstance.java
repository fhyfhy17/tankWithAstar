package com.ourpalm.tank.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.google.protobuf.GeneratedMessageLite;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.app.map.ReadHightMapData;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.constant.Constant;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_KILL_TIPS_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USER_WAR_RECORD_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_WAR_SCORE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.TipType;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.template.PreseasonTemplate;

public abstract class MapInstance {

	protected static final Logger logger = LogCore.runtime;
	protected int instanceId; // 地图实例ID
	protected String uniqueId;//战场唯一ID
	protected Map<Integer, AbstractInstance> tanks = new ConcurrentHashMap<>(); // 所有在线坦克列表,玩家下线得删除
	protected Map<Integer, IBuff> buffMap = new ConcurrentHashMap<>(); // buff列表
	protected Set<Integer> allTanks = new HashSet<>(); // 保存所有坦克ID,包括已离线的.用于坦克回收
	protected Map<Integer, Integer> deathMap = new ConcurrentHashMap<>(); // 死亡列表
	protected StateMachine stateMachine; // 地图状态机
	protected boolean firstKill = false; // 第一次击杀(一血)
	protected AtomicBoolean state = new AtomicBoolean(false); // 执行状态
	protected Object lock = new Object(); // 战斗锁
	protected int blueKillNum; // 蓝方击杀数
	protected int redKillNum; // 红方击杀数

	protected int blueMaxContinueLose;
	protected int redMaxContinueLose;

	protected Grid grid; // 网格信息
	protected ReadHightMapData readHightMapData; // 地图高度数据
	protected int warType; // 战场类型
	protected Queue<HitParam> hitQueue = new ConcurrentLinkedQueue<>();

	protected PreseasonTemplate preseasonTemplate;//新手赛模板
	protected Map<Integer,Integer> tankEnterScheduleMap = new HashMap<>();//进入游戏 进度
	//ai视野 
	protected 	ConcurrentHashMap<Integer, Set<Integer>> viewMap = new ConcurrentHashMap<>();


	
	public MapInstance(int instanceId) {
		this.instanceId = instanceId;
	}

	/** 进入地图 */
	public void enter(AbstractInstance tank) {
		tanks.put(tank.getId(), tank);
		allTanks.add(tank.getId());
		tank.setMapInstanceId(instanceId);
	}

	/** 离开地图 */
	public void leave(int tankInstanceId) {
		tanks.remove(tankInstanceId);
	}

	/** 清场操作 */
	public void leaveAll() {
		tanks.clear();
	}

	/** 广播消息 */
	public void brodcastMsg(Integer filterRoleId, Integer cmdType, Integer cmdId, byte[] data) {
		for (AbstractInstance tank : tanks.values()) {
			if (tank.getRoleId() == filterRoleId || tank.isRobot()) {
				continue;
			}
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
			if (connect != null) {
				connect.sendMsg(cmdType, cmdId, data);
			}
		}
	}

	/** 广播消息 */
	public void brodcastMsg(Integer cmdType, Integer cmdId, byte[] data) {
		for (AbstractInstance tank : tanks.values()) {
			if (tank.isRobot()) {
				continue;
			}
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
			if (connect != null) {
				connect.sendMsg(cmdType, cmdId, data);
			}
		}
	}

	/** 广播消息 */
	public void brodcastMsg(GeneratedMessageLite message) {
		int[] cmds = GameContext.getMsgParser().clazzToId(message.getClass());
		if (cmds == null) {
			LogCore.runtime.error("消息对应的ID未配置，Message={}", message.getClass().getSimpleName());
			return;
		}
		this.brodcastMsg(cmds[0], cmds[1], message.toByteArray());
	}

	/** AI广播消息 */
	public void aiBrodcastMsg(Integer cmdType, Integer cmdId, byte[] data) {
		for (AbstractInstance tank : tanks.values()) {
			if (tank.getAiMain() == 1 || tank.isRobot()) {
				continue;
			}
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
			if (connect != null) {
				connect.sendMsg(cmdType, cmdId, data);
			}
		}
	}

	/** 获取天气 */
	public int getWeather() {
		return 1;
	}

	/** 坦克死亡方法 */
	public abstract void death(AbstractInstance killer, AbstractInstance target);

	public abstract void death(AbstractInstance tank);

	public abstract void update();

	// 通知状态机
	public abstract void notify(NotifyState state, int tankInstanceId);

	/** 返回比赛开始了多长时间 */
	public abstract int durationTime();

	/** 返回地图id */
	public abstract String getMapId();

	public abstract void deathNotifyMsg(AbstractInstance killer, AbstractInstance target);

	/** 地图中是否有玩家 */
	public boolean hadPlayer() {
		for (AbstractInstance tank : this.getAllTank()) {
			if (!tank.isRobot()) {
				return true;
			}
		}
		return false;
	}

	// 处理buff
	protected void runBuff() {
		for (IBuff buff : this.buffMap.values()) {
			try {
				buff.update();
			} catch (Exception e) {
				LogCore.runtime.error("地图buff : " + buff.getId(), e);
			}
		}
	}

	/** 处理坦克更新 */
	protected void runUpate() {
		for (AbstractInstance tank : this.getAllTank()) {
			try {
				tank.update();
			} catch (Exception e) {
				LogCore.runtime.error("buff tankInstance id : " + tank.getId(), e);
			}
		}
	}

	/** 广播战绩 */
	public void brodcastWarScore(int overTime) {
		STC_WAR_SCORE_MSG scoreMsg = STC_WAR_SCORE_MSG.newBuilder().setBlue(blueKillNum).setRed(redKillNum).setTime(overTime).build();
		this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_WAR_SCORE_VALUE, scoreMsg.toByteArray());
	}

	/** 统计击杀数 */
	protected void calcuKillCount(AbstractInstance killer, AbstractInstance target) {
		if (killer.getId() == target.getId()) {
			return;
		}
		// 击杀者不算助攻
		target.removeKiller(killer.getId());

		// 统计死亡次数
		target.setDeathCount(target.getDeathCount() + 1);

		// 统计击杀次数
		killer.setKillCount(killer.getKillCount() + 1);
		// 排行榜加击杀
		GameContext.getRankApp().rankAddKill(killer.getRoleId());

		// 一生累计击杀数
		killer.setLifeKillCount(killer.getLifeKillCount() + 1);

		// 累计助攻次数
		this.calcuHelpKill(target);

		// 累计连杀数
		this.calcuContinueKill(killer);
	}

	/** 计算协助伤害 */
	protected void calcuAidKill(AbstractInstance killer, AbstractInstance target) {
		List<AbstractInstance> aidTanks = new ArrayList<AbstractInstance>();
		for(AbstractInstance tank : this.getAllTank()){
			if(target.getTeam() == tank.getTeam()
					|| tank.getId() == killer.getId()
					|| tank.deathNotView()){
				continue;
			}
			// 不在助攻列表中
			if (target.getKillerMap().containsKey(tank.getId())) {
				continue;
			}
			// 判断敌方是否看到死者
			float view = tank.get(AttrType.n_view);
			float range = Util.range(target.getX(), target.getZ(), tank.x, tank.z);
			if (view >= range) {
				tank.setAidKill(tank.getAidKill() + 1);
				aidTanks.add(tank);
				// 发送战绩
				this.notifyUserWarRecord(tank);
			}
		}

		if (Util.isEmpty(aidTanks)) {
			return;
		}

		// 计算协助伤害
		float lastDamage = target.getLastDamage() / aidTanks.size();
		for (AbstractInstance tank : aidTanks) {
			tank.setAidpAllDamage(tank.getAidpAllDamage() + (int) lastDamage);
		}
	}

	// 任务击杀统计
	protected abstract void deathQuest(AbstractInstance killer, AbstractInstance target);

	/** 击杀提示 */
	protected void killTipsNotify(AbstractInstance killer, AbstractInstance death) {
		if (killer.getId() == death.getId()) {
			return;
		}
		// 判断第一滴血
		if (!firstKill) {
			this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), death.getRoleName(), TipType.first_kill);

			// 设置状态
			this.firstKill = true;

			return;
		}

		// 连杀优先、一生累计击杀、普通击杀
		// 普通击杀提示
		if (hasCommonKillTips(killer, death)) {
			this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), death.getRoleName(), TipType.kill);
		}

		// 终结提示
		final int lifeKill = death.getLifeKillCount();
		if (lifeKill > 1) {
			this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), death.getRoleName(), TipType.stop_kill);
		}

		// 团灭提示
		boolean hadLive = false;
		for (AbstractInstance ist : tanks.values()) {
			if (ist.getTeam() == death.getTeam() && !ist.isDeath()) {
				hadLive = true;
			}
		}
		// 队友没人活着，代表团灭
		if (!hadLive) {
			this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), "", TipType.aced);
		}
	}

	/** 发送击杀广播 */
	private void killTipsNotifyMsg(int killerTankId, int killerTeam, String KillerName, int deathTankId, String deathName, TipType tipType) {

		STC_KILL_TIPS_MSG tips = STC_KILL_TIPS_MSG.newBuilder().setTankId(killerTankId).setTeam(killerTeam).setUserName(KillerName).setDieTankId(deathTankId).setDieName(deathName).setTipType(tipType)
				.build();

		this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_KILL_TIPS_VALUE, tips.toByteArray());
	}

	// 判断是否发送普通击杀广播
	private boolean hasCommonKillTips(AbstractInstance killer, AbstractInstance death) {
		// 优先连续击杀显示
		if (this.continueKillTips(killer, death)) {
			return false;
		}
		// 其次一生累计击杀显示
		if (this.lifeKillTips(killer, death)) {
			return false;
		}
		return true;
	}

	// 连杀通知消息
	private boolean continueKillTips(AbstractInstance killer, AbstractInstance death) {
		int continueKill = killer.getContinueKillCount();
		if (continueKill < 2) {
			return false;
		}
		TipType tipType = TipType.double_kill;
		switch (continueKill) {
		case 3:
			tipType = TipType.three_kill;
			break;
		case 4:
			tipType = TipType.four_kill;
			break;
		case 5:
			tipType = TipType.five_kill;
			break;
		}

		this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), death.getRoleName(), tipType);

		return true;
	}

	// 一生中累计击杀提示
	private boolean lifeKillTips(AbstractInstance killer, AbstractInstance death) {
		final int lifeKill = killer.getLifeKillCount();
		if (lifeKill < 3) {
			return false;
		}
		TipType tipType = TipType.special_kill;
		switch (lifeKill) {
		case 4:
			tipType = TipType.erupt_kill;
			break;
		case 5:
			tipType = TipType.no_stop_kill;
			break;
		case 6:
			tipType = TipType.dominate;
			break;
		case 7:
			tipType = TipType.god;
			break;
		}
		if (lifeKill > 7) {
			tipType = TipType.super_god;
		}
		this.killTipsNotifyMsg(killer.getTemplateId(), killer.getTeam().getNumber(), killer.getRoleName(), death.getTemplateId(), death.getRoleName(), tipType);

		return true;
	}

	// 累计连杀数
	private void calcuContinueKill(AbstractInstance killer) {
		long nowTime = System.currentTimeMillis();
		// 当前连杀数
		int currContinueKill = killer.getContinueKillCount();
		if ((nowTime - killer.getKillTime()) < this.continueKillTimeInterval(currContinueKill)) {
			// 设置连杀数
			currContinueKill += 1;
			killer.setContinueKillCount(currContinueKill);

			// 设置本场最大连杀数
			if (currContinueKill > killer.getMaxContinueKillCount()) {
				killer.setMaxContinueKillCount(currContinueKill);
			}
			killer.setKillTime(nowTime);
			return;
		}
		// 设置击杀时间
		killer.setKillTime(nowTime);
		killer.setContinueKillCount(1);
	}

	// 连杀时间间隔(单位毫秒)
	private int continueKillTimeInterval(int continueKillCount) {
		switch (continueKillCount) {
		case 1:
			return 10 * 1000; // 1杀到2杀时间间隔
		case 2:
			return 12 * 1000;
		case 3:
			return 14 * 1000;
		case 4:
			return 16 * 1000;
		}
		return 0;
	}

	// 累计助攻数
	private void calcuHelpKill(AbstractInstance death) {
		final Map<Integer, Long> killerMap = death.getKillerMap();
		if (Util.isEmpty(killerMap)) {
			return;
		}

		for (int instanceId : killerMap.keySet()) {
			long killTime = killerMap.get(instanceId);
			// 击杀前5秒算助攻
			if ((System.currentTimeMillis() - killTime) < Constant.HELP_KILL_DEATH_TIME_INTERVAL) {
				AbstractInstance killerTank = this.tanks.get(instanceId);
				if (killerTank == null) {
					continue;
				}
				//TODO 收益GameContext.getBattleApp().sendHitIcon(killerTank, INCOME_ICON_TYPE.INCOME_ICON_HELP);
				killerTank.setHelpKill(killerTank.getHelpKill() + 1);
				// 排行榜加助攻
				GameContext.getRankApp().rankAddHelp(killerTank.getRoleId());
				// 发送个人战绩
				this.notifyUserWarRecord(killerTank);

				continue;
			}
			killerMap.remove(instanceId);
		}
	}

	// 发送个人战绩
	public void notifyUserWarRecord(AbstractInstance tank) {
		if (tank.isRobot()) {
			return;
		}
		STC_USER_WAR_RECORD_MSG msg = STC_USER_WAR_RECORD_MSG.newBuilder().setDeath(tank.getDeathCount()).setHelpKill(tank.getHelpKill()).setKill(tank.getKillCount()).setAidKill(tank.getAidKill())
				.build();
		tank.sendMsg(msg);
	}

	public void putBuff(IBuff buff) {
		this.buffMap.put(buff.getId(), buff);
	}

	public IBuff remove(int id) {
		return this.buffMap.remove(id);
	}

	public int getInstanceId() {
		return instanceId;
	}

	public AtomicBoolean getState() {
		return state;
	}

	public Collection<AbstractInstance> getAllTank() {
		return this.tanks.values();
	}

	public AbstractInstance getTank(int instanceId) {
		return this.tanks.get(instanceId);
	}

	public Collection<Integer> getAllTanksId() {
		return this.allTanks;
	}

	public StateMachine getStateMachine() {
		return stateMachine;
	}

	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public int getBlueKillNum() {
		return blueKillNum;
	}

	public int getRedKillNum() {
		return redKillNum;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public ReadHightMapData getReadHightMapData() {
		return readHightMapData;
	}

	public void setReadHightMapData(ReadHightMapData readHightMapData) {
		this.readHightMapData = readHightMapData;
	}

	public int getWarType() {
		return warType;
	}

	public void putHitQueue(HitParam param) {

		this.hitQueue.add(param);
		if (param.getSource().isRobot()) {
			param.getSource();
		}
	}

	public Queue<HitParam> getHitQueue() {
		return this.hitQueue;
	}


	public PreseasonTemplate getPreseasonTemplate() {
		return preseasonTemplate;
	}

	public void setPreseasonTemplate(PreseasonTemplate preseasonTemplate) {
		this.preseasonTemplate = preseasonTemplate;
	}

	public int getBlueMaxContinueLose() {
		return blueMaxContinueLose;
	}

	public void setBlueMaxContinueLose(int blueMaxContinueLose) {
		this.blueMaxContinueLose = blueMaxContinueLose;
	}

	public int getRedMaxContinueLose() {
		return redMaxContinueLose;
	}

	public void setRedMaxContinueLose(int redMaxContinueLose) {
		this.redMaxContinueLose = redMaxContinueLose;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Map<Integer, Integer> getTankEnterScheduleMap() {
		return tankEnterScheduleMap;
	}

	public void setTankEnterScheduleMap(Map<Integer, Integer> tankEnterScheduleMap) {
		this.tankEnterScheduleMap = tankEnterScheduleMap;
	}

	public ConcurrentHashMap<Integer, Set<Integer>> getViewMap() {
		return viewMap;
	}

	public void setViewMap(ConcurrentHashMap<Integer, Set<Integer>> viewMap) {
		this.viewMap = viewMap;
	}

	public void forceClose() {
		// TODO Auto-generated method stub
		this.stateMachine.forceClose();
	}
}
