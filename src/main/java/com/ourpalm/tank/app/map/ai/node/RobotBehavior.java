package com.ourpalm.tank.app.map.ai.node;

import static com.ourpalm.tank.app.GameContext.getTankApp;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.applyEscapeStrategy;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.applyOuterSpaceStrategy;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.applyStayAwayStrategy;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.fire;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.fireMode;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.goofAround;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.inferiorAnalyze;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isAlive;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isEnemyDetected;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isEnemyDetectedSelf;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isHealthOverProgress;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isOuterSpace;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isPursue;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.isStayAway;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.killingSteal;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.move;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.moveable;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.notEscaping;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.notOuterSpace;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.notPursue;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.notStayAway;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.roadSearch;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.scoutAi;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.struggleAi;
import static com.ourpalm.tank.app.map.ai.node.AiNodes.timeAndScoreAi;

import com.ourpalm.tank.template.AiConfig;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.Restraint;
import com.ourpalm.tank.vo.behaviortree.BehaviorTree;
import com.ourpalm.tank.vo.behaviortree.Node;
import com.ourpalm.tank.vo.behaviortree.composite.Selector;
import com.ourpalm.tank.vo.behaviortree.composite.Sequence;
import com.ourpalm.tank.vo.behaviortree.decorator.Chance;
import com.ourpalm.tank.vo.behaviortree.decorator.Inverter;

public enum RobotBehavior {

	ESCAPE_WHEN_INFERIOR {

		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {

			Node<AbstractInstance> fireAvailable = new FireAvailableCondition(map);
			Node<AbstractInstance> rotateGun = new RotateGunAction(map);
			Node<AbstractInstance> moveWithCurrentStrategy = new Selector<>(new Sequence<>(moveable(), rotateGun, move(map)), roadSearch(map));

			Node<AbstractInstance> root = new Sequence<>(
				isAlive(),
				new Selector<>(
					new Sequence<>(
						notEscaping(), isEnemyDetected(map), fireAvailable, new Selector<>(
							new Sequence<>(new RestraintCondition(Restraint.INFERIOR), applyEscapeStrategy()),
							new Sequence<>(rotateGun, fire(map)))
					),
					fireMode(),
					moveWithCurrentStrategy
			));

			return new BehaviorTree<>(root);
		}

	},

	EWI_UNTIL_INVISIBLE {

		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}

	},

	STANDARD {

		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {

			Node<AbstractInstance> fireAvailable = new FireAvailableCondition(map);
			Node<AbstractInstance> pursueAvailable = new PursueAvailableCondition(map);
			Node<AbstractInstance> rotateGun = new RotateGunAction(map);

			Node<AbstractInstance> moveWithCurrentStrategy = new Selector<>(new Sequence<>(moveable(), rotateGun, move(map)), roadSearch(map));

			AiConfig config = getTankApp().getAiConfig();

			Node<AbstractInstance> root = new Sequence<>(
				// 前提是活着
				isAlive(), new Selector<>(new Sequence<>(
					// 未在逃跑状态，未在远离战场状态，未在外围移动狙击状态，未在追击状态，
					notEscaping(), notStayAway(), notOuterSpace(), notPursue(),
						// 如果遇敌，并且可开火，则旋转炮塔
						isEnemyDetected(map), fireAvailable, rotateGun, new Selector<>(
							// 概率触发：放水
							goofAround(),
							// 概率触发：保命
							struggleAi(map),
							// 概率触发：时间与比分
							new Chance<>(config.getChanceOfTimeAndScore() / 100f, timeAndScoreAi(map)),
							// 默认攻击行为，若血量大于 20%
							new Sequence<>(isHealthOverProgress(20), new Selector<>(
								// 概率触发：抢人头，若触发则开始追击
								new Chance<>(config.getChanceOfKillingSteal() / 100f, killingSteal(map)),
								// 克制击抵，都则逃跑
								inferiorAnalyze(),
								// 否则普通开火
								fire(map))),
							// 否则血量小于 20%，逃跑
							applyEscapeStrategy())),
					// 在追击状态，
					new Sequence<>(isPursue(), new Selector<>(
						// 追击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetected(map), pursueAvailable, rotateGun, fire(map))),
						// 否则走追击寻路，若有路走，走到下一个节点，否则寻路
						moveWithCurrentStrategy),
					// 在外围移动狙击状态或者远离战场状态（保命状态），
					new Sequence<>(new Selector<>(isStayAway(), isOuterSpace()), new Selector<>(
						// 外围移动狙击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetectedSelf(map), applyStayAwayStrategy(), moveWithCurrentStrategy)),
						// 否则走外围移动寻路，若有路走，走到下一个节点，否则寻路
						new Selector<>(
								new Sequence<>(isEnemyDetected(map), fireAvailable, rotateGun, fire(map)),
								new Sequence<>(applyOuterSpaceStrategy(), moveWithCurrentStrategy))),
					// 若在开火模式，则不往下进行寻路行为
					fireMode(),
					// 判断是否执行点灯的走位
					scoutAi(map),
					// 否则，根据当前策略进行寻路行为
					moveWithCurrentStrategy
			));

			return new BehaviorTree<>(root);
		}

	},

	TEACH_GUIDE_1 {

		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			return null;
		}
	},

	TEACH_GUIDE_2 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}
	},

	TEACH_GUIDE_3 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}
	},

	TEACH_GUIDE_4 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}
	},

	TEACH_GUIDE_5 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}
	},

	LEVEL_2 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {

			Node<AbstractInstance> fireAvailable = new FireAvailableCondition(map);
			Node<AbstractInstance> pursueAvailable = new PursueAvailableCondition(map);
			Node<AbstractInstance> rotateGun = new RotateGunAction(map);

			Node<AbstractInstance> moveWithCurrentStrategy = new Selector<>(new Sequence<>(moveable(), rotateGun, move(map)), roadSearch(map));

			AiConfig config = getTankApp().getAiConfig();

			Node<AbstractInstance> root = new Sequence<>(
				// 前提是活着
				isAlive(), new Selector<>(new Sequence<>(
					// 未在逃跑状态，未在远离战场状态，未在外围移动狙击状态，未在追击状态，
					notEscaping(), notPursue(),
						// 如果遇敌，并且可开火，则旋转炮塔
						isEnemyDetected(map), fireAvailable, rotateGun, new Selector<>(
							// 概率触发：放水
							goofAround(),
							// 默认攻击行为，若血量大于 20%
							new Sequence<>(isHealthOverProgress(20), new Selector<>(
								// 概率触发：抢人头，若触发则开始追击
								new Chance<>(config.getChanceOfKillingSteal() / 100f, killingSteal(map)),
								// 否则普通开火
								fire(map))),
							// 否则血量小于 20%，逃跑
							applyEscapeStrategy())),
					// 在追击状态，
					new Sequence<>(isPursue(), new Selector<>(
						// 追击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetected(map), pursueAvailable, rotateGun, fire(map))),
						// 否则走追击寻路，若有路走，走到下一个节点，否则寻路
						moveWithCurrentStrategy),
					// 若在开火模式，则不往下进行寻路行为
					fireMode(),
					// 否则，根据当前策略进行寻路行为
					moveWithCurrentStrategy
			));

			return new BehaviorTree<>(root);
		}
	},

	LEVEL_3 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {

			Node<AbstractInstance> fireAvailable = new FireAvailableCondition(map);
			Node<AbstractInstance> pursueAvailable = new PursueAvailableCondition(map);
			Node<AbstractInstance> rotateGun = new RotateGunAction(map);

			Node<AbstractInstance> moveWithCurrentStrategy = new Selector<>(new Sequence<>(moveable(), rotateGun, move(map)), roadSearch(map));

			AiConfig config = getTankApp().getAiConfig();

			Node<AbstractInstance> root = new Sequence<>(
				// 前提是活着
				isAlive(), new Selector<>(new Sequence<>(
					// 未在逃跑状态，未在远离战场状态，未在外围移动狙击状态，未在追击状态，
					notEscaping(), notStayAway(), notOuterSpace(), notPursue(),
						// 如果遇敌，并且可开火，则旋转炮塔
						isEnemyDetected(map), fireAvailable, rotateGun, new Selector<>(
							// 概率触发：放水
							goofAround(),
							// 概率触发：保命
							struggleAi(map),
							// 概率触发：时间与比分
							new Chance<>(config.getChanceOfTimeAndScore() / 100f, timeAndScoreAi(map)),
							// 默认攻击行为，若血量大于 20%
							new Sequence<>(isHealthOverProgress(20), new Selector<>(
								// 概率触发：抢人头，若触发则开始追击
								new Chance<>(config.getChanceOfKillingSteal() / 100f, killingSteal(map)),
								// 否则普通开火
								fire(map))),
							// 否则血量小于 20%，逃跑
							applyEscapeStrategy())),
					// 在追击状态，
					new Sequence<>(isPursue(), new Selector<>(
						// 追击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetected(map), pursueAvailable, rotateGun, fire(map))),
						// 否则走追击寻路，若有路走，走到下一个节点，否则寻路
						moveWithCurrentStrategy),
					// 在外围移动狙击状态或者远离战场状态（保命状态），
					new Sequence<>(new Selector<>(isStayAway(), isOuterSpace()), new Selector<>(
						// 外围移动狙击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetectedSelf(map), applyStayAwayStrategy(), moveWithCurrentStrategy)),
						// 否则走外围移动寻路，若有路走，走到下一个节点，否则寻路
						new Selector<>(
								new Sequence<>(isEnemyDetected(map), fireAvailable, rotateGun, fire(map)),
								new Sequence<>(applyOuterSpaceStrategy(), moveWithCurrentStrategy))),
					// 若在开火模式，则不往下进行寻路行为
					fireMode(),
					// 否则，根据当前策略进行寻路行为
					moveWithCurrentStrategy
			));

			return new BehaviorTree<>(root);
		}
	},
	
	LEVEL_4 {
		
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {

			Node<AbstractInstance> fireAvailable = new FireAvailableCondition(map);
			Node<AbstractInstance> pursueAvailable = new PursueAvailableCondition(map);
			Node<AbstractInstance> rotateGun = new RotateGunAction(map);

			Node<AbstractInstance> moveWithCurrentStrategy = new Selector<>(new Sequence<>(moveable(), rotateGun, move(map)), roadSearch(map));

			AiConfig config = getTankApp().getAiConfig();

			Node<AbstractInstance> root = new Sequence<>(
				// 前提是活着
				isAlive(), new Selector<>(new Sequence<>(
					// 未在逃跑状态，未在远离战场状态，未在外围移动狙击状态，未在追击状态，
					notEscaping(), notStayAway(), notOuterSpace(), notPursue(),
						// 如果遇敌，并且可开火，则旋转炮塔
						isEnemyDetected(map), fireAvailable, rotateGun, new Selector<>(
							// 概率触发：放水
							goofAround(),
							// 概率触发：保命
							struggleAi(map),
							// 概率触发：时间与比分
							new Chance<>(config.getChanceOfTimeAndScore() / 100f, timeAndScoreAi(map)),
							// 默认攻击行为，若血量大于 20%
							new Sequence<>(isHealthOverProgress(20), new Selector<>(
								// 概率触发：抢人头，若触发则开始追击
								new Chance<>(config.getChanceOfKillingSteal() / 100f, killingSteal(map)),
								// 克制击抵，都则逃跑
								inferiorAnalyze(),
								// 否则普通开火
								fire(map))),
							// 否则血量小于 20%，逃跑
							applyEscapeStrategy())),
					// 在追击状态，
					new Sequence<>(isPursue(), new Selector<>(
						// 追击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetected(map), pursueAvailable, rotateGun, fire(map))),
						// 否则走追击寻路，若有路走，走到下一个节点，否则寻路
						moveWithCurrentStrategy),
					// 在外围移动狙击状态或者远离战场状态（保命状态），
					new Sequence<>(new Selector<>(isStayAway(), isOuterSpace()), new Selector<>(
						// 外围移动狙击状态如果遇敌，并且可开火（不会放弃目标的开火），则旋转炮塔，普通开火
						new Sequence<>(isEnemyDetectedSelf(map), applyStayAwayStrategy(), moveWithCurrentStrategy)),
						// 否则走外围移动寻路，若有路走，走到下一个节点，否则寻路
						new Selector<>(
								new Sequence<>(isEnemyDetected(map), fireAvailable, rotateGun, fire(map)),
								new Sequence<>(applyOuterSpaceStrategy(), moveWithCurrentStrategy))),
					// 若在开火模式，则不往下进行寻路行为
					fireMode(),
					// 判断是否执行点灯的走位
					scoutAi(map),
					// 否则，根据当前策略进行寻路行为
					moveWithCurrentStrategy
			));

			return new BehaviorTree<>(root);
		}
	},

	LEVEL_5 {
		@Override
		public BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public abstract BehaviorTree<AbstractInstance> createBehaviorTree(MapInstance map);

	public static Node<AbstractInstance> invert(Node<AbstractInstance> node) {
		return new Inverter<>(node);
	}
}
