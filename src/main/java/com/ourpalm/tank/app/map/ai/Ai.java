package com.ourpalm.tank.app.map.ai;


import java.util.Stack;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.ReadHightMapData;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public abstract class Ai {
	protected static final Logger logger = LogCore.runtime;
	protected AbstractInstance self;	//自身坦克
	protected MapInstance mapInstance;	//所在地图实例
	protected Grid grid;				//地图网格信息
	protected ReadHightMapData readHightMapData;	//地图高度数据
	
	protected Stack<Node> roads = new Stack<>();			//道路
	protected float x;
	protected float y;
	protected float z;
	protected int pointLvl ;			//点级别
	protected int teamId ;				//所属队伍
	protected int munberId ;			//所属队伍成员ID
	protected LoopCount loopCount ;
	
	protected AbstractInstance target;	//目标坦克
	
	protected boolean isInited;
	
	public enum SearchRoadRet {
		find, // 找到
		noFind, // 找不到路
		end, // 已经走到目标点，无需再找
		noEnd, // 路还没走完
		;
	}
	
	public Ai(AbstractInstance self){
		this.self = self;
	}
	public Ai(){}
	
	public void init(){
		if (!isInited) {
			mapInstance = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
			// 复制一份网格信息
			this.grid = (Grid) mapInstance.getGrid().clone();
			this.readHightMapData = mapInstance.getReadHightMapData();
			isInited = true;
		}
	}
	
	public void rotateTurrent() {
		self.getRoadManager().rotateTurrent(self.getFireTarget(), self.getX(), self.getY(), self.getZ());
	}
	
	
	/** 行为更新 */
	public abstract void update();
	
	public abstract AiType getType();
	
	/** 重置AI，当切换AI的时候，需要调用该函数 */
	public void reset(AbstractInstance target){
		this.setTarget(target);
	}
	
	/** 清除AI，当切换AI的时候，需要调用该函数 */
	public void clean(){
		this.target = null;
	}
	
	/** 清空路径 */
	public void clearRoads(){}
	
	/** 设置目标 */
	public void setTarget(AbstractInstance target){
		this.target = target;
	};
	
	//寻找一个离自身最近的可攻击目标
	protected AbstractInstance findNearEnemyOfFire(AbstractInstance outside){
		float minRange = Integer.MAX_VALUE;
		AbstractInstance nearTank = null;
		//寻找离自身最近并在射程内的目标
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.isDeath() 
					|| tank.getId() == self.getId() 
					|| tank.getTeam() == self.getTeam()){
				continue;
			}
			//排除现有目标外,另一个离自身最近的目标
			if(outside != null && tank.getId() == outside.getId()){
				continue;
			}
			float range = Util.range(self.getX(), self.getZ(), tank.getX(), tank.getZ());
			if(range < minRange){
				minRange = range;
				nearTank = tank;
			}
		}
		
		//判断是否在自己射程内,并进入视野
		if(self.get(AttrType.range) >= minRange && self.view(nearTank)){
			return nearTank;
		}
		return null;
	}
	
	//寻找一个离自身最近的可攻击目标
	protected AbstractInstance findNearEnemyOfFireInFlag(AbstractInstance outside){
		float minRange = Integer.MAX_VALUE;
		AbstractInstance nearTank = null;
		//寻找离自身最近并在射程内的目标
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.isDeath() 
					|| tank.getId() == self.getId() 
					|| tank.getTeam() == self.getTeam() 
					|| !tank.isInFlag() ){
				continue;
			}
			//排除现有目标外,另一个离自身最近的目标
			if(outside != null && tank.getId() == outside.getId()){
				continue;
			}
			float range = Util.range(self.getX(), self.getZ(), tank.getX(), tank.getZ());
			if(range < minRange){
				minRange = range;
				nearTank = tank;
			}
		}
		
		//判断是否在自己射程内,并进入视野
		if(self.get(AttrType.range) >= minRange && self.view(nearTank)){
			return nearTank;
		}
		return null;
	}
	
	//寻找一个离自身最近的可攻击目标
	protected boolean checkIsPursue(AbstractInstance target){
		boolean view = self.view(target);
		
		// 血量出发百分之三十的时候触发追击
		boolean hp = target.get(AttrType.hp).intValue() <= ( target.get(AttrType.n_hpMax).intValue() * 0.3 );
		
		System.out.println( "hp " + hp + " view " + view );
		return view && hp;
		
//		double minRange = Integer.MAX_VALUE;
//
//		double range = Util.range(self.getX(), self.getZ(), target.getX(), target.getZ());
//		if(range < minRange){
//			minRange = range;
//		}
//
//		// 这里暂时设定 乘以一个系数，让视野能扩大一些，要不坦克的视野范围其实比坦克的射击距离还短，基本这个函数会每次都 return false
//		range = self.get(AttrType.range) * 1.5;
//		
//		//System.out.println( "range "+ range + " minRange " + minRange);
//		//判断是否在自己射程内,并进入视野
//		if( range >= minRange ){
//			return true;
//		}
//		return false;
	}
	
	//是否可开火
	protected boolean isFire(AbstractInstance target){
		return !self.isDeath() 
				&& target != null
				&& !target.isDeath()
				&& self.turrentTowardsWith(target)
				&& self.view(target) //是否进入视野
				&& self.fireRange(target) //进入射程
				&& self.isFireElevation(target) //俯仰角判断
				&& !grid.hadBarrier(self.getX(), self.getZ(), target.getX(), target.getZ()); //是否有阻挡
	}

	public AbstractInstance getTarget() {
		return target;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public boolean isInited() {
		return isInited;
	}

	public void setInited(boolean isInited) {
		this.isInited = isInited;
	}
	
}
