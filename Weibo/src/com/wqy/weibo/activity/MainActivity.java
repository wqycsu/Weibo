package com.wqy.weibo.activity;

import com.wqy.weibo.R;
import com.wqy.weibo.fragment.DiscoverFramgment;
import com.wqy.weibo.fragment.HomeFragment;
import com.wqy.weibo.fragment.MeFragment;
import com.wqy.weibo.fragment.MessageFragment;
import com.wqy.weibo.fragment.MoreFragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_main);
		inflater = LayoutInflater.from(this);
		fragmentManager = getSupportFragmentManager();
		resource = getResources();
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
	 * 初始化各个组件
	 */
	public void initViews(){
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, fragmentManager, R.id.main_content);
		int count = fragments.length;
		for(int i=0;i<count;i++){
			mTabSpec = mTabHost.newTabSpec(
					resource.getString(tabbarTextId[i])).setIndicator(
					getTabItemView(i));
			mTabHost.addTab(mTabSpec,fragments[i],null);
			
			//设置Tab按钮的背景
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tabbar_btn_bg);
		}
	}
	
	/**
	 * 给Tab按钮设置图标和文字
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

}
