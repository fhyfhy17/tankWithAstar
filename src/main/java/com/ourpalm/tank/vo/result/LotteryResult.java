package com.ourpalm.tank.vo.result;

import java.util.ArrayList;
import java.util.List;

public class LotteryResult extends Result{
	
	private List<Reward> reward = new ArrayList<>();
	
	public void addReward(Reward peshe) {
		this.reward.add(peshe);
	}
	
	public List<Reward> getReward(){
		return this.reward;
	}
	

	public static LotteryResult newFailure(String info){
		LotteryResult result = new LotteryResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
}
