package com.ourpalm.tank.domain;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.MapTemplate;

/**
 * 玩家所在战场记录，主要用于断线重连
 * @author wangkun
 *
 */
public class RoleBattle {

	private int roleId;
	private int tankId;				//主战坦克ID
	private int tankInstanceId; 	//坦克实例id
	private String nodeName;		//所在服务节点名
	private long createTime;		//战场创建时间
	private int warType;			//战场类型
	private int mapIndex;			//地图索引
	
	
	/** 判断比赛是否已经结束 **/
	public boolean hadOverTime(){
		MapTemplate mapTemplate = GameContext.getMapApp().getMapTemplate(mapIndex);
		if(mapTemplate == null){
			return true;
		}
		long time = (mapTemplate.getReadyTime() + mapTemplate.getOverTime()) * 1000;
		
		return (System.currentTimeMillis() - createTime) > time;
	}
	
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getTankInstanceId() {
		return tankInstanceId;
	}
	public void setTankInstanceId(int tankInstanceId) {
		this.tankInstanceId = tankInstanceId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getWarType() {
		return warType;
	}
	public void setWarType(int warType) {
		this.warType = warType;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getMapIndex() {
		return mapIndex;
	}
	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}
}
