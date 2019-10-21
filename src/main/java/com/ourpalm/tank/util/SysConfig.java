package com.ourpalm.tank.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.template.SysconfigTemplate;
import com.ourpalm.tank.type.XlsSheetType;

public class SysConfig {

	public static Map<Integer, Integer> map = new HashMap<>();

	public static void load() {
		String fileName = XlsSheetType.sysconfig.getXlsFileName();
		String sheetName = XlsSheetType.sysconfig.getSheetName();
		List<SysconfigTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, SysconfigTemplate.class);
		for (SysconfigTemplate sysconfigTemplate : list) {
			map.put(sysconfigTemplate.getId(), sysconfigTemplate.getValue());
		}
	}

	public static int get(int id) {
		return map.get(id);
	}
}
