package com.ourpalm.tank.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_CREATE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.LOGIN_STATE;
import com.ourpalm.tank.message.ROLE_MSG.STC_LOGIN_MSG;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.LoginUserInfo;
import com.ourpalm.tank.vo.result.LoginResult;

/**
 * 创建账号
 */
@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
	id = ROLE_MSG.CMD_ID.CTS_CREATE_VALUE
)
public class RoleCreateAction extends AbstractLoginAction implements Action<CTS_CREATE_MSG>{

	//暂时缓存正在申请的角色名，提高创建性能
	//这里只起到同一时间段申请同一个角色名时，起到一个缓冲的作用
	private final static Set<String> roleNameCacheSet = new HashSet<String>();
	private Logger logger = LogCore.runtime;
	private static Map<String, String> PFMAP = null;
	
	private static void initFPMAP(){
		PFMAP = new HashMap<>();
		PFMAP.put("1", "qzone");
		PFMAP.put("2", "pengyou");
		PFMAP.put("3", "tapp");
		PFMAP.put("4", "qplus");
		PFMAP.put("8", "qzone");
		PFMAP.put("10", "qqgame");
		PFMAP.put("11", "3366");
		PFMAP.put("101", "kapp");
	}
	 
	private static String getPfName( String pf ){
		if( PFMAP == null ){
			initFPMAP();
		}
		if( !PFMAP.containsKey(pf) ){
			return "unknow";
		}
		return PFMAP.get(pf);
	}
	
	@Override
	public MessageLite execute(ActionContext context, CTS_CREATE_MSG reqMsg) {
		STC_LOGIN_MSG.Builder builder = STC_LOGIN_MSG.newBuilder();
		final int areaId = reqMsg.getAreaId();
		final String roleName = reqMsg.getRoleName();
		final String uid = reqMsg.getId();
		final int country = reqMsg.getCountry();
		final String serviceId = reqMsg.getServiceId();
		final String serviceCode = reqMsg.getServiceCode();
		final String openKey = reqMsg.getKey();
		final String pfId = reqMsg.getPfId();
		String clientIp = reqMsg.getClientIp();
		String pf = getPfName(pfId);
		
		logger.info(" [PF_TEST] login create cmd areaId {} uid {} openKey {} clientIp {} pfId {} ", areaId, uid, openKey, clientIp, pfId );
		
		//非法字符
		//验证是否存在非法字符
		Set<String> sensitives = GameContext.getMutilDfaApp().matchSensitiveWord(roleName);
		if(!Util.isEmpty(sensitives)){
			builder.setState(LOGIN_STATE.ERROR_CREATE);
			builder.setInfo(Tips.ROLE_NAME_ILLEGAL);
			return builder.build();
		}

		//是否重复
		if(roleNameCacheSet.contains(roleName)){
			builder.setState(LOGIN_STATE.ERROR_CREATE);
			builder.setInfo(Tips.ROLE_NAME_HAD_EXIST);
			return builder.build();
		}

		//先把申请的角色名放入缓存区
		roleNameCacheSet.add(roleName);

		GameContext.getLock().lock(Action.RENAME_KEY);
		try{
			//验证角色名是否重复
			if(GameContext.getUserApp().roleNameHadExist(roleName)){
				builder.setState(LOGIN_STATE.ERROR_CREATE);
				builder.setInfo(Tips.ROLE_NAME_HAD_EXIST);

				if(logger.isDebugEnabled()){
					logger.debug("创建角色 roleName:{}重复", roleName);
				}
				return builder.build();
			}
			//删除缓存区角色名
			roleNameCacheSet.remove(roleName);
			
		}finally{
			GameContext.getLock().unlock(Action.RENAME_KEY);
		}
		
		GameContext.getLock().lock(uid);
		try{
			//创建账号
			GameContext.getUserApp().createUser(areaId, uid, roleName, country);
			
			RoleAccount role = GameContext.getUserApp().getRoleAccount(areaId, uid);
			role.setServiceId(serviceId);
			role.setServiceCode(serviceCode);
			role.setClientIp(clientIp);
			role.setOpenKey(openKey);
			role.setPfId(pfId);
			role.setPf(pf);
			GameContext.getUserApp().saveRoleAccount(role);
	
			int roleId = role.getRoleId();
			builder.setName(role.getRoleName());
			builder.setRoleId(roleId);
			builder.setTeachRwd(role.getTeachGold());
			builder.setCorpsId(0);
			builder.setState(LOGIN_STATE.CREATE_SUCCESS);
			builder.setTeamId("");
			builder.setActiveFlag(GameContext.getActiveShow());
			builder.setVipLevel(role.getVipLevel());
			builder.setLevel(role.getLevel());
	
			UserAttr userAttr = GameContext.getUserAttrApp().getUserAttr(roleId);
			builder.setIron(userAttr.getIron());
			builder.setDiamonds(userAttr.getDiamonds());
			builder.setExp(userAttr.getExp());
			builder.setGold(userAttr.getGold());
			builder.setHonor(userAttr.getHonor());
			builder.setRoleTankExp(userAttr.getTankExp());
	
			builder.setMainTankId(role.getMainTankId());
	
			UserInitTemplate initTemplate = GameContext.getUserApp().getUserInitTemplate();
			builder.setGoldToIronRate(initTemplate.getGoldToIronRate());
			builder.setToTankExpGoldRate(initTemplate.getToTankExpGoldRate());
			builder.setToTankExpIronRate(initTemplate.getToTankExpIronRate());
			builder.setTeachId(role.getTeachId());
			builder.setDiamondToGoldRate(initTemplate.getDiamondToGoldRate());
			builder.setTitleId(role.getCurrTitleId());

			
			if(logger.isDebugEnabled()){
				logger.debug("创建角色 {}", JSON.toJSONString(role));
			}
	
			//注册用户连接
			RoleConnect connect = new RoleConnect();
			connect.setIoId(context.getIoId());
			connect.setGateName(context.getFrom());
			connect.setNodeName(GameContext.getLocalNodeName());
			connect.setRoleId(roleId);
			connect.setAreaId(areaId);
			GameContext.getOnlineCenter().register(connect);
	
			if(GameContext.isCheckLogin()){
				LoginResult checkResult = this.loginCheck(uid, openKey, pf);
				if( checkResult.getState() == LOGIN_STATE.SUCCESS ) {
					LoginUserInfo userInfo = checkResult.getUserInfo();
					
					String returnJson = userInfo.getReturnJson();
					role.setReturnJson(returnJson);
					role.setServiceId(serviceId);
					GameContext.getUserApp().saveRoleAccount(role);
	
					// 保存 平台 黄钻和蓝钻信息
					GameContext.getUserApp().savePfUserInfo(roleId, userInfo.getPfUserInfo());
					GameContext.getUserApp().savePfYellowUserInfo(roleId, userInfo.getPfYellowUserInfo());
					
					builder.setPfUserInfo(JSON.toJSONString(userInfo.getPfUserInfo()));
				}
			}
			
			if(GameContext.isReportNeed()){
				//发送统计日志
				GameContext.getGameLogApp().sendRegisterLog(areaId, pfId, uid, clientIp);
				GameContext.getGameLogApp().sendLoginLog(areaId, pfId, uid, clientIp, role.getLevel());
			}
			
			GameContext.getGameDBLogApp().getUserLog().userRegister(uid, roleId, areaId, roleName, pf, System.currentTimeMillis()/1000, clientIp);
			GameContext.getGameDBLogApp().getUserLog().userLogin(uid, roleId, areaId, role.getRoleName(), role.getLevel(), pf, clientIp);
			
			GameContext.getOnlineCenter().sysLogin(connect, true);
			
			return builder.build();
		}finally{
			GameContext.getLock().unlock(uid);
		}
	}
}
		
