package com.wqy.weibo.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.wqy.weibo.R;
import com.wqy.weibo.activity.MainActivity;
import com.wqy.weibo.activity.WeiboDetailedContentActivity;
import com.wqy.weibo.adapter.HomeListAdapter;
import com.wqy.weibo.model.Weibo;
import com.wqy.weibo.pulldownlistview.PullDownRefreshListView;
import com.wqy.weibo.pulldownlistview.PullDownRefreshListView.OnLoadMoreListener;
import com.wqy.weibo.pulldownlistview.PullDownRefreshListView.OnRefreshListener;
import com.wqy.weibo.utils.Config;
import com.wqy.weibo.utils.GetWeiboInfoAsyncTask;
import com.wqy.weibo.utils.GetWeiboInfoFromNet;
import com.wqy.weibo.utils.SharedPreferenceHandler;

public class HomeFragment extends Fragment {

	private final String TAG = "weiquanyun";
	/**
	 * 下拉刷新的view
	 */
	private PullDownRefreshListView refreshView;
	/**
	 * 主页ListView的适配器
	 */
	private HomeListAdapter adapter;
	private SharedPreferenceHandler mHandler;
	
	private View rootView;
	/**
	 * 刷新请求时的since_id
	 */
	private String sinceId;
	
	private ArrayList<Weibo> weiboData;
	/**
	 * FragmentActivity和Fragment交互的回调
	 */
	public DataSetCallback mDataSetCallback;
	private Oauth2AccessToken token;
	/**
	 * 上拉加载更多调用该任务
	 */
	private GetWeiboInfoAsyncTask getWeiboInfoTask;
	/**
	 * 下拉刷新执行该任务
	 */
	private GetWeiboInfoFromNet getWeiboFromNetTask;
	private enum LoadMode{
		REFRESH,
		LOAD
	}
	/**
	 * 设置数据的回调借口
	 * @author Administrator
	 *
	 */
	public interface DataSetCallback{
		public ArrayList<Weibo> getWeiboData();
	}
	
	public static Handler finishRefreshHandler;
	public static final int FINISHED_REFRESH = 100;
	
	private ArrayList<Weibo> loadedData;
	
	private int lastSelectionIndex = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"HomeFragment onCreateView");
		rootView = inflater.inflate(R.layout.home_fragment_layout,container,false);
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG,"HomeFragment onCreate");
		mHandler = SharedPreferenceHandler.getSharedPreferenceHandler(getActivity().getApplicationContext());
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG,"HomeFragment onStart");
		weiboData = MainActivity.weiboData;
		token = mHandler.getAccessToken();
		init();
		loadedData = new ArrayList<Weibo>();
	}
	
	/**
	 * 初始化组件
	 */
	private void init(){
		
		// 下拉刷新上拉加载更多控件
		Log.d(TAG,"refreshView created in Home");
		refreshView = (PullDownRefreshListView) rootView.findViewById(R.id.home_list);
		adapter = new HomeListAdapter(this.getActivity(),weiboData);
		refreshView.setAdapter(adapter);
		refreshView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshView.post(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG,"pull down refresh...");
						loadedData.clear();
						sinceId = weiboData.get(0).getWeiboId();
						doLoadMore(LoadMode.REFRESH);
					}
				});
			}
		});// 这里id只是一个标志,用以区分不同页面上次下拉刷新的时间
		refreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				Log.d("weiquanyun","加载更多...");
				refreshView.initListFootView(adapter);
				loadedData.clear();
				lastSelectionIndex = refreshView.getLastVisiblePosition()-1;
				doLoadMore(LoadMode.LOAD);
			}
		});
		int p = mHandler.getPosition();
		int y = mHandler.getScrollY();

		refreshView.setSelectionFromTop(p, y);

		refreshView.setOnItemClickListener(new ListOnItemClickListener());
	}
	
	/**
	 * 测试模拟加载更多数据
	 */
	public void doLoadMore(final LoadMode mode) {
		if(mode==LoadMode.REFRESH){
			getWeiboFromNetTask = new GetWeiboInfoFromNet(getActivity(), loadedData, token,sinceId);
			getWeiboFromNetTask.execute();
		}else if(mode==LoadMode.LOAD){
			if(weiboData.size()>0){
				loadedData.add(weiboData.get(weiboData.size()-1));
			}
			getWeiboInfoTask = new GetWeiboInfoAsyncTask(getActivity(), loadedData, token, Config.COME_FROM_HOMEFRAGMENT);
			getWeiboInfoTask.execute();
		}
		finishRefreshHandler = new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg){
				if(msg.what==FINISHED_REFRESH){
					if(msg.obj!=null){
						loadedData = (ArrayList<Weibo>)msg.obj;
						Log.d(TAG,"loadData.size = "+loadedData.size());
					}
					onSuccess(mode);
				}
			}
		};
	}
	
	/**
	 * 数据加载成功
	 */
	public void onSuccess(LoadMode mode) {
		Log.d(TAG,"onSuccess()...");
//		items = testData.getCurrentPageItems(pageIndex);
		if(mode==LoadMode.REFRESH){
			adapter.addItems(loadedData, true);
		}else if(mode==LoadMode.LOAD){
			adapter.addItems(loadedData, false);
			refreshView.onLoadComplete();
			Log.d("weiquanyun","onSuccess->lastSelectionIndex:"+lastSelectionIndex);
			refreshView.setSelection(lastSelectionIndex);
		}
		if (refreshView.getRefreshState()) {
			Log.d(TAG,"finishRefreshing()...");
			refreshView.finishRefreshing();// 刷新完成隐藏下拉headerView
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG,"onDestroy()...");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d(TAG,"onDestroyView()...");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG,"onStop()...");
		//保存用户退出时所看的第一条微博ID，下次再次进入时应该获得小于该ID的微博，即发布比该微博早的微博
		if(weiboData.size()>0)
			mHandler.setLastWeiboId(weiboData.get(0).getWeiboId());
	}
	
	class ListOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Log.d(TAG,"parent.getId() = "+parent.getId());
			Bundle bundle = new Bundle();
			//bundle中存放的是放微博内容的HashMap
			bundle.putSerializable("weibo_content", weiboData.get(position));
			Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getActivity(),WeiboDetailedContentActivity.class);
			intent.putExtra("bundle", bundle);
			if (refreshView.getRefreshState()) {
				refreshView.finishRefreshing();// 刷新完成隐藏下拉headerView
			}
			getActivity().startActivity(intent);
		}
		
	}
}
