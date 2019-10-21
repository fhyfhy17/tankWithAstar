package com.ourpalm.tank.vo.result;

public class CorpsSaluteResult extends Result{

	private int gold;
	
	public static CorpsSaluteResult newFailure(String info){
		CorpsSaluteResult result = new CorpsSaluteResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
}
