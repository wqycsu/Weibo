package com.wqy.weibo.pulldownlistview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.view.animation.RotateAnimation;

import com.wqy.weibo.R;

public class PullDownRefreshView extends LinearLayout implements OnTouchListener{

	/**
	 * 下拉刷新状态
	 */
	public static final int STATUS_PULL_TO_REFRESH = 0;
	
	/**
	 * 释放立即刷新状态
	 */
	public static final int STATUS_RELEASE_TO_REFRESH = 1;
	
	/**
	 * 正在刷新状态
	 */
	public static final int STATUS_REFRESHING = 2;
	
	/**
	 * 刷新完成或者未刷新状态
	 */
	public static final int STATUS_FINISHED_REFRESH = 3;
	
	/**
	 * 下拉头部回到原始位置的速度
	 */
	public static final int SCROLL_SPEED = -10;
	
	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间 60*1000
	 */
	public static final long ONE_MINUTE = 60000;

	/**
	 * 一小时的毫秒值，用于判断上次的更新时间 60 * ONE_MINUTE
	 */
	public static final long ONE_HOUR = 3600000;

	/**
	 * 一天的毫秒值，用于判断上次的更新时间 24 * ONE_HOUR
	 */
	public static final long ONE_DAY = 86400000;

	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;

	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;

	/**
	 * 用于获得上次更新的时间，作为SharedPreference的键值
	 */
	public static final String UPDATE_AT = "update_at";
	
	/**
	 * 存储更新时间的SharedPreference
	 */
	private SharedPreferences sharedPreference;
	
	/**
	 * 上次下拉更新的时间（ 毫秒）
	 */
	private long lastUpdataAt;
	
	/**
	 * 下拉ListView的头部，显示箭头和刷新进度条
	 */
	private View header;
	
	/**
	 * 下拉的ListView
	 */
	private ListView listView;
	
	/**
	 * 刷新显示的进度条
	 */
	private ProgressBar headerProgressBar;
	
	/**
	 * 下拉和释放显示的箭头
	 */
	private ImageView headerArrow;
	
	/**
	 * 下拉和释放显示的文字
	 */
	private TextView headerText;
	
	/**
	 * 更新时间
	 */
	private TextView updateAt;
	
	/**
	 * 下拉头的布局参数
	 */
	private MarginLayoutParams headerLayoutParams;
	
	/**
	 * 防止多个界面的下拉刷新时间发生冲突用id区分
	 */
	private int updateId = -1;
	
	/**
	 * ListView上拉监听的第一项，可见项和总项
	 */
	private int firstItem, visibleItem, totalItem;

	/**
	 * 下拉刷新的回调接口
	 */
	private PullDownToRefreshListener mListener;
	
	/**
	 * 下拉刷新的监听器
	 * @author Administrator
	 *
	 */
	public interface PullDownToRefreshListener{
		public void onRefresh();
	}
	
	/**
	 * 设置刷新监视器
	 * @param mListener
	 */
	public void setOnRefreshListener(PullDownToRefreshListener mListener,int id){
		this.mListener = mListener;
		this.updateId = id;
	}
	
	/**
	 * 设置ListView上拉加载监听
	 */
	private OnLoadMoreListener mloadMoreListener;

	/**
	 * 上拉加载的监听器
	 * @author Administrator
	 *
	 */
	public interface OnLoadMoreListener{
		public void onLoadMore();
	}
	
	/**
	 * 设置初始数据加载状态为未完成
	 */
	public boolean misFinished = false;
	
	/**
	 * 设置初始数据加载状态为下拉刷新
	 */
	public boolean misRefreshing = false;

	/**
	 * 下拉头的高度
	 */
	private int hideHeaderHeight;

	/**
	 * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
	 * STATUS_REFRESHING 和 STATUS_FINISHED_REFRESH
	 */
	private int currentStatus = STATUS_FINISHED_REFRESH;

	/**
	 * 记录上一次的状态是什么，避免进行重复操作
	 */
	private int lastStatus = currentStatus;

	/**
	 * 手指按下时的屏幕纵坐标
	 */
	private float yDown;

	/**
	 * 在被判定为滚动之前用户手指可以移动的最大值。
	 */
	private int touchSlop;

	/**
	 * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
	 */
	private boolean loadOnce;

	/**
	 * 当前是否可以下拉，只有ListView滚动到头的时候才允许下拉
	 */
	private boolean ableToPull;
	
	/**
	 * 主页Fragment的layout
	 */
	private RelativeLayout homeFragmentLayout;
	
	public int pageindex = 1;
	public int DataSizePerPage = 10;

	public View listFootView;
	public RefreshingTask refreshTask;
	
	private LayoutInflater inflater;
	private Context context;
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PullDownRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PullDownRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public PullDownRefreshView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public void init(){
		sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.pulldown_header_view, null);
		homeFragmentLayout = (RelativeLayout)inflater.inflate(R.layout.home_fragment_layout, this);
		headerArrow = (ImageView)header.findViewById(R.id.header_arrow);
		headerText = (TextView)header.findViewById(R.id.header_description);
		updateAt = (TextView)header.findViewById(R.id.header_update_at);
		headerProgressBar = (ProgressBar)header.findViewById(R.id.header_progress_bar);
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		listView = (ListView)homeFragmentLayout.findViewById(R.id.home_list);
		setOrientation(VERTICAL);
		addView(header, 0);
	}
	
	/**
	 * 刷新下拉头部中更新时间描述
	 */
	public void refreshUpdateAtText(){
		lastUpdataAt = sharedPreference.getLong(UPDATE_AT+updateId, -1);
		long currentTime = System.currentTimeMillis();
		long pastTime = currentTime - lastUpdataAt;
		long timeFormat = 0;
		String updateAtValue;
		if(lastUpdataAt==-1){
			updateAtValue = context.getResources().getString(R.string.pulldown_updated_just_now);
		}else if(pastTime<0){
			updateAtValue = context.getResources().getString(R.string.pulldown_time_error);
		}else if(pastTime<ONE_MINUTE){
			updateAtValue = context.getResources().getString(R.string.pulldown_updated_just_now);
		}else if(pastTime<ONE_HOUR){
			timeFormat = pastTime/ONE_MINUTE;
			String value = timeFormat + "分";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else if(pastTime<ONE_DAY){
			timeFormat = timeFormat/ONE_HOUR;
			String value = timeFormat + "小时";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else if(pastTime<ONE_MONTH){
			timeFormat = timeFormat/ONE_DAY;
			String value = timeFormat + "天";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else if(pastTime<ONE_YEAR){
			timeFormat = timeFormat/ONE_MONTH;
			String value = timeFormat + "月";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else{
			timeFormat = timeFormat/ONE_YEAR;
			String value = timeFormat + "年";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}
		updateAt.setText(updateAtValue);
	}
	
	/**
	 * 讲下拉头向上偏移进行隐藏
	 */
	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b){
		super.onLayout(changed, l, t, r, b);
		if(changed&&!loadOnce){
			hideHeaderHeight = -header.getHeight();
			headerLayoutParams = (MarginLayoutParams)header.getLayoutParams();
			headerLayoutParams.topMargin = hideHeaderHeight;
			listView.setOnTouchListener(this);
			listView.setOnScrollListener(new OnScrollListener(){

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub
					if(isRefreshing()){
						return;
					}
					if(firstItem+visibleItem==totalItem&&!misFinished&&scrollState!=SCROLL_STATE_FLING){
						mloadMoreListener.onLoadMore();
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					if(isRefreshing())
						return;
					if(misFinished)
						return;
					firstItem = firstVisibleItem;
					visibleItem = visibleItemCount;
					totalItem = totalItemCount;
				}
				
			});
			loadOnce = true;
		}
	}

	/**
	 * 给上拉刷新控件注册一个监听器。
	 * 
	 * @param loadMoreListener
	 *            监听器的实现。
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		mloadMoreListener = loadMoreListener;
	}
	
	/**
	 * 下拉刷新时去掉上拉加载更多的listFootView
	 * 
	 * @return
	 */
	public boolean isRefreshing() {
		if (misRefreshing) {
			if (listFootView != null) {
				listView.removeFooterView(listFootView);
				listFootView = null;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 传递数据加载状态是否加载完成
	 * 
	 * @param isFinished
	 */
	public void setOnLoadState(boolean isFinished, boolean isRefreshing) {
		misFinished = isFinished;
		misRefreshing = isRefreshing;
	}
	
	/**
	 * 返回数据刷新状态
	 * 
	 * @return
	 */
	public boolean getRefreshState() {
		return misRefreshing;
	}
	
	/**
	 * 当listview被触摸时，处理下拉刷新逻辑
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		setIsAbleToPull(event);
		if(ableToPull){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				yDown = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float yMove = event.getRawY();
				int distance = (int)(yMove-yDown);
				// 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
				if(distance<=0||headerLayoutParams.topMargin<=hideHeaderHeight){
					return false;
				}
				if(distance<touchSlop){
					return false;
				}
				if(misRefreshing){
					return false;
				}
				if(currentStatus!=STATUS_REFRESHING){
					if(headerLayoutParams.topMargin>0){
						currentStatus = STATUS_RELEASE_TO_REFRESH;
					}else{
						currentStatus = STATUS_PULL_TO_REFRESH;
					}
					// 通过偏移下拉头的topMargin值，来实现下拉效果
					headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
					header.setLayoutParams(headerLayoutParams);
				}
				break;
			case MotionEvent.ACTION_UP:
			default:
				if(currentStatus==STATUS_RELEASE_TO_REFRESH){
					//如果释放时的状态是释放刷新，则调用刷新的任务
					refreshTask = new RefreshingTask();
					refreshTask.execute();
				}else if(currentStatus==STATUS_PULL_TO_REFRESH){
					// 松手时如果是下拉状态，就去调用隐藏下拉头的任务
					new HideHeaderTask().execute();
				}
				break;
			}
			//时刻记得下拉状态
			if(currentStatus==STATUS_PULL_TO_REFRESH||currentStatus==STATUS_RELEASE_TO_REFRESH){
				updateHeaderView();
				//当前正处于下拉或者释放状态，要让listView失去焦点，否则会触发点击事件
				listView.setPressed(false);
				listView.setFocusable(false);
				listView.setFocusableInTouchMode(false);
				lastStatus = currentStatus;
				return true;
			}
		}
		return true;
	}

	/**
	 * 更新下拉头信息
	 */
	private void updateHeaderView() {
		// TODO Auto-generated method stub
		if(lastStatus!=currentStatus){
			if(currentStatus==STATUS_RELEASE_TO_REFRESH){
				headerText.setText(R.string.release_refresh);
				headerProgressBar.setVisibility(View.GONE);
				headerArrow.setVisibility(View.VISIBLE);
				rotateArrow();
			}else if(currentStatus==STATUS_PULL_TO_REFRESH){
				headerText.setText(R.string.pulldown_refresh);
				headerProgressBar.setVisibility(View.GONE);
				headerArrow.setVisibility(View.VISIBLE);
				rotateArrow();
			}else if (currentStatus == STATUS_REFRESHING){
				headerText.setText(R.string.pulldown_refreshing);
				headerProgressBar.setVisibility(View.VISIBLE);
				headerArrow.clearAnimation();
				headerArrow.setVisibility(View.GONE);
			}
			refreshUpdateAtText();
		}
	}

	/**
	 * 翻转下拉和释放的箭头
	 */
	private void rotateArrow() {
		// TODO Auto-generated method stub
		float pivotX = headerArrow.getWidth()/2f;
		float pivotY = headerArrow.getHeight()/2f;
		
		float fromDegree = 0f;
		float toDegree = 0f;
		
		if(currentStatus==STATUS_PULL_TO_REFRESH){
			fromDegree = 180f;
			toDegree = 360f;
		} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
			fromDegree = 0f;
			toDegree = 180f;
		}
		RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, pivotX, pivotY);
		animation.setDuration(100);
		animation.setFillAfter(true);
		headerArrow.setAnimation(animation);
	}

	/**
	 * 根据当前ListView状态值设置{@link #ableToPull}的值，每次都是在onTouch中第一个执行，
	 * 这样就可以判断ListView应该刷新还是滚动
	 * @param event
	 */
	private void setIsAbleToPull(MotionEvent event) {
		// TODO Auto-generated method stub
		View firstChild = listView.getChildAt(0);
		if(firstChild!=null){
			int firstVisblePos = listView.getFirstVisiblePosition();
			if(firstVisblePos==0&&firstChild.getTop()==0){
				if(!ableToPull){
					yDown = event.getRawY();
					//如果ListView的首个item的上边距为0，则说明滚到了顶部，可以下拉刷新
					ableToPull = true;
				}
			}else{
				if(headerLayoutParams.topMargin!=hideHeaderHeight){
					headerLayoutParams.topMargin = hideHeaderHeight;
					header.setLayoutParams(headerLayoutParams);
				}
				ableToPull = false;
			}
		}else{
			//如果ListView中没有数据，也允许刷新
			ableToPull = true;
		}
	}
	
	/**
	 * 执行刷新的任务
	 * @author Administrator
	 *
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(mListener!=null){
				mListener.onRefresh();
			}
			int topMargin = headerLayoutParams.topMargin;
			while(true){
				topMargin = topMargin + SCROLL_SPEED;
				if(topMargin<=0){
					topMargin = 0;
					break;
				}
				publishProgress(topMargin);
				sleep(100);
			}
			currentStatus = STATUS_REFRESHING;
			publishProgress(0);
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			headerLayoutParams.topMargin = values[0];
			header.setLayoutParams(headerLayoutParams);
		}

	}
	
	/**
	 * 刷新完成或者未刷新时隐藏头部的任务
	 * @author Administrator
	 *
	 */
	class HideHeaderTask extends AsyncTask<Void, Integer, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int topMargin = headerLayoutParams.topMargin;
			while(true){
				topMargin = topMargin + SCROLL_SPEED;
				if(topMargin<=hideHeaderHeight){
					topMargin = hideHeaderHeight;
					break;
				}
				publishProgress(topMargin);
				sleep(100);
			}
			return topMargin;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			headerLayoutParams.topMargin = result;
			header.setLayoutParams(headerLayoutParams);
			currentStatus = STATUS_FINISHED_REFRESH;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			headerLayoutParams.topMargin = values[0];
			header.setLayoutParams(headerLayoutParams);
		}
		
	}
	
	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param time
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化listview的footview控件
	 */
	public void initListFootView(BaseAdapter adapter) {
		if (listFootView != null)
			return;
		listFootView = ((Activity) context).getLayoutInflater().inflate(R.layout.loading_foot_view, null);
		listView.addFooterView(listFootView);
		listView.setAdapter(adapter);
	}

	/**
	 * 移除listview的footview控件
	 */
	public void removeListFootView() {
		if (listFootView != null) {
			listView.removeFooterView(listFootView);
			listFootView = null;
		}
		setOnLoadState(true, false);
	}
	
	/**
	 * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
	 */
	public void finishRefreshing() {
		currentStatus = STATUS_FINISHED_REFRESH;
		sharedPreference.edit().putLong(UPDATE_AT + updateId, System.currentTimeMillis()).commit();
		new HideHeaderTask().execute();
	}
}
