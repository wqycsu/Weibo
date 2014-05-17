package com.wqy.weibo.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
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
 * 进入应用时获取微博内容的任务类，如果本地有内容则取出，没有的话从网络获取 
 * @author Administrator
 *
 */
public class GetWeiboInfoAsyncTask extends AsyncTask<Void, Void, String>{

	private Context context;
	/**
	 * 存储weibo信息的list
	 */
	private ArrayList<Weibo> weiboData;
	/**
	 * 从网络上获取的微博内容
	 */
	private ArrayList<Weibo> tempData;
	/**
	 * 数据库操作帮助类
	 */
	private DBHelper mDBHelper;
	
	private Oauth2AccessToken token;
	private int requestComFrom;
	/**
	 * 请求微博的since_id
	 * 若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
	 */
	private String fromWeiboId;
	
	public FinishLoginActivityCallback finishLoginActivity;
	
	public interface FinishLoginActivityCallback{
		public void finishLogin();
	}
	
	public void setFinishLoginActivityCallback(FinishLoginActivityCallback finishLoginActivity){
		this.finishLoginActivity = finishLoginActivity;
	}
	
	public GetWeiboInfoAsyncTask(Context context,ArrayList<Weibo> weiboData,Oauth2AccessToken token,int requestComFrom){
		this.context = context;
		this.weiboData = weiboData;
		this.token = token;
		this.requestComFrom = requestComFrom;
		this.mDBHelper = DBHelper.getDBHelperInstance(context);
		if (requestComFrom == Config.COME_FROM_HOMEFRAGMENT
				&& weiboData != null &&weiboData.size()>0){
			for(int i=0;i<weiboData.size();i++){
				Log.d("weibo","weibo.id->"+weiboData.get(i).getWeiboId());
			}
			this.fromWeiboId = weiboData.get(weiboData.size() - 1).getWeiboId();
			weiboData.clear();
		}else{
			
			fromWeiboId = SharedPreferenceHandler.getSharedPreferenceHandler(
					context).getLastWeiboId();
			Log.d("weiquanyun","fromWeiboId = "+fromWeiboId);
		}
		
	}
	
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		weiboData = mDBHelper.getWeibo(fromWeiboId);
		for(int i=0;i<weiboData.size();i++){
			if(weiboData.get(i).getRetweed_weibo()!=null)
				Log.d("weibo","weibo.name"+weiboData.get(i).getWeiboName()+" "+weiboData.get(i).getRetweed_weibo().getWeiboName());
			else
				Log.d("weibo","weibo.name"+weiboData.get(i).getWeiboName()+" "+weiboData.get(i).getRetweed_weibo());
		}
//		Log.d("weiquanyun","weiboData.size() = "+weiboData.size());
//		Log.d("weiquanyun","requestComFrom = "+requestComFrom);
		if(weiboData.size()==0&&requestComFrom==Config.COME_FROM_HOMEFRAGMENT){
			return "finish_refreshing";
		}
		if(weiboData!=null&&weiboData.size()>0){
			tempData = weiboData;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**
			 * 如果加载数据请求来自登陆Activity或者初始化Activity的话执行if
			 * 如果来自HomeFragment则执行else
			 */
			if(requestComFrom!=Config.COME_FROM_HOMEFRAGMENT){
				Message msg = LoadingActivity.handler.obtainMessage();
				msg.what = LoadingActivity.FINISHED_WITH_DATA;
				msg.obj = tempData;
				LoadingActivity.handler.sendMessage(msg);
			}else{
				Message msg = HomeFragment.finishRefreshHandler.obtainMessage();
				msg.what = HomeFragment.FINISHED_REFRESH;
				msg.obj = tempData;
				HomeFragment.finishRefreshHandler.sendMessage(msg);
				return null;
			}
		}else{
			new GetWeiboFromInternetTask().execute();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		if("finish_refreshing".equals(result)){
			Utils.getInstatnce(context).showToast("加载不出来了,刷新试试");
			Message msg = HomeFragment.finishRefreshHandler.obtainMessage();
			msg.what = HomeFragment.FINISHED_REFRESH;
			msg.obj = tempData;
			HomeFragment.finishRefreshHandler.sendMessage(msg);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 从网络上拉去微博内容
	 * @author Administrator
	 *
	 */
	class GetWeiboFromInternetTask extends AsyncTask<Void,Void,Void>  implements RequestListener{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			WeiboParameters mWeiboParams = new WeiboParameters();
			mWeiboParams.put("access_token", token.getToken());
			mWeiboParams.put("count", 20);
			AsyncWeiboRunner.requestAsync("https://api.weibo.com/2/statuses/friends_timeline.json", mWeiboParams, "GET", this);
			return null;
		}
		@Override
		public void onComplete(String json) {
			// TODO Auto-generated method stub
			try {
//				Log.d("weiquanyun",json);
				JSONObject jsonObject = new JSONObject(json);
				weiboData = Weibo.parseJson(jsonObject,context);
				if(requestComFrom!=Config.COME_FROM_HOMEFRAGMENT){
					Message msg = LoadingActivity.handler.obtainMessage();
					msg.what = LoadingActivity.FINISHED_WITH_DATA;
					msg.obj = weiboData;
//					Log.d("weiquanyun","weiboData = "+weiboData);
					LoadingActivity.handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "获取微博异常："+e.getMessage(), Toast.LENGTH_LONG).show();
//			Log.d("weiquanyun","获取微博异常："+e.getMessage());
			if(requestComFrom!=Config.COME_FROM_HOMEFRAGMENT){
				Message msg = LoadingActivity.handler.obtainMessage();
				msg.what = LoadingActivity.FINISHED_WITH_DATA;
				msg.obj = weiboData;
//				Log.d("weiquanyun","weiboData = "+weiboData);
				LoadingActivity.handler.sendMessage(msg);
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			finishLoginActivity.finishLogin();
		}
	}
	
}
