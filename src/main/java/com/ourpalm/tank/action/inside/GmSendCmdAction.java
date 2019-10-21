package com.ourpalm.tank.action.inside;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GMTS_SEND_CMD_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.GMTS_SEND_CMD_VALUE
)
public class GmSendCmdAction implements Action<GMTS_SEND_CMD_MSG>{

	private final Logger logger = LogCore.runtime;
	
	enum GMCmdType {

		closeBattle(1, "结束战斗"), 
		openTankAI(2, "设置开启AI坦克"),
		openTankAIPath(3, "AI坦克固定路线"),
		setPreseasonID(4, "设置AI关卡ID"),
		setPfVip(5, "切换腾讯平台VIP"),
		sendTank(9999, "发坦克"),
		changeMap(9998, "指定地图"),
		sendMember(9997, "发乘员"),
		;
		
		private int type;
		private String info;
		
		GMCmdType(int type, String info){
			this.type = type;
			this.info = info;
		}
		
		public int type(){
			return this.type;
		}
		
		public String getInfo(){
			return this.info;
		}
	}
	
	@Override
	public MessageLite execute(ActionContext context, GMTS_SEND_CMD_MSG reqMsg) {
		int cmdId =reqMsg.getCmdId();
		String params = reqMsg.getParams();
		
		if( cmdId == GMCmdType.closeBattle.type() ){
			GameContext.getMapApp().mapForceClose();
		} else if( cmdId == GMCmdType.openTankAI.type() ) {
			if( params.equals("0") ){
				GameContext.setOpenTankAI(false);
			} else {
				GameContext.setOpenTankAI(true);
			}
		} else if( cmdId == GMCmdType.openTankAIPath.type() ) {
			if( params.equals("0") ){
				GameContext.setUseAiPath(false);
			} else {
				GameContext.setUseAiPath(true);
			}
		} else if( cmdId == GMCmdType.setPreseasonID.type() ){
			try {
				int preseasonID = Integer.parseInt(params);
				if (preseasonID > 0 && preseasonID <= 5) {
					GameContext.setPreseasonID(preseasonID);
					logger.debug("成功设置关卡ID为  preseasonID = {}", preseasonID);
				} else {
					GameContext.setPreseasonID(0);
					logger.debug("成功设置关卡ID为  preseasonID = {}", 0);
				}
			} catch (Exception e) {

			}
		} else if( cmdId == GMCmdType.setPfVip.type() ){
			try {
				int preseasonID = Integer.parseInt(params);
				if (preseasonID == 0) {
					GameContext.setPfVIP(false);
					logger.debug("成功切换腾讯平台VIP为  false");
				} else {
					GameContext.setPfVIP(true);
					logger.debug("成功切换腾讯平台VIP为  true");
				}
			} catch (Exception e) {

			}
		} else if (cmdId == GMCmdType.sendTank.type()) {
			try {
				String[] sendTank = params.split("_");
				int roleId = Integer.parseInt(sendTank[0]);
				int tankId = Integer.parseInt(sendTank[1]);
				RoleTank roleTank = GameContext.getTankApp().tankAdd(roleId, tankId, "GM发送");
				GameContext.getTankApp().tankPush(roleTank);

				logger.debug("发坦克，roleId={},tankId={}", roleId, tankId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (cmdId == GMCmdType.changeMap.type()) {
			try {
				String mapId = params;
				GameContext.setMapId(mapId);
				logger.debug("指定地图，mapId={}", mapId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (cmdId == GMCmdType.sendMember.type()) {
			try {
				String[] sendTank = params.split("_");
				int roleId = Integer.parseInt(sendTank[0]);
				int memberId = Integer.parseInt(sendTank[1]);
				GameContext.getMemberNewApp().addMember(roleId, memberId);
				logger.debug("发乘员，memberId={}", memberId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
