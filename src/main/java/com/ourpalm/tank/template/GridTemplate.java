package com.ourpalm.tank.template;

/**
 * 格子信息
 * 
 * @author wangkun
 *
 */
public class GridTemplate {

	private int index;		//格子索引值
	private int x;			//格子位置坐标系的X坐标
	private int z;			//格子位置坐标系的Y坐标
	private float px;			//格子中心的世界X坐标
	private float py;			//格子中心的世界Y坐标
	private float pz;			//格子中心的世界Z坐标
	private int walk;			//是否通行 0: 不可通行  1: 可通行
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public float getPx() {
		return px;
	}
	public void setPx(float px) {
		this.px = px;
	}
	public float getPy() {
		return py;
	}
	public void setPy(float py) {
		this.py = py;
	}
	public float getPz() {
		return pz;
	}
	public void setPz(float pz) {
		this.pz = pz;
	}
	public int getWalk() {
		return walk;
	}
	public void setWalk(int walk) {
		this.walk = walk;
	}
}
