package com.ourpalm.tank.util.hotUpdate;

import java.util.Collection;
import java.util.Map;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.tank.TankFormula;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class MapXLSWatch extends FileWatchdog {

	public MapXLSWatch(String filename) {
		super(filename);
	}

	@Override
	protected void doOnChange() {
		logger.info("map.xls 文件更改");
		// 与新手赛有关的
		GameContext.getMatchApp().loadTemplate();
		Map<Integer, MapInstance> map = GameContext.getMapApp().getAllInstanceMap();
		for (MapInstance m : map.values()) {
			PreseasonTemplate t = null;
			if (m.getPreseasonTemplate() != null) {
				t = m.getPreseasonTemplate();
				PreseasonTemplate pp = GameContext.getMatchApp().getPreseasonMap().get(t.getId());
				if (pp != null) {
					m.setPreseasonTemplate(pp);
				}
			}
	
		}
		
		// 与新手赛有关的---结束
		
		//更新地图配置
		GameContext.getMapApp().loadHotUpdateMap();
	}

}
