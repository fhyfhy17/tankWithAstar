package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.BuildItem;
import com.ourpalm.tank.message.MATCH_MSG.GoodsItem;
import com.ourpalm.tank.message.MATCH_MSG.MatchItem;
import com.ourpalm.tank.message.MATCH_MSG.STC_NOTIFY_ENTER_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STG_SWITCH_LINK_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_OFFLINE_RELOGIN_MSG;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.SportMapInstance;

public abstract class AbstractEnterMap {
	/** 断线重连状态 **/
	public final static int RELINK_LOGIN = 0; 					//重连来自登录
	public final static int RELINK_MATCH = 1; 					//重连来自匹配
	
	protected Logger logger = LogCore.runtime;

	
	/** 是否可以返回战场 */
	protected boolean backBattleWar(int roleId, int ioId, String gateNode, int relinkType){
		//是否有战场记录
		RoleBattle roleBattle = GameContext.getMatchApp().getRoleBattle(roleId);
		if(roleBattle == null){
			this.backHall(roleId);
			return false;
		}
		//创建战场是否已结束
		if(roleBattle.hadOverTime()){
			this.backHall(roleId);
			return false;
		}
		
		//不是本地战场则不处理，交给所属战场处理
		if(!this.hasLocalBattleWar(roleBattle, ioId, gateNode, relinkType)){
			return true;
		}
		
		//判断是否可进入战场
		boolean enterFlag = this.enterBattle(roleId);
		
		//进入失败告知客户端返回大厅
		if(!enterFlag){
			this.backHall(roleId);
		}
		
		return enterFlag;
	}
	
	
	/** 通知客户端返回大厅 */
	private void backHall(int roleId){
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if(connect != null){
			connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_BACK_WAR_VALUE, null);
		}
	}
	
	
	
	protected void login(int roleId, int ioId, String gateNode){
		//检测多点登录
		GameContext.getOnlineCenter().checkMultiLogin(roleId, ioId);
		
		//注册用户连接
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		RoleConnect connect = new RoleConnect();
		connect.setIoId(ioId);
		connect.setGateName(gateNode);
		connect.setNodeName(GameContext.getLocalNodeName());
		connect.setRoleId(roleId);
		connect.setAreaId(role.getAreaId());
		GameContext.getOnlineCenter().register(connect);
		
		//加载数据到内存
		GameContext.getOnlineCenter().sysLogin(connect, false);
		
		logger.debug("登录本机, 加载数据完成... roleId = {}", roleId);
	}
	
	
	/** 判断是否为本地战场，不是本地战场将发送消息通知所属战场 */
	private boolean hasLocalBattleWar(RoleBattle roleBattle, int ioId, String gateNode, int relinkType){
		int roleId = roleBattle.getRoleId();
		String beforeNode = roleBattle.getNodeName();
		if(!beforeNode.equals(GameContext.getLocalNodeName())){
			RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
			if(remoteNode == null){
				//登录本机
				if(logger.isDebugEnabled()){
					logger.debug("断线重连, 远程节点不存在...");
				}
				return true;
			}
			//通知远程服务器加载玩家数据
			Message msg = new Message();
			msg.setCmdType((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			msg.setCmdId((byte)SERV_MSG.CMD_ID.STS_OFFLINE_RELOGIN_VALUE);
			msg.setFromNode(GameContext.getLocalNodeName());
			msg.setIoId(ioId);
			msg.setData(STS_OFFLINE_RELOGIN_MSG.newBuilder()
					.setGateNode(gateNode)
					.setRoleId(roleId)
					.setIoId(ioId)
					.setRelinkType(relinkType)
					.setWarType(roleBattle.getWarType())
					.setTargetNode(beforeNode)
					.build()
					.toByteArray());
			remoteNode.sendReqMsg(msg);
			logger.debug("断线重连, 通知服务器节点 {} 登录...", beforeNode);
			
			return false;
		}
		return true;
	}
	
	
	
	/** 请求进入战场 */
	protected boolean enterBattle(int roleId){
		RoleBattle roleBattle = GameContext.getMatchApp().getRoleBattle(roleId);
		if(roleBattle == null){
			return false;
		}
		//验证战场时间
		AbstractInstance tank = GameContext.getTankApp().getInstance(roleBattle.getTankInstanceId());
		if(tank == null){
			if(logger.isDebugEnabled()){
				logger.debug("断线重连, 坦克实例不存在... roleId = {}", roleId);
			}
			return false;
		}
		SportMapInstance mapInstance = (SportMapInstance)GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			return false;
		}
		
		//客户端切换网络的时候，并不会断开socket链接，下面做容错处理
		//让坦克对象离开地图，暂时不走复活逻辑
		//解决死亡后复活时间剩余2，3秒时切换网络，客户端先收到复活消息，并认为自己非死亡状态开始同步当前位置。最终导致瞬移
		//ready命令会将坦克放回地图实例
		mapInstance.leave(tank.getId());
		
		
		//判断比赛是否结束
		//避免把玩家拽入战场，看到结算战报后回来大厅
		//大厅又再次请求回到战场，如此死循环
		if(mapInstance.getStateMachine().hadOver()){
			return false;
		}
		
		//判断比赛是否结束
		if(roleBattle.hadOverTime()){
			if(logger.isDebugEnabled()){
				logger.debug("断线重连, 比赛已经结束, 不再允许进入战场... roleId = {}", roleId);
			}
			return false;
		}
		
		MapTemplate mapTemplate = mapInstance.getTemplate();
		
		//返回匹配结果,走正常进入流程
		STC_NOTIFY_ENTER_MSG.Builder builder = STC_NOTIFY_ENTER_MSG.newBuilder();
		builder.setMapId(mapTemplate.getMapId());
		builder.setSelf(tank.getTeam());
		builder.setMapName(mapTemplate.getName());
		builder.setSmallMapOffX(mapTemplate.getOffX());
		builder.setSmallMapOffY(mapTemplate.getOffY());
		builder.setSmallMapHeigh(mapTemplate.getOffHeigh());
		builder.setSmallMapWidth(mapTemplate.getOffWidth());
		builder.setWarType(WAR_TYPE.valueOf(roleBattle.getWarType()));
		builder.setWeather(mapInstance.getWeather());

		String roomId = GameContext.getLocalNodeName() + Cat.underline + mapInstance.getInstanceId();
		builder.setRoomId(roomId);
		
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance _tank = GameContext.getTankApp().getInstance(instanceId);
			if(_tank == null){
				continue;
			}
			MatchItem item = MatchItem.newBuilder()
					.setRoleId(_tank.getRoleId())
					.setRoleName(_tank.getRoleName())
					.setTankId(_tank.getTemplateId())
					.setTeamType(_tank.getTeam())
					.setAttack(_tank.getBattleScore())
					.setTitleId(_tank.getTitleId())
					.setHadElite(_tank.isEliteTank())
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(_tank.getRoleId()))
					.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(_tank.getRoleId()))
					.setTankInstanceId(_tank.getId())
					.addAllGoodBad(GameContext.getTankApp().getTankTemplate(_tank.getTemplateId()).getGoodBadList())
					
					.build();
			builder.addMatchList(item);
		}
		builder.setWeakTeam(tank.isWeakTeam());//是否遭遇强敌
		//发送消息
		tank.sendMsg(builder.build());
		
		//战斗对象加入缓存中
		GameContext.getMatchApp().saveRoleBattle(roleBattle);
		
		return true;
	}
	
	
	
	/** 进入地图 */
	protected void enterMap(int roleId){
		RoleBattle roleBattle = GameContext.getMatchApp().getRoleBattle(roleId);
		AbstractInstance tank = GameContext.getTankApp().getInstance(roleBattle.getTankInstanceId());
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		//进入地图
		mapInstance.enter(tank);
		//战斗信息放入本地
		roleBattle.setNodeName(GameContext.getLocalNodeName());
		GameContext.getMatchApp().saveRoleBattle(roleBattle);
		//清除坦克异常记录
		tank.clearClearMoveCount();
	}
	
	
	//通知网关切换链接
	protected void changeRoleLink(int ioId, String gateName){
		RemoteNode gateNode = GameContext.getGatewayManagerApp().getRemoteNode(gateName);
		if(gateNode == null){
			logger.warn("网关节点挂了...nodeName = "+gateName, new NullPointerException());
			return ;
		}
		STG_SWITCH_LINK_MSG msg = STG_SWITCH_LINK_MSG.newBuilder()
				.setIoId(ioId)
				.setNewNode(GameContext.getLocalNodeName())
				.build();
		
		Message message = new Message();
		message.setCmdType((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
		message.setCmdId((byte)SERV_MSG.CMD_ID.STG_SWITCH_LINK_VALUE);
		message.setFromNode(GameContext.getLocalNodeName());
		message.setIoId(ioId);
		message.setData(msg.toByteArray());
		
		gateNode.sendReqMsg(message);
		
		logger.info("断线重连, 通知网关切换链接... nodeName = {}", GameContext.getLocalNodeName());
	}
	
	
	// 其他坦克属性
	protected List<TankItem> getOtherTanks(MapInstance mapInstance, int roleId) {
		List<TankItem> list = new ArrayList<>();
		//这里应使用所有坦克实例ID来获取坦克对象
		//避免断线重连回到战场没法获得掉线者的坦克信息
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance tank = GameContext.getTankApp().getInstance(instanceId);
			if (tank == null || roleId == tank.getRoleId()) {
				continue;
			}
			if(tank.getBodyType() != BodyType.tank){
				continue;
			}
			TankItem item = GameContext.getBattleApp().buildTankItem(tank, BattleApp.otherTankAttr);
			list.add(item);
		}
		return list;
	}
	
	
	// 建筑列表
	protected List<BuildItem> getBuildItems(MapInstance mapInstance){
		List<BuildItem> list = new ArrayList<>();
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.getBodyType() != BodyType.tank){
				list.add(GameContext.getBattleApp().buildBuildItem(tank));
			}
		}
		return list;
	}

	
	// 获取道具列表
	protected List<GoodsItem> getGoodsItem(AbstractInstance tank) {
		//解决断线重连后，重新带满金币弹进入的问题
		//优先判断坦克身上所携带的
		List<GoodsItem> goodsList = new ArrayList<>();
		Map<Integer, Integer> goodsMap = tank.getAllGoods();
		if(!Util.isEmpty(goodsMap)){
			for(Entry<Integer, Integer> entry : goodsMap.entrySet()){
				GoodsItem goodsItem = GoodsItem.newBuilder()
						.setId(entry.getKey())
						.setCount(entry.getValue())
						.build();
				goodsList.add(goodsItem);
			}
			return goodsList;
		}
		
		final int roleId = tank.getRoleId();
		TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		//获取坦克所使用的炮弹
		int shellGoodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(tankTemplate.getShellType_i()));
		int warGoodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
		int[] goods = {shellGoodsId, warGoodsId};
		// 判断是否拥有
		for (int goodsId : goods) {
			if (goodsId == 0) {
				continue;
			}
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
			if (template == null) {
				continue;
			}
			int count = GameContext.getGoodsApp().getCount(roleId, goodsId);
			
			// 最大可允许携带数
			int maxCount = template.getCarryNum_i();
			if (count > maxCount) {
				count = maxCount;
			}
			GoodsItem goodsItem = GoodsItem.newBuilder().setId(goodsId).setCount(count).build();
			goodsList.add(goodsItem);
			// 放入坦克
			tank.putGoods(goodsId, count);
		}
		return goodsList;
	}

	// 所拥有的技能
	protected List<Integer> getSkillItem(AbstractInstance tank) {
		List<Integer> skillList = new ArrayList<>();
		for (Skill skill : tank.getAllSkill()) {
			if (skill.isActive()) {
				skillList.add(skill.getId());
			}
		}
		return skillList;
	}
	
	
	
	/** 跨服匹配 */
//	protected void svsMatch(RoleConnect connect, WAR_TYPE type, int tankId){
//		int roleId = connect.getRoleId();
//		//判断是否已在匹配队列中
//		if(GameContext.getMatchApp().hasMatchQueue(connect.getIoId())){
//			if(logger.isDebugEnabled()){
//				logger.debug("已在匹配队列中  roleId = {}, ioId = {}, tankId = {}", roleId, connect.getIoId(), tankId);
//			}
//			return ;
//		}
//		
//		String matchNodeName = GameContext.getMatchManagerApp().loopRemoteNodeName();
//		RemoteNode matchNode = GameContext.getMatchManagerApp().getRemoteNode(matchNodeName);
//		if(matchNode == null){
//			logger.warn("匹配服务器节点 {} 挂了。。。", matchNodeName);
//			return ;
//		}
//		
//		RoleArmyTitle roleArmyTitle = GameContext.getRoleArmyTitleApp().getRoleTitle(roleId);
//		int currTitleId = roleArmyTitle.getCurrTitleId();
//		
//		//按当前军衔随机地图
//		TankMatchAiTemplate template = GameContext.getRoleArmyTitleApp().getTankMatchMapTemplate(currTitleId);
//		String aiPros = template.getAiPros();
//		//军衔排行模式的AI个数百分比
//		if(WAR_TYPE.RANK == type){
//			aiPros = template.getRankAiPros();
//		}
//		
//		//根据玩家等级随机地图
//		int level = GameContext.getUserAttrApp().get(roleId, RoleAttr.level);
//		UserLevelTemplate userLevelTemplate = GameContext.getUserApp().getLevelTemplate(level);
//		int mapIndex = userLevelTemplate.randomMapIndex();
//		MapTemplate mapTemplate = GameContext.getMapApp().getMapTemplate(mapIndex);
//		
//		//发送申请匹配消息
//		MatchScore matchScore = GameContext.getRoleArmyTitleApp().getMatchScore(type, roleId, tankId);
//		
//		//计算战斗力
//		int battleScore = GameContext.getTankApp().calcAllBattleScore(roleId, tankId);
//		
//		STM_MATCH_MSG matchMsg = STM_MATCH_MSG.newBuilder()
//				.setType(type.getNumber())
//				.setIoId(connect.getIoId())
//				.setRoleId(roleId)
//				.setGateNode(connect.getGateName())
//				.setFromNode(GameContext.getLocalNodeName())
//				.setTankId(tankId)
//				.setScore(matchScore.getScore())
//				.setDiffer(matchScore.getDiffer())
//				.setTitleId(roleArmyTitle.getCurrTitleId())
//				.setMatchType(matchScore.getMatchType())
//				.setAiPros(aiPros)
//				.setMapIndex(mapIndex)
//				.setPlayerCount(mapTemplate.getPlayerCount())
//				.setBattleScore(battleScore)
//				.build();
//		
//		Message message = new Message();
//		message.setCmdId((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
//		message.setCmdId((byte)SERV_MSG.CMD_ID.STM_MATCH_VALUE);
//		message.setData(matchMsg.toByteArray());
//		message.setFromNode(GameContext.getLocalNodeName());
//		message.setIoId(connect.getIoId());
//		
//		matchNode.sendReqMsg(message);
//		
//		logger.debug("向匹配服务发送请求  roleId = {} ioId = {} tankId = {} matchScore = {} diff = {}"
//				, roleId, connect.getIoId(), tankId, matchScore.getScore(), matchScore.getDiffer());
//	}
}
