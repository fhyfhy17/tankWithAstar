package com.ourpalm.tank.action;

import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_RENAME_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_RENAME_MSG;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
	id = ROLE_MSG.CMD_ID.CTS_RENAME_VALUE
)
public class RenameAction implements Action<CTS_RENAME_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RENAME_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String name = reqMsg.getName();
		//验证是否存在非法字符
		Set<String> sensitives = GameContext.getMutilDfaApp().matchSensitiveWord(name);
		if(!Util.isEmpty(sensitives)){
			return buildMsg(false, Tips.ROLE_NAME_ILLEGAL);
		}
		
		GameContext.getLock().lock(Action.RENAME_KEY);
		
		try{
			boolean nameExist = GameContext.getUserApp().roleNameHadExist(name);
			if(nameExist){
				return buildMsg(false, Tips.ROLE_NAME_HAD_EXIST);
			}
			
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.rename);
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
			
			int roleId = connect.getRoleId();
			boolean consumeRst = GameContext.getUserAttrApp().changeAttribute(roleId, 
					AttrUnit.build(RoleAttr.gold, Operation.decrease, template.getGold()), OutputType.renameDec);
			
			if(!consumeRst){
				return buildMsg(false, Tips.NEED_GOLD);
			}
			
			RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
			String oldName = account.getRoleName();
			//删除旧有的与角色id绑定关系
			GameContext.getUserApp().delNameRoleIdLink(account.getRoleName());
			
			//添加新的名字与角色id绑定关系
			GameContext.getUserApp().addNameRoleIdLink(account.getRoleId(), name);
			
			account.setRoleName(name);
			GameContext.getUserApp().saveRoleAccount(account);
			
			GameContext.getGameDBLogApp().getRoleLog().roleRename(roleId, oldName, name);
			return buildMsg(true, "");
			
		}finally{
			GameContext.getLock().unlock(Action.RENAME_KEY);
		}
	}
	
	private STC_RENAME_MSG buildMsg(boolean success, String info) {
		return STC_RENAME_MSG.newBuilder()
						.setSuccess(success)
						.setInfo(info)
						.build();
	}
}
