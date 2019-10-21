package com.ourpalm.tank.app.corps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.CorpsDao;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.ShopItem;
import com.ourpalm.tank.domain.TechnologyInfo;
import com.ourpalm.tank.message.CORPS_MSG.ApplyItem;
import com.ourpalm.tank.message.CORPS_MSG.CorpsLevel;
import com.ourpalm.tank.message.CORPS_MSG.RecruitType;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_DISBAND_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_JOIN_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.CorpsGoodsTemplate;
import com.ourpalm.tank.template.CorpsShopTemplate;
import com.ourpalm.tank.template.CorpsTechTemplate;
import com.ourpalm.tank.template.CorpsTemplate;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.ShopPeshe;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.CorpsFateResult;
import com.ourpalm.tank.vo.result.CorpsResult;
import com.ourpalm.tank.vo.result.CorpsSaluteResult;
import com.ourpalm.tank.vo.result.CorpsShopResult;
import com.ourpalm.tank.vo.result.CorpsTechDevResult;
import com.ourpalm.tank.vo.result.CorpsTechDonateResult;
import com.ourpalm.tank.vo.result.Result;


public class CorpsAppImpl implements CorpsApp{
	private Logger logger = LogCore.runtime;
	private CorpsDao corpsDao;
	private CorpsTemplate initTemplate ;
	private Map<Integer, CorpsTechTemplate> techMap = new HashMap<>();
	private Map<Integer, CorpsShopTemplate> shopMap = new HashMap<>();
	//物品随机组(key=组ID)
	private Map<Integer, List<ShopPeshe>> randomGoodsMap = new HashMap<>();


	@Override
	public void start() {
		this.loadCorpsTemplate();
		this.loadCorpsTechTemplate();
		this.loadCorpsGoodsTemplate();
		this.loadCorpsShopTemplate();
	}


	/** 玩家登录处理 */
	@Override
	public void login(int roleId, boolean nextDay){
		if(!nextDay){
			return ;
		}
		//判断是否有军团
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if(corpsId <= 0){
			return ;
		}
		//处理活跃度
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return ;
		}
		final int nowTime = (int)(System.currentTimeMillis() / ( 24 * 60 * 60 * 1000));
		final Map<Integer, Integer> activeMap = new HashMap<>(corpsRole.getActiveMap());
		//清除过期记录
		for(Entry<Integer, Integer> entry : corpsRole.getActiveMap().entrySet()){
			int dateTime = entry.getKey();
			if(dateTime < (nowTime - 8)){
				activeMap.remove(dateTime);
				continue;
			}
		}
		//登录增加1点活跃度
		int active = 1;
		if(activeMap.containsKey(nowTime)){
			active += activeMap.get(nowTime);
		}
		activeMap.put(nowTime, active);
		corpsRole.setActiveMap(activeMap);
		corpsDao.saveCorpsRole(corpsRole);
	}


	@Override
	public CorpsTemplate getInitTemplate() {
		return initTemplate;
	}


	@Override
	public Result modifyDec(int corpsId, int roleId, String dec){
		if(corpsId <= 0){
			return Result.newFailure(Tips.CORPS_NO_HAD);
		}
		if(! this.hadCorpsModifyPower(corpsId, roleId)){
			return Result.newFailure(Tips.CORPS_NO_HAD);
		}

		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		corps.setDec(dec);
		corpsDao.saveCorpsInfo(corps);

		return Result.newSuccess();
	}


	@Override
	public Result modifyTitleName(int corpsId, int roleId, CorpsLevel corpsLvl, String name){
		if(corpsId <= 0){
			return Result.newFailure(Tips.CORPS_NO_HAD);
		}
		if(! this.hadCorpsModifyPower(corpsId, roleId)){
			return Result.newFailure(Tips.CORPS_NO_HAD);
		}

		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		switch(corpsLvl){
			case s1 : corps.setS1Name(name); break;
			case s2 : corps.setS2Name(name); break;
			case s3 : corps.setS3Name(name); break;
			case s4 : corps.setS4Name(name); break;
			case s5 : corps.setS5Name(name); break;
		}
		corpsDao.saveCorpsInfo(corps);

		return Result.newSuccess();
	}


	@Override
	public Result corpsApply(int roleId, int applyRoleId, CorpsInfo corps){
		//判断用户是否已加入军团
		int corpsId = GameContext.getUserApp().getCorpsId(applyRoleId);
		if(corpsId > 0){
			return Result.newFailure(Tips.CORPS_HAD);
		}
		
		//判断自己是否拥有审批权限
		CorpsRole corpsRole = corpsDao.getCorpsRole(corps.getId(), roleId);
		if( corpsRole == null ) {
			logger.error("获取军团角色信息异常 corpsId {} roleId {}", corps.getId(), roleId);
			return Result.newFailure(Tips.CORPS_IS_NULL);
		}
		
		if(corpsRole.getJob() != CorpsLevel.s1_VALUE
				&& corpsRole.getJob() != CorpsLevel.s2_VALUE){
			return Result.newFailure(Tips.CORPS_NO_POWER);
		}
		//判断军团人数
		if(this.corpsIsFull(corps)){
			//从申请列表中移除
			removeApplyRole(corps.getId(), applyRoleId);
			return Result.newFailure(Tips.CORPS_IS_FULL);
		}
		//加入军团
		this.putCorps(corps.getId(), applyRoleId);

		RoleConnect connect = GameContext.getUserApp().getRoleConnect(applyRoleId);
		if(connect != null){
			connect.sendMsg(STC_CORPS_JOIN_MSG.newBuilder()
					.setId(corps.getId())
					.setResult(Result.SUCCESS)
					.setInfo(Tips.CORPS_HAD + corps.getName())
					.build());
		}

		return Result.newSuccess();
	}


	/** 是否有修改权限 */
	private boolean hadCorpsModifyPower(CorpsRole corpsRole){
		final int corpsLvl = corpsRole.getJob();
		return corpsLvl == CorpsLevel.s1_VALUE || corpsLvl == CorpsLevel.s2_VALUE;
	}


	/** 军团踢人 */
	@Override
	public Result corspDrive(int corpsId, int roleId, int driveRoleId){
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_EXIST);
		}
		//是否有权限
		if(!this.hadCorpsModifyPower(corpsRole)){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_POWER);
		}
		CorpsRole tarRole = corpsDao.getCorpsRole(corpsId, driveRoleId);
		if(tarRole == null){
			return CorpsFateResult.newFailure(Tips.CORPS_OTHER_NO_EXIST);
		}
		//判断对方权限是否高于自身
		//职位越小表示权限大
		int selfJob = corpsRole.getJob();
		int tarJob = tarRole.getJob();
		if(tarJob <= selfJob){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_POWER);
		}
		//踢人操作
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(driveRoleId);
		if(connect != null){
			connect.sendMsg(STC_CORPS_DISBAND_MSG.newBuilder()
					.setResult(Result.SUCCESS)
					.setExit(false)
					.setInfo(Tips.CORPS_DRIVE_ROLE)
					.build());
		}
		//设置踢人者军团ID
		GameContext.getUserApp().saveCorpsId(driveRoleId, 0);
		
		//删除成员
		corpsDao.removeCorpsRole(corpsId, driveRoleId);

		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);

		//如果为副团长更新军团对象
		if(tarRole.getJob() == CorpsLevel.s2_VALUE){
			corps.getViceLeaders().remove(driveRoleId);
		}

		//添加踢人记录
		corps.getDriveRecords().put(driveRoleId, System.currentTimeMillis());
		corpsDao.saveCorpsInfo(corps);

		return Result.newSuccess();
	}



	@Override
	public CorpsFateResult corpFate(int type, int corpsId, int roleId, int targetRoleId){
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_EXIST);
		}
		//是否有权限
		if(!this.hadCorpsModifyPower(corpsRole)){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_POWER);
		}
		//自身权限是否比对方高
		CorpsRole tarRole = corpsDao.getCorpsRole(corpsId, targetRoleId);
		if(tarRole == null){
			return CorpsFateResult.newFailure(Tips.CORPS_OTHER_NO_EXIST);
		}
		//职位越小表示权限大
		int selfJob = corpsRole.getJob();
		int tarJob = tarRole.getJob();
		if(tarJob <= selfJob){
			return CorpsFateResult.newFailure(Tips.CORPS_NO_POWER);
		}
		switch(type){
			case 1 : return this.upJob(tarRole);
			case 2 : return this.downJob(tarRole);
		}
		return CorpsFateResult.newFailure("操作类型错误");
	}

	/** 提升职位 */
	private CorpsFateResult upJob(CorpsRole tarRole){
		final int tarJob = tarRole.getJob();
		final int corpsId = tarRole.getCorpsId();
		CorpsFateResult result = new CorpsFateResult();
		CorpsLevel rstLvl = CorpsLevel.valueOf(tarJob - 1);
		if(rstLvl == null || rstLvl == CorpsLevel.s1){
			result.setResult(Result.FAILURE);
			result.setInfo(Tips.CORPS_MAX_JOB);
			return result;
		}
		//如果是S2，需判断人数
		if(rstLvl == CorpsLevel.s2){
			CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
			Set<Integer> viceLeaders = corps.getViceLeaders();
			if(viceLeaders.size() >= 3){
				result.setResult(Result.FAILURE);
				result.setInfo(Tips.CORPS_JOB_FULL);
				result.setCorpsLvl(CorpsLevel.valueOf(tarJob).getNumber());
				return result;
			}
			viceLeaders.add(tarRole.getRoleId());
			corpsDao.saveCorpsInfo(corps);
			logger.debug("提升为副团长... tarRoleId = {} 副团长列表: {}", tarRole.getRoleId(), JSON.toJSONString(viceLeaders));
		}
		tarRole.setJob(rstLvl.getNumber());
		corpsDao.saveCorpsRole(tarRole);

		result.setResult(Result.SUCCESS);
		result.setFateRoleId(tarRole.getRoleId());
		result.setCorpsLvl(rstLvl.getNumber());
		return result;
	}


	/** 降低职位 */
	private CorpsFateResult downJob(CorpsRole tarRole){
		final int tarJob = tarRole.getJob();
		final int corpsId = tarRole.getCorpsId();
		CorpsFateResult result = new CorpsFateResult();
		CorpsLevel rstLvl = CorpsLevel.valueOf(tarJob + 1);
		if(rstLvl == null){
			result.setResult(Result.FAILURE);
			result.setInfo(Tips.CORPS_MIN_JOB);
			return result;
		}
		//副团长降职得清除副团长列表
		if(tarJob == CorpsLevel.s2_VALUE){
			CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
			corps.getViceLeaders().remove(tarRole.getRoleId());
			corpsDao.saveCorpsInfo(corps);
			logger.debug("从副团长降职... tarRoleId = {}, 副团长列表: {}", tarRole.getRoleId(), JSON.toJSONString(corps.getViceLeaders()));
		}

		tarRole.setJob(rstLvl.getNumber());
		corpsDao.saveCorpsRole(tarRole);

		result.setResult(Result.SUCCESS);
		result.setFateRoleId(tarRole.getRoleId());
		result.setCorpsLvl(rstLvl.getNumber());
		return result;
	}


	//判断军团权限是否可修改操作
	private boolean hadCorpsModifyPower(int corpsId, int roleId){
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return false;
		}
		final int corpsLvl = corpsRole.getJob();
		return corpsLvl == CorpsLevel.s1_VALUE || corpsLvl == CorpsLevel.s2_VALUE;
	}


	@Override
	public List<ApplyItem> getCorpsApplyList(int roleId){
		List<ApplyItem> list = new ArrayList<>();
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if(corpsId <= 0){
			return list;
		}

		Set<String> applySet = corpsDao.getCorpsApplyList(corpsId);
		for(String strRoleId : applySet){
			int _roleId = Integer.parseInt(strRoleId);
			RoleAccount _role = GameContext.getUserApp().getRoleAccount(_roleId);
			int level = _role.getLevel();
			//判断是否已经加入军团
			int _corpsId = GameContext.getUserApp().getCorpsId(_roleId);
			if(_corpsId > 0){
				corpsDao.removeCorpsApply(corpsId, _roleId);
				continue;
			}
			ApplyItem item = ApplyItem.newBuilder()
					.setRoleId(_roleId)
					.setRoleName(_role.getRoleName())
					.setLevel(level)
					.setVipLvl(_role.getVipLevel())
					.setBattleScore(_role.getBattleScore())
					.setTitleId(_role.getCurrTitleId())
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(_role.getRoleId()))
					.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(_role.getRoleId()))
					.build();
			list.add(item);
		}
		return list;
	}


	@Override
	public void stop() { }


	/** 向长官敬礼 */
	@Override
	public CorpsSaluteResult corpsSalute(int corpsId, int roleId){
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		if(corpsRole == null || corps == null){
			return CorpsSaluteResult.newFailure(Tips.CORPS_NO_EXIST);
		}

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);

		//判断敬礼时间
		boolean saluteBeforeEnter = new Date(role.getLastSaluteTime()).before(new Date(corpsRole.getEnterTime()));
		if (DateUtil.isSameDay(System.currentTimeMillis(), corpsRole.getSaluteTime())
				|| (saluteBeforeEnter
				&& DateUtil.getDaysInterval(System.currentTimeMillis(), role.getLastSaluteTime()) <= 0)) {
			return CorpsSaluteResult.newFailure(Tips.CORPS_HAD_SALUTE);
		}

		//敬礼奖励
		int saluteGold = initTemplate.getSaluteGold();
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.add, saluteGold), OutputType.corpsSaluteInc.type(), StringUtil.buildLogOrigin(corps.getName(), OutputType.corpsSaluteInc.getInfo()));

		//设置敬礼时间
		corpsRole.setSaluteTime(System.currentTimeMillis());
		corpsDao.saveCorpsRole(corpsRole);

		role.setLastSaluteTime(System.currentTimeMillis());
		GameContext.getUserApp().saveRoleAccount(role);

		CorpsSaluteResult result = new CorpsSaluteResult();
		result.setResult(Result.SUCCESS);
		result.setGold(saluteGold);

		return result;
	}

	@Override
	public CorpsResult createCorps(int areaId, int roleId, String name) {
		CorpsResult result = new CorpsResult();
		//判断名字
		boolean hadExist = corpsDao.hasExistCorpsName(areaId, name);
		if(hadExist){
			result.setInfo(Tips.CORPS_NAME_EXIST);
			return result;
		}
		//判断金币数是否足够
		//扣除金币
		final int createDiamonds = this.initTemplate.getCreateDiamonds();
		boolean cnsGoldRst = GameContext.getUserAttrApp().changeAttribute(roleId,
				AttrUnit.build(RoleAttr.diamonds, Operation.decrease, createDiamonds), true,
				OutputType.corpsCreateDec.type(),
				StringUtil.buildLogOrigin(name, OutputType.corpsCreateDec.getInfo()));
		if(!cnsGoldRst){
			result.setInfo(Tips.NEDD_DIAMONDS);
			return result;
		}

		//创建军团
		final int id = corpsDao.nextId();
		CorpsInfo corps = new CorpsInfo();
		corps.setId(id);
		corps.setAreaId(areaId);
		corps.setLeaderId(roleId);
		corps.setName(name);
		corps.setS1Name(this.initTemplate.getInitS1());
		corps.setS2Name(this.initTemplate.getInitS2());
		corps.setS3Name(this.initTemplate.getInitS3());
		corps.setS4Name(this.initTemplate.getInitS4());
		corps.setS5Name(this.initTemplate.getInitS5());

		//初始化科技
		for(String strId : initTemplate.getInitTech().split(Cat.comma)){
			if(Util.isEmpty(strId)){
				continue;
			}
			int techId = Integer.parseInt(strId);

			CorpsTechTemplate template = this.techMap.get(techId);
			if(template == null){
				continue;
			}
			//设置人数上限
			corps.setPlayerLimit(Math.max(corps.getPlayerLimit(), template.getPlayerCount()));
			//商城等级
			corps.setShopLevel(Math.max(corps.getShopLevel(), template.getLevelShop()));

			TechnologyInfo info = new TechnologyInfo();
			info.setTechId(techId);
			corps.getTechInfoMap().put(techId, info);
		}

		corpsDao.createCorps(corps);

		//创建成员
		CorpsRole corpsRole = new CorpsRole();
		corpsRole.setCorpsId(id);
		corpsRole.setJob(CorpsLevel.s1_VALUE);
		corpsRole.setRoleId(roleId);
		corpsRole.setEnterTime(System.currentTimeMillis());
		//商城自动刷新时间
		corpsRole.setShopSysFlushTime(System.currentTimeMillis() + this.initTemplate.getShopFlushTime() * 1000);
		//生成商城物品
		this.flushShop(corpsRole, 1);

		corpsDao.saveCorpsRole(corpsRole);

		//保存军团id
		GameContext.getUserApp().saveCorpsId(roleId, id);

		result.setResult(Result.SUCCESS);
		result.setCorpsId(id);
		return result;
	}


	/** 修改允许加入设置 */
	@Override
	public Result corpsSetting(int roleId, int corpsId, RecruitType type){
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return Result.newFailure(Tips.CORPS_NO_EXIST);
		}
		//判断权限
		if(!this.hadCorpsModifyPower(corpsRole)){
			return Result.newFailure(Tips.CORPS_NO_POWER);
		}
		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		corps.setRecruitType(type.getNumber());
		corpsDao.saveCorpsInfo(corps);

		return Result.newSuccess();
	}


	@Override
	public CorpsResult joinCorps(int corpsId, int roleId){
		CorpsResult result = new CorpsResult();
		result.setResult(Result.FAILURE);

		//判断自己是否已加入军团
		final int hadCorpsId = GameContext.getUserApp().getCorpsId(roleId);

		//这里应该重新获取一下CorpsRole对象，无此对象才真正没有加入军团
		if(corpsDao.getCorpsRole(hadCorpsId, roleId) != null){
			result.setInfo(Tips.CORPS_HAD);
			return result;
		}

		//表示自动寻找可添加的军团添加
		if(corpsId == 0){
			final RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			return this.autoJoinCorps(role.getAreaId(), roleId);
		}

		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		if(corps == null){
			result.setInfo(Tips.CORPS_NO_EXIST);
			return result;
		}

		//如果该军团踢了我，24小时内不能重复申请
		Map<Integer, Long> driveRec = corps.getDriveRecords();
		if (driveRec.containsKey(roleId)
				&& DateUtil.getDaysInterval(System.currentTimeMillis(), driveRec.get(roleId)) <= 0) {
			result.setInfo(Tips.CORPS_APPLY_TRY_LATER);
			return result;
		}
		
		//拒绝加入
		int recruitType = corps.getRecruitType();
		if(recruitType == RecruitType.refuse_VALUE){
			result.setInfo(Tips.CORPS_REFUSE_JOIN);
			return result;
		}
		//验证军团人数
		if(this.corpsIsFull(corps)){
			result.setInfo(Tips.CORPS_IS_FULL);
			return result;
		}

		//需审批
		if(recruitType == RecruitType.manual_VALUE){
			corpsDao.addCorpsApply(corps.getId(), roleId);
			result.setInfo(Tips.CORPS_SUBMIT_APPLY);
			return result;
		}

		//放入军团
		this.putCorps(corps.getId(), roleId);

		result.setResult(Result.SUCCESS);
		result.setInfo(String.format(Tips.CORPS_IN, corps.getName()));
		result.setCorpsId(corps.getId());

		//回收处理军团踢人记录信息
		this.recoveryCorpsDriveRecMap(corps);
		
		return result;
	}
	
	
	
	/** 回收处理军团踢人记录信息 */
	private void recoveryCorpsDriveRecMap(CorpsInfo corps){
		Map<Integer, Long> newDriveRecMap = new HashMap<>();
		for(Entry<Integer, Long> entry : corps.getDriveRecords().entrySet()){
			if(DateUtil.getDaysInterval(System.currentTimeMillis(), entry.getValue()) <= 0){
				newDriveRecMap.put(entry.getKey(), entry.getValue());
			}
		}
		//保存踢人时间记录
		if(!Util.isEmpty(newDriveRecMap)){
			corps.setDriveRecords(newDriveRecMap);
			this.corpsDao.saveCorpsInfo(corps);
		}
	}
	
	
	

	/** 判断商城随机组是否存在 */
	@Override
	public boolean hadGoodsGroupExist(int groupId){
		return randomGoodsMap.containsKey(groupId);
	}


	/** 解散军团 */
	@Override
	public Result corpsDisband(CorpsInfo corps, CorpsRole corpsRole){
		if(corps == null || corpsRole == null){
			return Result.newFailure(Tips.CORPS_NO_EXIST);
		}
		//判断权限
		if(corpsRole.getJob() != CorpsLevel.s1_VALUE){
			return Result.newFailure(Tips.CORPS_NO_POWER);
		}
		//解散逻辑
		int corpsId = corps.getId();
		for(CorpsRole _corpsRole : corpsDao.getCorpsRoleList(corpsId)){
			if(corpsRole.getRoleId() == _corpsRole.getRoleId()){
				continue;
			}

			// 发邮件
			String mailContent = String.format(Tips.CORPS_NO_EXIST_MAIL, corps.getName());
			GameContext.getMailApp().sendMail(
					_corpsRole.getRoleId(),
					mailContent,
					mailContent,
					0, 0, 0, 0,
					Collections.<Integer, Integer>emptyMap());

			// 发通知
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(_corpsRole.getRoleId());
			if(connect != null){
				connect.sendMsg(STC_CORPS_DISBAND_MSG.newBuilder()
						.setResult(Result.SUCCESS)
						.setExit(false)
						.setInfo(String.format(Tips.CORPS_NO_EXIST_MAIL, corps.getName()))
						.build());
				GameContext.getMailApp().promit(connect);
			}
			//设置军团id
			GameContext.getUserApp().saveCorpsId(_corpsRole.getRoleId(), 0);
		}
		
		corpsDao.removeCorpsInfo(corps);
		corpsDao.removeAllCorpsRole(corpsId);

		GameContext.getUserApp().saveCorpsId(corpsRole.getRoleId(), 0);

		return Result.newSuccess();
	}



	@Override
	public CorpsShopResult shopList(int roleId){
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			return CorpsShopResult.newFailure(Tips.CORPS_NO_EXIST);
		}

		CorpsShopResult result = new CorpsShopResult();
		int shopLvl = corps.getShopLevel();

		//重置手动刷新次数
		if(!DateUtil.isSameDay(System.currentTimeMillis(), corpsRole.getShopFlushTime())){
			corpsRole.setShopFlushCount(0);
			corpsDao.saveCorpsRole(corpsRole);
		}

		//判断系统刷新
		int countdownTime = corpsRole.shopSysCountdownTime();
		if(countdownTime <= 0){
			logger.debug("军团商城系统刷新...");
			List<ShopItem> list = this.flushShop(corpsRole, shopLvl);
			//更新刷新时间
			corpsRole.setShopSysFlushTime(System.currentTimeMillis() + this.initTemplate.getShopFlushTime() * 1000);
			corpsDao.saveCorpsRole(corpsRole);

			result.setShopList(list);
			result.setFlushTime(corpsRole.shopSysCountdownTime());
			result.setFlushGold(this.flushShopGold(corpsRole.getShopFlushCount()));

			result.setResult(Result.SUCCESS);
			return result;
		}
		Map<Integer, ShopItem> shopMap = corpsRole.getShopMap();
		result.setShopList(shopMap.values());
		result.setFlushTime(corpsRole.shopSysCountdownTime());
		result.setFlushGold(this.flushShopGold(corpsRole.getShopFlushCount()));

		result.setResult(Result.SUCCESS);
		return result;
	}


	/** 手动刷新商城 */
	@Override
	public CorpsShopResult handFlushShop(int roleId){
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		if(corps == null || corpsRole == null){
			return CorpsShopResult.newFailure(Tips.CORPS_NO_EXIST);
		}
		int shopLvl = corps.getShopLevel();
		//消耗金币
		int handFlushCount = corpsRole.getShopFlushCount();
		int needGold = this.flushShopGold(handFlushCount);
		logger.debug("手动刷新商城 count = {}, needGold = {}", handFlushCount, needGold);
		boolean consumeGold = GameContext.getUserAttrApp().changeAttribute(roleId,
				AttrUnit.build(RoleAttr.gold, Operation.decrease, needGold), OutputType.corpsShopRefreshDec.type(), StringUtil.buildLogOrigin(corps.getName(), OutputType.corpsShopRefreshDec.getInfo()));
		if(!consumeGold){
			return CorpsShopResult.newFailure(Tips.NEED_GOLD);
		}

		//刷新商城
		this.flushShop(corpsRole, shopLvl);

		//设置刷新时间、次数
		corpsRole.setShopFlushCount(handFlushCount + 1);
		corpsRole.setShopFlushTime(System.currentTimeMillis());
		corpsDao.saveCorpsRole(corpsRole);

		CorpsShopResult result = new CorpsShopResult();
		Map<Integer, ShopItem> shopMap = corpsRole.getShopMap();
		result.setShopList(shopMap.values());
		result.setFlushTime(corpsRole.shopSysCountdownTime());
		result.setFlushGold(this.flushShopGold(corpsRole.getShopFlushCount()));

		result.setResult(Result.SUCCESS);
		return result;
	}


	/** 返回刷新商城所需金币数 */
	private int flushShopGold(int flushCount){
		switch(flushCount){
			case 0 : return initTemplate.getShopFlushGold1();
			case 1 : return initTemplate.getShopFlushGold2();
			case 2 : return initTemplate.getShopFlushGold3();
			case 3 : return initTemplate.getShopFlushGold4();
		}
		if(flushCount > 3){
			return initTemplate.getShopFlushGold4();
		}
		return 0;
	}




	/** 刷新军团商城 */
	private List<ShopItem> flushShop(CorpsRole corpsRole, int level){
		CorpsShopTemplate template = this.shopMap.get(level);
		if(template == null){
			logger.warn("没有找到对应商城配置 level = {}", level);
			return new ArrayList<>();
		}
		List<ShopItem> list = new ArrayList<>();
		Map<Integer, ShopItem> shopMap = corpsRole.getShopMap();
		shopMap.clear();
		logger.debug("清空军团商城物品,开始刷新...");
		List<Integer> groupIds = template.getRandomGroups();
		for(int groupId : groupIds){
			ShopPeshe shopPeshe = RandomUtil.getPeshe(this.randomGoodsMap.get(groupId));
			ShopItem item = new ShopItem();
			item.setId(shopPeshe.getId());
			item.setGoodsId(shopPeshe.getGoodsId());
			item.setNum(shopPeshe.getNum());
			item.setGold(shopPeshe.getGold());

			shopMap.put(item.getId(), item);
			list.add(item);
		}

		logger.debug("军团商城物品: {}", JSON.toJSONString(shopMap));
		corpsDao.saveCorpsRole(corpsRole);

		return list;
	}


	//自动添加军团
	private CorpsResult autoJoinCorps(int areaId, int roleId){
		CorpsResult result = new CorpsResult();
		result.setResult(Result.FAILURE);

		List<CorpsInfo> list = corpsDao.getAllCorpsInfo(areaId);
		if(Util.isEmpty(list)){
			result.setInfo(Tips.CORPS_NO_FIND_AUTO_JOIN);
			return result;
		}
		CorpsInfo applyCorps = null;
		for(CorpsInfo corps : list){
			if(corps.getRecruitType() != RecruitType.auto_VALUE){
				continue;
			}
			if(this.corpsIsFull(corps)){
				continue;
			}
			applyCorps = corps;
			break;
		}
		if(applyCorps == null){
			result.setInfo(Tips.CORPS_NO_FIND_AUTO_JOIN);
			return result;
		}

		//如果该军团踢了我，24小时内不能重复申请
		Map<Integer, Long> driveRec = applyCorps.getDriveRecords();
		if (driveRec.containsKey(roleId)
				&& DateUtil.getDaysInterval(System.currentTimeMillis(), driveRec.get(roleId)) <= 0) {
			result.setInfo(Tips.CORPS_APPLY_TRY_LATER);
			return result;
		}

		//放入军团
		this.putCorps(applyCorps.getId(), roleId);

		result.setResult(Result.SUCCESS);
		result.setCorpsId(applyCorps.getId());
		
		//回收处理踢人计时列表
		this.recoveryCorpsDriveRecMap(applyCorps);
		
		return result;
	}

	//判断军团人数是否已满
	@Override
	public boolean corpsIsFull(CorpsInfo corps){
		int currPlayerSize = corpsDao.getCorpsRoleSize(corps.getId());
		return currPlayerSize >= corps.getPlayerLimit();
	}


	//添加入军团
	private void putCorps(int corpsId, int roleId){
		//添加成员
		CorpsRole corpsRole = new CorpsRole();
		corpsRole.setCorpsId(corpsId);
		corpsRole.setJob(CorpsLevel.s5_VALUE);
		corpsRole.setRoleId(roleId);
		corpsRole.setEnterTime(System.currentTimeMillis());
		//商城自动刷新时间
		corpsRole.setShopSysFlushTime(System.currentTimeMillis() + this.initTemplate.getShopFlushTime() * 1000);
		//生成商城物品
		this.flushShop(corpsRole, 1);

		corpsDao.saveCorpsRole(corpsRole);
		//删除申请列表
		corpsDao.removeCorpsApply(corpsId, roleId);

		//保存军团ID
		GameContext.getUserApp().saveCorpsId(roleId, corpsId);
	}



	/** 获取科技捐献状态 */
	@Override
	public int corpsTechState(CorpsInfo corps, CorpsRole corpsRole, int techId){
		CorpsTechTemplate template = this.techMap.get(techId);
		if(template == null){
			throw new NullPointerException("军团科技配置不存在 techId = " + techId);
		}

		//判断是否研发中
		if(this.hadTechDev(corps, techId)){
			return TECH_DEV;
		}

		//判断军团等级
		if(this.hadTechLvlMoreCorpsLvl(corps, template)){
			return CORPS_LVL_LESS;
		}

		//判断科技经验已满
		if(this.hadTechExpMax(corps, template)){
			return EXP_MAX;
		}

		//判断冷却
		if(this.hadDonateCoolTime(corpsRole)){
			return COOL;
		}

		return CAN;
	}


	@Override
	public CorpsTechDonateResult techGoodsDonate(int corpsId, int roleId, int techId, Map<Integer, Integer> goodsMap){
		if(Util.isEmpty(goodsMap)){
			return CorpsTechDonateResult.newFailure(Tips.CORPS_NO_GOODS_DONATE);
		}
		final CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		if(corps == null){
			return CorpsTechDonateResult.newFailure(Tips.CORPS_NO_EXIST);
		}

		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);

		//验证科技状态
		int state = this.corpsTechState(corps, corpsRole, techId);
		if(state != CAN){
			return CorpsTechDonateResult.newFailure(this.getStateInfo(state));
		}

		//判断物品是否可捐献
		int donateValue = 0;
		float coolTime = 0;
		int contributeValue = 0;
		int corpsGold = 0;
		for(Entry<Integer, Integer> entry : goodsMap.entrySet()){
			int goodsId = entry.getKey();
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
			if(template.getDonate_i() <= 0){
				return CorpsTechDonateResult.newFailure(Tips.CORPS_NOT_ALLOWED_DONATE);
			}
			coolTime += template.getDonate_i() * initTemplate.getDonateTimeValue() * entry.getValue();
			donateValue += template.getDonate_i() * entry.getValue();
			contributeValue += template.getContribute_i() * entry.getValue();
			corpsGold += template.getGangGold_i() * entry.getValue();
		}
		logger.debug("科技物品捐献 techId = {}, donateValue = {}, contributeValue = {}, corpsGold = {}",
				techId, donateValue, contributeValue, corpsGold);
		//判断物品是否足够
		if(!GameContext.getGoodsApp().removeGoods(roleId, goodsMap, StringUtil.buildLogOrigin(corps.getName(), "techId:" + techId, OutputType.corpsDonateGoodsDec.getInfo()))){
			return CorpsTechDonateResult.newFailure(Tips.CORPS_LESS_GOODS_DONATE);
		}

		return this.techDonate(corps, corpsRole, techId, donateValue,
				(int)coolTime, contributeValue, corpsGold, StringUtil.buildLogOrigin(corps.getName(), "techId:" + techId, OutputType.corpsDonateGoodsInc.getInfo()));
	}


	//返回状态错误信息
	private String getStateInfo(int state) {
		switch (state) {
		case COOL:
			return Tips.CORPS_TECH_DONATE_CD_COOL;
		case CORPS_LVL_LESS:
			return Tips.CORPS_LVL_LESS;
		case EXP_MAX:
			return Tips.CORPS_EXP_OVERFLOW_DONATE;
		case TECH_DEV:
			return Tips.CORPS_TECH_DEV;
		}
		return null;
	}


	private CorpsTechDonateResult techDonate(CorpsInfo corps, CorpsRole corpsRole, int techId,
			int donateValue, int coolTime, int contributeValue, int corpsGold, String origin){
		final int roleId = corpsRole.getRoleId();
		long techDonateTime = corpsRole.getTechDonateTime();
		if(techDonateTime < System.currentTimeMillis()){
			techDonateTime = System.currentTimeMillis();
		}
		long newDonateTime = techDonateTime + coolTime * 1000;
		logger.debug("科技捐献冷却时间 newDonateTime = {} 计算出的冷却时间 coolTime = {}", (newDonateTime - System.currentTimeMillis())/1000, coolTime);
		//判断是否大于冷却时间 + 最大允许超时时间
		if((newDonateTime - System.currentTimeMillis()) >= (initTemplate.getCoolTime() + initTemplate.getDonateOverstepTime()) * 1000){
			return CorpsTechDonateResult.newFailure(Tips.CORPS_TIME_OVERFLOW_DONATE);
		}
		TechnologyInfo techInfo = corps.getTechInfoMap().get(techId);
		int exp = techInfo.getCurExp() + donateValue;
		CorpsTechTemplate template = this.techMap.get(techId);
		if(exp > template.getTechMaxExp()){
			exp = template.getTechMaxExp();
		}
		//设置经验
		techInfo.setCurExp(exp);
		corpsDao.saveCorpsInfo(corps);

		logger.debug("科技捐献结果: {}", JSON.toJSONString(techInfo));

		//设置捐献值
		corpsRole.setContribution(corpsRole.getContribution() + contributeValue);
		if(DateUtil.isSameWeek(System.currentTimeMillis(), techDonateTime)){
			corpsRole.setWeekContribution(corpsRole.getWeekContribution() + contributeValue);
		} else {
			corpsRole.setWeekContribution(contributeValue);
		}

		//设置捐献冷却时间
		corpsRole.setTechDonateTime(newDonateTime);
		corpsDao.saveCorpsRole(corpsRole);
		logger.debug("CorpsRole: {}", JSON.toJSONString(corpsRole));
		//添加军团币
		int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.corps_gold);
		GameContext.getGoodsApp().addGoods(roleId, goodsId, corpsGold, origin);

		CorpsTechDonateResult result = new CorpsTechDonateResult();
		result.setResult(Result.SUCCESS);
		result.setState(this.corpsTechState(corps, corpsRole, techId));
		result.setCoolTime(corpsRole.donateCountdownTime());

		return result;
	}


	@Override
	public CorpsTechDonateResult techGoldDonate(int corpsId, int roleId, int techId){
		final CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		if(corps == null){
			return CorpsTechDonateResult.newFailure(Tips.CORPS_NO_EXIST);
		}

		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);

		//验证科技状态
		int state = this.corpsTechState(corps, corpsRole, techId);
		if(state != CAN){
			return CorpsTechDonateResult.newFailure(this.getStateInfo(state));
		}

		//计算所需的金币数
		TechnologyInfo techInfo = corps.getTechInfoMap().get(techId);
		CorpsTechTemplate template = this.techMap.get(techId);
		int exp = template.getTechMaxExp() - techInfo.getCurExp();
		if(exp < 0){
			exp = 0;
		}
		int gold = (int)(exp * initTemplate.getExpGoldValue());
		boolean changeGold = GameContext.getUserAttrApp().changeAttribute(roleId,
				AttrUnit.build(RoleAttr.gold, Operation.decrease, gold), true, OutputType.corpsDonateGoldDec.type(), StringUtil.buildLogOrigin(corps.getName(), "techId:" + techId, OutputType.corpsDonateGoldDec.getInfo()));
		if(!changeGold){
			return CorpsTechDonateResult.newFailure(Tips.NEED_GOLD);
		}
		logger.debug("金币捐献, 所需金币 = {} 剩余经验值 = {}", gold, exp);
		//换算贡献值
		int donateValue = exp;
		int contributeValue = (int)(gold * initTemplate.getGoldContrValue());
		int corpsGold = (int)(gold * initTemplate.getGoldCorpsMoneyValue());
		int coolTime = (int)(donateValue * initTemplate.getDonateTimeValue());

		return this.techDonate(corps, corpsRole, techId, donateValue,
				coolTime, contributeValue, corpsGold, StringUtil.buildLogOrigin(corps.getName(), "techId:" + techId, OutputType.corpsDonateGoldInc.getInfo()));
	}


	/**
	 * 科技研发
	 */
	@Override
	public CorpsTechDevResult techDev(int corpsId, int roleId, int techId){
		CorpsInfo corps = corpsDao.getCorpsInfo(corpsId);
		if(corps == null){
			return CorpsTechDevResult.newFailure(Tips.CORPS_NO_HAD);
		}
		//判断经验值是否已满
		Map<Integer, TechnologyInfo> techMap = corps.getTechInfoMap();
		TechnologyInfo techInfo = techMap.get(techId);
		if(techInfo == null){
			return CorpsTechDevResult.newFailure(Tips.CORPS_TECH_NO_EXIST);
		}
		CorpsTechTemplate template = this.techMap.get(techId);
		if(template.getTechMaxExp() > techInfo.getCurExp()){
			return CorpsTechDevResult.newFailure(Tips.CORPS_TECH_lESS_EXP);
		}

		//判断用户权限
		CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
		final int job = corpsRole.getJob();
		if(job != CorpsLevel.s1_VALUE && job != CorpsLevel.s2_VALUE){
			return CorpsTechDevResult.newFailure(Tips.CORPS_NO_POWER);
		}

		final int nextTechId = template.getNextTechId();
		if(nextTechId <= 0){
			return CorpsTechDevResult.newFailure(Tips.CORPS_TECH_MAX_LVL);
		}
		//升级处理
		CorpsTechTemplate nextTemplate = this.techMap.get(nextTechId);
		techMap.remove(techId);
		techInfo.setCurExp(0);
		techInfo.setDevOverTime(System.currentTimeMillis() + nextTemplate.getDevMaxTime() * 1000);
		techInfo.setTechId(nextTemplate.getTechId());
		techMap.put(nextTechId, techInfo);

		//设置军团等级、商城等级、军团最大人数
		corps.setLevel(Math.max(corps.getLevel(), nextTemplate.getCorpLevel()));
		corps.setShopLevel(Math.max(corps.getShopLevel(), nextTemplate.getLevelShop()));
		corps.setPlayerLimit(Math.max(corps.getPlayerLimit(), nextTemplate.getPlayerCount()));

		corpsDao.saveCorpsInfo(corps);

		logger.debug("下一级科技ID: "+ nextTechId + " teches: " + JSON.toJSONString(corps.getTechInfoMap()));

		CorpsTechDevResult result = new CorpsTechDevResult();
		result.setResult(Result.SUCCESS);
		result.setDevTime(techInfo.devOverplusTime());

		return result;
	}




	/** 判断捐献冷却时间 处于冷却状态返回true */
	@Override
	public boolean hadDonateCoolTime(CorpsRole corpsRole){
		//处于冷却状态
		if(corpsRole.isDonateCool()){
			//是否走完冷却时间
			if(corpsRole.donateCountdownTime() <= 0){
				corpsRole.setDonateCool(false);
				corpsDao.saveCorpsRole(corpsRole);
				return false;
			}
			return true;
		}
		//进入冷却状态
		if(corpsRole.donateCountdownTime() >= this.initTemplate.getCoolTime()){
			corpsRole.setDonateCool(true);
			corpsDao.saveCorpsRole(corpsRole);
			return true;
		}

		return false;
	}


	/** 判断科技经验值已达最大返回true */
	private boolean hadTechExpMax(CorpsInfo corps, CorpsTechTemplate template){
		Map<Integer, TechnologyInfo> techMap = corps.getTechInfoMap();
		int techId = template.getTechId();
		TechnologyInfo techInfo = techMap.get(techId);
		if(techInfo == null){
			throw new NullPointerException("科技不存在 teachId = " + techId);
		}
		return techInfo.getCurExp() >= template.getTechMaxExp();
	}



	/** 判断科技等级是否超越军团等级 大于等于返回true*/
	private boolean hadTechLvlMoreCorpsLvl(CorpsInfo corps, CorpsTechTemplate template){
		//主科技不受军团等级限制
		if(template.getMain() == 1){
			return false;
		}
		return template.getLevel() >= corps.getLevel();
	}



	/** 判断科技是否处于研发中 研发中返回true */
	private boolean hadTechDev(CorpsInfo corps, int techId){
		Map<Integer, TechnologyInfo> techMap = corps.getTechInfoMap();
		TechnologyInfo techInfo = techMap.get(techId);
		if(techInfo == null){
			throw new NullPointerException("科技不存在 teachId = " + techId);
		}
		return techInfo.devOverplusTime() > 0;
	}


	//加载军团商城
	private void loadCorpsShopTemplate(){
		String sourceFile = XlsSheetType.corpsShopTemplate.getXlsFileName();
		String sheetName = XlsSheetType.corpsShopTemplate.getSheetName();
		try{
			List<CorpsShopTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CorpsShopTemplate.class);
			for(CorpsShopTemplate template : list){
				template.init();
				shopMap.put(template.getLevel(), template);
			}
		}catch(Exception e){
			LogCore.startup.error(String.format("加载 {} sheet={} 异常", sourceFile, sheetName), e);
		}
	}


	//加载军团商城物品
	private void loadCorpsGoodsTemplate(){
		String sourceFile = XlsSheetType.corpsGoodsTemplate.getXlsFileName();
		String sheetName = XlsSheetType.corpsGoodsTemplate.getSheetName();
		try{
			List<CorpsGoodsTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CorpsGoodsTemplate.class);
			for(CorpsGoodsTemplate template : list){
				//验证物品是否存在
				int goodsId = template.getGoodsId();
				if(GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId) == null){
					LogCore.startup.error("{},{} 物品不存在, goodsId = {}", sourceFile, sheetName, goodsId);
					continue;
				}
				int group = template.getGroup();
				List<ShopPeshe> goodsPeshes = randomGoodsMap.get(group);
				if(goodsPeshes == null){
					goodsPeshes = new ArrayList<>();
					randomGoodsMap.put(group, goodsPeshes);
				}
				ShopPeshe peshe = new ShopPeshe();
				peshe.setId(template.getId());
				peshe.setGoodsId(goodsId);
				peshe.setNum(template.getCount());
				peshe.setGon(template.getWeight());
				peshe.setGold(template.getCorpsGold());
				goodsPeshes.add(peshe);
			}
		}catch(Exception e){
			LogCore.startup.error(String.format("加载 {} sheet={} 异常", sourceFile, sheetName), e);
		}
	}

	//加载军团科技配置
	private void loadCorpsTechTemplate(){
		String sourceFile = XlsSheetType.corpsTechTemplate.getXlsFileName();
		String sheetName = XlsSheetType.corpsTechTemplate.getSheetName();
		try{
			techMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, CorpsTechTemplate.class);
		}catch(Exception ex){
			LogCore.startup.error(String.format("加载 {} sheet={} 异常", sourceFile, sheetName), ex);
		}
	}

	//加载军团配置
	private void loadCorpsTemplate(){
		String sourceFile = XlsSheetType.corpsTemplate.getXlsFileName();
		String sheetName = XlsSheetType.corpsTemplate.getSheetName();
		try{
			initTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, CorpsTemplate.class).get(0);
		}catch(Exception ex){
			LogCore.startup.error(String.format("加载 {} sheet={} 异常", sourceFile, sheetName), ex);
		}
	}

	public void setCorpsDao(CorpsDao corpsDao) {
		this.corpsDao = corpsDao;
	}


	@Override
	public CorpsInfo getCorpsInfo(int id) {
		return corpsDao.getCorpsInfo(id);
	}


	@Override
	public CorpsInfo getCorpsInfo(int areaId, String name) {
		return corpsDao.getCorpsInfo(areaId, name);
	}


	@Override
	public CorpsRole getCorpsRole(int corpsId, int roleId) {
		return corpsDao.getCorpsRole(corpsId, roleId);
	}


	@Override
	public void saveCorpsRole(CorpsRole corpsRole) {
		corpsDao.saveCorpsRole(corpsRole);
	}


	@Override
	public void removeCorpsRole(int corpsId, int roleId) {
		corpsDao.removeCorpsRole(corpsId, roleId);
	}


	@Override
	public void removeApplyRole(int corpsId, int appleRoleId) {
		corpsDao.removeCorpsApply(corpsId, appleRoleId);
	}


	@Override
	public void lock(int key) {
		corpsDao.lock(key);
	}


	@Override
	public void unlock(int key) {
		corpsDao.unlock(key);
	}


	@Override
	public int getCorpsRoleSize(int corpsId) {
		return corpsDao.getCorpsRoleSize(corpsId);
	}


	@Override
	public List<CorpsRole> getCorpsRoleList(int corpsId) {
		return corpsDao.getCorpsRoleList(corpsId);
	}


	@Override
	public List<CorpsInfo> getCorpsInfoList(int areaId, int page) {
		return corpsDao.getCorpsInfoList(areaId, page);
	}


	@Override
	public boolean hasExistCorpsApply(int corpsId, int roleId) {
		return corpsDao.hasExistCorpsApply(corpsId, roleId);
	}


	@Override
	public int getCorpLevel(int roleId) {
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if (corpsId > 0) {
			CorpsRole corpsRole = corpsDao.getCorpsRole(corpsId, roleId);
			if (corpsRole != null) {
				CorpsInfo corpsInfo = corpsDao.getCorpsInfo(corpsRole.getCorpsId());
				return corpsInfo.getLevel();
			}
		}
		return 0;
	}
}
