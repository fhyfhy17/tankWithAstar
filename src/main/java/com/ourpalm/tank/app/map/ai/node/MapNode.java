package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.app.map.ReadHightMapData;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public abstract class MapNode extends Node<AbstractInstance> {

	protected MapInstance map;	                 // 所在地图实例
	protected Grid grid;				         // 地图网格信息
	protected ReadHightMapData readHightMapData; // 地图高度数据

	public MapNode(MapInstance map) {
		this.map = map;
		this.grid = (Grid) map.getGrid().clone();
		this.readHightMapData = map.getReadHightMapData();
	}

	@Override
	public void reset(AbstractInstance tank) {
		map = null;
		grid = null;
		readHightMapData = null;
	}
}
