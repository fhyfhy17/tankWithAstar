package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.CTS_MATERIAL_COMPOSITE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_MATERIAL_COMPOSITE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.TankPartMaterialTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE,
	id = PACKAGE_MSG.CMD_ID.CTS_MATERIAL_COMPOSITE_VALUE
)
public class MaterialCompositeAction implements Action<CTS_MATERIAL_COMPOSITE_MSG> { 

	@Override
	public MessageLite execute(ActionContext context, CTS_MATERIAL_COMPOSITE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		Result result = this.composite(connect.getRoleId(), reqMsg.getGoodsId(), reqMsg.getNum());
		
		STC_MATERIAL_COMPOSITE_MSG.Builder builder = STC_MATERIAL_COMPOSITE_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		builder.setGoodsId(reqMsg.getGoodsId());
		return builder.build();
	}

	private Result composite(int roleId, int goodsId, int num) {
		TankPartMaterialTemplate template = GameContext.getGoodsApp().getMaterialTemplate(goodsId);
		if (template == null) {
			return Result.newFailure("需要合成的材料错误");
		}
		
		GoodsBaseTemplate t = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		
		AttrUnit unit = AttrUnit.build(RoleAttr.iron, Operation.decrease, num*template.getComposite());
		if (!GameContext.getUserAttrApp().changeAttribute(roleId, unit, OutputType.materialComponseDec.type(), StringUtil.buildLogOrigin(t.getName_s(), OutputType.materialComponseDec.getInfo()))) {
			return Result.newFailure("铁块数量不足");
		}
		
		GameContext.getGoodsApp().addGoods(roleId, goodsId, num, OutputType.materialComponseInc.getInfo());
		return Result.newSuccess();
	}
}
