package com.ourpalm.tank.app.quest;

public enum ConditionType {
	login(1),   		//登录
	sign_in(2),   		//每日签到N次
	tank_part_level(3), //升级N次普通配件
	crew_lottery(4),    //进行N次武将抽取
	crew_level(5),   	//进行N次武将升级
	goodbox_open(6),   	//开启N个材料卡包
	battle_win(7),   	//获得N场对应战场胜利
	battle_kill(8),   	//在对应战场击杀N辆坦克
	elite_battle_win(9),		//使用精英坦克胜利
	elite_battle_kill(10),		//使用精英坦克击杀
	battle_flag_win(11),		//占旗胜利
	battle_damage_rank(12),		//战场累计伤害排名
	battle_kill_rank(13),		//战场击毁敌方坦克数量排名
	battle_win_self_damage_rank(14),//战场累计承受伤害排名，且本方获胜
	battle_win_no_dead(15),			//战斗中1次死亡也没有，且本方胜利
	battle_win_interrupt_flag(16),	//打断过敌方50%占旗，且本方胜利
	battle_first_kill(17),			//第一个击毁敌方坦克
	battle_kill_heigh_level(18),	//击毁不低于自身等级的敌方坦克
	battle_attach_buff(19),			//攻击造成敌人带debuff效果
	battle_five_kill(20),			//五杀
	battle_discover_enemy(21),		//点亮敌方坦克
	battle_win_army_team(22),		//与军团一起战斗，并胜利
	season_rank(23),		//赛季结束，传说排名
	tank_buy(24),			//坦克研发
	tank_elite_level(25),	//精英坦克最高等级
	vip_time(26),			//vip时间
	tank_elite(27),			//累计研发精英坦克数
	shop_buy(28),			//道具商店购买道具
	gold_consume(29),		//消耗金币
	donate(30), 			//军团贡献度
	straight_win(31), 		//连胜N场战斗
	battle(32), 			//进行N场战斗
	battle_kill_tank(33),	//累计击杀N辆坦克
	battle_type_mvp(34),	//指定战场类型获得MVP
	role_level(35),			//角色升级
	battle_reward_iron(36),	//战场结束获得银币
	one_battle_kill_flag_enemy(37), 	//一场战斗击杀N个正在夺旗的敌人
	collect_member(38),		//收集N个成员 
	collect_medal(39),		//收集N个勋章
	collect_gold(40),		//累计获取N个金币	
	battle_reward_honor(41),	//战斗累计获取N个荣誉点
	one_battle_alive_help(42),		//对应战场整场战斗不死，获得N个助攻	
	one_battle_alive_loop_kill(43),	//对应战场整场战斗不死，连杀N个敌人
	one_battle_alive_kill(44),		//对应战场整场战斗不死，击杀N个敌人
	one_battle_alive_fireBullet_kill(45),	//对应战场整场战斗不死，使用燃烧弹击杀N个敌人
	one_battle_dead_kill(46)		//对应战场死亡复仇同时炸死N个敌人
	
	
	
	;
	
	private int type;
	private ConditionType(int type) {
		this.type = type;
	}
	
	public int getType(){
		return this.type;
	}
	
	public static ConditionType get(int id) {
		for (ConditionType condition : ConditionType.values()) {
			if (condition.getType() == id) {
				return condition;
			}
		}
		return null;
	}
}
