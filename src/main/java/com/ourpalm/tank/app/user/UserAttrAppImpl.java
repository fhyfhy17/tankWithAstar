package com.ourpalm.tank.app.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.UserAttrDao;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttrItem;
import com.ourpalm.tank.message.ROLE_MSG.STC_ROLE_ATTR_SYNC_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_UP_LEVEL_MSG;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.template.UserLevelTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

public class UserAttrAppImpl implements UserAttrApp {
	
	private final static Logger logger = LogCore.runtime;
	private UserAttrDao userAttrDao;
	
	@Override
	public boolean changeAttribute(Integer roleId, AttrUnit unit, OutputType origin){
		return this.changeAttribute(roleId, unit, origin.type(), origin.getInfo());
	}
	
	@Override
	public boolean changeAttribute(Integer roleId, List<AttrUnit> list, OutputType origin){
		return this.changeAttribute(roleId, list, origin.type(), origin.getInfo());
	}
	
	@Override
	public boolean changeAttribute(Integer roleId, AttrUnit unit, int originId, String origin){
		return this.changeAttribute(roleId, unit, true, originId, origin);
	}
	
	@Override
	public boolean changeAttribute(Integer roleId, OutputType origin, AttrUnit... unit){
		return changeAttribute(roleId, origin.type(), origin.getInfo(), unit);
	}
	
	@Override
	public boolean changeAttribute(Integer roleId, int originId, String origin, AttrUnit... unit){
		List<AttrUnit> list = new ArrayList<>();
		for(AttrUnit au : unit){
			list.add(au);
		}
		return this.changeAttribute(roleId, list, originId, origin);
	}

	@Override
	public boolean changeAttribute(Integer roleId, AttrUnit unit, boolean check, int originId, String origin) {
		return this.changeAttribute(roleId, Collections.singletonList(unit), check, originId, origin);
	}

	@Override
	public boolean changeAttribute(Integer roleId, List<AttrUnit> list, int originId, String origin) {
		return this.changeAttribute(roleId, list, true, originId, origin);
	}

	@Override
	public boolean changeAttribute(Integer roleId, List<AttrUnit> list, boolean check, int originId, String origin) {
		if (Util.isEmpty(list)) {
			return true;
		}
		UserAttr userAttr = userAttrDao.getUserAttr(roleId);
		if (userAttr == null || !checkValue(userAttr, list, check)) {
			return false;
		}
		
		// 修改属性
		for (AttrUnit unit : list) {
			if(unit.getOperation() != Operation.equal
					&& unit.getValue() <= 0){
				continue;
			}
			if (RoleAttr.exp.equals(unit.getType())) {
				this.addExp(roleId, userAttr, unit.getValue(), origin);
				continue;
			}
			
			int source = userAttr.get(unit.getType());
			int result = caculate(roleId, source, unit, origin);
			userAttr.set(unit.getType(), result);
			
			if( GameContext.isReportNeed() && RoleAttr.diamonds.equals(unit.getType()) && unit.getOperation() == Operation.decrease ){
				//发送统计日志
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				// 上报单位为Q分（100Q分 = 10Q点 = 1Q币）
				GameContext.getGameLogApp().sendConsumeLog(role.getAreaId(), role.getPfId(), role.getUid(), role.getClientIp(), unit.getValue() * 10);	
			}
			
			// 目前监控货币类型：钻石，金币，局部经验
			if (RoleAttr.diamonds.equals(unit.getType()) || RoleAttr.gold.equals(unit.getType()) || 
					RoleAttr.tankExp.equals(unit.getType()) || RoleAttr.honor.equals(unit.getType())) {
				GameContext.getGameDBLogApp().getMoneyLog().changeValue( roleId, unit, originId, origin, unit.getValue(), source, userAttr.get(unit.getType()) );
			}
		}

		// 属性同步
		this.sync(roleId, userAttr, origin);
		userAttrDao.saveUserAttr(userAttr);
		return true;
	}

	
	private boolean checkValue(UserAttr userAttr, List<AttrUnit> list, boolean check) {
		for (AttrUnit unit : list) {
			if (unit.getValue() < 0) {
				logger.error("修改属性的值，不能是负数", new IllegalArgumentException());
				return false;
			}
			if (RoleAttr.exp.equals(unit.getType()) && !Operation.add.equals(unit.getOperation())) {
				logger.error("经验值只有增加操作", new IllegalArgumentException());
				return false;
			}
			//检查降低的属性是否足够
			if (check && Operation.decrease.equals(unit.getOperation()) && userAttr.get(unit.getType()) < unit.getValue()) {
				return false;
			}
		}
		return true;
	}

	private int caculate(int roleId, int source, AttrUnit unit, String origin) {
		if (unit.getValue() <= 0) {
			return source;
		}
		int result = source;
		switch (unit.getOperation()) {
			case add		: result += unit.getValue(); break;
			case decrease	: result -= unit.getValue(); break;
			case equal		: result = unit.getValue();  break;
		}
		result =  Math.max(result, 0);
		
		//发送日志
		//GameContext.getGameLogApp().sendItemChangeLog(roleId, unit.getOperation(), 
		//		String.valueOf(unit.getType().getNumber()), unit.getType().name(), unit.getValue(), origin);
		
		
		return result;
	}

	private void addExp(int roleId, UserAttr userAttr, Integer value, String origin){
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int currLevel = role.getLevel();
		UserLevelTemplate currLevelTemplate = GameContext.getUserApp().getLevelTemplate(currLevel);
		UserLevelTemplate nextLevelTemplate = GameContext.getUserApp().getLevelTemplate(currLevel + 1);
		
		int source = userAttr.getExp();
		int rstExp = source + value;
		userAttr.set(RoleAttr.exp, rstExp);
		
		//发送统计日志
		GameContext.getGameLogApp().sendAttrChangeLog(roleId, "exp", rstExp+"", value+"");
		
		boolean changeLvl = false;
		while(userAttr.get(RoleAttr.exp) >= currLevelTemplate.getExp() && nextLevelTemplate != null){
			role.setLevel(nextLevelTemplate.getLevel());
			this.levelUpNotifyMsg(roleId, nextLevelTemplate.getLevel());
			
			currLevelTemplate = nextLevelTemplate;
			nextLevelTemplate = GameContext.getUserApp().getLevelTemplate(currLevelTemplate.getLevel() + 1);
			
			changeLvl = true;
		}
		
		if(changeLvl){
			GameContext.getUserApp().saveRoleAccount(role);
			
			STC_ROLE_ATTR_SYNC_MSG.Builder builder = STC_ROLE_ATTR_SYNC_MSG.newBuilder();
			builder.setSource(origin);
			RoleAttrItem.Builder item = RoleAttrItem.newBuilder();
			item.setType(RoleAttr.level);
			item.setValue(role.getLevel());
			builder.addAttrs(item);
			
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
			if(connect != null){
				connect.sendMsg(builder.build());
			}
			GameContext.getGameDBLogApp().getRoleLog().roleUpLevel(roleId, currLevel, role.getLevel() );
		}
	}
	
	private void levelUpNotifyMsg(int roleId, int currLevel){
		//触发任务
		GameContext.getQuestTriggerApp().roleUpLevel(roleId, currLevel);
		
		//发送统计日志
		//GameContext.getGameLogApp().sendAttrChangeLog(roleId, "level", String.valueOf(currLevel), "1");
		
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if(connect == null){
			return ;
		}
		STC_UP_LEVEL_MSG msg = STC_UP_LEVEL_MSG.newBuilder()
				.setCurLevel(currLevel)
				.build();
		connect.sendMsg(msg);
	}

	/** 属性同步 */
	private void sync(int roleId, UserAttr userAttr, String origin) {
		Collection<RoleAttr> changeAttrs = userAttr.changeAttrs();
		if (Util.isEmpty(changeAttrs)) {
			return;
		}
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect == null) {
			return;
		}

		STC_ROLE_ATTR_SYNC_MSG.Builder builder = STC_ROLE_ATTR_SYNC_MSG.newBuilder();
		builder.setSource(origin);
		for (RoleAttr attrType: changeAttrs) {
			RoleAttrItem.Builder item = RoleAttrItem.newBuilder();
			item.setType(attrType);
			item.setValue(userAttr.get(attrType));
			builder.addAttrs(item);
		}
		connect.sendMsg(builder.build());
		changeAttrs.clear();
	}
	
	/**
	 * 当银币不够减时，通过金币与银币转换比，所金币转换成银币，再减
	 * @param roleId
	 * @param needIron	需要的银币
	 * @return	是否成功  失败时表明金币不足
	 */
	public boolean consumIron(int roleId, int needIron, int originId, String origin) {
		UserAttr attr = userAttrDao.getUserAttr(roleId);
		int iron = attr.get(RoleAttr.iron);
		if(iron < needIron){
			double rate = GameContext.getUserApp().getUserInitTemplate().getGoldToIronRate();
			int needRateIron = needIron - iron;
			int toGold = (int)Math.ceil(needRateIron / rate);	//需要转换为多少金币
			int gold = attr.get(RoleAttr.gold);
			if(toGold > 0 && toGold < gold) {
				changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, toGold), originId, origin);
				
				int toIron = toGold * (int)rate;		//转换后，玩家所有银币 
				int addIron = toIron - needRateIron;
				
				changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.decrease, iron), originId, origin);
				if(addIron > 0) {
					changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, addIron), originId, origin);
				}
				return true;
			} 
			return false;
		}
		//够减
		return changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.decrease, needIron), originId, origin);
	}
	
	public boolean consumIron(int roleId, int needIron, OutputType origin) {
		return this.consumIron(roleId, needIron, origin.type(), origin.getInfo());
	}
	
	@Override
	public UserAttr getUserAttr(int roleId){
		return this.userAttrDao.getUserAttr(roleId);
	}

	@Override
	public int get(int roleId, RoleAttr attr) {
		UserAttr userAttr = this.userAttrDao.getUserAttr(roleId);
		return userAttr.get(attr);
	}

	public void setUserAttrDao(UserAttrDao userAttrDao) {
		this.userAttrDao = userAttrDao;
	}

	@Override
	public Result goldTranslateToIron(int roleId, int gold, int rate) {
		int iron = gold * rate;
		if(iron <= 0){
			return Result.newFailure("超出对换上限");
		}
		
		boolean result = changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, gold), OutputType.goldTranslateToIronDec );
		if(!result) {
			return Result.newFailure("金币不足");
		}
		
		changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, iron), OutputType.goldTranslateToIronInc);
		
		//触发任务
		GameContext.getQuestTriggerApp().totalCashingIron(roleId, iron);
		
		return Result.newSuccess();
	}

	@Override
	public Result diamondTranslateToGold(int roleId, int diamonds) {
		UserInitTemplate template = GameContext.getUserApp().getUserInitTemplate();	
		int rate = template.getDiamondToGoldRate();
		
		int gold = diamonds * rate;
		if(gold <= 0) {
			return Result.newFailure("超出对换上限");
		}

		boolean result = changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.decrease, diamonds), OutputType.diamondTranslateToGoldDec);
		if(!result) {
			return Result.newFailure("钻石不足");
		}

		changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.add, gold), OutputType.diamondTranslateToGoldInc);

		return Result.newSuccess();
	}

}
