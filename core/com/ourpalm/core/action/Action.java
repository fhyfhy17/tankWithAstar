package com.ourpalm.core.action;

import com.google.protobuf.MessageLite;

public interface Action<T> {

	/** 修改角色名KEY */
	String RENAME_KEY = "ROLE_RENAME_KEY";

	/** 战斗组队锁 */
	String BATTLE_TEAM_LOCK = "BATTLE_TEAM_";

	MessageLite execute(ActionContext context, T reqMsg);

}
