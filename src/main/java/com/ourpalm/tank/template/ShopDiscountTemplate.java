package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.KeySupport;

public class ShopDiscountTemplate implements KeySupport<Integer>{
	private int day;
	private String desc;
	private int itemId1;
	private int itemId2;
	private int itemId3;
	private int itemId4;
	private int itemId5;
	private int itemId6;
	private int itemId7;
	private int itemId8;
	private int itemId9;
	private int itemId10;
	private int itemId11;
	private int itemId12;
	private int itemId13;
	private int itemId14;
	private int itemId15;
	private int itemId16;
	private int itemId17;
	private int itemId18;
	private int itemId19;
	private int itemId20;
	private int count1;
	private int count2;
	private int count3;
	private int count4;
	private int count5;
	private int count6;
	private int count7;
	private int count8;
	private int count9;
	private int count10;
	private int count11;
	private int count12;
	private int count13;
	private int count14;
	private int count15;
	private int count16;
	private int count17;
	private int count18;
	private int count19;
	private int count20;
	
	//<Id, count>
	private Map<Integer, Integer> items = new HashMap<Integer, Integer>();
	
	
	public void init(){
		this.put(itemId1, count1, items);
		this.put(itemId2, count2, items);
		this.put(itemId3, count3, items);
		this.put(itemId4, count4, items);
		this.put(itemId5, count5, items);
		this.put(itemId6, count6, items);
		this.put(itemId7, count7, items);
		this.put(itemId8, count8, items);
		this.put(itemId9, count9, items);
		this.put(itemId10, count10, items);
		this.put(itemId11, count11, items);
		this.put(itemId12, count12, items);
		this.put(itemId13, count13, items);
		this.put(itemId14, count14, items);
		this.put(itemId15, count15, items);
		this.put(itemId16, count16, items);
		this.put(itemId17, count17, items);
		this.put(itemId18, count18, items);
		this.put(itemId19, count19, items);
		this.put(itemId20, count20, items);
	}
	
	private void put(Integer id, Integer count, Map<Integer, Integer> map){
		if(id <= 0 || count <= 0){
			return;
		}
		
		if(map.containsKey(id)){
			LogCore.startup.error("打折商品重复 day={}, itemId={}", day, id);
			return;
		}
		map.put(id, count);
	}
	
	public Map<Integer, Integer> getItems(){
		return new HashMap<Integer, Integer>(this.items);
	}
	
	
	@Override
	public Integer getKey(){
		return this.day;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getItemId1() {
		return itemId1;
	}
	public void setItemId1(int itemId1) {
		this.itemId1 = itemId1;
	}
	public int getItemId2() {
		return itemId2;
	}
	public void setItemId2(int itemId2) {
		this.itemId2 = itemId2;
	}
	public int getItemId3() {
		return itemId3;
	}
	public void setItemId3(int itemId3) {
		this.itemId3 = itemId3;
	}
	public int getItemId4() {
		return itemId4;
	}
	public void setItemId4(int itemId4) {
		this.itemId4 = itemId4;
	}
	public int getItemId5() {
		return itemId5;
	}
	public void setItemId5(int itemId5) {
		this.itemId5 = itemId5;
	}
	public int getItemId6() {
		return itemId6;
	}
	public void setItemId6(int itemId6) {
		this.itemId6 = itemId6;
	}
	public int getItemId7() {
		return itemId7;
	}
	public void setItemId7(int itemId7) {
		this.itemId7 = itemId7;
	}
	public int getItemId8() {
		return itemId8;
	}
	public void setItemId8(int itemId8) {
		this.itemId8 = itemId8;
	}
	public int getItemId9() {
		return itemId9;
	}
	public void setItemId9(int itemId9) {
		this.itemId9 = itemId9;
	}
	public int getItemId10() {
		return itemId10;
	}
	public void setItemId10(int itemId10) {
		this.itemId10 = itemId10;
	}
	public int getCount1() {
		return count1;
	}
	public void setCount1(int count1) {
		this.count1 = count1;
	}
	public int getCount2() {
		return count2;
	}
	public void setCount2(int count2) {
		this.count2 = count2;
	}
	public int getCount3() {
		return count3;
	}
	public void setCount3(int count3) {
		this.count3 = count3;
	}
	public int getCount4() {
		return count4;
	}
	public void setCount4(int count4) {
		this.count4 = count4;
	}
	public int getCount5() {
		return count5;
	}
	public void setCount5(int count5) {
		this.count5 = count5;
	}
	public int getCount6() {
		return count6;
	}
	public void setCount6(int count6) {
		this.count6 = count6;
	}
	public int getCount7() {
		return count7;
	}
	public void setCount7(int count7) {
		this.count7 = count7;
	}
	public int getCount8() {
		return count8;
	}
	public void setCount8(int count8) {
		this.count8 = count8;
	}
	public int getCount9() {
		return count9;
	}
	public void setCount9(int count9) {
		this.count9 = count9;
	}
	public int getCount10() {
		return count10;
	}
	public void setCount10(int count10) {
		this.count10 = count10;
	}

	public int getItemId11() {
		return itemId11;
	}

	public void setItemId11(int itemId11) {
		this.itemId11 = itemId11;
	}

	public int getItemId12() {
		return itemId12;
	}

	public void setItemId12(int itemId12) {
		this.itemId12 = itemId12;
	}

	public int getItemId13() {
		return itemId13;
	}

	public void setItemId13(int itemId13) {
		this.itemId13 = itemId13;
	}

	public int getItemId14() {
		return itemId14;
	}

	public void setItemId14(int itemId14) {
		this.itemId14 = itemId14;
	}

	public int getItemId15() {
		return itemId15;
	}

	public void setItemId15(int itemId15) {
		this.itemId15 = itemId15;
	}

	public int getItemId16() {
		return itemId16;
	}

	public void setItemId16(int itemId16) {
		this.itemId16 = itemId16;
	}

	public int getItemId17() {
		return itemId17;
	}

	public void setItemId17(int itemId17) {
		this.itemId17 = itemId17;
	}

	public int getItemId18() {
		return itemId18;
	}

	public void setItemId18(int itemId18) {
		this.itemId18 = itemId18;
	}

	public int getItemId19() {
		return itemId19;
	}

	public void setItemId19(int itemId19) {
		this.itemId19 = itemId19;
	}

	public int getItemId20() {
		return itemId20;
	}

	public void setItemId20(int itemId20) {
		this.itemId20 = itemId20;
	}

	public int getCount11() {
		return count11;
	}

	public void setCount11(int count11) {
		this.count11 = count11;
	}

	public int getCount12() {
		return count12;
	}

	public void setCount12(int count12) {
		this.count12 = count12;
	}

	public int getCount13() {
		return count13;
	}

	public void setCount13(int count13) {
		this.count13 = count13;
	}

	public int getCount14() {
		return count14;
	}

	public void setCount14(int count14) {
		this.count14 = count14;
	}

	public int getCount15() {
		return count15;
	}

	public void setCount15(int count15) {
		this.count15 = count15;
	}

	public int getCount16() {
		return count16;
	}

	public void setCount16(int count16) {
		this.count16 = count16;
	}

	public int getCount17() {
		return count17;
	}

	public void setCount17(int count17) {
		this.count17 = count17;
	}

	public int getCount18() {
		return count18;
	}

	public void setCount18(int count18) {
		this.count18 = count18;
	}

	public int getCount19() {
		return count19;
	}

	public void setCount19(int count19) {
		this.count19 = count19;
	}

	public int getCount20() {
		return count20;
	}

	public void setCount20(int count20) {
		this.count20 = count20;
	}

	public void setItems(Map<Integer, Integer> items) {
		this.items = items;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
