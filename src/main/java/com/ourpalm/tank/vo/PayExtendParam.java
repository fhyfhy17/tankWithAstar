package com.ourpalm.tank.vo;

/**
 * 支付扩展参数
 * 
 * @author wangkun
 *
 */
public class PayExtendParam {
	
	private String nodeName;	//所在节点名
	private int ioId;			//IO id
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public int getIoId() {
		return ioId;
	}
	public void setIoId(int ioId) {
		this.ioId = ioId;
	}
	
	public static PayExtendParam encode(String params){
		String[] strs = params.split("\\|");
		PayExtendParam param = new PayExtendParam();
		param.setNodeName(strs[0]);
		param.setIoId(Integer.parseInt(strs[1]));
		return param;
	}
	
	public String decode(){
		return nodeName + "|" + ioId;
	}
}
