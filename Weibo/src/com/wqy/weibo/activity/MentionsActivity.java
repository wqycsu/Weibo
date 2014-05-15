package com.wqy.weibo.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.wqy.weibo.utils.Defs;
import com.wqy.weibo.utils.Log;

public class MentionsActivity extends Activity {
	
	private static final Uri PROFILE_URI = Uri.parse(Defs.MENTIONS_SCHEMA);
	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		extractUidFromUri();
		setTitle("Profile:Hello, "+ uid);
	}


	private void extractUidFromUri() {
		Uri uri = getIntent().getData();
		if (uri !=null&& PROFILE_URI.getScheme().equals(uri.getScheme())) {
			uid = uri.getQueryParameter(Defs.PARAM_UID);
			Log.d("uid from url: "+ uid);
		}
	}
}
