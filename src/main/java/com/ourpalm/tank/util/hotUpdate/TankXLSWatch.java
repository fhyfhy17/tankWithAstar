package com.ourpalm.tank.util.hotUpdate;

import java.util.Collection;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.tank.TankFormula;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class TankXLSWatch extends FileWatchdog {

	public TankXLSWatch(String filename) {
		super(filename);
	}

	@Override
	protected void doOnChange() {
		logger.info("tank.xls 文件更改");
		GameContext.getTankApp().hotLoadTankTemplate();
		Map<Integer, MapInstance> maps = GameContext.getMapApp().getAllInstanceMap();
		for (MapInstance in : maps.values()) {
			Collection<AbstractInstance> tanks = in.getAllTank();
			for (AbstractInstance tankInstance : tanks) {
				// 设置属性
				Map<AttrType, Float> attrMap = GameContext.getTankApp().tankBornAttr(tankInstance);
				// 赋值到坦克身上
				tankInstance.setAttribute(attrMap);
				// 计算最终属性
				TankFormula.calcAttr(attrMap);
				// 设置当前血量
				tankInstance.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));
				// 初始化坦克
				tankInstance.init();
			}
		}
	}

}
