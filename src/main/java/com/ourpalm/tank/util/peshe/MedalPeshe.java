package com.ourpalm.tank.util.peshe;

public class MedalPeshe extends Peshe {

	private int medalId;
	
	public MedalPeshe(int medalId, int gon){
		this.medalId = medalId;
		this.gon = gon;
	}
	
	public void setMedalId(int medalId){
		this.medalId = medalId;
	}
	
	public int getMedalId(){
		return this.medalId;
	}
}
