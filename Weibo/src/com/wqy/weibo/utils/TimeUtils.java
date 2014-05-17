package com.wqy.weibo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.wqy.weibo.R;

import android.util.Log;

public class TimeUtils {
	
	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间 60*1000
	 */
	public static final long ONE_MINUTE = 60000;

	/**
	 * 一小时的毫秒值，用于判断上次的更新时间 60 * ONE_MINUTE
	 */
	public static final long ONE_HOUR = 3600000;

	/**
	 * 一天的毫秒值，用于判断上次的更新时间 24 * ONE_HOUR
	 */
	public static final long ONE_DAY = 86400000;

	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;

	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;
	
	/**
	 * 计算时区差别产生的时间差
	 */
	public static long cachedTime = -1;
	public static long stringTime2LongTime(String timeString){
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss +0800 yyyy", Locale.ENGLISH);
		SimpleDateFormat sdfNoZone = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
		if(cachedTime==-1){
			Date weiboTime;
			try {
				Log.d("weiquanyun","timeString = "+timeString);
				weiboTime = dateFormat.parse(timeString);
				String timeStringNoZone = changeTimeNoTimeZone(timeString);
				
				Date weiboTimeNoZone;
				weiboTimeNoZone = sdfNoZone.parse(timeStringNoZone);
				cachedTime = weiboTime.getTime() - weiboTimeNoZone.getTime();
				return weiboTime.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
				return (long) 0;
			}
		}else{
			Date weiboTimeNoZone;
			String timeStringNoZone = changeTimeNoTimeZone(timeString);
			try {
				weiboTimeNoZone = sdfNoZone.parse(timeStringNoZone);
				return weiboTimeNoZone.getTime()+cachedTime;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (long) 0;
			}
		}
	}
	
	/**
	 * 去掉微博创建时间中的时区标识
	 * @param timeString
	 * @return
	 */
	public static String changeTimeNoTimeZone(String timeString){
		return timeString.replace("+0800", "");
	}
	
	public static String pareseTime(String timeString){
		long weibotime = Long.parseLong(timeString);
		String showTime;
		long currentTime = System.currentTimeMillis();
		long pastTime = currentTime - weibotime;
		long timeFormat = 0;
		if(pastTime<ONE_MINUTE){
			showTime = "刚刚";
		}else if(pastTime<ONE_HOUR){
			timeFormat = pastTime/ONE_MINUTE;
			showTime = timeFormat + "分钟前";
		}else if(pastTime<ONE_DAY){
			timeFormat = pastTime/ONE_HOUR;
			showTime = timeFormat + "小时前";
		}else if(pastTime<ONE_MONTH){
			timeFormat = pastTime/ONE_DAY;
			showTime = timeFormat + "天前";
		}else if(pastTime<ONE_YEAR){
			timeFormat = pastTime/ONE_MONTH;
			showTime = timeFormat + "月前";
		}else{
			timeFormat = pastTime/ONE_YEAR;
			showTime = timeFormat + "年前";
		}
		return showTime;
	}
}
