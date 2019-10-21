package com.ourpalm.tank.app.match;

public class TeamItem {

    private int id;
    private int level;
    private int type;
    private int elite;
    private double score;

    private String getName() {
    	switch (type) {
		case 1:
			return "轻";
		case 2:
			return "反";
		case 3:
			return "中";
		case 4:
			return "重";
		default:
			return "炮";
		}
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	public int getElite() {
		return elite;
	}

	public void setElite(int elite) {
		this.elite = elite;
	}

    @Override
    public String toString() {
        return level + getName() + (elite == 1 ? "+" : "");
    }
}
