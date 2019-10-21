package com.ourpalm.tank.vo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_ONE_VIEW_MSG;

/**
 * AI 视野检测
 * 
 * @author fhy
 *
 */
public enum AITankViewDeal {
	INSTANCE,;

	/** 视野刷新频率 */
	public static final int VIEW_REFRESH_PERIOD = 250;

	public void update(MapInstance map, AbstractInstance self, long now) {
		if (now - self.getLastViewRefreshTime() < VIEW_REFRESH_PERIOD) {
			return;
		}
		self.setLastViewRefreshTime(now);
		Set<Integer> viewTanks = new HashSet<>();
		Collection<AbstractInstance> allTank = map.getAllTank();
		for (AbstractInstance tank : allTank) {
			if (tank.getTeam().ordinal() == self.getTeam().ordinal()) {
				continue;
			}
			float range = Util.range(self.getX(), self.getZ(), tank.getX(), tank.getZ());
			// 判断是否在自己视野内
			if (self.get(AttrType.n_view) >= range) {
				viewTanks.add(tank.getId());
			}
		}
		// 更新视野
		updateViewMap(map, self, viewTanks, false);
	}

	private void updateViewMap(MapInstance map, AbstractInstance self, Set<Integer> set, boolean isRole) {

		ConcurrentHashMap<Integer, Set<Integer>> viewMap = map.getViewMap();
		boolean ifChange = false;
		if (!viewMap.containsKey(self.getId())) {
			viewMap.put(self.getId(), set);
			ifChange = true;
			// 这里是发现 收益，发图标等
			if (set.size() > 0) {
				for (Integer tankId : set) {
					AbstractInstance beFoundTank = map.getTank(tankId);
					if (beFoundTank != null) {
						GameContext.getBattleApp().buildIncomeInfo(self, beFoundTank, 0, true, false, false, false);
					}
				}
			}
		} else {
			Set<Integer> oldView = viewMap.get(self.getId());
			if (oldView.size() != set.size()) {
				ifChange = true;
			}
			for (Integer tankId : set) {
				if (!oldView.contains(tankId)) {
					ifChange = true;
					// 这里是发现 收益，发图标等
					AbstractInstance beFoundTank = map.getTank(tankId);
					if (beFoundTank != null) {
						GameContext.getBattleApp().buildIncomeInfo(self, beFoundTank, 0, true, false, false, false);
					}
				}
			}

		}
		if (ifChange) {
			viewMap.put(self.getId(), set);
			if (!isRole) {
				sendView(map.getAllTank(), set, self);
			}
		}
	}

	private void sendView(Collection<AbstractInstance> tanks, Set<Integer> set, AbstractInstance self) {
		for (AbstractInstance t : tanks) {
			if (t.getRoleId() > 0 && t.getTeam().ordinal() == self.getTeam().ordinal()) {
				STC_ONE_VIEW_MSG.Builder builder = STC_ONE_VIEW_MSG.newBuilder();
				builder.setTankId(self.getId());
				if (set.size() > 0) {
					builder.addAllTankIds(set);
					builder.setInclueDate(true);
				} else {
					builder.addAllTankIds(set);
					builder.setInclueDate(false);
				}
				RoleConnect _connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(t.getRoleId());
				if (_connect != null) {
					_connect.sendMsg(builder.build());
				}
			}
		}
	}

	/**
	 * 玩家视野更新给AI
	 */
	public void roleViewChange(AbstractInstance self, boolean includData, List<Integer> list) {
		MapInstance map = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		if (map == null) {
			return;
		}
		Set<Integer> set = null;
		if (includData) {
			set = new HashSet<>(list);
		} else {
			set = new HashSet<>();
		}
		Set<Integer> keepSet = new HashSet<>();
		for (Integer integer : set) {
			if (map.getTank(integer) != null) {
				keepSet.add(integer);
			}
		}

		updateViewMap(map, self, keepSet, true);
	}

	/**
	 * 判断目标是否在 我方的视野中
	 * 
	 * @param target
	 * @param self
	 * @return
	 */
	public boolean view(AbstractInstance target, AbstractInstance self) {
		if (target == null || self == null) {
			LogCore.runtime.info("视野自己或者目标传进来为空");
			return false;
		}
		if (target.getMapInstanceId() != self.getMapInstanceId()) {
			return false;
		}
		MapInstance map = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		if (map == null) {
			return false;
		}
		ConcurrentHashMap<Integer, Set<Integer>> viewMap = map.getViewMap();
		if (viewMap == null) {
			return false;
		}
		boolean inview = false;
		for (Entry<Integer, Set<Integer>> entry : viewMap.entrySet()) {

			AbstractInstance tank = map.getTank(entry.getKey());
			if (tank == null) {
				continue;
			}
			if (tank.getTeam().ordinal() != self.getTeam().ordinal()) {
				continue;
			}
			if (entry.getValue().contains(target.getId())) {
				inview = true;
			}
		}
		return inview;
	}

	/**
	 * @param target
	 * @param self
	 * @return
	 */
	public boolean viewSelf(AbstractInstance target, AbstractInstance self) {
		if (!view(target, self)) {
			return false;
		}
		float range = Util.range(self.getX(), self.getZ(), target.getX(), target.getZ());
		// 判断是否在自己视野内
		return self.get(AttrType.n_view) >= range;
	}
}
