package com.ourpalm.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.ourpalm.core.log.LogCore;

public class NetUtil {

	/** 返回外网IP地址，如果没配置外网IP则返回内网IP */
	public static String getRealIp() {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP
		try{
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}
		if (netip != null && !"".equals(netip)) {
			return netip;
		} 
		return localip;
	}
	
	
	/** 返回内网IP地址 */
	public static String getHostIp(){
		String localip = null;
		try{
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
						break;
					}
				}
			}
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}
		return localip;
	}
	
	
	public static void main(String[] args){
		System.out.println(getHostIp());
	}
}
