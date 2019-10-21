package com.ourpalm.tank.vo.result;

/**
 * 科技捐献结果对象
 */
public class CorpsTechDonateResult extends Result{

	private int state;		//科技状态
	private int coolTime;	//捐献冷却时间
	
	public static CorpsTechDonateResult newFailure(String info){
		CorpsTechDonateResult result = new CorpsTechDonateResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getCoolTime() {
		return coolTime;
	}
	public void setCoolTime(int coolTime) {
		this.coolTime = coolTime;
	}
}
