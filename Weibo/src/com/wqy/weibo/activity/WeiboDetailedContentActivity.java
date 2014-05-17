package com.wqy.weibo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.wqy.weibo.R;

public class WeiboDetailedContentActivity extends Activity {

	private ImageView back;
	private ImageView more;
	private final String TAG = "weiquanyun";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消窗口标题
		setContentView(R.layout.weibo_detailed_content_layout);
		init();
	}

	/**
	 * 初始化组件
	 */
	public void init(){
		back = (ImageView)findViewById(R.id.weibo_back);
		more = (ImageView)findViewById(R.id.weibo_more);
		back.setOnClickListener(new ButtonClickListener());
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	class ButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.equals(back)){
				Log.d(TAG,"onClick(back)");
				onBackPressed();
			}
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
