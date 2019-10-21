package com.ourpalm.tank.action;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_LOGIN_MSG;
import com.ourpalm.tank.message.ROLE_MSG.LOGIN_STATE;
import com.ourpalm.tank.message.ROLE_MSG.STC_LOGIN_MSG;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.vo.LoginUserInfo;
import com.ourpalm.tank.vo.result.LoginResult;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, 
	id = ROLE_MSG.CMD_ID.CTS_LOGIN_VALUE
)
public class RoleLoginAction extends AbstractLoginAction implements Action<CTS_LOGIN_MSG> {
	
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
	public MessageLite execute(ActionContext context, CTS_LOGIN_MSG reqMsg) {
		int areaId = reqMsg.getAreaId();
		String userId = reqMsg.getId();
		String version = reqMsg.getVersion().trim();
		String serviceCode = reqMsg.getServiceCode();
		String openKey = reqMsg.getKey();
		String clientIp = reqMsg.getClientIp();
		String pfId = reqMsg.getPfId();
		String pf = getPfName(pfId);
		
		logger.info(" [PF_TEST] login cmd areaId {} uid {} version {} openKey {} clientIp {} pfId {} ", areaId, userId, version, openKey, clientIp, pfId );
		
		/* pfId :
			(1,'QQ空间')
			(2,'腾讯朋友')
			(3,'腾讯微博')
			(4,'腾讯QPlus')
			(8,'手机空间')
			(9,'手机朋友')
			(10,'QQ游戏')
			(11,'3366')
			(12,'游戏官网')
			(13,'iOS')
			(14,'Android')
			(15,'漫游')
			(16,'游戏人生')
			(17,'腾讯游戏联盟')
			(23,'腾讯游戏盒子')
			(101,'开心网')
		 */
		
		if(logger.isDebugEnabled()){
			logger.debug("收到登录请求, areaId = {}, version = {}, tokenId = {}", areaId, version, userId);
		}
		
		//验证客户端版本号
		int currVersion = Integer.parseInt(GameContext.getClientVersion().replace(".", ""));
		int clientVersion = Integer.parseInt(version.replace(".", ""));
		if(currVersion > clientVersion){
			return STC_LOGIN_MSG.newBuilder()
					.setState(LOGIN_STATE.FAILURE)
					.setInfo("客户端版本过低")
					.build();
		}
		
		String uid = userId;
		LoginUserInfo userInfo = null;
		if(GameContext.isCheckLogin()){
			LoginResult checkResult = this.loginCheck(userId, openKey, pf);
			if(checkResult.getState() != LOGIN_STATE.SUCCESS){
				return STC_LOGIN_MSG.newBuilder()
						.setState(checkResult.getState())
						.setInfo(checkResult.getInfo())
						.build();
			}
			userInfo = checkResult.getUserInfo();
			
			uid = userInfo.getId();
		}
		
		GameContext.getLock().lock(uid);
		try{
			logger.info(" [PF_TEST] Check login areaId {} uid {} ioid {} ", areaId, uid, context.getIoId() );
			LoginResult result = GameContext.getOnlineCenter().login(areaId, uid, context.getIoId());
			
			STC_LOGIN_MSG.Builder builder = STC_LOGIN_MSG.newBuilder();
			builder.setState(result.getState());
			builder.setInfo(result.getInfo());
			
			if(result.getState() == LOGIN_STATE.SUCCESS){
				
				logger.info(" [PF_TEST] Check login OK areaId {} uid {} ioid {} ", areaId, uid, context.getIoId() );
				
				RoleAccount role = GameContext.getUserApp().getRoleAccount(areaId, uid);
				//设置特征串
				role.setServiceCode(serviceCode);
				role.setClientIp(clientIp);
				role.setOpenKey(openKey);
				role.setPfId(pfId);
				role.setPf(pf);
				//保存角色账号信息
				GameContext.getUserApp().saveRoleAccount(role);
				
				int roleId = role.getRoleId();
				
				if( userInfo != null ){
					// 保存 平台 黄钻和蓝钻信息
					GameContext.getUserApp().savePfUserInfo(roleId, userInfo.getPfUserInfo());
					GameContext.getUserApp().savePfYellowUserInfo(roleId, userInfo.getPfYellowUserInfo());
				}
				
				builder.setTeamId(role.getTeamId() == null ? "" : role.getTeamId());
				builder.setActiveFlag(GameContext.getActiveShow());
				builder.setVipLevel(role.getVipLevel());
				builder.setLevel(role.getLevel());
				
				//当前军衔
				builder.setTitleId(role.getCurrTitleId());
				builder.setName(role.getRoleName());
				builder.setRoleId(roleId);
				builder.setCorpsId(GameContext.getUserApp().getCorpsId(roleId));
				builder.setState(LOGIN_STATE.SUCCESS);
				builder.setTeachRwd(role.getTeachGold());
				
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
				if( userInfo != null ){
					builder.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(role.getRoleId()));
					builder.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(role.getRoleId()));
				}
				
				if(logger.isDebugEnabled()){
					logger.debug("SEND: 登录成功 {}", JSON.toJSONString(role));
				}
				
				//注册用户连接
				RoleConnect connect = new RoleConnect();
				connect.setIoId(context.getIoId());
				connect.setGateName(context.getFrom());
				connect.setNodeName(GameContext.getLocalNodeName());
				connect.setRoleId(roleId);
				connect.setAreaId(areaId);
				GameContext.getOnlineCenter().register(connect);
				
				//系统内部加载登录
				GameContext.getOnlineCenter().sysLogin(connect, true);
				
				if(GameContext.isReportNeed()){
					//发送统计日志
					GameContext.getGameLogApp().sendLoginLog(areaId, pfId, userId, clientIp, role.getLevel());
				}
				GameContext.getGameDBLogApp().getUserLog().userLogin(userId, roleId, areaId, role.getRoleName(), role.getLevel(), pf, clientIp);
			}
			return builder.build();
		}finally{
			GameContext.getLock().unlock(uid);
		}
	}
}
