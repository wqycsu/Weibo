package com.wqy.weibo.fragment;

import java.util.ArrayList;

import com.wqy.weibo.R;
import com.wqy.weibo.adapter.HomeListAdapter;
import com.wqy.weibo.adapter.TestDataLoader;
import com.wqy.weibo.pulldownlistview.PullDownRefreshView;
import com.wqy.weibo.pulldownlistview.PullDownRefreshView.OnLoadMoreListener;
import com.wqy.weibo.pulldownlistview.PullDownRefreshView.PullDownToRefreshListener;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class HomeFragment extends Fragment{

	private final String TAG = "weiquanyun";
	/**
	 * 下拉刷新的view
	 */
	private PullDownRefreshView refreshView;
	/**
	 * 装载内容的listView
	 */
	private ListView listView;
	/**
	 * 主页ListView的适配器
	 */
	private HomeListAdapter adapter;
	
	private static final int HOME_ID = 1;
	private int pageIndex;
	public int DataSizePerPage = 10;
	TestDataLoader testData = new TestDataLoader();
	ArrayList<String> items = new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"HomeFragment onCreateView");
		return inflater.inflate(R.layout.home_fragment_layout,container,false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG,"HomeFragment onCreate");
		init();
	}
	
	/**
	 * 初始化组件
	 */
	private void init(){
		// 下拉刷新上拉加载更多控件
		refreshView = (PullDownRefreshView) getActivity().findViewById(R.id.pulldownlistview);
		listView = (ListView) refreshView.getChildAt(1);
		adapter = new HomeListAdapter(this.getActivity());
		listView.setAdapter(adapter);
		refreshView.setOnRefreshListener(new PullDownToRefreshListener() {
			@Override
			public void onRefresh() {
				refreshView.post(new Runnable() {
					@Override
					public void run() {
						refreshView.setOnLoadState(false, true); // 设置加载状态,参数为isFinished和isRefreshing
						pageIndex = 1;
						doLoadMore();
					}
				});
			}
		}, HOME_ID);// 这里id只是一个标志,用以区分不同页面上次下拉刷新的时间
		refreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				refreshView.setOnLoadState(false, false);
				pageIndex++;
				doLoadMore();
			}
		});
		doLoadMore();
	}
	
	/**
	 * 测试模拟加载更多数据
	 */
	public void doLoadMore() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				onSuccess();
			}
		}, 1500);
	}
	
	/**
	 * 数据加载成功
	 */
	public void onSuccess() {
		items = testData.getCurrentPageItems(pageIndex);
		if (refreshView.getRefreshState()) {
			if (adapter != null)
				adapter.clear();
			refreshView.finishRefreshing();// 刷新完成隐藏下拉headview
		}
		refreshView.setOnLoadState(false, false);
		refreshView.initListFootView(adapter); // 初始化加载更多的footview
		adapter.addItems(items);
		if ((items == null || items.size() == 0) && pageIndex == 1) {
			refreshView.removeListFootView(); // 移除加载更多的footview
			return;
		}
		if (items == null || items.size() < DataSizePerPage) {
			Toast.makeText(this.getActivity(), R.string.loading_data_finished, Toast.LENGTH_SHORT).show();
			refreshView.removeListFootView();
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG,"HomeFragment onStart");
	}
}
