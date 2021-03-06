package com.wqy.weibo.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.wqy.weibo.R;
import com.wqy.weibo.fragment.DiscoverFramgment;
import com.wqy.weibo.fragment.HomeFragment;
import com.wqy.weibo.fragment.HomeFragment.DataSetCallback;
import com.wqy.weibo.fragment.MeFragment;
import com.wqy.weibo.fragment.MessageFragment;
import com.wqy.weibo.fragment.MoreFragment;
import com.wqy.weibo.model.Weibo;
import com.wqy.weibo.utils.SharedPreferenceHandler;

public class MainActivity extends FragmentActivity implements DataSetCallback{

	private final String TAG = "weiquanyun";
	private LayoutInflater inflater;
	private int[] tabbarImgId = { R.drawable.tabbar_btn_home,
			R.drawable.tabbar_btn_message, R.drawable.tabbar_btn_me,
			R.drawable.tabbar_btn_discover, R.drawable.tabbar_btn_more };
	private int[] tabbarTextId = { R.string.tabbar_text_home,
			R.string.tabbar_text_message, R.string.tabbar_text_me,
			R.string.tabbar_text_discover, R.string.tabbar_text_more };
	private Class[] fragments = { HomeFragment.class, MessageFragment.class,
			MeFragment.class, DiscoverFramgment.class, MoreFragment.class }; 
	private FragmentTabHost mTabHost;
	private FragmentManager fragmentManager;
	private TabSpec mTabSpec;
	private Resources resource;
	public static ArrayList<Weibo> weiboData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"MainActivity onCreate()...");
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消窗口标题
		setContentView(R.layout.activity_main);
		inflater = LayoutInflater.from(this);
		fragmentManager = getSupportFragmentManager();
		resource = getResources();
		weiboData = (ArrayList<Weibo>)getIntent().getSerializableExtra("weibo_info");
		Log.d(TAG,"weiboData "+weiboData);
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	/**
	 * 初始化组件
	 */
	public void initViews(){
		//实例化FragmentTabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, fragmentManager, R.id.main_content);
		int count = fragments.length;
		for(int i=0;i<count;i++){
			mTabSpec = mTabHost.newTabSpec(
					resource.getString(tabbarTextId[i])).setIndicator(
					getTabItemView(i));
			mTabHost.addTab(mTabSpec,fragments[i],null);
			
			//设置tabhost按钮选中背景
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tabbar_btn_bg);
		}
	}
	
	/**
	 * 给Tab设置图片和文字
	 * @param index item id
	 */
	private View getTabItemView(int index){
		View view = inflater.inflate(R.layout.tabbar_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.tabbar_image);
		imageView.setImageResource(tabbarImgId[index]);
		
		TextView textView = (TextView) view.findViewById(R.id.tabbar_text);		
		textView.setText(tabbarTextId[index]);
	
		return view;
	}

	@Override
	public ArrayList<Weibo> getWeiboData() {
		// TODO Auto-generated method stub
		return weiboData;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		//程序退出时清除位置记录
		SharedPreferenceHandler.getSharedPreferenceHandler(this).setListViewPositionY(0, 0);
	}
}
