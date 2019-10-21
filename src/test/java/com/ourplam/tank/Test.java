package com.ourplam.tank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class Test {
	private final static int MAX_PLAYER = 10; 
	private Object lock = new Object();
	private List<Integer> mathList = new ArrayList<>();

	private void matchPlayer(){
		LinkedList<Integer> tanks ;
		synchronized (lock) {
			if(mathList.size() < MAX_PLAYER){
				return ;
			}
			tanks = new LinkedList<>(mathList);
			mathList.clear();
		}
		List<Integer> list = new ArrayList<>();
		while(true){
			boolean isNoEnough = false;
			for(int i = 0; i < MAX_PLAYER; i++){
				Integer tank = tanks.poll();
				if(tank == null){
					System.out.println("人数也不足...");
					isNoEnough = true;
					break;
				}
				list.add(tank);
			}
			if(isNoEnough){
				synchronized (lock) {
					this.mathList.addAll(list);
					System.out.println("扔回队列..."+JSON.toJSONString(list));
				}
				return ;
			}
			this.match(new ArrayList<>(list));
			list.clear();
		}
		
	}
	
	private void match(List<Integer> list){
		System.out.println("通知匹配结果..."+JSON.toJSONString(list));
	}
	
	public static void main(String[] args){
		Test test = new Test();
		List<Integer> list = new ArrayList<>();
		for(int i = 1 ; i <= 112; i++){
			list.add(i);
		}
		test.mathList.addAll(list);
		
		test.matchPlayer();
	}
}
