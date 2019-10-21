package com.ourpalm.tank.app.battle;

import org.w3c.dom.Attr;

import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_ICON_TYPE;
import com.ourpalm.tank.message.MATCH_MSG.BuildItem;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.vo.AbstractInstance;

public interface BattleApp {
	
	void login(int roleId, boolean nextDay);
	
	void offline(int roleId);

	/** 攻击 */
	void hitTank(HitParam param);
	/** 攻击 新 */
	void hitTankNew(HitParam param);
	/** 封装坦克属性item */
	TankItem buildTankItem(AbstractInstance tank, AttrType[] attrs);
	
	/** 封装建筑item */
	BuildItem buildBuildItem(AbstractInstance tank);
	
	RoleWarInfo getRoleWarInfo(int roleId);
	
	void saveRoleWarInfo(RoleWarInfo roleWarInfo);

	/** 发送击打收益图标 */
	void sendHitIcon(AbstractInstance tank, INCOME_ICON_TYPE type);
	/**收益*/
	void buildIncomeInfo(AbstractInstance source, AbstractInstance target, float damage, boolean find, boolean stab, boolean crit, boolean hitdie) ;
	public final static AttrType[] selfTankAttrs = new AttrType[]{
			AttrType.hp,
			AttrType.maxHp,
			AttrType.range,
			AttrType.danjia_time,
			AttrType.danjia_num,
			AttrType.speed,
			AttrType.rotate_speed,
			AttrType.can_pre,
			AttrType.focal_radil_move,
			AttrType.focal_radil_lock,
			AttrType.focal_radil_static,
			AttrType.focal_radil_fire,
			AttrType.breath_mult_range,
			AttrType.focal_speed_move,
			AttrType.focal_speed_lock,
			AttrType.focal_speed_static,
			AttrType.focal_speed_fire,
			AttrType.camera_ratate_speed,
			AttrType.followcamera_speed,
			AttrType.breath_time,
			AttrType.focal_distance_max,
			AttrType.focal_distance_min,
			AttrType.active_lock_angle,
			AttrType.view,
			AttrType.sidewayFrictionExtremumSlip,
			AttrType.elevation,
			AttrType.z_speed,
			AttrType.auto_lock_angle,
			AttrType.out_auto_lock_angle,
			AttrType.head_rotate_speed,
			AttrType.head_auuto_rotate_speed,
			AttrType.auto_lock_inertia_speed,
			AttrType.lock_itervaltime,
			AttrType.acceleration_time_start,
			AttrType.acceleration_value_start,
			AttrType.acceleration_time_end,
			AttrType.acceleration_value_end,
			AttrType.acceleration_break_time_start,
			AttrType.acceleration_break_value_start,
			AttrType.acceleration_break_time_end,
			AttrType.acceleration_break_value_end,
			AttrType.r_acceleration_time_start,
			AttrType.r_acceleration_value_start,
			AttrType.r_acceleration_time_end,
			AttrType.r_acceleration_value_end,
			AttrType.r_acceleration_break_time_start,
			AttrType.r_acceleration_break_value_start,
			AttrType.r_acceleration_break_time_end,
			AttrType.r_acceleration_break_value_end,
			AttrType.r_stay_time_start,
			AttrType.r_stay_value_start,
			AttrType.r_stay_time_end,
			AttrType.r_stay_value_end,
			AttrType.r_stay_break_time_start,
			AttrType.r_stay_break_value_start,
			AttrType.r_stay_break_time_end,
			AttrType.r_stay_break_value_end,
			AttrType.fdodge,
			AttrType.idodge,
			AttrType.bdodge,
			AttrType.stable_add,
			AttrType.focalFireTime,
			AttrType.weight,
			AttrType.positive_friction,
			AttrType.lateral_friction,
			AttrType.fire_skew,
			AttrType.out_active_lock_angle,
			AttrType.had_revolve,
			AttrType.sliding_speed,
			AttrType.wheel_weight,
			AttrType.sideways_extrenum_value,
			AttrType.sideways_asymptote_slip,
			AttrType.sideways_asymptote_value,
			AttrType.forward_extrenum_slip,
			AttrType.forward_extrenum_value,
			AttrType.forward_asymptote_slip,
			AttrType.forward_asymptote_value,
			AttrType.auto_brake_star_speed,
			AttrType.auto_brake_end_speed,
			AttrType.auto_brake_force_scale,
			AttrType.wheel_suspension_distance,
			AttrType.wheel_suspension_spring,
			AttrType.wheel_suspension_damper,
			AttrType.wheel_suspension_target_postion,
			AttrType.tank_on_fire_force,
			AttrType.tank_on_beat_force,
			AttrType.wheel_radius,
			AttrType.tank_top_speed,
			AttrType.tank_top_speed_back,
			AttrType.up_hill_start_angle,
			AttrType.up_hill_end_angle,
			AttrType.up_hill_mid_angle,
			AttrType.up_hill_mid_power,
			
			AttrType.n_minHit,//最小伤害
			AttrType.n_maxHit,//最大伤害
			AttrType.n_crit,//暴击
			AttrType.n_isNear,//有无近程伤害
			AttrType.n_isFar,//有无远程伤害
			AttrType.n_fireSpeed,//射击速度
			AttrType.n_stab,//穿透力
			AttrType.n_minPrecision,//最小精度
			AttrType.n_maxPrecision,//最大精度
			AttrType.n_aimSpeed,//瞄准速度
			AttrType.n_hpMax,//最大生命值 
			AttrType.n_dr,//伤害减免
			AttrType.n_ArmorTurretFront,//炮塔装甲前
			AttrType.n_ArmorTurretMiddle,//炮塔装甲侧
			AttrType.n_ArmorTurretbehind,//炮塔装甲后	
			AttrType.n_ArmorBodyFront,// 车身装甲前
			AttrType.n_ArmorBodyMiddle,//车身装甲侧
			AttrType.n_ArmorBodybehind,//车身装甲后
			AttrType.n_turrentSpeep,//炮塔转速
			AttrType.n_bodySpeed,//车身转速
			AttrType.n_nimble,//灵活度
			AttrType.n_speed,//速度
			AttrType.n_speedUp,//加速能力
			AttrType.n_view,//视野
			AttrType.n_camouflage,//伪装
			
			
			AttrType.maxAngularDegree,	//每秒角度	
			AttrType.steerTorque,//辅助转向，值越大转向越快
			AttrType.wheelLROffset,	//车轮位置往外偏移，增加车轮距
			AttrType.engineTorque,	//动力
			AttrType.brakeTorque,	//刹车阻力
			AttrType.MassCenterOffsetX,	//车子重心偏移X
			AttrType.MassCenterOffsetY,	//车子重心偏移Y
			AttrType.MassCenterOffsetZ,//车子重心偏移Z
			AttrType.sideways_stiffness,//正面僵直
			AttrType.forward_stiffness,//侧僵直
			AttrType.engineTorqueK,	//动力系数
			AttrType.wheelDampingRate,	//车轮阻力
			AttrType.elevation1,	//俯视角
	};
	
	public final static AttrType[] otherTankAttr = new AttrType[]{
			AttrType.fdodge,
			AttrType.idodge,
			AttrType.bdodge,
			AttrType.stable_add,
			AttrType.hp,
			AttrType.maxHp,
			AttrType.range,
			AttrType.view,
			AttrType.speed,
			AttrType.danjia_time,
			AttrType.rotate_speed,
			AttrType.had_revolve,
			AttrType.wheel_weight,
			AttrType.weight,
			AttrType.wheel_radius,
			AttrType.tank_top_speed,
			AttrType.tank_top_speed_back,
			AttrType.up_hill_start_angle,
			AttrType.up_hill_end_angle,
			AttrType.up_hill_mid_angle,
			AttrType.up_hill_mid_power,
			
			AttrType.n_fireSpeed,//射击速度
			AttrType.n_hpMax,//最大生命值 
			AttrType.n_speed,//速度
			AttrType.n_speedUp,//加速能力
			AttrType.n_view,//视野
			AttrType.n_ArmorTurretFront,//炮塔装甲前
			AttrType.n_ArmorTurretMiddle,//炮塔装甲侧
			AttrType.n_ArmorTurretbehind,//炮塔装甲后	
			AttrType.n_ArmorBodyFront,// 车身装甲前
			AttrType.n_ArmorBodyMiddle,//车身装甲侧
			AttrType.n_ArmorBodybehind,//车身装甲后
			AttrType.n_stab,//穿透力
			AttrType.n_camouflage,//隐藏值
	};
}

