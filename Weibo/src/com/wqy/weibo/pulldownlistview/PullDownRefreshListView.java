package com.wqy.weibo.pulldownlistview;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wqy.weibo.R;
import com.wqy.weibo.utils.SharedPreferenceHandler;
import com.wqy.weibo.utils.Utils;

public class PullDownRefreshListView extends ListView implements OnScrollListener{

	private static final String TAG = "pulldownview";

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
	public static final int STATUS_DONE = 3;

	/**
	 * 正在加载 
	 */
	public static final int STATUS_LOADING = 4;

	private final float RATIO = 1.7f;

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
	private LinearLayout header;

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
	 * 加载更多前，记住选择的位置
	 */
	private int lastSelection;

	/**
	 * 可见的第一项
	 */
	private int firstItemIndex;

	private int lastVisibleItem;

	public static boolean isBusy;

	/**
	 * 防止多个界面的下拉刷新时间发生冲突用id区分
	 */
	private int updateId = -1;

	/**
	 * 下拉刷新的回调接口
	 */
	private OnRefreshListener refreshListener;

	/**
	 * 下拉刷新的监听器
	 * @author Administrator
	 *
	 */
	public interface OnRefreshListener{
		public void onRefresh();
	}

	private int scrollPos = 0;
	private int scrollTop;
	
	/**
	 * 设置ListView上拉加载监听
	 */
	public OnLoadMoreListener mLoadMoreListener;

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
	private int screenWidth;
	private int hideHeaderWidth;
	
	/**
	 * 下拉头的左padding
	 */
	private int leftPadding;
	/**
	 * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
	 * STATUS_REFRESHING 和 STATUS_FINISHED_REFRESH
	 */
	private int currentStatus = STATUS_DONE;

	/**
	 * 手指按下时的屏幕纵坐标
	 */
	private int startY;

	private boolean isBack;

	/**
	 * 判断是否正在加载
	 */
	private boolean isLoading;

	private boolean isRefreshable;

	public int pageindex = 1;
	
	/**
	 * 用于保证startY的值在一个完整的touch事件中只被记录一次
	 */
	private boolean isRecored;

	public View listFootView;

	private LayoutInflater inflater;
	private Context context;
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PullDownRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PullDownRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	/**
	 * @param context
	 */
	public PullDownRefreshListView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public void init(){
		Log.d(TAG,"PullDownRefreshView init()");
		sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
		inflater = LayoutInflater.from(context);
		header = (LinearLayout)inflater.inflate(R.layout.pulldown_header_view, null);
		headerArrow = (ImageView)header.findViewById(R.id.header_arrow);
		headerText = (TextView)header.findViewById(R.id.header_description);
		updateAt = (TextView)header.findViewById(R.id.header_update_at);
		headerProgressBar = (ProgressBar)header.findViewById(R.id.header_progress_bar);
		listFootView = inflater.inflate(R.layout.loading_foot_view, null);
		
		measureView(header);
		hideHeaderHeight = header.getMeasuredHeight();
		hideHeaderWidth = header.getMeasuredWidth();
		screenWidth = Utils.screenWidth;
		leftPadding = (screenWidth-hideHeaderWidth)/2;
		
		header.setPadding(0, -hideHeaderHeight, 0, 0);

		addHeaderView(header,null,false);
		addFooterView(listFootView,null,false);

		refreshUpdateAtText();

		listFootView.setVisibility(View.GONE);
		setOnScrollListener(this);

		isRefreshable = false;
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
			timeFormat = pastTime/ONE_HOUR;
			String value = timeFormat + "小时";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else if(pastTime<ONE_MONTH){
			timeFormat = pastTime/ONE_DAY;
			String value = timeFormat + "天";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else if(pastTime<ONE_YEAR){
			timeFormat = pastTime/ONE_MONTH;
			String value = timeFormat + "月";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}else{
			timeFormat = pastTime/ONE_YEAR;
			String value = timeFormat + "年";
			updateAtValue = String.format(context.getResources().getString(R.string.pulldown_updated_at), value);
		}
		updateAt.setText(updateAtValue);
	}

	/**
	 * 计算child View的尺寸
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
				0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		Log.d("weiquanyun","child = "+child);
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 设置下拉刷新监听器
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}


	/**
	 * 给上拉刷新控件注册一个监听器。
	 * @param loadMoreListener
	 *            监听器的实现。
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		mLoadMoreListener = loadMoreListener;
	}

	/**
	 * 当listview被触摸时，处理下拉刷新逻辑
	 */
	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = (int) event.getY();
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置");
					refreshUpdateAtText();
				}
				break;

			case MotionEvent.ACTION_UP:

				if (currentStatus != STATUS_REFRESHING && currentStatus != STATUS_LOADING) {
					if (currentStatus == STATUS_DONE) {
						// 什么都不做
					}
					if (currentStatus == STATUS_PULL_TO_REFRESH) {
						currentStatus = STATUS_DONE;
						changeHeaderViewByState();

						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
						currentStatus = STATUS_REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if(lastVisibleItem==getCount()-1&&currentStatus==STATUS_DONE&&!isLoading){
					int distance = tempY-startY;
//					Log.d("weiquanyun","distance = "+distance);
					if(distance<-100){
						onLoadMore();
					}
				}

				if (currentStatus != STATUS_REFRESHING && isRecored && currentStatus != STATUS_LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (currentStatus == STATUS_RELEASE_TO_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < hideHeaderHeight)
								&& (tempY - startY) > 0) {
							currentStatus = STATUS_PULL_TO_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							currentStatus = STATUS_DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (currentStatus == STATUS_PULL_TO_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= hideHeaderHeight) {
							currentStatus = STATUS_RELEASE_TO_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							currentStatus = STATUS_DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (currentStatus == STATUS_DONE) {
						if (tempY - startY > 0) {
							currentStatus = STATUS_PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (currentStatus == STATUS_PULL_TO_REFRESH) {
						header.setPadding(0, -hideHeaderHeight
								+ (int)((tempY - startY) / RATIO), 0, 0);

					}

					// 更新headView的paddingTop
					if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
						header.setPadding(0, (int)((tempY - startY) / RATIO)
								- hideHeaderHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}


	/**
	 * 翻转下拉和释放的箭头
	 */
	private void rotateArrow() {
		//Log.d(TAG,"rotateArrow()...");
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
		headerArrow.startAnimation(animation);
	}

	private void onLoadMore(){
		if(mLoadMoreListener!=null){
			isLoading = true;
			mLoadMoreListener.onLoadMore();
		}
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	/**
	 * 初始化listView的FootView控件
	 */
	public void initListFootView(BaseAdapter adapter) {
		listFootView.setVisibility(View.VISIBLE);
		setAdapter(adapter);
		setSelection(lastSelection+1);
	}

	/**
	 * 移除listView的FootView控件
	 */
	public void removeFooterView(){
		Log.d("weiquanyun","移除加载更多");
		listFootView.setVisibility(View.GONE);
		isLoading = false;
	}

	/**
	 * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
	 */
	public void finishRefreshing() {
		currentStatus = STATUS_DONE;
		sharedPreference.edit().putLong(UPDATE_AT + updateId, System.currentTimeMillis()).commit();
		changeHeaderViewByState();
	}

	/**
	 * 返回当前是否为刷新状态
	 * @return
	 */
	public boolean getRefreshState(){
		if(currentStatus==STATUS_REFRESHING)
			return true;
		else
			return false;
	}
	
	/**
	 * 加载完成
	 */
	public void onLoadComplete(){
		removeFooterView();
	}

	/**
	 * 执行点击刷新
	 */
	public void doClickRefresh(){
		currentStatus = STATUS_REFRESHING;
		changeHeaderViewByState();
		onRefresh();
	}
	
	/**
	 * 根据状态改变头部信息
	 */
	private void changeHeaderViewByState() {
		switch (currentStatus) {
		case STATUS_RELEASE_TO_REFRESH:
			headerArrow.setVisibility(View.VISIBLE);
			headerProgressBar.setVisibility(View.GONE);
			headerText.setVisibility(View.VISIBLE);
			updateAt.setVisibility(View.VISIBLE);

			rotateArrow();

			headerText.setText(R.string.release_refresh);

			Log.v(TAG, "当前状态，松开刷新");
			break;
		case STATUS_PULL_TO_REFRESH:
			headerProgressBar.setVisibility(View.GONE);
			headerText.setVisibility(View.VISIBLE);
			updateAt.setVisibility(View.VISIBLE);

			rotateArrow();

			// 是由STATUS_RELEASE_TO_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				rotateArrow();

				headerText.setText(R.string.pulldown_refresh);
			} else {
				headerText.setText(R.string.pulldown_refresh);
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case STATUS_REFRESHING:

			header.setPadding(0, 0, 0, 0);

			headerProgressBar.setVisibility(View.VISIBLE);
			headerArrow.clearAnimation();
			headerArrow.setVisibility(View.GONE);
			headerText.setText(R.string.pulldown_refreshing);
			updateAt.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case STATUS_DONE:
			header.setPadding(0, -1 * hideHeaderHeight, 0, 0);

			headerProgressBar.setVisibility(View.GONE);
			headerArrow.clearAnimation();
			headerArrow.setImageResource(R.drawable.pulldown_arrow);
			headerText.setText(R.string.pulldown_refresh);
			updateAt.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		//滑动时，设置为忙状态
		if(scrollState==SCROLL_STATE_IDLE){
			isBusy = false; 
			// scrollPos记录当前可见的List顶端的一行的位置   
			scrollPos = getFirstVisiblePosition();    
			View v=getChildAt(0);  
			scrollTop=(v==null)?0:v.getTop();
			SharedPreferenceHandler.getSharedPreferenceHandler(context).setListViewPositionY(scrollPos, scrollTop);
		}else{
			isBusy = true;
		}
		lastVisibleItem = view.getLastVisiblePosition();
		if((view.getLastVisiblePosition()+1)==view.getCount()&&scrollState==SCROLL_STATE_IDLE){
			lastSelection = view.getLastVisiblePosition();
			Log.d(TAG, "lastSelection = "+lastSelection);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		firstItemIndex = firstVisibleItem;
	}
}
