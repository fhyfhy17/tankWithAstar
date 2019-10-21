package com.ourpalm.tank.app.map.ai;

public enum AiType {
	death, // 死亡，无行为
	move, // 移动，移动行为
	fire, // 开火，开火行为
	escape, // 逃跑，移动行为
	stayAndFire, // 停止，射击，开火行为
	circle, // 绕圈射击，开火行为
	pursue,// 追击，开火行为
	moveToFlag,// 朝旗子方向直接移动（有目标就攻击,遇到目标切换fireAndMoveToEnemy或fire），移动行为
	FireToFlag,// 朝旗子方向移动攻击(只以旗内敌人为目标，遇到目标切换fireAndMoveToEnemy或fire)，移动行为
	fireAndMoveToEnemy,// 向敌人靠拢攻击（短兵战/自杀攻击），开火行为
	fireAndMoveOutFlag,// 走出旗子范围，开火行为
	;

}
