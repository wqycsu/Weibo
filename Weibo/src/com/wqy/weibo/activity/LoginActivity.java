package com.wqy.weibo.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.wqy.weibo.R;
import com.wqy.weibo.model.Weibo;
import com.wqy.weibo.utils.Config;
import com.wqy.weibo.utils.GetWeiboInfoAsyncTask;
import com.wqy.weibo.utils.GetWeiboInfoAsyncTask.FinishLoginActivityCallback;
import com.wqy.weibo.utils.SharedPreferenceHandler;

public class LoginActivity extends Activity implements OnClickListener,WeiboAuthListener{

	private Button login;
	private SharedPreferenceHandler handler;
	/**
	 * access_token
	 */
	private String token;
	private String uid;
	/**
	 * 认证实例
	 */
	private WeiboAuth mWeiboAuth;
	/**
	 * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  
	 */
    private Oauth2AccessToken mAccessToken;
    private Intent intent;
    private ArrayList<Weibo> weiboData;
    public static LoginActivity instance;
    private GetWeiboInfoAsyncTask getWeiboTast;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.actvity_login);
		instance = this;
	}

	@Override
	public void onStart(){
		super.onStart();
		init();
	}
	
	public void init(){
		login = (Button)findViewById(R.id.login_button);
		handler = SharedPreferenceHandler.getSharedPreferenceHandler(getApplication());
		mWeiboAuth = new WeiboAuth(this,Config.APP_KEY,Config.REDIRECT_URL,Config.SCOPE);
		mAccessToken = handler.getAccessToken();
		if(mAccessToken.isSessionValid()){
			//token is valid, to do something
			/**
			 * 其实这里不做什么，因为，如果验证过用户已登录，则不会进入登陆界面。
			 */
		}
		login.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(login)){
			if(!mAccessToken.isSessionValid())
				mWeiboAuth.anthorize(this);
		}
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Toast.makeText(LoginActivity.this, 
                "认证取消", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onComplete(Bundle bundle) {
		// TODO Auto-generated method stub
		mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
		if(mAccessToken.isSessionValid()){
			handler.setAccessToken(mAccessToken);
			Toast.makeText(LoginActivity.this, "获得access_token成功", Toast.LENGTH_SHORT).show();

			weiboData = (ArrayList<Weibo>)getIntent().getSerializableExtra("weibo_info");
			if(weiboData==null)
				weiboData = new ArrayList<Weibo>();
			getWeiboTast = new GetWeiboInfoAsyncTask(getBaseContext(), weiboData, mAccessToken,Config.COME_FROM_LOGINACTIVITY);
			getWeiboTast.setFinishLoginActivityCallback(new FinishLoginActivityCallback() {
				
				@Override
				public void finishLogin() {
					// TODO Auto-generated method stub
					LoginActivity.this.finish();
				}
			});
			getWeiboTast.execute();
		}
	}
	
	@Override
	public void onWeiboException(WeiboException exception) {
		// TODO Auto-generated method stub
		Toast.makeText(LoginActivity.this, 
                "认证异常:"+exception.getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		intent = null;
		mWeiboAuth = null;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		Message msg = LoadingActivity.handler.obtainMessage();
		msg.what = LoadingActivity.FINISHED;
		LoadingActivity.handler.sendMessage(msg);
	}
}
