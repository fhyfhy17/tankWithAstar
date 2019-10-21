package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.map.astar.Point;

public class TankAiTestTemplate {
	private int index;			//AI坦克
	private int camp;			//阵营
	private int tankId;			//坦克ID
	private int battleScore;	//
	private String path;		//AI固定路径
	
	private List<Point> points = new ArrayList<>();
	
	public void init(){
		if(Util.isEmpty(path)){
			LogCore.startup.warn("坦克配置表中,没有配置任何AI固定路线");
			return ;
		}
		this.buildPointsList(path, points);
	}
	
	private void buildPointsList(String path, List<Point> points){
		for(String point : path.split(Cat.comma)){
			if(Util.isEmpty(point)){
				continue;
			}
			String[] index = point.split(Cat.colon);
			try {
				int x = Integer.parseInt(index[0]);
				int y = Integer.parseInt(index[1]);
				points.add(new Point(x,y));
			} catch (Exception e) {
				LogCore.startup.warn("坦克配置表中,AI固定路线,配置异常！");
			}
		}
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCamp() {
		return camp;
	}
	public void setCamp(int camp) {
		this.camp = camp;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getBattleScore() {
		return battleScore;
	}
	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
}
