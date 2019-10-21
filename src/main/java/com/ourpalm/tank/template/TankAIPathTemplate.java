package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.type.AIPointType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.peshe.AIPointPeshe;

public class TankAIPathTemplate {

	private int id;		//索引ID
	private int _1;		//旗杆位置概率
	private int _2; 	//旗杆外围概率
	private int _3;		//中部偏内概率
	private int _4;		//中部概率
	private int _5;		//外围中部概率
	private int _6;		//外围概率
	private int _7; 	//最上面
	
	private List<AIPointPeshe> pointList = new ArrayList<>();
	
	public void init(){
		this.build(AIPointType.flag, _1);
		this.build(AIPointType.flag_outside, _2);
		this.build(AIPointType.middle_inside, _3);
		this.build(AIPointType.middle, _4);
		this.build(AIPointType.outside_middle, _5);
		this.build(AIPointType.outside, _6);
		this.build(AIPointType.up, _7);
	}
	
	
	/** 随机行径路径类型 */
	public int randomPointType(){
		AIPointPeshe peshe = RandomUtil.getPeshe(pointList);
		if(peshe == null){
			return AIPointType.outside.type;
		}
		return peshe.getType().type;
	}
	
	
	
	private void build(AIPointType type, int rate){
		AIPointPeshe peshe = new AIPointPeshe();
		peshe.setType(type);
		peshe.setGon(rate);
		pointList.add(peshe);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int get_1() {
		return _1;
	}
	public void set_1(int _1) {
		this._1 = _1;
	}
	public int get_2() {
		return _2;
	}
	public void set_2(int _2) {
		this._2 = _2;
	}
	public int get_3() {
		return _3;
	}
	public void set_3(int _3) {
		this._3 = _3;
	}
	public int get_4() {
		return _4;
	}
	public void set_4(int _4) {
		this._4 = _4;
	}
	public int get_5() {
		return _5;
	}
	public void set_5(int _5) {
		this._5 = _5;
	}
	public int get_6() {
		return _6;
	}
	public void set_6(int _6) {
		this._6 = _6;
	}
	public int get_7() {
		return _7;
	}
	public void set_7(int _7) {
		this._7 = _7;
	}
}
