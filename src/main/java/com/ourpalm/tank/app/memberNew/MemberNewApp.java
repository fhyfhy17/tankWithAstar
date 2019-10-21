package com.ourpalm.tank.app.memberNew;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_LIST_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_STAR_UP_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_UP_DOWN_MSG;
import com.ourpalm.tank.vo.result.Result;

public interface MemberNewApp extends Service {
	/**
	 * 取得当前装备乘员的属性
	 * 
	 * @param roleId
	 * @return
	 */
	Map<AttrType, Float> getMemberAttr(int roleId);

	/**
	 * 取得当前装备乘员技能的属性
	 * 
	 * @param roleId
	 * @return
	 */
	Map<AttrType, Float> getMemberSkillAttr(int roleId);

	/**
	 * 取得当前装备乘员的战斗力
	 * 
	 * @param roleId
	 * @return
	 */
	int getMemberBattleNum(int roleId);

	/**
	 * 请求乘员列表
	 * 
	 * @param roleId
	 * @param builder
	 */
	void getMemberList(int roleId, STC_MEMBER_NEW_LIST_MSG.Builder builder);

	/**
	 * 添加乘员
	 * 
	 * @param roleId
	 * @param memberTemplateId
	 */
	void addMember(int roleId, int memberTemplateId);

	/**
	 * 上阵下阵乘员
	 * 
	 * @param roleId
	 * @param uniqueId
	 *            乘员唯一ID
	 * @param builder
	 * @param type
	 *            0下1上
	 * @param column
	 *            操作的栏
	 * @return
	 */
	Result memerUpAndDown(int roleId, String uniqueId, STC_MEMBER_NEW_UP_DOWN_MSG.Builder builder, int type, int column);

	/**
	 * 乘员升级
	 * 
	 * @param roleId
	 * @param uniqueId
	 *            要升级的
	 * @param eatUniqueIdIds
	 *            被吃的乘员
	 * @param goodsMap
	 *            被吃的经验卡
	 * @return
	 */
	Result upLevel(int roleId, String uniqueId, List<String> eatUniqueIdIds, Map<Integer, Integer> goodsMap);

	/**
	 * 乘员升星
	 * 
	 * @param roleId
	 * @param uniqueId
	 *            要升星的
	 * @param eatUniqueIdIds
	 *            被吃的乘员
	 * @param builder
	 * @return
	 */
	Result upStar(int roleId, String uniqueId, List<String> eatUniqueIdIds, STC_MEMBER_NEW_STAR_UP_MSG.Builder builder);

	/**
	 * 栏位主推
	 * 
	 * @param roleId
	 * @param column
	 */
	void columnUpdateSend(int roleId, int column);
}
