package com.ourpalm.tank.vo.result;

public class CorpsFateResult extends Result{

	private int fateRoleId;		//被提升者roleId;
	private int corpsLvl;		//军团级别
	
	public static CorpsFateResult newFailure(String info){
		CorpsFateResult result = new CorpsFateResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
	
	public int getFateRoleId() {
		return fateRoleId;
	}
	public void setFateRoleId(int fateRoleId) {
		this.fateRoleId = fateRoleId;
	}
	public int getCorpsLvl() {
		return corpsLvl;
	}
	public void setCorpsLvl(int corpsLvl) {
		this.corpsLvl = corpsLvl;
	}
}
