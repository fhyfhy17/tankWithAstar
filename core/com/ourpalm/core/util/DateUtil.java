package com.ourpalm.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	public static long MILLINSECOND = 1L;

	public static long SECOND = 1000L;

	public static long MIN = 60 * SECOND;

	public static long HOUR = 60 * MIN;

	public static long DAY = 24 * HOUR;

	public static long WEEK = 7 * DAY;

	public static long YEAR = 365 * DAY;

	public static int SECOND_TO_DAY = 24 * 60 * 60;

	private static final String format = "yyyy年MM月dd日 HH:mm:ss";
	private static final String YYYYMMDDFORMAT = "yyyyMMdd";
	private static final String YYYYMMDDFORMATCN = "yyyy年MM月dd日";
	private static final String YYYYMMDDHHmmSSFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date str2Date(String str) {
		if (str.trim().equals(""))
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDDHHmmSSFORMAT);
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String millsToString(long time) {
		if (time <= 0)
			return null;

		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDDHHmmSSFORMAT);
		return formatter.format(new Date(time));
	}

	/**
	 * "yyyy年MM月dd日 HH:mm:ss";
	 * 
	 * @param date
	 * @return
	 */
	public static String date2Str(Date date) {
		if (null == date) {
			throw new java.lang.NullPointerException();
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * 判断两日期是否为同一天
	 */
	public static boolean isSameDay(Date srcDate, Date destDate) {
		if (null == srcDate || null == destDate) {
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(srcDate);
		cal2.setTime(destDate);

		if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
			return false;
		}

		if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			return false;
		}

		if (cal1.get(Calendar.DATE) != cal2.get(Calendar.DATE)) {
			return false;
		}
		return true;
	}

	/**
	 * 根据毫秒数，判断是不是同一天
	 * 
	 * @param srcDate
	 * @param destDate
	 * @return
	 */
	public static boolean isSameDay(long srcDate, long destDate) {
		if (srcDate <= 0 || destDate <= 0) {
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(srcDate);
		cal2.setTimeInMillis(destDate);

		if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
			return false;
		}

		if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			return false;
		}

		if (cal1.get(Calendar.DATE) != cal2.get(Calendar.DATE)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当月第几天
	 * 
	 * @return
	 */
	public static int getCurrentDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取同月，两日期相差天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static int calcBetweenTwoDays(long before, long after) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(before);
		cal2.setTimeInMillis(after);

		return Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 获取两个时间戳之间相差天数
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDaysInterval(long start, long end) {
		return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(start - end));
	}

	/**
	 * 把日期转成毫秒
	 * 
	 * @param date
	 * @return
	 */
	public static long convertDateToMillis(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis();
	}

	public static boolean isSameWeek(long srcDate, long destDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(srcDate);
		cal2.setTimeInMillis(destDate);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	public static long convertYYYYMMDDStr2Millis(String str) {
		Date d = yyyyMMDDStr2Date(str);
		if (d == null)
			return 0;

		return convertDateToMillis(d);
	}

	public static Date yyyyMMDDStr2Date(String str) {
		if (str.trim().equals(""))
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDDFORMAT);
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String str2CnStrDate(String str) {
		Date d = yyyyMMDDStr2Date(str);
		if (d == null)
			return "";

		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDDFORMATCN);
		return formatter.format(d);
	}

	/**
	 * 计算两个日期之间的天数(精确到秒）
	 * 
	 * @param lastExchangeDate
	 * @param date
	 * @return
	 */
	public static int betweenSecondDates(int time1, int time2) {
		return Math.abs((time1 - time2) / (24 * 60 * 60));
	}

	/**
	 * 计算两个日期之间的天数
	 * 
	 * @param lastExchangeDate
	 * @param date
	 * @return
	 */
	public static int betweenDates(long time1, long time2) {
		if (time1 == 0 || time2 == 0) {
			return 0;
		}

		time1 = getAM0Time(time1);
		time2 = getAM0Time(time2);
		return Math.abs((int) ((time2 - time1) / (24 * 60 * 60 * 1000)));
	}

	/**
	 * 转化成0点时间
	 * 
	 * @param millisTime
	 * @return
	 */
	public static long getAM0Time(long millisTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisTime);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 0点时间，之后几天
	 * 
	 * @return
	 */
	public static long timeAddDay(long millisTime, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisTime);

		calendar.add(Calendar.DAY_OF_YEAR, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 返回24小时制的，小时
	 * 
	 * @param millis
	 * @return
	 */
	public static int getTimeHour(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 某时间是昨天
	 * 
	 * @param time
	 *            某时间
	 * 
	 * @return
	 */
	public static boolean isYesterday(long time) {
		Calendar csometime = Calendar.getInstance();
		csometime.setTimeInMillis(time);
		csometime.add(Calendar.DATE, 1);
		Calendar ctoday = Calendar.getInstance();
		if (csometime.get(Calendar.DAY_OF_YEAR) == ctoday.get(Calendar.DAY_OF_YEAR) && csometime.get(Calendar.YEAR) == ctoday.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * 验证时间是否是今天以前
	 * 
	 * @param time
	 *            时间
	 * @return 昨天
	 */
	public static boolean beforeToday(final long time) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(time);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(System.currentTimeMillis());

		if (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR)) {
			return true;
		}
		if (c1.get(Calendar.DAY_OF_YEAR) < c2.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}

}
