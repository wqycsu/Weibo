package com.wqy.weibo.activity;

import com.wqy.weibo.utils.Defs;
import com.wqy.weibo.utils.Log;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
/**
 * 处理话题Activity
 * @author Administrator
 *
 */
public class TrendActivity extends Activity {

	private static final Uri PROFILE_URI = Uri.parse(Defs.TRENDS_SCHEMA);

	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		extractUidFromUri();
		setTitle("Profile1:Hello, "+ uid);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void extractUidFromUri() {
		Uri uri = getIntent().getData();
		if (uri !=null&& PROFILE_URI.getScheme().equals(uri.getScheme())) {
			uid = uri.getQueryParameter(Defs.PARAM_UID);
			Log.d("uid from url: "+ uid);
		}
	}

}
