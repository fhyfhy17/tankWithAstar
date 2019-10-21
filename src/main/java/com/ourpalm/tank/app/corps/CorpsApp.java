package com.ourpalm.tank.app.corps;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.message.CORPS_MSG.ApplyItem;
import com.ourpalm.tank.message.CORPS_MSG.CorpsLevel;
import com.ourpalm.tank.message.CORPS_MSG.RecruitType;
import com.ourpalm.tank.template.CorpsTemplate;
import com.ourpalm.tank.vo.result.CorpsFateResult;
import com.ourpalm.tank.vo.result.CorpsResult;
import com.ourpalm.tank.vo.result.CorpsSaluteResult;
import com.ourpalm.tank.vo.result.CorpsShopResult;
import com.ourpalm.tank.vo.result.CorpsTechDevResult;
import com.ourpalm.tank.vo.result.CorpsTechDonateResult;
import com.ourpalm.tank.vo.result.Result;

public interface CorpsApp extends Service{
	/** 1：可捐献 2：冷却中 3：经验值最大 4：军团等级不足 5：在研发中*/
	int CAN = 1;
	int COOL = 2;
	int EXP_MAX = 3;
	int CORPS_LVL_LESS = 4;
	int TECH_DEV = 5;

	/** 军团信息 */
	CorpsInfo getCorpsInfo(int id);
	CorpsInfo getCorpsInfo(int areaId, String name);

	/** 返回军团成员信息 */
	CorpsRole getCorpsRole(int corpsId, int roleId);

	/** 保存军团成员信息 */
	void saveCorpsRole(CorpsRole corpsRole);

	/** 删除军团成员 */
	void removeCorpsRole(int corpsId, int roleId);

	/** 删除申请的玩家 */
	void removeApplyRole(int corpsId, int appleRoleId);

	void lock(int key);
	void unlock(int key);

	/** 返回当前军团人数 */
	int getCorpsRoleSize(int corpsId);

	/** 判断军团人数是否已满 */
	boolean corpsIsFull(CorpsInfo corpsInfo);

	/** 返回军团所有成员列表 */
	List<CorpsRole> getCorpsRoleList(int corpsId);

	/** 分页获取军团信息 */
	List<CorpsInfo> getCorpsInfoList(int areaId, int page);

	/** 判断是否在此军团的审批队列中 */
	boolean hasExistCorpsApply(int corpsId, int roleId);

	CorpsTemplate getInitTemplate();

	/** 创建军团 */
	CorpsResult createCorps(int areaId, int roleId, String name);

	/** 玩家登录处理 */
	void login(int roleId, boolean nextDay);

	/** 加入军团 */
	CorpsResult joinCorps(int corpsId, int roleId);

	/** 修改军团宣言 */
	Result modifyDec(int corpsId, int roleId, String dec);

	/** 修改军团职位名称 */
	Result modifyTitleName(int corpsId, int roleId, CorpsLevel corpsLvl, String name);

	/** 返回军团申请加入列表 */
	List<ApplyItem> getCorpsApplyList(int roleId);

	/** 审批加入申请 */
	Result corpsApply(int roleId, int applyRoleId, CorpsInfo corps);

	/** 获取科技捐献状态 */
	int corpsTechState(CorpsInfo corps, CorpsRole corpsRole, int techId);

	/** 科技物品捐献 */
	CorpsTechDonateResult techGoodsDonate(int corpsId, int roleId, int techId, Map<Integer, Integer> goodsMap);

	/** 金币捐献 */
	CorpsTechDonateResult techGoldDonate(int corpsId, int roleId, int techId);

	/** 科技研发 */
	CorpsTechDevResult techDev(int corpsId, int roleId, int techId);

	/** 判断商城随机组是否存在 */
	boolean hadGoodsGroupExist(int groupId);

	/** 军团商城列表 */
	CorpsShopResult shopList(int roleId);

	/** 判断捐献冷却时间 处于冷却状态返回true */
	boolean hadDonateCoolTime(CorpsRole corpsRole);

	/** 手动刷新商城 */
	CorpsShopResult handFlushShop(int roleId);

	/** 提升职位 */
	CorpsFateResult corpFate(int type, int corpsId, int roleId, int targetRoleId);

	/** 退出和解散军团 */
	Result corpsDisband(CorpsInfo corps, CorpsRole corpsRole);

	/** 修改允许加入设置 */
	Result corpsSetting(int roleId, int corpsId, RecruitType type);

	/** 踢成员 */
	Result corspDrive(int corpsId, int roleId, int driveRoleId);

	/** 向长官敬礼 */
	CorpsSaluteResult corpsSalute(int corpsId, int roleId);
	/**取得军团等级*/
	int getCorpLevel(int roleId);
}
