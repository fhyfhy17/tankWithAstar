package com.ourpalm.tank.app.match;

import java.util.Collection;
import java.util.Map;

public class Team {

    private int pivotId;                  // 队伍枢纽
    private double battleScore;              // 队伍战斗力
    private Map<Integer, TeamItem> tanks; // 队伍包含坦克

    public Team(Map<Integer, TeamItem> tanks) {
        this.tanks = tanks;
        if (tanks == null || tanks.isEmpty()) {
            throw new IllegalArgumentException();
        }
        findPivot();
    }

    private void findPivot() {
    	TeamItem pivot = null;
        for (TeamItem t : tanks.values()) {
            if (pivot == null || pivot.getLevel() < t.getLevel()) {
                pivot = t;
            }
            battleScore += t.getScore();
        }
        if (pivot != null) {
            pivotId = pivot.getId();
        }
    }

    public int getPivotId() {
        return pivotId;
    }

    public TeamItem getPivot() {
        return tanks.get(pivotId);
    }

    public int sizeOfTanks() {
        return tanks.size();
    }

    public Collection<TeamItem> getTanks() {
        return tanks.values();
    }

    @Override
    public String toString() {
        return getTanks().toString();
    }

    @Override
    public int hashCode() {
        return pivotId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Team) {
        	Team team = (Team) obj;
            return team.pivotId == pivotId;
        }
        return false;
    }

	public double getBattleScore() {
		return battleScore;
	}

	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}
}
