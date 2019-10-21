package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.type.EffectType;
import com.ourpalm.tank.util.peshe.TankEffectPeshe;

public class TankTemplate extends AbstractAttribute{

	private String name_s;
	private int tankType_i;			//坦克类型
	private int tank_type;//金币车 珍藏车 稀有车
	private int country_i;			//国家
	private float roleTankExp;		//获得全局坦克经验百分比
	private int tired;				//疲劳
	
	private int revolve_i;			//是否可转炮塔
	private float sliding_speed;	//滑屏速度
	
	private int shellType_i;		//使用的炮弹类型
	private int needTankId_i; //前置坦克

	private int needIron;//购买需要银币 
	private int needExp;//研发需要经验
	
	private int showBattleNum;
	
	private float sidewayFrictionExtremumSlip; //车身旋转极值
	private float sideways_extrenum_value;
	private float sideways_asymptote_slip;
	private float sideways_asymptote_value;
	private float forward_extrenum_slip;
	private float forward_extrenum_value;
	private float forward_asymptote_slip;
	private float forward_asymptote_value;
	
	private float wheel_suspension_distance;
	private float wheel_suspension_spring;
	private float wheel_suspension_damper;
	private float wheel_suspension_target_postion;
	private float tank_on_fire_force;
	private float tank_on_beat_force;
	private float autoBrakeStarSpeed;
	private float autoBrakeEndSpeed;
	private float autoBrakeForceScale;
	private float wheel_radius;
	
	private float  tank_top_speed;
	private float  tank_top_speed_back;
	private float  up_hill_start_angle;
	private float  up_hill_end_angle;
	private float  up_hill_mid_angle;
	private float  up_hill_mid_power;
	
	private String icon_s;			//图标
	private String model_s;			//模型
	private int open_money_i;		//激活金币
	
	private int parts1_i;
	private int parts2_i;
	private int parts3_i;
	private int parts4_i;
	private int parts5_i;
	private int parts6_i;
	private int parts7_i;
	
	//两件精英配件合成后，所衍生属性
	private int sp_pro1_i;
	private float sp_pro1_add_num_i;
	private int sp_pro2_i;
	private float sp_pro2_add_num_i;
	
	private int cat_damed_buff;		//火炮受损buff 
	private int cat_bre_buff;		//履带受损buff
	private int fire_buff;			//机身起火buff
	private int speed_slow_buff;	//发动机受损buff
	private int cd_slow_buff;		//弹药架受损buff
	
	private int level_i;  //坦克级别
	
							

	private float maxAngularDegree;	//每秒角度	
	private float steerTorque;//辅助转向，值越大转向越快
	private float wheelLROffset;	//车轮位置往外偏移，增加车轮距
	private float engineTorque;	//动力
	private float brakeTorque;	//刹车阻力
	private float MassCenterOffsetX;	//车子重心偏移X
	private float MassCenterOffsetY;	//车子重心偏移Y
	private float MassCenterOffsetZ;//车子重心偏移Z
	private float sideways_stiffness;//正面僵直
	private float forward_stiffness;//侧僵直
	private float engineTorqueK;//动力系数
	private float wheelDampingRate;//车轮阻力
	private float elevation1;//俯视角
	//两件精英配件合成后，所衍生属性
	private Map<AttrType, Float> additionalAttrMap = new HashMap<>();
	
	//成员属性加成折扣比率(成员对应的相关属性，根据坦克表中的成员加成率打折后加到坦克身上)
	private Map<AttrType, Float> memberAttrMap = new HashMap<>();
	
	public void init(){
		super.init();
		
		//坦克是否可旋转炮塔属性
		this.property.put(AttrType.had_revolve, (float)this.revolve_i);
		this.property.put(AttrType.sliding_speed, this.sliding_speed);
		this.property.put(AttrType.sidewayFrictionExtremumSlip, this.sidewayFrictionExtremumSlip);
		this.property.put(AttrType.sideways_extrenum_value, this.sideways_extrenum_value);
		this.property.put(AttrType.sideways_asymptote_slip, this.sideways_asymptote_slip);
		this.property.put(AttrType.sideways_asymptote_value, this.sideways_asymptote_value);
		this.property.put(AttrType.forward_extrenum_slip, this.forward_extrenum_slip);
		this.property.put(AttrType.forward_extrenum_value, this.forward_extrenum_value);
		this.property.put(AttrType.forward_asymptote_slip, this.forward_asymptote_slip);
		this.property.put(AttrType.forward_asymptote_value, this.forward_asymptote_value);
		this.property.put(AttrType.auto_brake_star_speed, this.autoBrakeStarSpeed);
		this.property.put(AttrType.auto_brake_end_speed, this.autoBrakeEndSpeed);
		this.property.put(AttrType.auto_brake_force_scale, this.autoBrakeForceScale);
		this.property.put(AttrType.wheel_suspension_distance, this.wheel_suspension_distance);
		this.property.put(AttrType.wheel_suspension_spring, this.wheel_suspension_spring);
		this.property.put(AttrType.wheel_suspension_damper, this.wheel_suspension_damper);
		this.property.put(AttrType.wheel_suspension_target_postion, this.wheel_suspension_target_postion);
		this.property.put(AttrType.tank_on_fire_force, this.tank_on_fire_force);
		this.property.put(AttrType.tank_on_beat_force, this.tank_on_beat_force);
		this.property.put(AttrType.wheel_radius, this.wheel_radius);
		
		this.property.put(AttrType.tank_top_speed, this.tank_top_speed);
		this.property.put(AttrType.tank_top_speed_back, this.tank_top_speed_back);
		
		this.property.put(AttrType.up_hill_start_angle, this.up_hill_start_angle);
		this.property.put(AttrType.up_hill_end_angle, this.up_hill_end_angle);
		this.property.put(AttrType.up_hill_mid_angle, this.up_hill_mid_angle);
		this.property.put(AttrType.up_hill_mid_power, this.up_hill_mid_power);
		
		this.property.put(AttrType.maxAngularDegree, this.maxAngularDegree);
		this.property.put(AttrType.steerTorque, this.steerTorque);
		this.property.put(AttrType.wheelLROffset, this.wheelLROffset);
		this.property.put(AttrType.engineTorque, this.engineTorque);
		this.property.put(AttrType.brakeTorque, this.brakeTorque);
		this.property.put(AttrType.MassCenterOffsetX, this.MassCenterOffsetX);
		this.property.put(AttrType.MassCenterOffsetY, this.MassCenterOffsetY);
		this.property.put(AttrType.MassCenterOffsetZ, this.MassCenterOffsetZ);
		this.property.put(AttrType.sideways_stiffness, this.sideways_stiffness);
		this.property.put(AttrType.forward_stiffness, this.forward_stiffness);
		this.property.put(AttrType.engineTorqueK, this.engineTorqueK);
		this.property.put(AttrType.wheelDampingRate, this.wheelDampingRate);
		this.property.put(AttrType.elevation1, this.elevation1);
		
		
		//初始化衍生属性
		this.buildAdditionalAttr(sp_pro1_i, sp_pro1_add_num_i);
		this.buildAdditionalAttr(sp_pro2_i, sp_pro2_add_num_i);
		
		memberAttrMap.put(AttrType.maxHp, this.crew_hp);
		memberAttrMap.put(AttrType.atk, this.crew_act);
		memberAttrMap.put(AttrType.danjia_time, this.crew_rate);
		memberAttrMap.put(AttrType.speed, this.crew_speed);
		memberAttrMap.put(AttrType.chuanjia, this.crew_chuanjia);
		memberAttrMap.put(AttrType.fdef, this.crew_fdef);
		memberAttrMap.put(AttrType.idef, this.crew_idef);
		memberAttrMap.put(AttrType.bdef, this.crew_bdef);
	}
	
	public Map<AttrType, Float> getAdditionalAttrMap(){
		return new HashMap<>(this.additionalAttrMap);
	}
	
	
	private void buildAdditionalAttr(int attr, float value){
		AttrType attrType = AttrType.valueOf(attr);
		if(attrType == null){
			return ;
		}
		this.additionalAttrMap.put(attrType, value);
	}
	
	
	

	
	//获取受损buff
	public int getHitPartBuff(TankEffectPeshe peshe){
		if(peshe == null){
			return -1;
		}
		EffectType effect = peshe.getEffect();
		if(effect == null){
			return -1;
		}
		switch(effect){
			case ammo_rack_hit : return cd_slow_buff;
			case gun_hit : return cat_damed_buff;
			case track_hit :  return cat_bre_buff ;
			case engine_hit : return speed_slow_buff;
			case body_fire : return fire_buff;
			default : break;
		}
		return -1;
	}
	
	public String getName_s() {
		return name_s;
	}
	public void setName_s(String name_s) {
		this.name_s = name_s;
	}
	public int getTankType_i() {
		return tankType_i;
	}
	public void setTankType_i(int tankType_i) {
		this.tankType_i = tankType_i;
	}
	public int getCountry_i() {
		return country_i;
	}
	public void setCountry_i(int country_i) {
		this.country_i = country_i;
	}
	public String getIcon_s() {
		return icon_s;
	}
	public void setIcon_s(String icon_s) {
		this.icon_s = icon_s;
	}
	public String getModel_s() {
		return model_s;
	}
	public void setModel_s(String model_s) {
		this.model_s = model_s;
	}
	public int getOpen_money_i() {
		return open_money_i;
	}
	public void setOpen_money_i(int open_money_i) {
		this.open_money_i = open_money_i;
	}
	public int getParts1_i() {
		return parts1_i;
	}
	public void setParts1_i(int parts1_i) {
		this.parts1_i = parts1_i;
	}
	public int getParts2_i() {
		return parts2_i;
	}
	public void setParts2_i(int parts2_i) {
		this.parts2_i = parts2_i;
	}
	public int getParts3_i() {
		return parts3_i;
	}
	public void setParts3_i(int parts3_i) {
		this.parts3_i = parts3_i;
	}
	public int getParts4_i() {
		return parts4_i;
	}
	public void setParts4_i(int parts4_i) {
		this.parts4_i = parts4_i;
	}

	public int getParts5_i() {
		return parts5_i;
	}

	public void setParts5_i(int parts5_i) {
		this.parts5_i = parts5_i;
	}

	public int getParts6_i() {
		return parts6_i;
	}

	public void setParts6_i(int parts6_i) {
		this.parts6_i = parts6_i;
	}

	public int getParts7_i() {
		return parts7_i;
	}

	public void setParts7_i(int parts7_i) {
		this.parts7_i = parts7_i;
	}

	public int getNeedTankId_i() {
		return needTankId_i;
	}

	public void setNeedTankId_i(int needTankId_i) {
		this.needTankId_i = needTankId_i;
	}

	public int getCat_damed_buff() {
		return cat_damed_buff;
	}

	public void setCat_damed_buff(int cat_damed_buff) {
		this.cat_damed_buff = cat_damed_buff;
	}

	public int getCat_bre_buff() {
		return cat_bre_buff;
	}

	public void setCat_bre_buff(int cat_bre_buff) {
		this.cat_bre_buff = cat_bre_buff;
	}

	public int getFire_buff() {
		return fire_buff;
	}

	public void setFire_buff(int fire_buff) {
		this.fire_buff = fire_buff;
	}

	public int getSpeed_slow_buff() {
		return speed_slow_buff;
	}

	public void setSpeed_slow_buff(int speed_slow_buff) {
		this.speed_slow_buff = speed_slow_buff;
	}

	public int getCd_slow_buff() {
		return cd_slow_buff;
	}

	public void setCd_slow_buff(int cd_slow_buff) {
		this.cd_slow_buff = cd_slow_buff;
	}

	public int getShellType_i() {
		return shellType_i;
	}

	public void setShellType_i(int shellType_i) {
		this.shellType_i = shellType_i;
	}

	public int getSp_pro1_i() {
		return sp_pro1_i;
	}

	public void setSp_pro1_i(int sp_pro1_i) {
		this.sp_pro1_i = sp_pro1_i;
	}

	public float getSp_pro1_add_num_i() {
		return sp_pro1_add_num_i;
	}

	public void setSp_pro1_add_num_i(float sp_pro1_add_num_i) {
		this.sp_pro1_add_num_i = sp_pro1_add_num_i;
	}

	public int getSp_pro2_i() {
		return sp_pro2_i;
	}

	public void setSp_pro2_i(int sp_pro2_i) {
		this.sp_pro2_i = sp_pro2_i;
	}

	public float getSp_pro2_add_num_i() {
		return sp_pro2_add_num_i;
	}

	public void setSp_pro2_add_num_i(float sp_pro2_add_num_i) {
		this.sp_pro2_add_num_i = sp_pro2_add_num_i;
	}
	
	public Map<AttrType, Float> getMemberAttrMap(){
		return this.memberAttrMap;
	}
	
	public int getRevolve_i() {
		return revolve_i;
	}
	public void setRevolve_i(int revolve_i) {
		this.revolve_i = revolve_i;
	}
	public float getSliding_speed() {
		return sliding_speed;
	}
	public void setSliding_speed(float sliding_speed) {
		this.sliding_speed = sliding_speed;
	}
	public float getRoleTankExp() {
		return roleTankExp;
	}
	public void setRoleTankExp(float roleTankExp) {
		this.roleTankExp = roleTankExp;
	}


	public float getSideways_extrenum_value() {
		return sideways_extrenum_value;
	}


	public void setSideways_extrenum_value(float sideways_extrenum_value) {
		this.sideways_extrenum_value = sideways_extrenum_value;
	}


	public float getSideways_asymptote_slip() {
		return sideways_asymptote_slip;
	}


	public void setSideways_asymptote_slip(float sideways_asymptote_slip) {
		this.sideways_asymptote_slip = sideways_asymptote_slip;
	}


	public float getSideways_asymptote_value() {
		return sideways_asymptote_value;
	}


	public void setSideways_asymptote_value(float sideways_asymptote_value) {
		this.sideways_asymptote_value = sideways_asymptote_value;
	}


	public float getForward_extrenum_slip() {
		return forward_extrenum_slip;
	}


	public void setForward_extrenum_slip(float forward_extrenum_slip) {
		this.forward_extrenum_slip = forward_extrenum_slip;
	}
	public float getForward_extrenum_value() {
		return forward_extrenum_value;
	}
	public void setForward_extrenum_value(float forward_extrenum_value) {
		this.forward_extrenum_value = forward_extrenum_value;
	}
	public float getForward_asymptote_slip() {
		return forward_asymptote_slip;
	}
	public void setForward_asymptote_slip(float forward_asymptote_slip) {
		this.forward_asymptote_slip = forward_asymptote_slip;
	}
	public float getForward_asymptote_value() {
		return forward_asymptote_value;
	}
	public void setForward_asymptote_value(float forward_asymptote_value) {
		this.forward_asymptote_value = forward_asymptote_value;
	}
	public float getSidewayFrictionExtremumSlip() {
		return sidewayFrictionExtremumSlip;
	}
	public void setSidewayFrictionExtremumSlip(float sidewayFrictionExtremumSlip) {
		this.sidewayFrictionExtremumSlip = sidewayFrictionExtremumSlip;
	}
	public float getAutoBrakeStarSpeed() {
		return autoBrakeStarSpeed;
	}
	public void setAutoBrakeStarSpeed(float autoBrakeStarSpeed) {
		this.autoBrakeStarSpeed = autoBrakeStarSpeed;
	}
	public float getAutoBrakeEndSpeed() {
		return autoBrakeEndSpeed;
	}
	public void setAutoBrakeEndSpeed(float autoBrakeEndSpeed) {
		this.autoBrakeEndSpeed = autoBrakeEndSpeed;
	}
	public float getAutoBrakeForceScale() {
		return autoBrakeForceScale;
	}
	public void setAutoBrakeForceScale(float autoBrakeForceScale) {
		this.autoBrakeForceScale = autoBrakeForceScale;
	}
	public int getTired() {
		return tired;
	}
	public void setTired(int tired) {
		this.tired = tired;
	}
	public float getWheel_suspension_distance() {
		return wheel_suspension_distance;
	}
	public void setWheel_suspension_distance(float wheel_suspension_distance) {
		this.wheel_suspension_distance = wheel_suspension_distance;
	}
	public float getWheel_suspension_spring() {
		return wheel_suspension_spring;
	}
	public void setWheel_suspension_spring(float wheel_suspension_spring) {
		this.wheel_suspension_spring = wheel_suspension_spring;
	}
	public float getWheel_suspension_damper() {
		return wheel_suspension_damper;
	}
	public void setWheel_suspension_damper(float wheel_suspension_damper) {
		this.wheel_suspension_damper = wheel_suspension_damper;
	}
	public float getWheel_suspension_target_postion() {
		return wheel_suspension_target_postion;
	}
	public void setWheel_suspension_target_postion(float wheel_suspension_target_postion) {
		this.wheel_suspension_target_postion = wheel_suspension_target_postion;
	}


	public float getTank_on_fire_force() {
		return tank_on_fire_force;
	}


	public void setTank_on_fire_force(float tank_on_fire_force) {
		this.tank_on_fire_force = tank_on_fire_force;
	}


	public float getTank_on_beat_force() {
		return tank_on_beat_force;
	}


	public void setTank_on_beat_force(float tank_on_beat_force) {
		this.tank_on_beat_force = tank_on_beat_force;
	}


	public float getWheel_radius() {
		return wheel_radius;
	}


	public void setWheel_radius(float wheel_radius) {
		this.wheel_radius = wheel_radius;
	}


	public float getTank_top_speed() {
		return tank_top_speed;
	}


	public void setTank_top_speed(float tank_top_speed) {
		this.tank_top_speed = tank_top_speed;
	}


	public float getTank_top_speed_back() {
		return tank_top_speed_back;
	}


	public void setTank_top_speed_back(float tank_top_speed_back) {
		this.tank_top_speed_back = tank_top_speed_back;
	}


	public float getUp_hill_start_angle() {
		return up_hill_start_angle;
	}


	public void setUp_hill_start_angle(float up_hill_start_angle) {
		this.up_hill_start_angle = up_hill_start_angle;
	}


	public float getUp_hill_end_angle() {
		return up_hill_end_angle;
	}


	public void setUp_hill_end_angle(float up_hill_end_angle) {
		this.up_hill_end_angle = up_hill_end_angle;
	}


	public float getUp_hill_mid_angle() {
		return up_hill_mid_angle;
	}


	public void setUp_hill_mid_angle(float up_hill_mid_angle) {
		this.up_hill_mid_angle = up_hill_mid_angle;
	}


	public float getUp_hill_mid_power() {
		return up_hill_mid_power;
	}


	public void setUp_hill_mid_power(float up_hill_mid_power) {
		this.up_hill_mid_power = up_hill_mid_power;
	}


	public int getLevel_i() {
		return level_i;
	}


	public void setLevel_i(int level_i) {
		this.level_i = level_i;
	}

	public int getTank_type() {
		return tank_type;
	}

	public void setTank_type(int tank_type) {
		this.tank_type = tank_type;
	}

	public int getNeedIron() {
		return needIron;
	}

	public void setNeedIron(int needIron) {
		this.needIron = needIron;
	}

	public int getNeedExp() {
		return needExp;
	}

	public void setNeedExp(int needExp) {
		this.needExp = needExp;
	}

	public float getMaxAngularDegree() {
		return maxAngularDegree;
	}

	public void setMaxAngularDegree(float maxAngularDegree) {
		this.maxAngularDegree = maxAngularDegree;
	}

	public float getSteerTorque() {
		return steerTorque;
	}

	public void setSteerTorque(float steerTorque) {
		this.steerTorque = steerTorque;
	}

	public float getWheelLROffset() {
		return wheelLROffset;
	}

	public void setWheelLROffset(float wheelLROffset) {
		this.wheelLROffset = wheelLROffset;
	}

	public float getEngineTorque() {
		return engineTorque;
	}

	public void setEngineTorque(float engineTorque) {
		this.engineTorque = engineTorque;
	}

	public float getBrakeTorque() {
		return brakeTorque;
	}

	public void setBrakeTorque(float brakeTorque) {
		this.brakeTorque = brakeTorque;
	}

	public float getMassCenterOffsetX() {
		return MassCenterOffsetX;
	}

	public void setMassCenterOffsetX(float massCenterOffsetX) {
		MassCenterOffsetX = massCenterOffsetX;
	}

	public float getMassCenterOffsetY() {
		return MassCenterOffsetY;
	}

	public void setMassCenterOffsetY(float massCenterOffsetY) {
		MassCenterOffsetY = massCenterOffsetY;
	}

	public float getMassCenterOffsetZ() {
		return MassCenterOffsetZ;
	}

	public void setMassCenterOffsetZ(float massCenterOffsetZ) {
		MassCenterOffsetZ = massCenterOffsetZ;
	}

	public float getSideways_stiffness() {
		return sideways_stiffness;
	}

	public void setSideways_stiffness(float sideways_stiffness) {
		this.sideways_stiffness = sideways_stiffness;
	}

	public float getForward_stiffness() {
		return forward_stiffness;
	}

	public void setForward_stiffness(float forward_stiffness) {
		this.forward_stiffness = forward_stiffness;
	}

	public void setAdditionalAttrMap(Map<AttrType, Float> additionalAttrMap) {
		this.additionalAttrMap = additionalAttrMap;
	}

	public void setMemberAttrMap(Map<AttrType, Float> memberAttrMap) {
		this.memberAttrMap = memberAttrMap;
	}

	public float getEngineTorqueK() {
		return engineTorqueK;
	}

	public void setEngineTorqueK(float engineTorqueK) {
		this.engineTorqueK = engineTorqueK;
	}

	public float getWheelDampingRate() {
		return wheelDampingRate;
	}

	public void setWheelDampingRate(float wheelDampingRate) {
		this.wheelDampingRate = wheelDampingRate;
	}

	public int getShowBattleNum() {
		return showBattleNum;
	}

	public void setShowBattleNum(int showBattleNum) {
		this.showBattleNum = showBattleNum;
	}

	public float getElevation1() {
		return elevation1;
	}

	public void setElevation1(float elevation1) {
		this.elevation1 = elevation1;
	}



	
}
