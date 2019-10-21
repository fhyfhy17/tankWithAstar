package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

public class BattleTeamWeightTemplate {
	
	private float _1;
	private float _2;
	private float _3;
	private float _4;
	private float _5;
	
	private List<Float> weights = new ArrayList<>();
	
	public void init(){
		weights.add(_1);
		weights.add(_2);
		weights.add(_3);
		weights.add(_4);
		weights.add(_5);
	}
	
	public List<Float> getWeights(){
		return new ArrayList<>(weights);
	}
	
	public float get_1() {
		return _1;
	}
	public void set_1(float _1) {
		this._1 = _1;
	}
	public float get_2() {
		return _2;
	}
	public void set_2(float _2) {
		this._2 = _2;
	}
	public float get_3() {
		return _3;
	}
	public void set_3(float _3) {
		this._3 = _3;
	}
	public float get_4() {
		return _4;
	}
	public void set_4(float _4) {
		this._4 = _4;
	}
	public float get_5() {
		return _5;
	}
	public void set_5(float _5) {
		this._5 = _5;
	}
}
