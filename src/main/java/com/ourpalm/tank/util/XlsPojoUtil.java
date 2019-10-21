
package com.ourpalm.tank.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.KeySupport;
import com.ourpalm.core.util.Util;

import jxl.Cell;
import jxl.Workbook;

public class XlsPojoUtil {
	final static Logger logger = LogCore.startup;
	
	private static <T> T cellToProperty(String value, T message, String cluName){
		String propertyName = cluName;
		PropertyDescriptor prop = null;
		try {
			prop = new PropertyDescriptor(propertyName, message.getClass());
		} catch (Exception ex) {
			prop = null;
		}
		if (null == prop) {
			return message;
		}

		String type = prop.getPropertyType().getSimpleName();
		try {
			if (!prop.getPropertyType().isArray()) {
				// 数值型，默认值为：0
				if (value.equals("") && !type.equals("String")
						&& !type.equals("Date") && !type.equals("boolean")) {
					//value = "0";
					//直接用POJO中的默认值,不再将默认值设置为0
					return message;
				} else if (type.equals("boolean")
						&& (value.trim().equals("1") || value.trim()
								.equalsIgnoreCase("true"))) {
					value = "true";
				}
				if (type.equals("int")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Integer(value) });
				} else if (type.equals("long")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Long(value) });
				} else if (type.equals("String")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { value });
				} else if (type.equals("Date")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Date(value) });
				} else if (type.equals("short")) {
					prop.getWriteMethod().invoke(
							message,
							new Object[] { /* new Short(value) */(short) Integer
									.parseInt(value) });
				} else if (type.equals("byte")) {
					prop.getWriteMethod().invoke(
							message,
							new Object[] { /* new Byte(value) */(byte) Short
									.parseShort(value) });
				} else if (type.equals("boolean")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Boolean(value) });
				} else if (type.equals("double")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Double(value) });
				} else if (type.equals("float")) {
					prop.getWriteMethod().invoke(message,
							new Object[] { new Float(value) });
				}
			}
		}catch(Exception ex){
			logger.error("config value error: class=" 
					+ message.getClass().getSimpleName() + " colName=" + cluName
					+" type=" + type + " value=" + value);
		}
		return message;
	}
	
	public static Set<String> sheetToStringSet(String sourceFile,String sheetName){
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			int row = 0;
			Set<String> result = new HashSet<String>();
			for (row = 2; row < rsRows; row++) {
				Cell cell = rs.getCell(0, row);
				String key = toTrim(cell.getContents());
				result.add(key);
			}
			return result;
		} catch (Exception e) {
			logger.error("sheetToStringSet excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName ,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return  new HashSet<String>();
	}
	
	public static List<String> sheetToStringList(String sourceFile,String sheetName){
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			int row = 0;
			List<String> result = new ArrayList<String>();
			for (row = 2; row < rsRows; row++) {
				Cell cell = rs.getCell(0, row);
				String key = toTrim(cell.getContents());
				result.add(key);
			}
			return result;
		} catch (Exception e) {
			logger.error("sheetToStringList excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName ,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return  new ArrayList<String>();
	}

	public static List<List<String>> sheetToList(String sourceFile, String sheetName){
		Workbook book = null;
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			logger.info("loading "+sourceFile+" "+sheetName+" allRows="+rsRows);
			int rsColumns = rs.getColumns();
			int row = 0, column = 0;
			List<List<String>> result = new ArrayList<List<String>>();
			for (row = 2; row < rsRows; row++) {
				String key = rs.getCell(0, row).getContents();
				curRow = row;
				curKey = key;
				List<String> rowList = new ArrayList<String>();
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					curColName = toTrim( rs.getCell(column, 0).getContents());
					if (curColName == null || curColName.equals("")) {
						continue;
					}
					Cell cell = rs.getCell(column, row);
					String content = cell.getContents();
					String value = toTrim(null == content?"":content);
					rowList.add(value);
				}
				result.add(rowList);
			}
			return result;
		} catch (Exception e) {
			logger.error("Excel 加载异常 ：sourceFile : " + sourceFile + " sheetName = " + sheetName + " curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow, e);
		} finally {
			if (book != null)
				book.close();
		}
		return  new ArrayList<List<String>>();
	}
	/**
	 * 默认将EXCEL第一列的值作为KEY
	 * 
	 * @param <T>
	 * @param sourceFile
	 * @param sheetName
	 * @param t
	 * @return
	 */
	private static <T> Map<String, T> sheetToMap(String sourceFile, String sheetName, Class<T> t, boolean linked) {
		Workbook book = null;
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			logger.info("loading "+sourceFile+" "+sheetName+" allRows="+rsRows);
			int rsColumns = rs.getColumns();
			int row = 0, column = 0;
			Map<String, T> map = null;
			if(linked){
				map = new LinkedHashMap<String,T>();
			}else{
				map = new HashMap<String, T>();
			}
			for (row = 2; row < rsRows; row++) {
				curRow = row;
				T as = t.newInstance();
				boolean put = false ;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					Cell cell = rs.getCell(column, row);
					Cell c = rs.getCell(column, 0);
					curColName = toTrim(c.getContents());
					//第一行、第一列为空
					if (curColName == null || curColName.equals("")) {
						continue;
					}

					String value = cell.getContents();
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					//对象(属性名) 值 和列名  ------把值封装进对象  
					as = cellToProperty(value, as, curColName);
				}
				if(put){
					Cell cell = rs.getCell(0, row);
					String key = cell.getContents();
					if(!Util.isEmpty(key) && map.containsKey(key)){
						logger.error("have same key:" + key +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					map.put(key, as);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return new HashMap<String, T>();
	}

	public static <K,T extends KeySupport<K>> Map<K, T> sheetToGenericLinkedMap(String sourceFile,String sheetName,Class<T> t){
		return sheetToGenericMap(sourceFile, sheetName, t, true);
	}
	
	public static <K,T extends KeySupport<K>> Map<K, T> sheetToGenericMap(String sourceFile,String sheetName,Class<T> t){
		return sheetToGenericMap(sourceFile, sheetName, t, false);
	}
	
	/**
	 * 根据指定的列值作为KEY
	 * 
	 * @param <T>
	 * @param sourceFile
	 * @param sheetName
	 * @param t
	 * @param keyLists
	 * @return
	 */
	public static <K,T extends KeySupport<K>> Map<K,T> sheetToGenericMap(String sourceFile,String sheetName,Class<T> t, boolean linked) {
		Workbook book = null;
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			logger.info("loading "+sourceFile+" "+sheetName+" allRows="+rsRows);
			int rsColumns = rs.getColumns();
			int row = 0, column = 0;
			Map<K,T> map = null;
			if(linked){
				map = new LinkedHashMap<K,T>();
			}else{
				map = new HashMap<K, T>();
			}
			for (row = 2; row < rsRows; row++) {
				curRow = row;
				T as = t.newInstance();
				boolean put = false ;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					Cell cell = rs.getCell(column, row);
					Cell c = rs.getCell(column, 0);
					curColName = toTrim(c.getContents());
					// 第一行、第一列为空
					if (curColName == null || curColName.equals("")) {
						continue;
					}
					String value = cell.getContents();
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					// 对象(属性名) 值 和列名 ------把值封装进对象
					as = cellToProperty(value, as, curColName);
				}
				if(put){
					if(map.containsKey(as.getKey())){
						logger.error("have same key:" + as.getKey() +" in file:" + sourceFile + " sheet:" + sheetName);
					}
					map.put(as.getKey(), as);
				}
			}
			return map;
		} catch (Exception e) {
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (null != book) {
				book.close();
			}
		}
		return new HashMap<K, T>();
	}

	
	public static <T> List<T> sheetToLinkedList(String sourceFile, String sheetName, Class<T> t){
		return sheetToList(sourceFile, sheetName, t, true);
	}
	
	public static <T> List<T> sheetToList(String sourceFile, String sheetName, Class<T> t){
		return sheetToList(sourceFile, sheetName, t, false);
	}
	
	
	
	private static <T> List<T> sheetToList(String sourceFile, String sheetName, Class<T> t, boolean linkedList) {
		Workbook book = null;
		int curRow = 0;
		int curCol = 0;
		String curKey = "";
		String curColName = "";
		try {
			book = Workbook.getWorkbook(new File(sourceFile));
			jxl.Sheet rs = book.getSheet(sheetName);
			int rsRows = rs.getRows();
			logger.info("loading "+sourceFile+" "+sheetName+" allRows="+rsRows);
			int rsColumns = rs.getColumns();
			int row = 0, column = 0;
			
			List<T> list = null;
			if(linkedList){
				list = new LinkedList<T>();
			} else {
				list = new ArrayList<T>();
			}
			for (row = 2; row < rsRows; row++) {
				T as = t.newInstance();
				boolean put = false ;
				String key = rs.getCell(0, row).getContents();
				curRow = row;
				curKey = key;
				for (column = 0; column < rsColumns; column++) {
					curCol = column;
					Cell cell = rs.getCell(column, row);
					Cell c = rs.getCell(column, 0);
					String cumName = toTrim(c.getContents());
 					curColName = cumName;
					//第一行、第一列为空
					if (cumName == null || cumName.equals("")) {
						continue;
					}
	
					String value = cell.getContents();
					value = (null== value)? "" : value.trim();
					if(value.length() > 0){
						put = true ;
					}
					//对象(属性名) 值 和列名  ------把值封装进对象  
					as = cellToProperty(value, as, cumName);
				}
				if(put){
					list.add(as);
				}
				
			}
			return list;
		} catch (Exception e) {
			logger.error("excel load error ：sourceFile=" + sourceFile +" sheetName=" + sheetName +
					" curKey=" + curKey + " curCol=" + curCol + " curRow=" + curRow + " curColName=" + curColName,e);
		} finally {
			if (book != null)
				book.close();
		}
		return new ArrayList<T>();
	}
	
	public static <T> Map<String, T> sheetToLinkedMap(String sourceFile, String sheetName, Class<T> t){
		return sheetToMap(sourceFile,sheetName,t,true);
	}
	
	public static <T> Map<String, T> sheetToMap(String sourceFile, String sheetName, Class<T> t){
		return sheetToMap(sourceFile,sheetName,t,false);
	}
	

	/**
	 * 去掉字符串的前后空格
	 * 
	 * @param str
	 * @return
	 */
	private static String toTrim(String str) {
		if (str != null) {
			str = str.trim();
		}
		return str;
	}
}