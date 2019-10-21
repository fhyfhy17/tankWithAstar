package com.ourpalm.tank.app.member;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleLottery;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryType;
import com.ourpalm.tank.message.MEMBER_MSG.MemberItem;
import com.ourpalm.tank.template.MemberLotteryConfig;
import com.ourpalm.tank.template.MemberTemplate;
import com.ourpalm.tank.vo.result.LotteryResult;
import com.ourpalm.tank.vo.result.MemberCompositeResult;
import com.ourpalm.tank.vo.result.Result;

public interface MemberApp extends Service{
	
	int MULTI_COUNT = 5 ; //连抽数目
	
	MemberTemplate getMemberTemplate(int templateId); 
	
	MemberLotteryConfig getLotteryConfig(); 
	
	RoleLottery getRoleLottery(int roleId); 
	
	Map<String, RoleMember> getRoleMembers(int roleId);
	
	Map<Integer, String> getUseMemberId(int roleId);
	
	Map<String, RoleMember> getUseMember(int roleId);
	
	Result exchangeUseMember(int roleId, boolean dumpMedal, int type, String instanceId);
	
	/** 成员升级 */
	Result memberLevelUp(int roleId, String instanceId, List<String> eatInstanceIds, Map<Integer, Integer> goodsMap); 
	
	/** 成员佩戴勋章 */
	Result memberExchangeMedal(int roleId, String instanceId, List<Integer> medalIds);
	
	void login(int roleId, boolean nextDay); 
	
	/** 抽奖 */
	LotteryResult lottery(int roleId, LotteryType type);

	/** 合成成员 */
	MemberCompositeResult composite(int roleId, int goodsId); 

	MemberItem buildMemberItem(RoleMember member);
	
	/**
	 * 成员属性集合
	 * @param roleId
	 * @return
	 */
	Map<AttrType, Float> getMemberAttr(int roleId);
	
	
	/** 计算所使用的成员战斗力 */
	int calcUseMemberStrength(int roleId);
	
	/** 计算所使用的成员匹配分 */
	int calcUseMemberMatchScore(int roleId);
	
	/** 使用成员的勋章战斗力加成比 */
	float calcUseMedalStrengthRat(int roleId);
	
	/** 使用成员的勋章匹配分加成比 */
	float calcUseMedalMatchScoreRat(int roleId);
	
	/** 成员勋章开孔 */
	Result memberOpenMedalHole(int roleId, int templateId, int index);
	
	RoleMember addMember(int roleId, int templateId, String origin);
	
	RoleMember getRoleMember(int roleId, int templateId);
}
