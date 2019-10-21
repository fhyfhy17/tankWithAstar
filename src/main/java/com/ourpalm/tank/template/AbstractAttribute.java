package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.IComponent;

public class AbstractAttribute implements IComponent,KeySupport<Integer>{
	
	protected int id_i;			
	protected int level_i;			//等级
	protected float hp;	
	protected float fdef;				//前护甲
	protected float idef;				//侧护甲
	protected float bdef;				//后护甲
	protected float derate;			//伤害减免
	protected float fdodge;				//前跳弹值
	protected float idodge;				//侧跳弹值
	protected float bdodge;				//后跳弹值
	protected float atk;				//攻击力
	protected float chuanjia;			//穿甲值
	protected float chuanjia_rat;		//穿甲浮动比率
	protected float mul_chuanjia;		//穿甲暴击倍率
	protected float mul_dodge;		//跳弹伤害倍率
	protected float range;				//射程
	protected float danjia_time;		//弹夹填弹时间
	protected float danjia_num;			//弹夹填装个数
	protected float speed;			//速度
	protected float rotate_speed;		//车身转动速度
	protected float can_pre;			//火炮精度
	protected float focal_radil_move;		//准心半径_移动
	protected float focal_radil_lock;		//准心半径_锁定
	protected float focal_radil_static; 	//准心半径_静止
	protected float focal_radil_fire;		//准心半径_开火
	protected float focal_speed_move;		//准心缩放速度_移动
	protected float focal_speed_lock;		//准心缩放速度_锁定
	protected float focal_speed_static;	//准心缩放速度_静止
	protected float focal_speed_fire;		//准心缩放速度_开火
	protected float camera_ratate_speed;	//摄像机旋转速度
	protected float followcamera_speed;	//炮台跟随摄像机速度
	protected float breath_time;			//准心缩放周期
	protected float breath_mult_range;	//准心半径周期
	protected float focal_distance_max;	//准心锁定最远距离
	protected float focal_distance_min;	//准心锁定最近距离
	protected float active_lock_angle;	//手动锁定角度
	protected float out_active_lock_angle; //脱离手动锁定角度
	protected float cat_damed_rat;		//火炮受损率
	protected float cat_bre_rat;			//履带受损率
	protected float fire_rat;				//机身起火率
	protected float speed_slow_rat;		//引擎受损率
	protected float cd_slow_rat;			//弹药架受损率
	protected float view;					//视野
	protected float depression;			//仰视角
	protected float elevation;			//俯视角
	protected float z_speed;				//上下调整速度
	protected float s_aim;				//手动瞄准角度
	protected float z_aim;				//自动瞄准角度
	protected float auto_lock_angle;		//自动锁定角度
	protected float out_auto_lock_angle;	//脱离自动锁定角度
	protected float head_rotate_speed;	//炮管锁定旋转角度
	protected float head_auuto_rotate_speed;	//炮管自动锁定旋转角度
	protected float auto_lock_inertia_speed;	//自动锁定惯性速度
	protected float lock_itervaltime;			//目标锁定间隔
	protected float crew_hp;					//成员血量加成比率
	protected float crew_act;					//成员攻击加成比率
	protected float crew_rate;					//成员射速加成比率
	protected float crew_speed;					//成员速度加成比率
	
	protected float crew_chuanjia;				//成员穿甲加成比率
	protected float crew_fdef;					//成员前护甲加成比率
	protected float crew_idef;					//成员侧护甲加成比率	
	protected float crew_bdef;					//成员后护甲加成比率
	protected float stable_add;					//稳定加成
	protected float focalFireTime;				//圈开火放大时间
	
	protected float acceleration_time_start;
	protected float acceleration_value_start;
	protected float acceleration_time_end;
	protected float acceleration_value_end;
	protected float acceleration_break_time_start;
	protected float acceleration_break_value_start;
	protected float acceleration_break_time_end;
	protected float acceleration_break_value_end;
	protected float r_acceleration_time_start;
	protected float r_acceleration_value_start;
	protected float r_acceleration_time_end;
	protected float r_acceleration_value_end;
	protected float r_acceleration_break_time_start;
	protected float r_acceleration_break_value_start;
	protected float r_acceleration_break_time_end;
	protected float r_acceleration_break_value_end;
	protected float r_stay_time_start;
	protected float r_stay_value_start;
	protected float r_stay_time_end;
	protected float r_stay_value_end;
	protected float r_stay_break_time_start;
	protected float r_stay_break_value_start;
	protected float r_stay_break_time_end;
	protected float r_stay_break_value_end;

	protected float strength;					//战斗力
	protected float matchScore;					//匹配分
	
	protected float wheel_weight;				//轮子重量
	protected float weight;						//重量
	protected float positive_friction;			//正向摩擦力
	protected float lateral_friction;			//转角摩擦力
	protected float fire_skew;					//开炮偏移量
	
	
	protected Map<AttrType, Float> property = new HashMap<AttrType, Float>();
	
	
	private float n_minHit;//最小伤害
	private float n_maxHit;//最大伤害
	private float n_crit;//暴击
	private float n_isNear;//有无近程伤害
	private float n_isFar;//有无远程伤害
	private float n_fireSpeed;//射击速度
	private float n_stab;//穿透力
	private float n_minPrecision;//最小精度
	private float n_maxPrecision;//最大精度
	private float n_aimSpeed;//瞄准速度
	private float n_hpMax;//最大生命值 
	private float n_dr;//伤害减免
	private float n_ArmorTurretFront;//炮塔装甲前
	private float n_ArmorTurretMiddle;//炮塔装甲侧
	private float n_ArmorTurretbehind;//炮塔装甲后	
	private float n_ArmorBodyFront;// 车身装甲前
	private float n_ArmorBodyMiddle;//车身装甲侧
	private float n_ArmorBodybehind;//车身装甲后
	private float n_turrentSpeep;//炮塔转速
	private float n_bodySpeed;//车身转速
	private float n_nimble;//灵活度
	private float n_speed;//速度
	private float n_speedUp;//加速能力
	private float n_view;//视野
	private float n_camouflage;//伪装
	
	private String good1;
	private String good2;
	private String good3;
	private String bad1;
	private String bad2;
	private String bad3;
	private List<String> goodBadList = new ArrayList<>();
	protected Map<AttrType, Float> newProperty = new HashMap<AttrType, Float>();
	public void init() {
		property.put(AttrType.maxHp, hp);              
		property.put(AttrType.fdef, fdef);
		property.put(AttrType.idef, idef);
		property.put(AttrType.bdef,  bdef);
		property.put(AttrType.derate, derate);
		property.put(AttrType.fdodge, fdodge);
		property.put(AttrType.idodge, idodge);
		property.put(AttrType.bdodge, bdodge);
		property.put(AttrType.atk, atk);
		property.put(AttrType.chuanjia, chuanjia);
		property.put(AttrType.chuanjia_rat, chuanjia_rat);
		property.put(AttrType.mul_chuanjia, mul_chuanjia);
		property.put(AttrType.mul_dodge, mul_dodge);
		property.put(AttrType.range, range);
		property.put(AttrType.danjia_time, danjia_time);
		property.put(AttrType.danjia_num, danjia_num);
		property.put(AttrType.speed, speed);
		property.put(AttrType.rotate_speed, rotate_speed);
		property.put(AttrType.focalFireTime, focalFireTime);
		property.put(AttrType.can_pre, can_pre);
		property.put(AttrType.focal_radil_move, focal_radil_move);
		property.put(AttrType.focal_radil_lock, focal_radil_lock);
		property.put(AttrType.focal_radil_static, focal_radil_static);
		property.put(AttrType.focal_radil_fire, focal_radil_fire);
		property.put(AttrType.focal_speed_move, focal_speed_move);
		property.put(AttrType.focal_speed_lock, focal_speed_lock);
		property.put(AttrType.focal_speed_static, focal_speed_static);
		property.put(AttrType.focal_speed_fire, focal_speed_fire);
		property.put(AttrType.camera_ratate_speed, camera_ratate_speed);
		property.put(AttrType.followcamera_speed, followcamera_speed);
		property.put(AttrType.breath_time, breath_time);
		property.put(AttrType.breath_mult_range, breath_mult_range);
		property.put(AttrType.focal_distance_max, focal_distance_max);
		property.put(AttrType.focal_distance_min, focal_distance_min);
		property.put(AttrType.active_lock_angle, active_lock_angle);
		property.put(AttrType.cat_damed_rat, cat_damed_rat);
		property.put(AttrType.cat_bre_rat, cat_bre_rat);
		property.put(AttrType.fire_rat, fire_rat);
		property.put(AttrType.speed_slow_rat, speed_slow_rat);
		property.put(AttrType.cd_slow_rat, cd_slow_rat);
		property.put(AttrType.view, view);
		property.put(AttrType.elevation, elevation);
		property.put(AttrType.z_speed, z_speed);
		property.put(AttrType.auto_lock_angle, auto_lock_angle);
		property.put(AttrType.out_auto_lock_angle, out_auto_lock_angle);
		property.put(AttrType.head_rotate_speed, head_rotate_speed);
		property.put(AttrType.head_auuto_rotate_speed, head_auuto_rotate_speed);
		property.put(AttrType.auto_lock_inertia_speed, auto_lock_inertia_speed);
		property.put(AttrType.lock_itervaltime, lock_itervaltime);
		property.put(AttrType.crew_hp, crew_hp);
		property.put(AttrType.crew_act, crew_act);
		property.put(AttrType.crew_rate, crew_rate);
		property.put(AttrType.crew_speed, crew_speed);
		
		property.put(AttrType.crew_chuanjia, crew_chuanjia);				
		property.put(AttrType.crew_fdef, crew_fdef);					
		property.put(AttrType.crew_idef, crew_idef);					
		property.put(AttrType.crew_bdef, crew_bdef);					
		property.put(AttrType.stable_add, stable_add);					
		
		property.put(AttrType.acceleration_time_start,acceleration_time_start);
		property.put(AttrType.acceleration_value_start,acceleration_value_start);
		property.put(AttrType.acceleration_time_end,acceleration_time_end);
		property.put(AttrType.acceleration_value_end,acceleration_value_end);
		property.put(AttrType.acceleration_break_time_start,acceleration_break_time_start);
		property.put(AttrType.acceleration_break_value_start,acceleration_break_value_start);
		property.put(AttrType.acceleration_break_time_end,acceleration_break_time_end);
		property.put(AttrType.acceleration_break_value_end,acceleration_break_value_end);
		property.put(AttrType.r_acceleration_time_start,r_acceleration_time_start);
		property.put(AttrType.r_acceleration_value_start,r_acceleration_value_start);
		property.put(AttrType.r_acceleration_time_end,r_acceleration_time_end);
		property.put(AttrType.r_acceleration_value_end,r_acceleration_value_end);
		property.put(AttrType.r_acceleration_break_time_start,r_acceleration_break_time_start);
		property.put(AttrType.r_acceleration_break_value_start,r_acceleration_break_value_start);
		property.put(AttrType.r_acceleration_break_time_end,r_acceleration_break_time_end);
		property.put(AttrType.r_acceleration_break_value_end,r_acceleration_break_value_end);
		property.put(AttrType.r_stay_time_start,r_stay_time_start);
		property.put(AttrType.r_stay_value_start,r_stay_value_start);
		property.put(AttrType.r_stay_time_end,r_stay_time_end);
		property.put(AttrType.r_stay_value_end,r_stay_value_end);
		property.put(AttrType.r_stay_break_time_start,r_stay_break_time_start);
		property.put(AttrType.r_stay_break_value_start,r_stay_break_value_start);
		property.put(AttrType.r_stay_break_time_end,r_stay_break_time_end);
		property.put(AttrType.r_stay_break_value_end,r_stay_break_value_end);
		
		
		property.put(AttrType.weight,weight);
		property.put(AttrType.wheel_weight, wheel_weight);
		property.put(AttrType.positive_friction,positive_friction);
		property.put(AttrType.lateral_friction,lateral_friction);
		property.put(AttrType.fire_skew, fire_skew);
		
		property.put(AttrType.out_active_lock_angle, out_active_lock_angle);
		
		//==新改属性
		this.newProperty.put(AttrType.n_minHit, this.n_minHit);
		this.newProperty.put(AttrType.n_maxHit, this.n_maxHit);
		this.newProperty.put(AttrType.n_crit, this.n_crit);
		this.newProperty.put(AttrType.n_isNear, this.n_isNear);
		this.newProperty.put(AttrType.n_isFar, this.n_isFar);
		this.newProperty.put(AttrType.n_fireSpeed, this.n_fireSpeed);
		this.newProperty.put(AttrType.n_stab, this.n_stab);
		this.newProperty.put(AttrType.n_minPrecision, this.n_minPrecision);
		this.newProperty.put(AttrType.n_maxPrecision, this.n_maxPrecision);
		this.newProperty.put(AttrType.n_aimSpeed, this.n_aimSpeed);
		this.newProperty.put(AttrType.n_hpMax, this.n_hpMax);
		this.newProperty.put(AttrType.n_dr, this.n_dr);
		this.newProperty.put(AttrType.n_ArmorTurretFront, this.n_ArmorTurretFront);
		this.newProperty.put(AttrType.n_ArmorTurretMiddle, this.n_ArmorTurretMiddle);
		this.newProperty.put(AttrType.n_ArmorTurretbehind, this.n_ArmorTurretbehind);
		this.newProperty.put(AttrType.n_ArmorBodyFront, this.n_ArmorBodyFront);
		this.newProperty.put(AttrType.n_ArmorBodyMiddle, this.n_ArmorBodyMiddle);
		this.newProperty.put(AttrType.n_ArmorBodybehind, this.n_ArmorBodybehind);
		this.newProperty.put(AttrType.n_turrentSpeep, this.n_turrentSpeep);
		this.newProperty.put(AttrType.n_bodySpeed, this.n_bodySpeed);
		this.newProperty.put(AttrType.n_nimble, this.n_nimble);
		this.newProperty.put(AttrType.n_speed, this.n_speed);
		this.newProperty.put(AttrType.n_speedUp, this.n_speedUp);
		this.newProperty.put(AttrType.n_view, this.n_view);
		this.newProperty.put(AttrType.n_camouflage, this.n_camouflage);
		this.goodBadList.add(good1);
		this.goodBadList.add(good2);
		this.goodBadList.add(good3);
		this.goodBadList.add(bad1);
		this.goodBadList.add(bad2);
		this.goodBadList.add(bad3);
	}
	
	
	@Override
	public Integer getKey() {
		return this.id_i;
	}
	
	
	@Override
	public Map<AttrType, Float> getAttr(){
		return this.property;
	}


	public int getId_i() {
		return id_i;
	}


	public void setId_i(int id_i) {
		this.id_i = id_i;
	}


	public int getLevel_i() {
		return level_i;
	}


	public void setLevel_i(int level_i) {
		this.level_i = level_i;
	}


	public float getHp() {
		return hp;
	}


	public void setHp(float hp) {
		this.hp = hp;
	}


	public float getFdef() {
		return fdef;
	}


	public void setFdef(float fdef) {
		this.fdef = fdef;
	}


	public float getIdef() {
		return idef;
	}


	public void setIdef(float idef) {
		this.idef = idef;
	}


	public float getBdef() {
		return bdef;
	}


	public void setBdef(float bdef) {
		this.bdef = bdef;
	}


	public float getDerate() {
		return derate;
	}


	public void setDerate(float derate) {
		this.derate = derate;
	}


	public float getFdodge() {
		return fdodge;
	}


	public void setFdodge(float fdodge) {
		this.fdodge = fdodge;
	}


	public float getIdodge() {
		return idodge;
	}


	public void setIdodge(float idodge) {
		this.idodge = idodge;
	}


	public float getBdodge() {
		return bdodge;
	}


	public void setBdodge(float bdodge) {
		this.bdodge = bdodge;
	}


	public float getAtk() {
		return atk;
	}


	public void setAtk(float atk) {
		this.atk = atk;
	}


	public float getChuanjia() {
		return chuanjia;
	}


	public void setChuanjia(float chuanjia) {
		this.chuanjia = chuanjia;
	}


	public float getChuanjia_rat() {
		return chuanjia_rat;
	}


	public void setChuanjia_rat(float chuanjia_rat) {
		this.chuanjia_rat = chuanjia_rat;
	}


	public float getMul_chuanjia() {
		return mul_chuanjia;
	}


	public void setMul_chuanjia(float mul_chuanjia) {
		this.mul_chuanjia = mul_chuanjia;
	}


	public float getMul_dodge() {
		return mul_dodge;
	}


	public void setMul_dodge(float mul_dodge) {
		this.mul_dodge = mul_dodge;
	}


	public float getRange() {
		return range;
	}


	public void setRange(float range) {
		this.range = range;
	}


	public float getDanjia_time() {
		return danjia_time;
	}


	public void setDanjia_time(float danjia_time) {
		this.danjia_time = danjia_time;
	}


	public float getDanjia_num() {
		return danjia_num;
	}


	public void setDanjia_num(float danjia_num) {
		this.danjia_num = danjia_num;
	}


	public float getSpeed() {
		return speed;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}


	public float getRotate_speed() {
		return rotate_speed;
	}


	public void setRotate_speed(float rotate_speed) {
		this.rotate_speed = rotate_speed;
	}


	public float getCan_pre() {
		return can_pre;
	}


	public void setCan_pre(float can_pre) {
		this.can_pre = can_pre;
	}


	public float getFocal_radil_move() {
		return focal_radil_move;
	}


	public void setFocal_radil_move(float focal_radil_move) {
		this.focal_radil_move = focal_radil_move;
	}


	public float getFocal_radil_lock() {
		return focal_radil_lock;
	}


	public void setFocal_radil_lock(float focal_radil_lock) {
		this.focal_radil_lock = focal_radil_lock;
	}


	public float getFocal_radil_static() {
		return focal_radil_static;
	}


	public void setFocal_radil_static(float focal_radil_static) {
		this.focal_radil_static = focal_radil_static;
	}


	public float getFocal_radil_fire() {
		return focal_radil_fire;
	}


	public void setFocal_radil_fire(float focal_radil_fire) {
		this.focal_radil_fire = focal_radil_fire;
	}


	public float getFocal_speed_move() {
		return focal_speed_move;
	}


	public void setFocal_speed_move(float focal_speed_move) {
		this.focal_speed_move = focal_speed_move;
	}


	public float getFocal_speed_lock() {
		return focal_speed_lock;
	}


	public void setFocal_speed_lock(float focal_speed_lock) {
		this.focal_speed_lock = focal_speed_lock;
	}


	public float getFocal_speed_static() {
		return focal_speed_static;
	}


	public void setFocal_speed_static(float focal_speed_static) {
		this.focal_speed_static = focal_speed_static;
	}


	public float getFocal_speed_fire() {
		return focal_speed_fire;
	}


	public void setFocal_speed_fire(float focal_speed_fire) {
		this.focal_speed_fire = focal_speed_fire;
	}


	public float getCamera_ratate_speed() {
		return camera_ratate_speed;
	}


	public void setCamera_ratate_speed(float camera_ratate_speed) {
		this.camera_ratate_speed = camera_ratate_speed;
	}


	public float getFollowcamera_speed() {
		return followcamera_speed;
	}


	public void setFollowcamera_speed(float followcamera_speed) {
		this.followcamera_speed = followcamera_speed;
	}


	public float getBreath_time() {
		return breath_time;
	}


	public void setBreath_time(float breath_time) {
		this.breath_time = breath_time;
	}


	public float getBreath_mult_range() {
		return breath_mult_range;
	}


	public void setBreath_mult_range(float breath_mult_range) {
		this.breath_mult_range = breath_mult_range;
	}


	public float getFocal_distance_max() {
		return focal_distance_max;
	}


	public void setFocal_distance_max(float focal_distance_max) {
		this.focal_distance_max = focal_distance_max;
	}


	public float getFocal_distance_min() {
		return focal_distance_min;
	}


	public void setFocal_distance_min(float focal_distance_min) {
		this.focal_distance_min = focal_distance_min;
	}


	public float getActive_lock_angle() {
		return active_lock_angle;
	}


	public void setActive_lock_angle(float active_lock_angle) {
		this.active_lock_angle = active_lock_angle;
	}


	public float getOut_active_lock_angle() {
		return out_active_lock_angle;
	}


	public void setOut_active_lock_angle(float out_active_lock_angle) {
		this.out_active_lock_angle = out_active_lock_angle;
	}


	public float getCat_damed_rat() {
		return cat_damed_rat;
	}


	public void setCat_damed_rat(float cat_damed_rat) {
		this.cat_damed_rat = cat_damed_rat;
	}


	public float getCat_bre_rat() {
		return cat_bre_rat;
	}


	public void setCat_bre_rat(float cat_bre_rat) {
		this.cat_bre_rat = cat_bre_rat;
	}


	public float getFire_rat() {
		return fire_rat;
	}


	public void setFire_rat(float fire_rat) {
		this.fire_rat = fire_rat;
	}


	public float getSpeed_slow_rat() {
		return speed_slow_rat;
	}


	public void setSpeed_slow_rat(float speed_slow_rat) {
		this.speed_slow_rat = speed_slow_rat;
	}


	public float getCd_slow_rat() {
		return cd_slow_rat;
	}


	public void setCd_slow_rat(float cd_slow_rat) {
		this.cd_slow_rat = cd_slow_rat;
	}


	public float getView() {
		return view;
	}


	public void setView(float view) {
		this.view = view;
	}


	public float getDepression() {
		return depression;
	}


	public void setDepression(float depression) {
		this.depression = depression;
	}


	public float getElevation() {
		return elevation;
	}


	public void setElevation(float elevation) {
		this.elevation = elevation;
	}


	public float getZ_speed() {
		return z_speed;
	}


	public void setZ_speed(float z_speed) {
		this.z_speed = z_speed;
	}


	public float getS_aim() {
		return s_aim;
	}


	public void setS_aim(float s_aim) {
		this.s_aim = s_aim;
	}


	public float getZ_aim() {
		return z_aim;
	}


	public void setZ_aim(float z_aim) {
		this.z_aim = z_aim;
	}


	public float getAuto_lock_angle() {
		return auto_lock_angle;
	}


	public void setAuto_lock_angle(float auto_lock_angle) {
		this.auto_lock_angle = auto_lock_angle;
	}


	public float getOut_auto_lock_angle() {
		return out_auto_lock_angle;
	}


	public void setOut_auto_lock_angle(float out_auto_lock_angle) {
		this.out_auto_lock_angle = out_auto_lock_angle;
	}


	public float getHead_rotate_speed() {
		return head_rotate_speed;
	}


	public void setHead_rotate_speed(float head_rotate_speed) {
		this.head_rotate_speed = head_rotate_speed;
	}


	public float getHead_auuto_rotate_speed() {
		return head_auuto_rotate_speed;
	}


	public void setHead_auuto_rotate_speed(float head_auuto_rotate_speed) {
		this.head_auuto_rotate_speed = head_auuto_rotate_speed;
	}


	public float getAuto_lock_inertia_speed() {
		return auto_lock_inertia_speed;
	}


	public void setAuto_lock_inertia_speed(float auto_lock_inertia_speed) {
		this.auto_lock_inertia_speed = auto_lock_inertia_speed;
	}


	public float getLock_itervaltime() {
		return lock_itervaltime;
	}


	public void setLock_itervaltime(float lock_itervaltime) {
		this.lock_itervaltime = lock_itervaltime;
	}


	public float getCrew_hp() {
		return crew_hp;
	}


	public void setCrew_hp(float crew_hp) {
		this.crew_hp = crew_hp;
	}


	public float getCrew_act() {
		return crew_act;
	}


	public void setCrew_act(float crew_act) {
		this.crew_act = crew_act;
	}


	public float getCrew_rate() {
		return crew_rate;
	}


	public void setCrew_rate(float crew_rate) {
		this.crew_rate = crew_rate;
	}


	public float getCrew_speed() {
		return crew_speed;
	}


	public void setCrew_speed(float crew_speed) {
		this.crew_speed = crew_speed;
	}


	public float getCrew_chuanjia() {
		return crew_chuanjia;
	}


	public void setCrew_chuanjia(float crew_chuanjia) {
		this.crew_chuanjia = crew_chuanjia;
	}


	public float getCrew_fdef() {
		return crew_fdef;
	}


	public void setCrew_fdef(float crew_fdef) {
		this.crew_fdef = crew_fdef;
	}


	public float getCrew_idef() {
		return crew_idef;
	}


	public void setCrew_idef(float crew_idef) {
		this.crew_idef = crew_idef;
	}


	public float getCrew_bdef() {
		return crew_bdef;
	}


	public void setCrew_bdef(float crew_bdef) {
		this.crew_bdef = crew_bdef;
	}


	public float getStable_add() {
		return stable_add;
	}


	public void setStable_add(float stable_add) {
		this.stable_add = stable_add;
	}


	public float getFocalFireTime() {
		return focalFireTime;
	}


	public void setFocalFireTime(float focalFireTime) {
		this.focalFireTime = focalFireTime;
	}


	public float getAcceleration_time_start() {
		return acceleration_time_start;
	}


	public void setAcceleration_time_start(float acceleration_time_start) {
		this.acceleration_time_start = acceleration_time_start;
	}


	public float getAcceleration_value_start() {
		return acceleration_value_start;
	}


	public void setAcceleration_value_start(float acceleration_value_start) {
		this.acceleration_value_start = acceleration_value_start;
	}


	public float getAcceleration_time_end() {
		return acceleration_time_end;
	}


	public void setAcceleration_time_end(float acceleration_time_end) {
		this.acceleration_time_end = acceleration_time_end;
	}


	public float getAcceleration_value_end() {
		return acceleration_value_end;
	}


	public void setAcceleration_value_end(float acceleration_value_end) {
		this.acceleration_value_end = acceleration_value_end;
	}


	public float getAcceleration_break_time_start() {
		return acceleration_break_time_start;
	}


	public void setAcceleration_break_time_start(float acceleration_break_time_start) {
		this.acceleration_break_time_start = acceleration_break_time_start;
	}


	public float getAcceleration_break_value_start() {
		return acceleration_break_value_start;
	}


	public void setAcceleration_break_value_start(float acceleration_break_value_start) {
		this.acceleration_break_value_start = acceleration_break_value_start;
	}


	public float getAcceleration_break_time_end() {
		return acceleration_break_time_end;
	}


	public void setAcceleration_break_time_end(float acceleration_break_time_end) {
		this.acceleration_break_time_end = acceleration_break_time_end;
	}


	public float getAcceleration_break_value_end() {
		return acceleration_break_value_end;
	}


	public void setAcceleration_break_value_end(float acceleration_break_value_end) {
		this.acceleration_break_value_end = acceleration_break_value_end;
	}


	public float getR_acceleration_time_start() {
		return r_acceleration_time_start;
	}


	public void setR_acceleration_time_start(float r_acceleration_time_start) {
		this.r_acceleration_time_start = r_acceleration_time_start;
	}


	public float getR_acceleration_value_start() {
		return r_acceleration_value_start;
	}


	public void setR_acceleration_value_start(float r_acceleration_value_start) {
		this.r_acceleration_value_start = r_acceleration_value_start;
	}


	public float getR_acceleration_time_end() {
		return r_acceleration_time_end;
	}


	public void setR_acceleration_time_end(float r_acceleration_time_end) {
		this.r_acceleration_time_end = r_acceleration_time_end;
	}


	public float getR_acceleration_value_end() {
		return r_acceleration_value_end;
	}


	public void setR_acceleration_value_end(float r_acceleration_value_end) {
		this.r_acceleration_value_end = r_acceleration_value_end;
	}


	public float getR_acceleration_break_time_start() {
		return r_acceleration_break_time_start;
	}


	public void setR_acceleration_break_time_start(float r_acceleration_break_time_start) {
		this.r_acceleration_break_time_start = r_acceleration_break_time_start;
	}


	public float getR_acceleration_break_value_start() {
		return r_acceleration_break_value_start;
	}


	public void setR_acceleration_break_value_start(float r_acceleration_break_value_start) {
		this.r_acceleration_break_value_start = r_acceleration_break_value_start;
	}


	public float getR_acceleration_break_time_end() {
		return r_acceleration_break_time_end;
	}


	public void setR_acceleration_break_time_end(float r_acceleration_break_time_end) {
		this.r_acceleration_break_time_end = r_acceleration_break_time_end;
	}


	public float getR_acceleration_break_value_end() {
		return r_acceleration_break_value_end;
	}


	public void setR_acceleration_break_value_end(float r_acceleration_break_value_end) {
		this.r_acceleration_break_value_end = r_acceleration_break_value_end;
	}


	public float getR_stay_time_start() {
		return r_stay_time_start;
	}


	public void setR_stay_time_start(float r_stay_time_start) {
		this.r_stay_time_start = r_stay_time_start;
	}


	public float getR_stay_value_start() {
		return r_stay_value_start;
	}


	public void setR_stay_value_start(float r_stay_value_start) {
		this.r_stay_value_start = r_stay_value_start;
	}


	public float getR_stay_time_end() {
		return r_stay_time_end;
	}


	public void setR_stay_time_end(float r_stay_time_end) {
		this.r_stay_time_end = r_stay_time_end;
	}


	public float getR_stay_value_end() {
		return r_stay_value_end;
	}


	public void setR_stay_value_end(float r_stay_value_end) {
		this.r_stay_value_end = r_stay_value_end;
	}


	public float getR_stay_break_time_start() {
		return r_stay_break_time_start;
	}


	public void setR_stay_break_time_start(float r_stay_break_time_start) {
		this.r_stay_break_time_start = r_stay_break_time_start;
	}


	public float getR_stay_break_value_start() {
		return r_stay_break_value_start;
	}


	public void setR_stay_break_value_start(float r_stay_break_value_start) {
		this.r_stay_break_value_start = r_stay_break_value_start;
	}


	public float getR_stay_break_time_end() {
		return r_stay_break_time_end;
	}


	public void setR_stay_break_time_end(float r_stay_break_time_end) {
		this.r_stay_break_time_end = r_stay_break_time_end;
	}


	public float getR_stay_break_value_end() {
		return r_stay_break_value_end;
	}


	public void setR_stay_break_value_end(float r_stay_break_value_end) {
		this.r_stay_break_value_end = r_stay_break_value_end;
	}


	public float getStrength() {
		return strength;
	}


	public void setStrength(float strength) {
		this.strength = strength;
	}


	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}


	public float getPositive_friction() {
		return positive_friction;
	}


	public void setPositive_friction(float positive_friction) {
		this.positive_friction = positive_friction;
	}


	public float getLateral_friction() {
		return lateral_friction;
	}


	public void setLateral_friction(float lateral_friction) {
		this.lateral_friction = lateral_friction;
	}


	public float getFire_skew() {
		return fire_skew;
	}


	public void setFire_skew(float fire_skew) {
		this.fire_skew = fire_skew;
	}


	public float getWheel_weight() {
		return wheel_weight;
	}


	public void setWheel_weight(float wheel_weight) {
		this.wheel_weight = wheel_weight;
	}


	public float getMatchScore() {
		return matchScore;
	}


	public void setMatchScore(float matchScore) {
		this.matchScore = matchScore;
	}
	public float getN_minHit() {
		return n_minHit;
	}

	public void setN_minHit(float n_minHit) {
		this.n_minHit = n_minHit;
	}

	public float getN_maxHit() {
		return n_maxHit;
	}

	public void setN_maxHit(float n_maxHit) {
		this.n_maxHit = n_maxHit;
	}

	public float getN_crit() {
		return n_crit;
	}

	public void setN_crit(float n_crit) {
		this.n_crit = n_crit;
	}



	public float getN_isNear() {
		return n_isNear;
	}


	public void setN_isNear(float n_isNear) {
		this.n_isNear = n_isNear;
	}


	public float getN_isFar() {
		return n_isFar;
	}


	public void setN_isFar(float n_isFar) {
		this.n_isFar = n_isFar;
	}


	public float getN_fireSpeed() {
		return n_fireSpeed;
	}

	public void setN_fireSpeed(float n_fireSpeed) {
		this.n_fireSpeed = n_fireSpeed;
	}

	public float getN_stab() {
		return n_stab;
	}

	public void setN_stab(float n_stab) {
		this.n_stab = n_stab;
	}

	public float getN_minPrecision() {
		return n_minPrecision;
	}

	public void setN_minPrecision(float n_minPrecision) {
		this.n_minPrecision = n_minPrecision;
	}

	public float getN_maxPrecision() {
		return n_maxPrecision;
	}

	public void setN_maxPrecision(float n_maxPrecision) {
		this.n_maxPrecision = n_maxPrecision;
	}

	public float getN_aimSpeed() {
		return n_aimSpeed;
	}

	public void setN_aimSpeed(float n_aimSpeed) {
		this.n_aimSpeed = n_aimSpeed;
	}

	public float getN_hpMax() {
		return n_hpMax;
	}

	public void setN_hpMax(float n_hpMax) {
		this.n_hpMax = n_hpMax;
	}

	public float getN_dr() {
		return n_dr;
	}

	public void setN_dr(float n_dr) {
		this.n_dr = n_dr;
	}

	public float getN_ArmorTurretFront() {
		return n_ArmorTurretFront;
	}

	public void setN_ArmorTurretFront(float n_ArmorTurretFront) {
		this.n_ArmorTurretFront = n_ArmorTurretFront;
	}

	public float getN_ArmorTurretMiddle() {
		return n_ArmorTurretMiddle;
	}

	public void setN_ArmorTurretMiddle(float n_ArmorTurretMiddle) {
		this.n_ArmorTurretMiddle = n_ArmorTurretMiddle;
	}


	public float getN_ArmorTurretbehind() {
		return n_ArmorTurretbehind;
	}

	public void setN_ArmorTurretbehind(float n_ArmorTurretbehind) {
		this.n_ArmorTurretbehind = n_ArmorTurretbehind;
	}


	public float getN_ArmorBodyFront() {
		return n_ArmorBodyFront;
	}

	public void setN_ArmorBodyFront(float n_ArmorBodyFront) {
		this.n_ArmorBodyFront = n_ArmorBodyFront;
	}

	public float getN_ArmorBodyMiddle() {
		return n_ArmorBodyMiddle;
	}

	public void setN_ArmorBodyMiddle(float n_ArmorBodyMiddle) {
		this.n_ArmorBodyMiddle = n_ArmorBodyMiddle;
	}


	public float getN_ArmorBodybehind() {
		return n_ArmorBodybehind;
	}

	public void setN_ArmorBodybehind(float n_ArmorBodybehind) {
		this.n_ArmorBodybehind = n_ArmorBodybehind;
	}


	public float getN_turrentSpeep() {
		return n_turrentSpeep;
	}

	public void setN_turrentSpeep(float n_turrentSpeep) {
		this.n_turrentSpeep = n_turrentSpeep;
	}


	public float getN_bodySpeed() {
		return n_bodySpeed;
	}

	public void setN_bodySpeed(float n_bodySpeed) {
		this.n_bodySpeed = n_bodySpeed;
	}


	public float getN_nimble() {
		return n_nimble;
	}

	public void setN_nimble(float n_nimble) {
		this.n_nimble = n_nimble;
	}


	public float getN_speed() {
		return n_speed;
	}

	public void setN_speed(float n_speed) {
		this.n_speed = n_speed;
	}

	public float getN_speedUp() {
		return n_speedUp;
	}

	public void setN_speedUp(float n_speedUp) {
		this.n_speedUp = n_speedUp;
	}


	public float getN_view() {
		return n_view;
	}

	public void setN_view(float n_view) {
		this.n_view = n_view;
	}


	public float getN_camouflage() {
		return n_camouflage;
	}

	public void setN_camouflage(float n_camouflage) {
		this.n_camouflage = n_camouflage;
	}

	public String getGood1() {
		return good1;
	}

	public void setGood1(String good1) {
		this.good1 = good1;
	}

	public String getGood2() {
		return good2;
	}

	public void setGood2(String good2) {
		this.good2 = good2;
	}

	public String getGood3() {
		return good3;
	}

	public void setGood3(String good3) {
		this.good3 = good3;
	}

	public String getBad1() {
		return bad1;
	}

	public void setBad1(String bad1) {
		this.bad1 = bad1;
	}

	public String getBad2() {
		return bad2;
	}

	public void setBad2(String bad2) {
		this.bad2 = bad2;
	}

	public String getBad3() {
		return bad3;
	}

	public void setBad3(String bad3) {
		this.bad3 = bad3;
	}

	public List<String> getGoodBadList() {
		return goodBadList;
	}


	public void setGoodBadList(List<String> goodBadList) {
		this.goodBadList = goodBadList;
	}


	@Override
	public Map<AttrType, Float> getNewAttr() {
		return this.newProperty;
	}

}
