package com.ourpalm.tank.template;

import java.util.List;

import com.ourpalm.tank.app.map.astar.Point;

public class MapAiPointTemplate {

	private String mapId;
	private int level;
	private int team;
	private List<Point> points;
	
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
