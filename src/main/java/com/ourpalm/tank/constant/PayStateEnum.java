package com.ourpalm.tank.constant;

public enum PayStateEnum {
	SUCCESS, // 成功
	MONEY_LESS, // 实际金额小于发起金额（可能有作弊）
	ITEM_NON,// 物品不存在，可能过了很久，配表都改了，玩家才再次登录
	;
}
