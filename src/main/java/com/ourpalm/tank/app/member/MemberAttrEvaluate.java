package com.ourpalm.tank.app.member;

import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public interface MemberAttrEvaluate extends Service {
	
	/** 返回正在使用的成员属性Map */
	Map<AttrType, Float> getMemberAttr(int roleId);
	
	/** 计算成员战斗力 */
	int calcMemberStrength(RoleMember member);
	
	/** 计算成员匹配分 */
	int calcMemberMatchScore(RoleMember member);
}
