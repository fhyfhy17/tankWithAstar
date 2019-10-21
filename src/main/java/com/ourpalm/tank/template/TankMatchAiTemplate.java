package com.ourpalm.tank.template;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;

public class TankMatchAiTemplate {

	private int id;					//军衔ID
	private String rankAiPros;		//军衔排行AI个数百分比
	private String aiPros;			//对战模式AI个数百分比
	
	public void init(){
		if(Util.isEmpty(aiPros)){
			LogCore.startup.error("aiPros = {} 没有配置地图信息");
			return ;
		}
		try{
			for(String pro : aiPros.split(Cat.comma)){
				if(Util.isEmpty(pro)){
					continue;
				}
				Float.parseFloat(pro);
			}
		}catch(Exception e){
			LogCore.startup.error("id = {} aiPros异常...", id);
		}
		
		try{
			for(String pro : rankAiPros.split(Cat.comma)){
				if(Util.isEmpty(pro)){
					continue;
				}
				Float.parseFloat(pro);
			}
		}catch(Exception e){
			LogCore.startup.error("id = {} rankAiPros异常...", id);
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAiPros() {
		return aiPros;
	}

	public void setAiPros(String aiPros) {
		this.aiPros = aiPros;
	}
	public String getRankAiPros() {
		return rankAiPros;
	}

	public void setRankAiPros(String rankAiPros) {
		this.rankAiPros = rankAiPros;
	}
}
