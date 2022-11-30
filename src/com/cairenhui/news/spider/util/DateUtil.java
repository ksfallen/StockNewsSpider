package com.cairenhui.news.spider.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Integer getTimeInSec(Date date) {
		return (int)(date.getTime()/1000);
	}
	
	public static String formatDate(Date date, String format){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			String res = sdf.format(date);
			return res;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static Date parseDate(String dateStr, String format, boolean flag) {
		Date res = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			res = sdf.parse(dateStr);
		}catch(Exception e){
			e.printStackTrace();
			res = new Date();
		}
		if(flag){
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			cal.setTime(res);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			res = cal.getTime();
		}
		return res;
	}
	
	public static Date parseDate(String dateStr, String format){
		return parseDate(dateStr, format, false);
	}
	
	/**
	 * 获取一天开始的秒数
	 * @param date
	 * @return
	 */
	public static long getStartTime(Date date) {
		Calendar today = Calendar.getInstance();
        today.setTimeInMillis(date.getTime());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        long beginTime = today.getTimeInMillis() / 1000;
        return beginTime;
	}

	/**
	 * 获取一天结束的秒数
	 * @param date
	 * @return
	 */
	public static long getEndTime(Date date) {
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(date.getTime());
		today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        long endTime = today.getTimeInMillis() / 1000;
        return endTime;
	}
	
	/**
	 * 秒数转化为日期
	 * @param seconds
	 * @return
	 */
	public static Date getSecToDate(int seconds){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Long(seconds) * 1000);
		return cal.getTime();
	}
}
