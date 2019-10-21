package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图网格数据
 * 
 * @author wangkun
 *
 */
public class MapDataTemplate {
	private String mapId;			//地图ID
	private float deviationX;		//X轴坐标偏移量(网格信息只是整张地图的一部分可活动范围)
	private float deviationZ;		//Z轴坐标偏移量(二维世界中的Y轴)
	private int cols;
	private int rows;
	private int cellSize;			//格子大小
	private List<GridTemplate> nodeList = new ArrayList<>();	//格子信息
	
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public float getDeviationX() {
		return deviationX;
	}
	public void setDeviationX(float deviationX) {
		this.deviationX = deviationX;
	}
	public float getDeviationZ() {
		return deviationZ;
	}
	public void setDeviationZ(float deviationZ) {
		this.deviationZ = deviationZ;
	}
	public List<GridTemplate> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<GridTemplate> nodeList) {
		this.nodeList = nodeList;
	}
	public int getCols() {
		return cols;
	}
	public void setCols(int cols) {
		this.cols = cols;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getCellSize() {
		return cellSize;
	}
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}
}
