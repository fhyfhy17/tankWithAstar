package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.dblog.MoneyDBLog;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class MoneyLogModule{
	
	private final int LOG_ID = 2000;
	
	enum MoneyDBLogType {

		addDiamonds(200001, "钻石来源"), 
		useDiamonds(200002, "钻石使用"),

		addGold(200003, "金币来源"), 
		useGold(200004, "金币使用"),

		addTankExp(200005, "坦克经验来源"), 
		useTankExp(200006, "坦克经验使用"),

		addHonor(200007, "荣誉来源"), 
		useHonor(200008, "荣誉使用"),
		;
		
		private int type;
		private String info;
		
		MoneyDBLogType(int type, String info){
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

	public void changeValue(int roleId, AttrUnit unit, int originId, String origin, int chargeValue, int oldValue, int nowValue) {
		int logSubId = 0;
		if (RoleAttr.diamonds.equals(unit.getType())) {
			if( unit.getOperation() == Operation.decrease ){
				logSubId = MoneyDBLogType.useDiamonds.type();
			} else if( unit.getOperation() == Operation.add ) {
				logSubId = MoneyDBLogType.addDiamonds.type();
			} else {
				return;
			}
		} else if (RoleAttr.gold.equals(unit.getType())) {
			if( unit.getOperation() == Operation.decrease ){
				logSubId = MoneyDBLogType.useGold.type();
			} else if( unit.getOperation() == Operation.add ) {
				logSubId = MoneyDBLogType.addGold.type();
			} else {
				return;
			}
		} else if (RoleAttr.tankExp.equals(unit.getType())) {
			if( unit.getOperation() == Operation.decrease ){
				logSubId = MoneyDBLogType.useTankExp.type();
			} else if( unit.getOperation() == Operation.add ) {
				logSubId = MoneyDBLogType.addTankExp.type();
			} else {
				return;
			}
		} else if (RoleAttr.honor.equals(unit.getType())) {
			if( unit.getOperation() == Operation.decrease ){
				logSubId = MoneyDBLogType.useHonor.type();
			} else if( unit.getOperation() == Operation.add ) {
				logSubId = MoneyDBLogType.addHonor.type();
			} else {
				return;
			}
		} else {
			return;
		}

		MoneyDBLog log = new MoneyDBLog();
		log.initLog(roleId, this.LOG_ID, logSubId, originId, origin, chargeValue, oldValue, nowValue);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}

}
