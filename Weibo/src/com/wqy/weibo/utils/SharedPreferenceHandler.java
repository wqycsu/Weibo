package com.wqy.weibo.utils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的帮助类
 * @author Administrator
 *
 */
public class SharedPreferenceHandler {

	/**
	 * 存储基本属性信息的SharedPreferences
	 */
	private SharedPreferences mSharedPreference;
	private static SharedPreferenceHandler mSharedPreferenceHandler;
	private final String SHAREDPREFERENCE = "weibo_wqy";
	public  final String PAGEINDEX = "page_index";
	public final String POSITION ="position";
	public final String Y = "y";
	public  final String TOKEN = "token";
	public  final String USER_ID = "user_id";
	public final String LAST_WEIBO_ID = "last_weibo_id";
	/**
	 * access_token的有效期
	 */
	public final String EXPRIRES_IN = "exprires_in";
	private SharedPreferences.Editor editor;
	private Context context;
	
	private SharedPreferenceHandler(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mSharedPreference = context.getSharedPreferences(SHAREDPREFERENCE, Context.MODE_PRIVATE);
	}

	public static SharedPreferenceHandler getSharedPreferenceHandler(Context context){
		if(mSharedPreferenceHandler==null)
			mSharedPreferenceHandler = new SharedPreferenceHandler(context);
		return mSharedPreferenceHandler;
	}
	
	/**
	 * 获得离开home界面时，用户所看的页面微博位置
	 * @return
	 */
	public int getPageIndex(){
		return mSharedPreference.getInt(PAGEINDEX, 1);
	}
	
	/**
	 * 设置切换Fragment时，用户所看的页面微博
	 */
	public void setWeiboIndex(int index){
		editor = mSharedPreference.edit();
		editor.putInt(PAGEINDEX, index);
		editor.commit();
	}
	
	/**
	 * 设置ListView滚动的x和y
	 * @param x
	 * @param y
	 */
	public void setListViewPositionY(int position,int y){
		editor = mSharedPreference.edit();
		editor.putInt(POSITION, position);
		editor.putInt(Y, y);
		editor.commit();
	}
	
	public int getPosition(){
		return mSharedPreference.getInt(POSITION, 0);
	}
	
	public int getScrollY(){
		return mSharedPreference.getInt(Y, 0);
	}
	/**
	 * 保存token
	 * @param token
	 */
	public void setAccessToken(Oauth2AccessToken token){
		editor = mSharedPreference.edit();
		editor.putString(TOKEN, token.getToken());
		editor.putString(USER_ID, token.getUid());
		editor.putLong(EXPRIRES_IN, token.getExpiresTime());
		editor.commit();
	}
	
	/**
	 * 获得存储的token
	 * @return
	 *       token已经保存的话则返回，否则返回null
	 */
	public Oauth2AccessToken getAccessToken(){
		Oauth2AccessToken token = new Oauth2AccessToken();
		token.setToken(mSharedPreference.getString(TOKEN, ""));
		token.setExpiresTime(mSharedPreference.getLong(EXPRIRES_IN, 0));
		token.setUid(mSharedPreference.getString(USER_ID, ""));
		return token;
	}
	
	/**
	 * 注销时，清楚保存的access_token
	 */
	public void clearAccessToken(){
		editor = mSharedPreference.edit();
		editor.clear();
		editor.commit();
	}
	
	public void setLastWeiboId(String lastWeiboId){
		editor = mSharedPreference.edit();
		editor.putString(LAST_WEIBO_ID, lastWeiboId);
	}
	
	public String getLastWeiboId(){
		return mSharedPreference.getString(LAST_WEIBO_ID, null);
	}
}
