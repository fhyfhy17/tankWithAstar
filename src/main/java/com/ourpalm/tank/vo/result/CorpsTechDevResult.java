package com.ourpalm.tank.vo.result;

/**
 * 科技研发结果对象
 * 
 * @author wangkun
 *
 */
public class CorpsTechDevResult extends Result{

	private int devTime;	//研发倒计时 单位：秒
	
	public static CorpsTechDevResult newFailure(String info){
		CorpsTechDevResult result = new CorpsTechDevResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}

	public int getDevTime() {
		return devTime;
	}
	public void setDevTime(int devTime) {
		this.devTime = devTime;
	}
}
