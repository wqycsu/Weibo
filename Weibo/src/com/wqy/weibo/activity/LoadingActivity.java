package com.wqy.weibo.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.wqy.weibo.R;
import com.wqy.weibo.model.Weibo;
import com.wqy.weibo.utils.Config;
import com.wqy.weibo.utils.GetWeiboInfoAsyncTask;
import com.wqy.weibo.utils.SharedPreferenceHandler;

public class LoadingActivity extends Activity {

	private final String TAG = "weiquanyun";
	public static Handler handler;
	public static final int FINISHED_WITH_DATA = 900;
	public static final int FINISHED = 901;
	private SharedPreferenceHandler mHandler;
	private Oauth2AccessToken mAccessToken;
	private Intent intent;
	private ArrayList<Weibo> weiboData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loading);
		mHandler = SharedPreferenceHandler.getSharedPreferenceHandler(getApplicationContext());
		mAccessToken = mHandler.getAccessToken();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		handler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==FINISHED_WITH_DATA){
					LoginActivity.instance.finish();
					weiboData = (ArrayList<Weibo>)msg.obj;
//					Log.d(TAG,"weiboData = "+weiboData);
					intent = new Intent(LoadingActivity.this,MainActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("weibo_info", weiboData);
					intent.putExtras(bundle);
					startActivity(intent);
					LoadingActivity.this.finish();
				}else if(msg.what==FINISHED){
					LoadingActivity.this.finish();
				}
			}
		};
		handler.postDelayed(new TestRunnalbe(), 500);
	}

	class TestRunnalbe implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mAccessToken.isSessionValid()){
				Log.d(TAG,"获取微博缓存数据");
				new GetWeiboInfoAsyncTask(LoadingActivity.this, weiboData, mAccessToken,Config.COME_FROM_LOADINGACTIVITY).execute();
			}else{
				Log.d(TAG,"登陆微博");
				Bundle bundle = new Bundle();
				weiboData = new ArrayList<Weibo>();
				bundle.putSerializable("weibo_info", weiboData);
				intent = new Intent(LoadingActivity.this,LoginActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
