package com.wqy.weibo.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.wqy.weibo.activity.LoadingActivity;
import com.wqy.weibo.fragment.HomeFragment;
import com.wqy.weibo.model.Weibo;

/**
 * 从网络上拉去微博内容
 * @author Administrator
 *
 */
public class GetWeiboInfoFromNet extends AsyncTask<Void,Void,Void>  implements RequestListener{

	private Oauth2AccessToken token;
	/**
	 * 刷新请求的来源
	 */
	private int requestComFrom;
	/**
	 * 从网络上获取的微博内容
	 */
	private ArrayList<Weibo> tempData;
	/**
	 * 数据库操作帮助类
	 */
	private DBHelper mDBHelper;
	private Context context;
	private String sinceId;
	/**
	 * 从网络上获取微博内容
	 * @param context
	 * @param tempData 装载微博的List
	 * @param token
	 * @param sinceId 请求的sinceId
	 */
	public GetWeiboInfoFromNet(Context context,ArrayList<Weibo> tempData,Oauth2AccessToken token,String sinceId){
		mDBHelper = DBHelper.getDBHelperInstance(context);
		this.tempData = tempData;
		this.context = context;
		this.token = token;
		this.sinceId = sinceId;
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		WeiboParameters mWeiboParams = new WeiboParameters();
		mWeiboParams.put("access_token", token.getToken());
		mWeiboParams.put("count", 20);
		if(sinceId!=null)
			mWeiboParams.put("since_id", Long.parseLong(sinceId));
		AsyncWeiboRunner.requestAsync("https://api.weibo.com/2/statuses/friends_timeline.json", mWeiboParams, "GET", this);
		return null;
	}
	@Override
	public void onComplete(String jsonString) {
		// TODO Auto-generated method stub
		JSONObject json;
		try {
			json = new JSONObject(jsonString);
			tempData = Weibo.parseJson(json,context);
			Message msg = HomeFragment.finishRefreshHandler.obtainMessage();
			msg.what = HomeFragment.FINISHED_REFRESH;
			msg.obj = tempData;
			Log.d("weiquanyun","结束HomeFragment的刷新");
			HomeFragment.finishRefreshHandler.sendMessage(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onWeiboException(WeiboException e) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "获取微博异常："+e.getMessage(), Toast.LENGTH_LONG).show();
		Log.d("weiquanyun","获取微博异常："+e.getMessage());
		Message msg = HomeFragment.finishRefreshHandler.obtainMessage();
		msg.what = HomeFragment.FINISHED_REFRESH;
		msg.obj = tempData;
		Log.d("weiquanyun","结束HomeFragment的刷新");
		HomeFragment.finishRefreshHandler.sendMessage(msg);
	}
}
