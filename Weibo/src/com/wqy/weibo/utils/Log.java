package com.wqy.weibo.utils;

/**
 * 自定义Log类
 * @author Administrator
 *
 */
public class Log{
	private final static String TAG = "weiquanyun";
	/**
	 * Log开关变量
	 */
	private static boolean flag = true;
	
	public static void d(String msg){
		if(flag)
			android.util.Log.d(TAG,msg);
	}
	
	public static void e(String msg){
		if(flag)
			android.util.Log.e(TAG,msg);
	}
	
	public static void v(String msg){
		if(flag)
			android.util.Log.v(TAG,msg);
	}
	
	public static void i(String msg){
		if(flag)
			android.util.Log.i(TAG,msg);
	}
	
	public static void d(String TAG,String msg){
		if(flag)
			android.util.Log.d(TAG,msg);
	}
	
	public static void e(String TAG,String msg){
		if(flag)
			android.util.Log.e(TAG,msg);
	}
	
	public static void v(String TAG,String msg){
		if(flag)
			android.util.Log.v(TAG,msg);
	}
	
	public static void i(String TAG,String msg){
		if(flag)
			android.util.Log.i(TAG,msg);
	}
}
