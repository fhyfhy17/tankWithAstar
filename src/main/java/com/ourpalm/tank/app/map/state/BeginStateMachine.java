package com.ourpalm.tank.app.map.state;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.app.map.ai.Ai;
import com.ourpalm.tank.app.map.ai.node.RobotBehavior;
import com.ourpalm.tank.app.quest.BattleResultRecord;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_TYPE;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HOLD_FLAG_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_WAR_REPORT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.WarReportItem;
import com.ourpalm.tank.message.BATTLE_MSG.WarResultItem;
import com.ourpalm.tank.message.BATTLE_MSG.WarRewardEnum;
import com.ourpalm.tank.message.BATTLE_MSG.WarRewardEnumInfo;
import com.ourpalm.tank.message.BATTLE_MSG.WarRewardInfo;
import com.ourpalm.tank.message.BATTLE_MSG.WarStatisticsItem;
import com.ourpalm.tank.message.MATCH_MSG.STC_BEGIN_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_ONLY_QUIT_TEAM_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_PLAYER_READY_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.BattleHandUpTemplate;
import com.ourpalm.tank.template.ExtraIncomeTemplate;
import com.ourpalm.tank.template.ExtraIncomeTextTemplate;
import com.ourpalm.tank.template.GoodsShellTemplate;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.IncomeDesc;
import com.ourpalm.tank.vo.IncomeInfo;
import com.ourpalm.tank.vo.SportMapInstance;
import com.ourpalm.tank.vo.TeamIncomeTypeInfo;
import com.ourpalm.tank.vo.behaviortree.BehaviorTree;

public abstract class BeginStateMachine implements StateMachine {
	protected final Logger logger = LogCore.runtime;
	public final static int RED_HOLD = 1; // 红方占旗
	public final static int BULE_HOLD = 2; // 蓝方占旗
	public final static int DOUBLE_HOLE = 3; // 双方占旗
	public final static int NO_HOLE = 4; // 无一方占旗

	protected long beginTime; // 比赛开始时间
	protected long time; // 比赛时长

	protected SportMapInstance mapInstance;
	protected MapTemplate mapTemplate;

	protected TEAM winTeam; // 获胜方
	protected boolean flagWin = false; // 是否占旗获胜
	protected int mvpTankId; // MVP获得者坦克实例ID
	protected int mvpRoleId; // MVP获得者
	protected int mvpCount = 1; // MVP获得次数

	protected final float x; // 旗帜位置
	// private float y;
	protected final float z;
	protected final float radius; // 半径

	protected int currHold = NO_HOLE; // 当前状态
	protected int redHoldTime; // 红方占旗时间
	protected int blueHoldTime; // 蓝方占旗时间
	protected final int holdTime; // 获胜占领时间(单位:秒)

	protected int readyTime; // 比赛开始倒计时(单位:秒)
	protected boolean beginBuffAdd = false; // 是否已添加比赛开始BUFF

	protected STC_WAR_REPORT_MSG warReportMsg; // 战报信息

	public BeginStateMachine(SportMapInstance mapInstance) {
		this.mapInstance = mapInstance;
		this.beginTime = System.currentTimeMillis();

		this.mapTemplate = mapInstance.getTemplate();

		this.time = (mapTemplate.getOverTime() + mapTemplate.getReadyTime() + 2) * 1000;
		this.readyTime = mapTemplate.getReadyTime() + 2;

		this.x = mapTemplate.getX();
		// this.y = mapTemplate.getY();
		this.z = mapTemplate.getZ();
		this.radius = mapTemplate.getRadius();
		this.holdTime = mapTemplate.getTime();

		// 通知地图实例开始计时
		mapInstance.notify(NotifyState.begin, 1);

		BehaviorTree<AbstractInstance> bt = RobotBehavior.STANDARD.createBehaviorTree(mapInstance);

		int sizeOfAi = 0;

		// 初始化AI
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (!tank.isRobot()) {
				continue;
			}
			sizeOfAi++;
			bt.initialize(tank);
			tank.set(AttrType.n_view, tank.get(AttrType.n_view) / 2);
			tank.getRoadManager().initialize();
			tank.setBehaviorTree(bt);

			for (Ai ai : tank.getAllAi()) {
				ai.init();
			}
			// 当前AI设置为移动
			tank.moveAiSwitch();
		}

		int numOfAiUsingFSM = (int) (sizeOfAi * (getTankApp().getAiConfig().getFsmScale() / 100f));
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (!tank.isRobot()) {
				continue;
			}
			if (tank.isPreseason()) {
				tank.setUsingFSM(true);
				continue;
			}
			if (numOfAiUsingFSM > 0) {
				numOfAiUsingFSM--;
				tank.setUsingFSM(true);
			}
		}
	}

	// 是否正式开始
	protected boolean hadBeginGame() {
		return (System.currentTimeMillis() - this.beginTime) > (this.readyTime * 1000);
	}

	/** 添加出场buff */
	protected void addBeginBuff() {
		// 开场添加buff
		if (!this.beginBuffAdd) {
			// 添加出场buff
			for (AbstractInstance tank : mapInstance.getAllTank()) {
				GameContext.getBuffApp().putBuff(tank, tank, mapTemplate.getBirthBuffId());
				if (tank.isRobot()) {
					tank.setAiRunning(true);
				}
			}
			//开场 穿甲弹
			chuanJianDanDeal();
			
			this.beginBuffAdd = true;
		}
	}

	private void chuanJianDanDeal() {
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (!tank.isRobot()) {
				int goodsId = getDanByTankType(tank);
				int count = tank.getGoods(goodsId);
				if (count <= 0) {
					continue;
				}
				// 扣除道具
				final int currCount = count - 1;
				tank.putGoods(goodsId, currCount);

				// 扣除物品
				final int roleId = tank.getRoleId();
				GameContext.getGoodsApp().removeGoods(roleId, goodsId, 1, OutputType.tankFireUseGoodsDec.getInfo());
				tank.setBattleGoods(goodsId);
				final GoodsShellTemplate goodsShellTemplate = GameContext.getGoodsApp().getGoodsShellTemplate(goodsId);
				if (goodsShellTemplate != null) {
					tank.changeAttr(AttrType.n_stab, tank.get(AttrType.n_stab) * (1 + goodsShellTemplate.getChuajia()));
				}
			}
		}
	}

	private int getDanByTankType(AbstractInstance tank) {
		TankTemplate t = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		if (t == null) {
			return 0;
		}
		int type = t.getTankType_i();
		switch (type) {
		case 1:
			return 101;
		case 2:
			return 102;
		case 3:
			return 103;
		case 4:
			return 104;
		default:
			break;
		}
		return 0;
	}
	
	// 切换比赛结束状态
	protected void changeEndStateMachine() {
		if (this.winTeam == null) {
			return;
		}
		// 删除对战对象
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.isRobot()) {
				continue;
			}
			if (tank.getBehaviorTree() != null) {
				tank.getBehaviorTree().clearChildren(tank);
				tank.getBahaviorDataContext().reset();
				tank.setBehaviorTree(null);
			}
			GameContext.getMatchApp().removeRoleBattle(tank.getRoleId());
			
			// 解除组队 （估计是临时方案）
			try {
				GameContext.getMatchApp().teamQuit(tank.getRoleId());
				STC_ONLY_QUIT_TEAM_MSG.Builder onlyQuitBuilder = STC_ONLY_QUIT_TEAM_MSG.newBuilder();
				RoleConnect con = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
				if (con != null) {
					con.sendMsg(onlyQuitBuilder.build());
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		
		StateMachine stateMachine = StateMachineFactory.createEndStateMachine(mapInstance, this.warReportMsg);
		mapInstance.setStateMachine(stateMachine);
		mapInstance.leaveAll();
	}

	// 判断占旗逻辑
	protected void checkCaptureFlag() {
		// 已分出胜负返回
		if (this.winTeam != null) {
			return;
		}

		final int currState = this.checkHoldState();
		// 累加占领时间
		switch (currState) {
		case RED_HOLD:
			this.redHoldTime += 1;
			if (this.redHoldTime > this.holdTime) {
				this.winTeam = TEAM.RED;
				break;
			}
			this.blueHoldTime = 0;
			break;
		case BULE_HOLD:
			this.blueHoldTime += 1;
			if (this.blueHoldTime > this.holdTime) {
				this.winTeam = TEAM.BLUE;
				break;
			}
			this.redHoldTime = 0;
			break;

		case NO_HOLE:
			this.redHoldTime = 0;
			this.blueHoldTime = 0;
			break;
		}

		// 占旗获胜方更新任务进度
		if (this.winTeam != null) {
			this.flagWin = true;
			for (AbstractInstance tank : this.holdFlagTanks(winTeam)) {
				BattleResultRecord record = tank.getQuestRecord();
				if (record != null) {
					record.setFlagWin(true);
				}
			}
		}

		// 当前状态没发生变化
		if (currState == currHold) {
			return;
		}

		// 广播战旗消息
		this.notifyFlagMsg(currState);

		// 设置当前状态
		this.currHold = currState;
	}

	// 广播战旗消息
	private void notifyFlagMsg(int currState) {
		switch (currState) {
		case DOUBLE_HOLE:
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_HOLD_PAUSE_VALUE, null);

			// 更新打断占领旗成就任务进度
			this.interruptHoldFlagQuest(currHold);

			break;

		case RED_HOLD:
			STC_HOLD_FLAG_MSG redHoldMsg = STC_HOLD_FLAG_MSG.newBuilder().setCurrTime(this.redHoldTime).setEndTime(this.holdTime).setTeamId(TEAM.RED_VALUE).build();
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_HOLD_FLAG_VALUE, redHoldMsg.toByteArray());
			break;

		case BULE_HOLD:
			STC_HOLD_FLAG_MSG buleHoldMsg = STC_HOLD_FLAG_MSG.newBuilder().setCurrTime(this.blueHoldTime).setEndTime(this.holdTime).setTeamId(TEAM.BLUE_VALUE).build();
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_HOLD_FLAG_VALUE, buleHoldMsg.toByteArray());
			break;

		case NO_HOLE:
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_HOLD_STOP_VALUE, null);
			break;
		}
	}

	// 打断占领成就任务进度
	private void interruptHoldFlagQuest(int lastHoldState) {
		TEAM interrupTeam = null;
		switch (lastHoldState) {
		case RED_HOLD:
			interrupTeam = TEAM.BLUE;
			break;
		case BULE_HOLD:
			interrupTeam = TEAM.RED;
			break;
		}
		if (interrupTeam == null) {
			return;
		}
	}

	// 判断当前占旗状态
	private int checkHoldState() {
		boolean blueTeam = this.isHoldFlag(TEAM.BLUE);
		boolean redTeam = this.isHoldFlag(TEAM.RED);
		// 双方同时占旗
		if (blueTeam && redTeam) {
			return DOUBLE_HOLE;
		}
		if (blueTeam) {
			return BULE_HOLD;
		}
		if (redTeam) {
			return RED_HOLD;
		}

		return NO_HOLE;
	}

	// 是否占旗
	private boolean isHoldFlag(TEAM team) {
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.isDeath() || tank.getTeam() != team) {
				continue;
			}
			if (this.inRadius(tank.getX(), tank.getZ())) {
				// logger.debug("旗帜范围内 name = {} [{},{},{}]",
				// tank.getRoleName(), tank.getX(), tank.getY(), tank.getZ());
				return true;
			}
		}
		return false;
	}

	// 返回占旗坦克
	private List<AbstractInstance> holdFlagTanks(TEAM team) {
		final List<AbstractInstance> list = new ArrayList<>();
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.isDeath() || tank.getTeam() != team) {
				continue;
			}
			if (this.inRadius(tank.getX(), tank.getZ())) {
				list.add(tank);
			}
		}
		return list;
	}

	/** 判断是否在旗帜范围内 */
	private boolean inRadius(float tankX, float tankZ) {
		float x = Math.abs(tankX - this.x);
		float z = Math.abs(tankZ - this.z);

		int range = (int) Math.sqrt(x * x + z * z);
		if (this.radius > range) {
			return true;
		}
		return false;
	}

	// 判断比赛结束时间
	protected void checkOverTime() {
		// 已分出胜负返回
		if (this.winTeam != null) {
			return;
		}

		if ((System.currentTimeMillis() - beginTime) > time) {
			int blueKill = mapInstance.getBlueKillNum();
			int redKill = mapInstance.getRedKillNum();
			if (blueKill > redKill) {
				this.winTeam = TEAM.BLUE;
			} else if (redKill > blueKill) {
				this.winTeam = TEAM.RED;
			} else {
				// 伤害总量判断输赢
				int blueAllHurt = 0;
				int redAllHurt = 0;
				for (int instanceId : mapInstance.getAllTanksId()) {
					AbstractInstance tank = GameContext.getTankApp().getInstance(instanceId);
					if (tank == null) {
						continue;
					}
					if (tank.getTeam() == TEAM.BLUE) {
						blueAllHurt += tank.getHitAllDamage();
					} else {
						redAllHurt += tank.getHitAllDamage();
					}
				}
				if (blueAllHurt > redAllHurt) {
					this.winTeam = TEAM.BLUE;
				} else if (redAllHurt > blueAllHurt) {
					this.winTeam = TEAM.RED;
				} else {
					this.winTeam = TEAM.BLUE;
				}
			}
			logger.debug("红方  {} VS 蓝方 {}", redKill, blueKill);
		}
	}

	
	protected void fightScoreSort() {
		List<AbstractInstance> redList = new ArrayList<>();
		List<AbstractInstance> blueList = new ArrayList<>();
		for (AbstractInstance ist : mapInstance.getAllTank()) {
			int findNum = 1;
			List<IncomeInfo> findList = ist.getIncomeMap().get(INCOME_TYPE.INCOME_TYPE_FIND);
			if (findList != null) {
				findNum = findList.size();
			}
			long score = findNum * ist.getHitAllDamage() == 0 ? 1 : findNum * ist.getHitAllDamage();
			ist.setFightScore(score);
			if (ist.getTeam() == TEAM.BLUE) {
				blueList.add(ist);
			}
			if (ist.getTeam() == TEAM.RED) {
				redList.add(ist);
			}
		}
		Collections.sort(redList, comp);
		Collections.sort(blueList, comp);
		AbstractInstance blueMvpTank = null;
		AbstractInstance redMvpTank = null;
		AbstractInstance mvpTank = null;
		if (!Util.isEmpty(redList)) {
			for (int i = 0; i < redList.size(); i++) {
				redList.get(i).setFightRank(i + 1);
			}
			redMvpTank = redList.get(0);
			mvpTank = redMvpTank;
		}
		if (!Util.isEmpty(blueList)) {
			for (int i = 0; i < blueList.size(); i++) {
				blueList.get(i).setFightRank(i + 1);
			}
			blueMvpTank = blueList.get(0);
			mvpTank = blueMvpTank;
		}

		if (redMvpTank != null && blueMvpTank != null) {
			List<AbstractInstance> list = new ArrayList<>();
			list.add(redMvpTank);
			list.add(blueMvpTank);
			Collections.sort(list, comp);
			mvpTank = list.get(0);
		}

		if (mvpTank == null) {
			return;
		}
		this.mvpTankId = mvpTank.getId();
		this.mvpRoleId = mvpTank.getRoleId();

		if (mvpTank.isRobot()) {
			return;
		}
		// 设置获得MVP
		BattleResultRecord record = mvpTank.getQuestRecord();
		record.setMvp(true);
		logger.debug("获胜方={} MVP={}", this.winTeam, this.mvpRoleId);
	}
	
	Comparator<AbstractInstance> comp = new Comparator<AbstractInstance>() {
		
		@Override
		public int compare(AbstractInstance g1, AbstractInstance g2) {
			if (g1.getFightScore()< g2.getFightScore()) {
				return 1;
			} else if (g1.getFightScore() > g2.getFightScore()) {
				return -1;
			} else {
				if (g1.getHitAllDamage()<g2.getHitAllDamage()) {
					return 1;
				} else if (g1.getHitAllDamage() > g2.getHitAllDamage()) {
					return -1;
				} else {
					if (g1.getBattleScore()< g2.getBattleScore()) {
						return 1;
					} else if (g1.getBattleScore() >g2.getBattleScore()) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		}
	};
	
	// MVP通报
	protected void checkMVP() {
		if (this.winTeam == null) {
			return;
		}

		AbstractInstance mvpTank = null;
		// 获取获胜方的坦克
		for (AbstractInstance ist : mapInstance.getAllTank()) {
			// MVP得分 = (击杀数 *1.6 + 助攻数 * 0.8 + 协作 * 0.5）* （1 - 死亡数 * 0.05）
			float score = (ist.getKillCount() * 1.6f + ist.getHelpKill() * 0.8f + ist.getAidKill() * 0.5f) * (1f - ist.getDeathCount() * 0.05f);
			ist.setMvpScore(score);
			if (this.winTeam == ist.getTeam()) {
				if (mvpTank == null) {
					mvpTank = ist;
					continue;
				}
				if (ist.getMvpScore() > mvpTank.getMvpScore()) {
					mvpTank = ist;
				}
			}
		}
		if (mvpTank == null) {
			return;
		}

//		this.mvpTankId = mvpTank.getId();
//		this.mvpRoleId = mvpTank.getRoleId();

//		logger.debug("获胜方{} MVP{}", this.winTeam, this.mvpRoleId);

		if (mvpTank.isRobot()) {
			return;
		}
		// 设置获得MVP
		BattleResultRecord record = mvpTank.getQuestRecord();
		record.setMvp(true);
	}

	protected List<AbstractInstance> redMoney() {
		List<AbstractInstance> roleIds = new ArrayList<>();
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.getTeam() == this.winTeam) {
				roleIds.add(tank);
			}
		}
		if (GameContext.getRedMoneyApp().saveRedMoney(mapInstance.getUniqueId(), roleIds)) {
			return roleIds;
		}
		return null;
	}

	// 通知比赛结束
	protected void notifyGameOver() {
		if (this.winTeam == null) {
			return;
		}

		STC_WAR_REPORT_MSG.Builder builder = STC_WAR_REPORT_MSG.newBuilder();
		STC_WAR_REPORT_MSG.Builder savebuilder = STC_WAR_REPORT_MSG.newBuilder();
		builder.setUniqueId(mapInstance.getUniqueId());
		// 红包计算
		List<AbstractInstance> redMoneyRoles = redMoney();

		// 比赛结果
		WarResultItem.Builder item = WarResultItem.newBuilder();
		item.setMvpRoleId(this.mvpRoleId);
		item.setMvpCount(this.mvpCount);
		item.setTime(mapInstance.durationTime());
		item.setResult(this.winTeam.getNumber());
		item.setBlue(mapInstance.getBlueKillNum());
		item.setRed(mapInstance.getRedKillNum());
		item.setFlagWin(flagWin);
		builder.setResult(item);
		savebuilder.setResult(item);

		// 计算队伍伤害
		float redAllDamage = 0f; // 红方总伤害
		float blueAllDamage = 0f; // 蓝方总伤害
		float redAllReceiveDamage = 0f; // 红方受到总伤害
		float blueAllReceiveDamage = 0f; // 蓝方受到总伤害
		float redAllAidDamage = 0f; // 红方协助总伤害
		float blueAllAidDamage = 0f; // 蓝方协助总伤害
		for (int instanceId : mapInstance.getAllTanksId()) {
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if (ist == null) {
				continue;
			}
			if (ist.getTeam() == TEAM.RED) {
				redAllDamage += ist.getHitAllDamage();
				redAllReceiveDamage += ist.getReceiveAllDamage();
				redAllAidDamage += ist.getAidpAllDamage();
				continue;
			}
			blueAllDamage += ist.getHitAllDamage();
			blueAllReceiveDamage += ist.getReceiveAllDamage();
			blueAllAidDamage += ist.getAidpAllDamage();
		}
		redAllDamage = Math.max(redAllDamage, 1f);
		blueAllDamage = Math.max(blueAllDamage, 1f);
		redAllReceiveDamage = Math.max(redAllReceiveDamage, 1f);
		blueAllReceiveDamage = Math.max(blueAllReceiveDamage, 1f);
		redAllAidDamage = Math.max(redAllAidDamage, 1f);
		blueAllAidDamage = Math.max(blueAllAidDamage, 1f);
		//排名
		fightScoreSort();
		
		for (int instanceId : mapInstance.getAllTanksId()) {
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if (ist == null) {
				continue;
			}
			WarReportItem.Builder reportItem = WarReportItem.newBuilder();
			reportItem.setId(ist.getId());
			reportItem.setTankId(ist.getTemplateId());
			reportItem.setElite(ist.isEliteTank() ? 1 : 0);
			reportItem.setRoleName(ist.getRoleName());
			reportItem.setTeam(ist.getTeam().getNumber());
			reportItem.setKill(ist.getKillCount());
			reportItem.setDeath(ist.getDeathCount());
			reportItem.setHelpKill(ist.getHelpKill());
			reportItem.setAidKill(ist.getAidKill());
			reportItem.setMvpFlag(mvpTankId == ist.getId() ? 1 : 0);
			reportItem.setMvpScore(0);
			reportItem.setLevel(ist.getLevel());
			reportItem.setTitleId(ist.getTitleId());
			reportItem.setBattleScore(ist.getBattleScore());
			reportItem.setRoleId(ist.getRoleId());
			reportItem.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(ist.getRoleId()));
			reportItem.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(ist.getRoleId()));
			reportItem.setRank(ist.getFightRank());
			
			WarStatisticsItem.Builder statisItem = WarStatisticsItem.newBuilder();
			int damage = ist.getHitAllDamage();
			int receiveDamage = ist.getReceiveAllDamage();
			int aidDamage = ist.getAidpAllDamage();

			statisItem.setDamage(damage);
			statisItem.setAidDamage(aidDamage);
			statisItem.setReceiveDamage(receiveDamage);

			BattleResultRecord record = ist.getQuestRecord();
			float damagePer = 0f;
			float aidDamagePer = 0f;
			float receiveDamagePer = 0f;
			if (ist.getTeam() == TEAM.RED) {
				damagePer = damage / redAllDamage * 100;
				aidDamagePer = aidDamage / redAllAidDamage * 100;
				receiveDamagePer = receiveDamage / redAllReceiveDamage * 100;
				statisItem.setDamagePer(damagePer);
				statisItem.setAidDamagePer(aidDamagePer);
				statisItem.setReceiveDamagePer(receiveDamagePer);
			} else {
				damagePer = damage / blueAllDamage * 100;
				aidDamagePer = aidDamage / blueAllAidDamage * 100;
				receiveDamagePer = receiveDamage / blueAllReceiveDamage * 100;
				statisItem.setDamagePer(damagePer);
				statisItem.setAidDamagePer(aidDamagePer);
				statisItem.setReceiveDamagePer(receiveDamagePer);
			}

			record.setDamagePercent((int) damagePer);
			record.setHelpDamagePercent((int) aidDamagePer);

			reportItem.setStatisItem(statisItem);
			builder.addReports(reportItem);
			savebuilder.addReports(reportItem);
		}

		for (int instanceId : mapInstance.getAllTanksId()) {
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if (ist == null) {
				continue;
			}
			// 奖励信息
			if (ist.isRobot()) {
				continue;
			}

			// WarRewardsItem rewardsItem = this.giveBattleRewards(ist,
			// mapInstance.getWarType());
			// builder.setRewards(rewardsItem);

			WarRewardInfo.Builder warRInfo = caclIncome(ist, mapInstance.getWarType(), ist.getTeam().ordinal() == winTeam.ordinal() ? true : false);
			builder.setRewardInfo(warRInfo);

			if (redMoneyRoles != null) {
				if (redMoneyRoles.contains(ist)) {
					builder.setRedMoney(true);
				} else {
					builder.setRedMoney(false);
				}
			}
			ist.sendMsg(builder.build());
		}

		// 策划需求，输出战报日志
		for (int instanceId : mapInstance.getAllTanksId()) {
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if (ist == null) {
				continue;
			}

			// if(ist.isRobot() && GameContext.getGameTest() == 0 ){
			// continue;
			// }

			if (ist.isRobot()) {
				// GameContext.getGameDBLogApp().getBattleLog().robotReportCount(
				// SysConfig.get(11),
				// mapInstance.getInstanceId(),
				// ist.getTeam().toString(),
				// ist.getTemplateId(),
				//
				// ist.getKillCount(),
				// ist.getHelpKill(),
				// ist.getDeathCount(),
				// ist.getMissCount(),
				// ist.getFindCount(),
				// ist.getFindCount()
				// );
				// GameContext.getGameDBLogApp().getBattleLog().robotReportValue(SysConfig.get(11),
				// mapInstance.getInstanceId(), ist.getTeam().toString(),
				// ist.getTemplateId(),
				// ist.getHitAllDamage(), ist.getAidpAllDamage(),
				// ist.getReceiveAllDamage());
			} else {
				GameContext.getGameDBLogApp().getBattleLog().reportCount(ist.getRoleId(), mapInstance.getInstanceId(), ist.getTeam().toString(), ist.getTemplateId(),
						ist.getKillCount(), ist.getHelpKill(), ist.getDeathCount(), ist.getMissCount(), ist.getFindCount(), ist.getFindCount(),this.winTeam==ist.getTeam()?1:0);
				
				GameContext.getGameDBLogApp().getBattleLog().reportValue(ist.getRoleId(), mapInstance.getInstanceId(), ist.getTeam().toString(), ist.getTemplateId(), ist.getHitAllDamage(),
						ist.getAidpAllDamage(), ist.getReceiveAllDamage());
			}
		}

		// 去掉奖励保存一份到状态机中
		this.warReportMsg = savebuilder.build();

		logger.debug(" {} 比赛结束...", mapInstance.getInstanceId());
	}

	/**
	 * 计算坦克额外收益
	 *
	 * @param tank
	 * @param warType
	 * @param incomeDesc
	 * @param builder
	 */
	private void extraIncome(AbstractInstance tank, int warType, IncomeDesc incomeDesc, WarRewardInfo.Builder builder,boolean win) {
		// SP_TANK =1;//特殊车
		// WEAK_TEAM=2;//遭遇强敌
		// CALL_UP=3;//战斗召唤
		// FRIEND=4;//好友
		// CROP=5;//军团
		// VIP=6;//VIP
		int baseExp = incomeDesc.getExp();
		int baseIron = incomeDesc.getIron();
		int baseHonor = incomeDesc.getHonor();

		int totalExp = baseExp;
		int totalIron = baseIron;
		int totalHonor = baseHonor;
		ExtraIncomeTemplate extraIncomeTemplate = GameContext.getMapApp().getExtraIncomeTemplate();

		TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		// 特殊车
		if (tankTemplate.getTank_type() == 3) {
			// 金币车
			totalExp = (int) (totalExp + baseExp * 1.0f * extraIncomeTemplate.getGoldExp() / 100);
			totalIron = (int) (totalIron + baseIron * 1.0f * extraIncomeTemplate.getGoldIron() / 100);
			buildWarRewardEnumInfo(builder, extraIncomeTemplate.getGoldExp(), extraIncomeTemplate.getGoldIron(), WarRewardEnum.SP_TANK);
		} else if (tankTemplate.getTank_type() == 4) {
			// 珍藏车
			totalExp = (int) (totalExp + baseExp * 1.0f * extraIncomeTemplate.getTreasureExp() / 100);
			totalIron = (int) (totalIron + baseIron * 1.0f * extraIncomeTemplate.getTreasureIron() / 100);
			buildWarRewardEnumInfo(builder, extraIncomeTemplate.getTreasureExp(), extraIncomeTemplate.getTreasureIron(), WarRewardEnum.SP_TANK);
		} else if (tankTemplate.getTank_type() == 5) {
			// 稀有车
			totalExp = (int) (totalExp + baseExp * 1.0f * extraIncomeTemplate.getRareExp() / 100);
			totalIron = (int) (totalIron + baseIron * 1.0f * extraIncomeTemplate.getRareIron() / 100);
			buildWarRewardEnumInfo(builder, extraIncomeTemplate.getRareExp(), extraIncomeTemplate.getRareExp(), WarRewardEnum.SP_TANK);
		}
		// 遭遇强敌
		if (tank.isWeakTeam()) {
			totalExp = (int) (totalExp + baseExp * 1.0f * extraIncomeTemplate.getWeakExp() / 100);
			totalIron = (int) (totalIron + baseIron * 1.0f * extraIncomeTemplate.getWeakIron() / 100);
			buildWarRewardEnumInfo(builder, extraIncomeTemplate.getWeakExp(), extraIncomeTemplate.getWeakIron(), WarRewardEnum.WEAK_TEAM);
		}
		// 组队收益加成
		TeamIncomeTypeInfo teamIncomePercent = GameContext.getMatchApp().getTeamIncome(tank);
		if (teamIncomePercent.getType() != 0) {
			totalExp = (int) (totalExp + baseExp * teamIncomePercent.getIncome() * 1.0 / 100);
			totalIron = (int) (totalIron + baseIron * teamIncomePercent.getIncome() * 1.0 / 100);
			WarRewardEnum teamType = null;
			if (teamIncomePercent.getType() == 1) {
				teamType = WarRewardEnum.FRIEND;
			} else {
				teamType = WarRewardEnum.CROP;
			}
			buildWarRewardEnumInfo(builder, teamIncomePercent.getIncome(), teamIncomePercent.getIncome(), teamType);

		}
		// 战斗召唤 TODO

		// vip
		float vipPercent = GameContext.getVipApp().getPrivilegeTankExpPercent(tank.getRoleId());
		if (!(vipPercent - 0 <= Math.abs(0.00001))) {
			totalExp = (int) (totalExp + baseExp * vipPercent);
			totalIron = (int) (totalIron + baseIron * vipPercent);
			buildWarRewardEnumInfo(builder, (int) (vipPercent * 100), (int) (vipPercent * 100), WarRewardEnum.VIP);
		}

		incomeDesc.setExp(totalExp);
		incomeDesc.setIron(totalIron);
		incomeDesc.setHonor(totalHonor);

		TankTemplate template = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		final int roleTankExp = (int) (incomeDesc.getExp() * template.getRoleTankExp());
		incomeDesc.setRoleExp(roleTankExp);

		//挂机判断
		int handupNum =tank.getHandup();
		BattleHandUpTemplate handTemplate = GameContext.getMapApp().getBattleHandupMap().get(handupNum);
		if(handTemplate==null){
			handTemplate=GameContext.getMapApp().getBattleHandupMap().get(GameContext.getMapApp().getBattleHandupMap().size()-1);
		}
		float handUpRat= handTemplate.getHandupValue();
		incomeDesc.setExp((int)(incomeDesc.getExp()*handUpRat));
		incomeDesc.setIron((int)(incomeDesc.getIron()*handUpRat));
		incomeDesc.setHonor((int)(incomeDesc.getHonor()*handUpRat));
		incomeDesc.setRoleExp((int)(incomeDesc.getRoleExp()*handUpRat));

		// 5v5系数
		// 5v5 等战斗人数，收益结算
		float winLoseExpRate = 0;
		float winLoseIronRate = 0;
		if (win) {
			winLoseExpRate = extraIncomeTemplate.getWin5v5Exp() * 1.0f / 100;
			winLoseIronRate = extraIncomeTemplate.getWin5v5Iron() * 1.0f / 100;

		} else {
			winLoseExpRate = extraIncomeTemplate.getLose5v5Exp() * 1.0f / 100;
			winLoseIronRate = extraIncomeTemplate.getLose5v5Iron() * 1.0f / 100;
		}
		incomeDesc.setExp((int) (incomeDesc.getExp() + incomeDesc.getExp() * winLoseExpRate));
		incomeDesc.setIron((int) (incomeDesc.getIron() + incomeDesc.getIron() * winLoseIronRate));

		// 给全局坦克经验
		GameContext.getUserAttrApp().changeAttribute(tank.getRoleId(), AttrUnit.build(RoleAttr.tankExp, Operation.add, incomeDesc.getRoleExp()), OutputType.battleEndRewardInc.type(),
				OutputType.battleEndRewardInc.getInfo() + "_" + mapInstance.getWarType());

		// 给坦克经验
		RoleTank roleTank = GameContext.getTankApp().getRoleTank(tank.getRoleId(), tank.getTemplateId());
		roleTank.setExp(roleTank.getExp() + incomeDesc.getExp());
		GameContext.getTankApp().saveRoleTank(roleTank);
		// 同步坦克信息
		GameContext.getTankApp().tankPush(roleTank);
		// 发银币
		GameContext.getUserApp().addBattleEndIronReward(tank.getRoleId(), incomeDesc.getIron());
		// 给荣誉
		if (totalHonor > 0) {
			GameContext.getUserAttrApp().changeAttribute(tank.getRoleId(), AttrUnit.build(RoleAttr.honor, Operation.add, totalHonor), OutputType.battleEndRewardInc.type(),
					OutputType.battleEndRewardInc.getInfo() + "_" + mapInstance.getWarType());

			GameContext.getQuestTriggerApp().totalBattleHonor(tank.getRoleId(), warType, totalHonor);
		}

	}

	private void buildWarRewardEnumInfo(WarRewardInfo.Builder builder, int exp, int iron, WarRewardEnum type) {
		WarRewardEnumInfo.Builder enumBuilder = WarRewardEnumInfo.newBuilder();
		enumBuilder.setType(type);
		ExtraIncomeTextTemplate extraIncomeTextTemplate = GameContext.getMapApp().getExtraIncomeTextTemplateMap().get(type.getNumber());
		enumBuilder.addContent(extraIncomeTextTemplate.getMainText());
		enumBuilder.addContent(MessageFormat.format(extraIncomeTextTemplate.getSubText1(), exp));
		enumBuilder.addContent(MessageFormat.format(extraIncomeTextTemplate.getSubText1(), iron));
		builder.addInfos(enumBuilder);
	}

	/**
	 * 计算坦克收益
	 *
	 * @param tank
	 * @param warType
	 * @return
	 */
	private WarRewardInfo.Builder caclIncome(AbstractInstance tank, int warType,boolean win) {
		Map<INCOME_TYPE, List<IncomeInfo>> incomeMap = tank.getIncomeMap();
		IncomeDesc incomeDesc = new IncomeDesc();
		for (List<IncomeInfo> list : incomeMap.values()) {
			for (IncomeInfo incomeInfo : list) {
				incomeDesc.setExp(incomeDesc.getExp() + incomeInfo.getExp());
				incomeDesc.setHonor(incomeDesc.getHonor() + incomeInfo.getHonor());
				incomeDesc.setIron(incomeDesc.getIron() + incomeInfo.getIron());
			}
		}


		//测试
//		incomeDesc.setExp(100);
//		incomeDesc.setIron(123);
//		incomeDesc.setRoleExp(156);
		RoleAccount role = GameContext.getUserApp().getRoleAccount(tank.getRoleId());
		WarRewardInfo.Builder builder = WarRewardInfo.newBuilder();
		builder.setVip(role.getVipLevel());
		// 额外收益计算
		extraIncome(tank, warType, incomeDesc, builder,win);


		//测试
//		WarRewardEnumInfo.Builder enumBuilder = WarRewardEnumInfo.newBuilder();
//		enumBuilder.setType(WarRewardEnum.CALL_UP);
//		enumBuilder.addContent("战斗召唤");
//		enumBuilder.addContent("银币加成 5%");
//		enumBuilder.addContent("战斗召唤 8%");
//		builder.addInfos(enumBuilder);
//		WarRewardEnumInfo.Builder enumBuilder2 = WarRewardEnumInfo.newBuilder();
//		enumBuilder2.setType(WarRewardEnum.WEAK_TEAM);
//		enumBuilder2.addContent("遭遇强敌");
//		enumBuilder2.addContent("银币加成 6%");
//		enumBuilder2.addContent("战斗召唤 7%");
//		builder.addInfos(enumBuilder2);
		builder.setTotalExp(incomeDesc.getExp());
		builder.setTotalIron(incomeDesc.getIron());
		builder.setRoleTotalExp(incomeDesc.getRoleExp());
		builder.setHandup(tank.getHandup() > 0);
		return builder;
	}

	// 触发完成任务
	protected void completeQuest() {
		if (this.winTeam == null) {
			return;
		}
		final int warType = mapInstance.getWarType();
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.isRobot()) {
				continue;
			}
			BattleResultRecord record = tank.getQuestRecord();
			if (record == null) {
				continue;
			}
			record.setWarType(warType);
			record.setWin(tank.getTeam() == this.winTeam);
			record.setTank(tank);

			// 触发任务
			GameContext.getQuestTriggerApp().battleResult(tank.getRoleId(), record);
		}

	}

	// 排名计算
	/**
	 * private void calcRank(AbstractInstance tank, BattleResultRecord record){
	 * int allHelpDamage = tank.getHelpAllDamage(); //协助送伤害 //按当时在线人排行
	 * for(AbstractInstance tst : mapInstance.getAllTank()){ if(tst.getId() ==
	 * tank.getId() || tst.getTeam() == tank.getTeam()){ continue; }
	 *
	 * allHelpDamage += tst.getHelpAllDamage();
	 *
	 * //接受伤害排行 if(tst.getReceiveAllDamage() > tank.getReceiveAllDamage()){
	 * record.setReceiveDamageRank(record.getReceiveDamageRank() + 1); }
	 * //协助伤害排行 if(tst.getHelpAllDamage() > tank.getHelpAllDamage()){
	 * record.setHelpDamageRank(record.getHelpDamageRank() + 1); } } //计算协助伤害比
	 * int helpDamagePercent = (int)((float)tank.getHelpAllDamage() /
	 * (float)allHelpDamage * 100f);
	 * record.setHelpDamagePercent(helpDamagePercent); }
	 */

	@Override
	public void notify(NotifyState state, int tankInstanceId) {
		// 此状态下收到客户端准备完毕消息时，立刻告知开始游戏
		// 断线重连逻辑
		if (state == NotifyState.enter) {
			MapTemplate template = mapInstance.getTemplate();
			int overTime = template.getOverTime() + template.getReadyTime() - mapInstance.durationTime();
			if (overTime < 0) {
				overTime = 0;
			}
			STC_BEGIN_MSG beginMsg = STC_BEGIN_MSG.newBuilder().setOverTime(overTime).setReadyTime(1).build();
			AbstractInstance tank = GameContext.getTankApp().getInstance(tankInstanceId);
			if (tank != null) {
				tank.sendMsg(beginMsg);
			}
			// 主推比分
			mapInstance.brodcastWarScore(overTime);
			// 发送个人战绩
			mapInstance.notifyUserWarRecord(tank);
			// 战旗广播
			this.notifyFlagMsg(this.currHold);

			// 通知客户端已经准备好的玩家
			for (AbstractInstance _tank : mapInstance.getAllTank()) {
				mapInstance.brodcastMsg(STC_PLAYER_READY_MSG.newBuilder().setId(_tank.getId()).build());
			}
		}
	}

	@Override
	public boolean hadOver() {
		return this.winTeam != null;
	}

	@Override
	public void forceClose() {
		this.winTeam = TEAM.BLUE;
	}

	public int getCurrHold() {
		return currHold;
	}

	public int getRedHoldTime() {
		return redHoldTime;
	}

	public int getBlueHoldTime() {
		return blueHoldTime;
	}

	public int getHoldTime() {
		return holdTime;
	}

	public int getTotalTime() {
		return (int) time;
	}

	public long getBeginTime() {
		return beginTime;
	}
}
