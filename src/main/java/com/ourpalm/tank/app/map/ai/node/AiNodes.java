package com.ourpalm.tank.app.map.ai.node;

import static com.ourpalm.tank.app.GameContext.getBattleApp;
import static com.ourpalm.tank.app.GameContext.getTankApp;
import static com.ourpalm.tank.app.map.ai.node.TankCategoryCondition.isHT;
import static com.ourpalm.tank.app.map.ai.node.TankCategoryCondition.isLT;
import static com.ourpalm.tank.app.map.ai.node.TankCategoryCondition.isMT;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.app.map.ai.node.EnemyDetectedCondition.ViewRange;
import com.ourpalm.tank.app.map.state.BeginStateMachine;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.template.AiConfig;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.Cate;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.Restraint;
import com.ourpalm.tank.vo.behaviortree.DataContext;
import com.ourpalm.tank.vo.behaviortree.Node;
import com.ourpalm.tank.vo.behaviortree.composite.Selector;
import com.ourpalm.tank.vo.behaviortree.composite.Sequence;
import com.ourpalm.tank.vo.behaviortree.decorator.Chance;
import com.ourpalm.tank.vo.behaviortree.decorator.Inverter;

public enum AiNodes {

	;

	/**
	 * 标准开火节点
	 *
	 * @param map 地图实例引用
	 * @return    标准开火节点
	 */
	public static Node<AbstractInstance> fire(MapInstance map) {
		return new FireAction(map);
	}

	/**
	 * 判断是否还有下一个目标点可走的节点
	 *
	 * @return 判断是否还有下一个目标点可走的节点
	 */
	public static Node<AbstractInstance> moveable() {
		return new HasNextRoadCondition();
	}

	/**
	 * 判断是否到达下一个目标点可走的节点
	 *
	 * @param map 地图实例引用
	 * @return    判断是否到达下一个目标点可走的节点
	 */
	public static Node<AbstractInstance> reachNode(MapInstance map) {
		return new ReachNextNodeCondition(map);
	}

	/**
	 * 标准行走节点，走到下一个目标点
	 *
	 * @param map 地图实例引用
	 * @return    标准行走节点
	 */
	public static Node<AbstractInstance> move(MapInstance map) {
		return new MoveAction(map);
	}

	/**
	 * 设置当前寻路策略为默认
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> resetStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.DEFAULT);
	}

	/**
	 * 设置当前寻路策略为逃跑
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyEscapeStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.ESCAPE);
	}

	/**
	 * 设置当前寻路策略为另一个逃跑
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyEscapeUntilInvisibleStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.ESCAPE_UNTIL_INVISIBLE);
	}

	/**
	 * 设置当前寻路策略为追击
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyPursueStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.PURSUE);
	}

	/**
	 * 设置当前寻路策略为远离战场
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyStayAwayStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.STAY_AWAY_FROM_BATTLEFIELD);
	}

	/**
	 * 设置当前寻路策略为外围移动
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyOuterSpaceStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.OUTER_SPACE_PREFERRED);
	}

	/**
	 * 设置当前寻路策略为侦察
	 *
	 * @return 设置寻路策略节点
	 */
	public static Node<AbstractInstance> applyScoutStrategy() {
		return new SetSearchStrategyAction(SearchStrategy.SCOUT);
	}

	/**
	 * 判断是否在坦克类型上自己克制目标
	 *
	 * @return 判断节点
	 */
	public static Node<AbstractInstance> isSuperiorThanTarget() {
		return new RestraintCondition(Restraint.SUPERIOR);
	}

	/**
	 * 判断是否在坦克类型上目标克制自己
	 *
	 * @return 判断节点
	 */
	public static Node<AbstractInstance> isInferiorThanTarget() {
		return new RestraintCondition(Restraint.INFERIOR);
	}

	/**
	 * 设置 FIRE_MODE 为 true
	 *
	 * @param  fireMode 开火模式，若在开火模式则直到开火目标不可攻击才继续行走
	 * @return          动作节点
	 */
	public static Node<AbstractInstance> setFireMode(final boolean fireMode) {
		return new Node<AbstractInstance>() {

			@Override
			public boolean eval(AbstractInstance receiver) {
				DataContext dc = receiver.getBahaviorDataContext();
				dc.putBoolean(FireAction.FIRE_MODE, fireMode);
				return true;
			}
		};
	}

	/**
	 * 评估当前 FIRE_MODE 是否为 true
	 *
	 * @return 评估条件节点
	 */
	public static Node<AbstractInstance> fireMode() {
		return new Node<AbstractInstance>() {

			@Override
			public boolean eval(AbstractInstance receiver) {
				DataContext dc = receiver.getBahaviorDataContext();
				return dc.getBoolean(FireAction.FIRE_MODE);
			}

		};
	}

	/**
	 * 放水动作节点
	 *
	 * @return 放水动作节点
	 */
	public static Node<AbstractInstance> goofAround() {

		final AiConfig config = getTankApp().getAiConfig();

		Node<AbstractInstance> reduceAttr = new Node<AbstractInstance>() {

			@Override
			public boolean eval(AbstractInstance receiver) {

				DataContext dc = receiver.getBahaviorDataContext();

				float peneLoss = config.getFactorOfReduceAttr() / 100f;
				// TODO 暂时降低穿深
				int chuanjia = receiver.get(AttrType.chuanjia).intValue();
				int lossVal = (int) (chuanjia * peneLoss);
				receiver.set(AttrType.chuanjia, chuanjia - lossVal);
				dc.putInt(GoofAroundCondition.GOOF_AROUND_LOSS_PENE, lossVal);

				return true;
			}
		};

		return new Sequence<>(
			// 若放水（一次性），要么降低精度，要么执行逃跑
			new GoofAroundCondition(),
				new Selector<>(
					new Chance<>(config.getChanceOfGoofAroundReduceAttr() / 100f, reduceAttr),
					new Chance<>(config.getChanceOfGoofAroundEscape() / 100f, applyEscapeStrategy())));
	}

	/**
	 * 抢人头动作节点
	 *
	 * @param  map 地图实例引用
	 * @return     抢人头动作节点
	 */
	public static Node<AbstractInstance> killingSteal(MapInstance map) {

		// 初始化抢人头的条件
		KillingStealCondition ks = new KillingStealCondition(map);
		AiConfig config = getTankApp().getAiConfig();

		// 初始化抢人头的一些参数
		ks.detectRange = config.getKsDetectRange();
		ks.thresholdHealth = config.getKsDetectHealth() / 100f;

		// 若满足抢人头条件，则执行追击
		return new Sequence<>(ks, applyPursueStrategy());
	}

	/**
	 * 基础条件节点：活着
	 *
	 * @return 活着条件节点
	 */
	public static Node<AbstractInstance> isAlive() {
		return new AliveCondition();
	}

	/**
	 * 基础条件节点：死亡
	 *
	 * @return 死亡条件节点
	 */
	public static Node<AbstractInstance> isDeath() {
		return new Inverter<>(isAlive());
	}

	/**
	 * 基础条件节点：是机器人
	 *
	 * @return 机器人条件节点
	 */
	public static Node<AbstractInstance> isRobot() {
		return new RobotCondition();
	}

	/**
	 * 基础条件节点：红队
	 *
	 * @return 红队条件节点
	 */
	public static Node<AbstractInstance> isRedTeam() {
		return new RedTeamCondition();
	}

	/**
	 * 基础条件节点：蓝队
	 *
	 * @return 蓝队条件节点
	 */
	public static Node<AbstractInstance> isBlueTeam() {
		return new Inverter<>(isRedTeam());
	}

	/**
	 * 条件节点：属性大于等于给定阈值
	 *
	 * @param  attr  属性
	 * @param  value 给定的阈值
	 * @return       属性大于给定阈值条件节点
	 */
	public static Node<AbstractInstance> isAttrOver(AttrType attr, int value) {
		return new AttrOverCondition(attr, value);
	}

	/**
	 * 条件节点：属性小于等于给定阈值
	 *
	 * @param  attr  属性
	 * @param  value 给定的阈值
	 * @return       属性小于给定阈值条件节点
	 */
	public static Node<AbstractInstance> isAttrUnder(AttrType attr, int value) {
		return new Inverter<>(isAttrUnder(attr, value));
	}

	/**
	 * 条件节点：血量大于等于给定阈值百分比
	 *
	 * @param  progress 给定的阈值百分比
	 * @return          血量大于等于给定阈值百分比
	 */
	public static Node<AbstractInstance> isHealthOverProgress(int progress) {
		HealthOverProgressCondition node = new HealthOverProgressCondition();
		node.thresholdProgress = progress;
		return node;
	}

	/**
	 * 条件节点：血量小于给定阈值百分比
	 *
	 * @param  progress 给定的阈值百分比
	 * @return          血量小于给定阈值百分比
	 */
	public static Node<AbstractInstance> isHealthUnderProgress(int progress) {
		return new Inverter<>(isHealthOverProgress(progress));
	}

	public static Node<AbstractInstance> allAttrMultiple(float multiple) {
		AllAttrMultipleAction node = new AllAttrMultipleAction();
		node.multiple = multiple;
		return node;
	}

	public static Node<AbstractInstance> attrMultiple(AttrType attr, float multiple) {
		AttrMultipleAction node = new AttrMultipleAction();
		node.attr = attr;
		node.multiple = multiple;
		return node;
	}

	public static Node<AbstractInstance> isFlagOccupiedOverProgress(MapInstance map, TEAM team, int progress) {
		FlagOccupiedProgressOverCondition node = new FlagOccupiedProgressOverCondition(map);
		node.team = team;
		node.thresholdProgress = progress;
		return node;
	}

	public static Node<AbstractInstance> isFlagOccupiedUnderProgress(MapInstance map, TEAM team, int progress) {
		return new Inverter<>(isFlagOccupiedOverProgress(map, team, progress));
	}

	public static Node<AbstractInstance> isFlagOccupiedOverTime(MapInstance map, TEAM team, int time) {
		FlagOccupiedTimeOverCondition node = new FlagOccupiedTimeOverCondition(map);
		node.team = team;
		node.thresholdTime = time;
		return node;
	}

	public static Node<AbstractInstance> isFlagOccupiedUnderTime(MapInstance map, TEAM team, int time) {
		return new Inverter<>(isFlagOccupiedOverTime(map, team, time));
	}

	public static Node<AbstractInstance> atFlagCircle() {
		return new AtFlagCircleCondition();
	}

	public static Node<AbstractInstance> notAtFlagCircle() {
		return new Inverter<>(new AtFlagCircleCondition());
	}

	public static Node<AbstractInstance> hasNextRoads() {
		return new HasNextRoadCondition();
	}

	public static Node<AbstractInstance> roadSearch(MapInstance map) {
		SearchRoardAction node = new SearchRoardAction(map);
		return node;
	}

	public static Node<AbstractInstance> isEscaping() {
		MoveStrategyCondition msc = new MoveStrategyCondition(SearchStrategy.ESCAPE);
		MoveStrategyCondition msc2 = new MoveStrategyCondition(SearchStrategy.ESCAPE_UNTIL_INVISIBLE);
		return new Selector<>(msc, msc2);
	}

	public static Node<AbstractInstance> notEscaping() {
		return new Inverter<>(isEscaping());
	}

	public static Node<AbstractInstance> isStayAway() {
		MoveStrategyCondition msc = new MoveStrategyCondition(SearchStrategy.STAY_AWAY_FROM_BATTLEFIELD);
		return msc;
	}

	public static Node<AbstractInstance> notStayAway() {
		return new Inverter<>(isStayAway());
	}

	public static Node<AbstractInstance> isPursue() {
		MoveStrategyCondition msc = new MoveStrategyCondition(SearchStrategy.PURSUE);
		return msc;
	}

	public static Node<AbstractInstance> notPursue() {
		return new Inverter<>(isPursue());
	}

	public static Node<AbstractInstance> isOuterSpace() {
		MoveStrategyCondition msc = new MoveStrategyCondition(SearchStrategy.OUTER_SPACE_PREFERRED);
		return msc;
	}

	public static Node<AbstractInstance> notOuterSpace() {
		return new Inverter<>(isOuterSpace());
	}

	public static Node<AbstractInstance> idelFor(int seconds) {
		IdelFor node = new IdelFor();
		node.seconds = seconds;
		return node;
	}

	public static Node<AbstractInstance> isEnemyDetected(MapInstance map, ViewRange vr) {
		return new EnemyDetectedCondition(map, vr);
	}

	public static Node<AbstractInstance> isEnemyDetected(MapInstance map) {
		return new EnemyDetectedCondition(map, ViewRange.SHARED);
	}

	public static Node<AbstractInstance> isEnemyDetectedSelf(MapInstance map) {
		return new EnemyDetectedCondition(map, ViewRange.OWN);
	}

	public static Node<AbstractInstance> isEnemyDetectedSafely(MapInstance map) {
		return new EnemyDetectedCondition(map, ViewRange.SAFE);
	}

	public static Node<AbstractInstance> loseContinuously(int loseCount) {
		return null;

	}

	public static Node<AbstractInstance> winContinuously(int loseCount) {
		return null;

	}

	/**
	 * 保命 AI
	 *
	 * @param map
	 * @return
	 */
	public static Node<AbstractInstance> struggleAi(MapInstance map) {

		// 自己视野内有敌人
		Node<AbstractInstance> viewSelf   = isEnemyDetectedSelf(map);

		Node<AbstractInstance> struggleAi = new Sequence<>(new StruggleConditon(map),
			new Selector<>(
				new Sequence<>(viewSelf, applyStayAwayStrategy()),
				applyOuterSpaceStrategy()
		));

		return struggleAi;
	}

	/**
	 * 时间和比分 AI
	 *
	 * @param map
	 * @return
	 */
	public static Node<AbstractInstance> timeAndScoreAi(MapInstance map) {

		// 我方人头与敌方人头差值大于等于 1
		Node<AbstractInstance> scoreGE1  = BattleScoreOffsetCondition.GE(map, 1);
		// 我方人头与敌方人头差值小于等于 -1
		Node<AbstractInstance> scoreLEM1 = BattleScoreOffsetCondition.LE(map, -1);
		// 我方人头与敌方人头差值大于 3
		Node<AbstractInstance> scoreG3   = BattleScoreOffsetCondition.G(map, 3);

		// 剩余比赛时间大于 20%，即时间过了不超过 80%
		Node<AbstractInstance> timeOver20 = BattleTimePassedCondition.LE(map, 80);
		// 剩余比赛时间大于 40%，即时间过了不超过 60%
		Node<AbstractInstance> timeOver40 = BattleTimePassedCondition.LE(map, 60);
		// 剩余比赛时间小于 40%，即时间过了超过 60%
		Node<AbstractInstance> timeUnder40 = BattleTimePassedCondition.G(map, 60);

		Node<AbstractInstance> lt = isLT();                             // 是轻坦
		Node<AbstractInstance> htOrMt = new Selector<>(isMT(), isHT()); // 是重坦或者中坦

		Node<AbstractInstance> moveFire = setFireMode(false);
		Node<AbstractInstance> stillFire = setFireMode(true);

		Node<AbstractInstance> fireX = new Selector<>(
			new Sequence<>(lt, moveFire),     // 轻坦移动开火直到目标或自己死亡
			new Sequence<>(htOrMt, stillFire) // 重坦或者中坦站桩开火直到目标或者自己死亡
		);

		Node<AbstractInstance> timeAndScoreAi = new Selector<>(
			new Sequence<>(timeOver20, new Selector<>(
				new Sequence<>(timeOver40, scoreGE1, fireX),
				new Sequence<>(timeUnder40, scoreLEM1, fireX)),
			new Sequence<>(scoreG3, fireX))
		);

		return timeAndScoreAi;
	}

	public static Node<AbstractInstance> inferiorAnalyze() {

		Node<AbstractInstance> inferiorChance
			= new InferiorCondition();

		return new Sequence<>(
			new RestraintCondition(Restraint.INFERIOR),
			inferiorChance,
			new Selector<>(isHT(), applyEscapeUntilInvisibleStrategy()),
			new Selector<>(isMT(), setFireMode(false)));
	}

	/**
	 * 轻坦点灯 AI
	 *
	 * @param  map 地图实例引用
	 * @return     轻坦点灯 AI 节点
	 */
	public static Node<AbstractInstance> scoutAi(MapInstance map) {

		// float chanceOfChangeStrategy = 0.1f;
		// int secondsOfIdel = 3;

		Node<AbstractInstance> rotateGun = new RotateGunAction(map);
		Node<AbstractInstance> scoutCondition = new ScoutCondition(map);
		Node<AbstractInstance> moveWithAlternatelyStrategy = new Selector<>(
			new Sequence<>(moveable(), rotateGun, move(map)),
			roadSearch(map));

		return new Sequence<>(
			scoutCondition,
			moveWithAlternatelyStrategy
			);
	}

	static class AliveCondition extends Node<AbstractInstance> {

		public static final String GOOF_AROUND_CHANCE    = "Alive.goofAroundChance";
		public static final String STRUGGLE_CHANCE       = "Alive.struggleChance";
		public static final String TIME_AND_SCORE_CHANCE = "Alive.timeAndScoreChance";
		public static final String KILLING_STEAL_CHANCE  = "Alive.killingStealChance";
		public static final String SCOUT_CHANCE          = "Alive.scoutChance";
		public static final String INFERIOR_CHANCE       = "Alive.inferiorChance";

		@Override
		public void init(AbstractInstance receiver) {
			super.init(receiver);
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putDouble(GOOF_AROUND_CHANCE,    Math.random());
			dc.putDouble(STRUGGLE_CHANCE,       Math.random());
			dc.putDouble(TIME_AND_SCORE_CHANCE, Math.random());
			dc.putDouble(KILLING_STEAL_CHANCE,  Math.random());
			dc.putDouble(SCOUT_CHANCE,          Math.random());
			dc.putDouble(INFERIOR_CHANCE,       Math.random());
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			if (!receiver.isDeath()) {
				receiver.syncCoordinate(receiver.getFireTarget(), receiver.getX(), receiver.getY(), receiver.getZ());
				// 旋转炮塔
				AbstractInstance target = receiver.getFireTarget();
				if (target != null && receiver.get(AttrType.had_revolve) == 1) {
					receiver.getRoadManager().rotateTurrent(target, receiver.getX(), receiver.getY(), receiver.getZ());
				}
			}
			return !receiver.isDeath();
		}
	}

	static class RobotCondition extends Node<AbstractInstance> {

		@Override
		public boolean eval(AbstractInstance receiver) {
			return receiver.isRobot();
		}
	}

	static class RedTeamCondition extends Node<AbstractInstance> {

		@Override
		public boolean eval(AbstractInstance receiver) {
			return receiver.getTeam() == TEAM.RED;
		}
	}

	static class AttrMultipleAction extends Node<AbstractInstance> {

		AttrType attr;
		float multiple;

		@Override
		public boolean eval(AbstractInstance receiver) {
			// Multiple 设置倍率
			return false;
		}
	}

	static class MultiAttrMultipleAction extends Node<AbstractInstance> {

		public static final String CHANGE_HISTORY = "MultiAttrMultipleAction.history";

		Map<AttrType, Float> attrs;

		@Override
		public void init(AbstractInstance receiver) {
			super.init(receiver);
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putBean(CHANGE_HISTORY, new HashMap<>());
		}

		@Override
		public void reset(AbstractInstance receiver) {
			DataContext dc = receiver.getBahaviorDataContext();
			dc.remove(CHANGE_HISTORY);
			super.reset(receiver);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			// TODO 更改属性
			return false;
		}
	}

	static class AllAttrMultipleAction extends Node<AbstractInstance> {

		float multiple;

		@Override
		public boolean eval(AbstractInstance receiver) {
			// Multiple 设置倍率
			return false;
		}
	}

	static class AttrOverCondition extends Node<AbstractInstance> {

		AttrType attr;
		int value;

		AttrOverCondition(AttrType attr, int value) {
			this.attr = attr;
			this.value = value;
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			return receiver.get(attr) >= value;
		}
	}

	static class HealthOverProgressCondition extends Node<AbstractInstance> {

		int thresholdProgress;

		@Override
		public boolean eval(AbstractInstance receiver) {
			int progress = (int) (receiver.get(AttrType.hp) * 100.0 / receiver.get(AttrType.n_hpMax));
			return progress >= thresholdProgress;
		}
	}

	static class FlagOccupiedProgressOverCondition extends MapNode {

		TEAM team;
		int thresholdProgress;

		public FlagOccupiedProgressOverCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			StateMachine sm = map.getStateMachine();
			if (sm instanceof BeginStateMachine) {
				BeginStateMachine bsm = (BeginStateMachine) sm;
				int holdTime = team == TEAM.RED ? bsm.getRedHoldTime() : bsm.getBlueHoldTime();
				int progress = (int) (holdTime * 100.0 / bsm.getHoldTime());
				return progress >= thresholdProgress;
			}
			return false;
		}
	}

	static class BattleTimeProgressOverCondition extends MapNode {

		int thresholdProgress;

		public BattleTimeProgressOverCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			StateMachine sm = map.getStateMachine();
			if (sm instanceof BeginStateMachine) {
				BeginStateMachine bsm = (BeginStateMachine) sm;
				int time = bsm.getTotalTime();
				long btime = bsm.getBeginTime();
				int progress = (int) ((System.currentTimeMillis() - btime) * 100.0 / time);
				return progress >= thresholdProgress;
			}
			return false;
		}
	}

	static class FlagOccupiedTimeOverCondition extends MapNode {

		TEAM team;
		int thresholdTime;

		public FlagOccupiedTimeOverCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			StateMachine sm = map.getStateMachine();
			if (sm instanceof BeginStateMachine) {
				BeginStateMachine bsm = (BeginStateMachine) sm;
				int holdTime = team == TEAM.RED ? bsm.getRedHoldTime() : bsm.getBlueHoldTime();
				return holdTime >= thresholdTime;
			}
			return false;
		}
	}

	static class AtFlagCircleCondition extends Node<AbstractInstance> {

		@Override
		public boolean eval(AbstractInstance receiver) {
			return receiver.isInFlag();
		}
	}

	static class TeamHealthUnderProgressCondition extends MapNode {

		TEAM team;
		int progress;

		public TeamHealthUnderProgressCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			for (AbstractInstance ai : map.getAllTank()) {
				if (ai.getTeam() == team) {
					int progress = (int) (ai.get(AttrType.hp) * 100.0 / ai.get(AttrType.n_hpMax));
					if (progress > this.progress) {
						return false;
					}
				}
			}
			return true;
		}
	}

	static class IdelFor extends Node<AbstractInstance> {

		public static final String IDEL_UNTIL = "IdelFor.idelUntil";

		int seconds;

		@Override
		public void init(AbstractInstance receiver) {
			super.init(receiver);
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putLong(IDEL_UNTIL, 0);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			DataContext dc = receiver.getBahaviorDataContext();
			long idel = dc.getLong(IDEL_UNTIL);
			// 初始化
			if (idel == 0) {
				dc.putLong(IDEL_UNTIL, 1);
				return false;
			} else if (idel == 1) {
				dc.putLong(IDEL_UNTIL, System.currentTimeMillis() + seconds * 1000);
				return true;
			} else if (idel > System.currentTimeMillis()) {
				return true;
			} else {
				dc.putLong(IDEL_UNTIL, 1);
				return false;
			}
		}
	}

	static class AtBirthPositionCondition extends MapNode {

		public AtBirthPositionCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	static class GoofAroundCondition extends Node<AbstractInstance> {

		public static final String GOOF_AROUND_FLAG = "GoofAroundCondition.flag";
		public static final String GOOF_AROUND_LOSS_PENE = "GoofAroundCondition.lossPene";

		@Override
		public void init(AbstractInstance receiver) {
			super.init(receiver);
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putBoolean(GOOF_AROUND_FLAG, false);
			// 这里要恢复被扣掉的属性
			int lossVal = dc.exists(GOOF_AROUND_LOSS_PENE) ? dc.getInt(GOOF_AROUND_LOSS_PENE) : 0;
			receiver.set(AttrType.chuanjia, receiver.get(AttrType.chuanjia) + lossVal);
			dc.putInt(GOOF_AROUND_LOSS_PENE, 0);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			// 放水 AI
			AiConfig config = getTankApp().getAiConfig();
			// 获得目标
			AbstractInstance target = receiver.getFireTarget();
			DataContext dc = receiver.getBahaviorDataContext();
			if (target == null || !target.isRobot() || dc.getBoolean(GOOF_AROUND_FLAG)) {
				return false;
			}

			// 若目标连输 3 场
			RoleWarInfo rwi = getBattleApp().getRoleWarInfo(receiver.getRoleId());
			if (rwi == null) {
				return false;
			}
			boolean loseContinue = rwi.getLoopLoseCount() >= config.getGoofAroundContinueLose();

			if (loseContinue) {
				dc.putBoolean(GOOF_AROUND_FLAG, true);

				float chance = (float) dc.getDouble(AliveCondition.GOOF_AROUND_CHANCE);
				float c = getTankApp().getAiConfig().getChanceOfGoofAround() / 100f;
				if (chance < c) {
					LogCore.runtime.info(receiver.getRoleName() + " 执行放水 AI");
					return true;
				}

				return false;
			}

			return false;
		}
	}

	static class StruggleConditon extends MapNode {

		Node<AbstractInstance> condition;

		public StruggleConditon(MapInstance map) {
			super(map);

			AiConfig config = getTankApp().getAiConfig();

			// 比赛时间大于 80%
			Node<AbstractInstance> timeOver20 = BattleTimePassedCondition.GE(map, config.getStruggleTriggerTime());
			// 我方人头 - 敌方人头 <= 3
			Node<AbstractInstance> scoreLE3   = BattleScoreOffsetCondition.LE(map, config.getStruggleTriggerScoreOffset());

			condition = new Sequence<>(timeOver20, scoreLE3);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {
			boolean eval = condition.eval(receiver);
			if (!eval) {
				return false;
			}

			DataContext dc = receiver.getBahaviorDataContext();
			float chance = (float) dc.getDouble(AliveCondition.STRUGGLE_CHANCE);
			float c = getTankApp().getAiConfig().getChangeOfStruggle() / 100f;
			if (chance < c) {
				LogCore.runtime.info(receiver.getRoleName() + " 执行保命 AI");
				return true;
			}
			return false;
		}
	}

	static class KillingStealCondition extends MapNode {

		float detectRange = 30.0f;
		float thresholdHealth = 0.2f;

		public KillingStealCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {

			// 判断周围 30 米内有无残血敌人
			for(AbstractInstance tank : map.getAllTank()) {

				if (tank.isDeath()
						|| tank.getId() == receiver.getId()
						|| tank.getTeam() == receiver.getTeam()) {
					continue;
				}

				float range = Util.range(receiver.getX(), receiver.getZ(), tank.getX(), tank.getZ());
				if (range < detectRange) {
					if (tank.get(AttrType.hp) * 1.0 / tank.get(AttrType.n_hpMax) < thresholdHealth) {
						// 找到了目标
						receiver.setFireTarget(tank);
						LogCore.runtime.info("{} 触发抢人头 {}", receiver.getRoleName(), tank.getRoleName());
						return true;
					}
				}
			}

			return false;
		}
	}

	/**
	 * 条件节点：轻坦点灯判断条件
	 */
	static class ScoutCondition extends MapNode {

		public ScoutCondition(MapInstance map) {
			super(map);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {

			// 是轻坦
			if (!isLT(receiver)) {
				return false;
			}

			DataContext dc = receiver.getBahaviorDataContext();
			float chance = (float) dc.getDouble(AliveCondition.SCOUT_CHANCE);
			float c = getTankApp().getAiConfig().getChanceOfScout() / 100f;
			if (chance < c) {
//				LogCore.runtime.info(receiver.getRoleName() + " 轻坦执行点灯 AI");
				return true;
			}

			return false;
		}

		private boolean isLT(AbstractInstance tank) {
			return tank.getCate() == Cate.LT;
		}
	}

	static class InferiorCondition extends Node<AbstractInstance> {

		final String INFERIOR_ACTION_FLAG = "RestraintCondition.inferiorActionFlag";

		@Override
		public void init(AbstractInstance receiver) {
			super.init(receiver);
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putBoolean(INFERIOR_ACTION_FLAG, false);
		}

		@Override
		public boolean eval(AbstractInstance receiver) {

			DataContext dc = receiver.getBahaviorDataContext();
			if (dc.getBoolean(INFERIOR_ACTION_FLAG)) {
				return false;
			}

			float chance = (float) dc.getDouble(AliveCondition.INFERIOR_CHANCE);
			float c = getTankApp().getAiConfig().getChanceOfInferior() / 100f;
			if (chance < c) {
				dc.putBoolean(INFERIOR_ACTION_FLAG, true);
				LogCore.runtime.info(receiver.getRoleName() + " 相克逃跑 AI");
				return true;
			}

			return false;
		}
	}
}
