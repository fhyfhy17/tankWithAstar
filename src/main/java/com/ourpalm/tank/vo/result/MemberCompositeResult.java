package com.ourpalm.tank.vo.result;

import com.ourpalm.tank.domain.RoleMember;

public class MemberCompositeResult extends Result {
	private RoleMember member;

	public RoleMember getMember() {
		return member;
	}

	public void setMember(RoleMember member) {
		this.member = member;
	}
	
	public static MemberCompositeResult newFailure(String info){
		MemberCompositeResult result = new MemberCompositeResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
}
