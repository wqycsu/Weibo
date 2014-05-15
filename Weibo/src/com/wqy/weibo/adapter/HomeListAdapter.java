package com.wqy.weibo.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wqy.weibo.R;
import com.wqy.weibo.model.Weibo;
import com.wqy.weibo.pulldownlistview.PullDownRefreshListView;
import com.wqy.weibo.utils.ImageLoader;
import com.wqy.weibo.utils.Log;
import com.wqy.weibo.utils.TimeUtils;
import com.wqy.weibo.utils.Utils;
import com.wqy.weibo.utils.WeiboTextDispose;
import com.wqy.weibo.view.NoUnderlineClickableSpan;
import com.wqy.weibo.view.TextViewWithLink;

public class HomeListAdapter extends BaseAdapter {

	private ArrayList<Weibo> weiboData;
	private Context context;
	private String headImageUrl;
	private ImageLoader mImageLoader;
	private ArrayList<String> imgurls;
	private Weibo weibo;
	private Weibo retweetedWeibo;
	private LayoutInflater inflater;
	private ViewHolder holder = null;
	private int weiboTextLineHight;
	
	private SpannableString str;
	private NoUnderlineClickableSpan noUnderlineSpan;
	public HomeListAdapter(Context context,ArrayList<Weibo> weiboData) {
		this.weiboData = weiboData;
		this.context = context;
		this.mImageLoader = new ImageLoader(context);
		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * 向list中添加数据
	 * @param newItems
	 * @param addFirst true在顶部添加
	 */
	public void addItems(ArrayList<Weibo> newItems,boolean addFirst) {
		if (newItems == null || newItems.size() <= 0)
			return;
		if(addFirst){
			weiboData.addAll(0, newItems);
		}else{
			weiboData.addAll(newItems);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return weiboData.size();
	}

	@Override
	public Object getItem(int position) {
		return weiboData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.weibo_item_layout, null);
			holder.headImage = (ImageView) convertView.findViewById(R.id.head_icon);
			holder.weiboName = (TextView) convertView.findViewById(R.id.name);
			holder.sendTime = (TextView) convertView.findViewById(R.id.weibo_time);
			holder.comeFrom = (TextView) convertView.findViewById(R.id.from);
			holder.weiboInfo = (TextView) convertView.findViewById(R.id.weibo_content_text);
			//	ImageView weiboImage;
			holder.weiboContendImages = (GridView)convertView.findViewById(R.id.weibo_contentimgs_gridview);
			holder.transCount = (TextView)convertView.findViewById(R.id.transCount);
			holder.commentCount = (TextView)convertView.findViewById(R.id.commentCount);
			holder.likeCount = (TextView)convertView.findViewById(R.id.likeCount);
//			
			//获取其他的view
			holder.weiboHeader = (RelativeLayout)convertView.findViewById(R.id.weibo_header);
			holder.like = (TableRow)convertView.findViewById(R.id.addLike);
			holder.comment = (TableRow)convertView.findViewById(R.id.showComment);
			holder.trans = (TableRow)convertView.findViewById(R.id.transWeibo);
			holder.retweetedWeibo = (RelativeLayout)convertView.findViewById(R.id.weibo_retweet_content);
			
			//转发微博内容
			holder.rweiboInfo = (TextView)holder.retweetedWeibo.findViewById(R.id.retweeted_weibo_content_text);
			holder.rweiboContendImages = (GridView)holder.retweetedWeibo.findViewById(R.id.retweeted_weibo_contentimgs_gridview);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		weibo = weiboData.get(position);
		retweetedWeibo = weibo.getRetweed_weibo();
		holder.rweiboInfo.setTag(retweetedWeibo);
		holder.rweiboContendImages.setTag(retweetedWeibo);
		headImageUrl = weibo.getHeadImageUrl();
		holder.headImage.setTag(headImageUrl);
		//从网络获取头像
		mImageLoader.displayImage(headImageUrl, holder.headImage, false);
//		if(!PullDownRefreshListView.isBusy)
//			mImageLoader.displayImage(headImageUrl, holder.headImage, false);
//		else
//			mImageLoader.displayImage(headImageUrl, holder.headImage, true);
//		Log.d("name is null???"+weibo.get("name"));
		holder.weiboName.setText(weibo.getWeiboName());
		holder.sendTime.setText(TimeUtils.pareseTime(weibo.getCreateAt()));
		holder.comeFrom.setText(weibo.getComeFrom());
		//emoji表情有待解析
		weiboTextLineHight = holder.weiboInfo.getLineHeight();
//		Log.d("weiboTextLineHight = "+weiboTextLineHight);
		str = Utils.changeTextToFace(context, Html.fromHtml(Utils.atBlue(weibo.getWeiboInfo())),weiboTextLineHight);
		holder.weiboInfo.setText(str);
		holder.weiboInfo.setMovementMethod(TextViewWithLink.LocalLinkMovementMethod.getInstance());
//		Log.d(Utils.changeTextToFace(context, Html.fromHtml(Utils.atBlue(weibo.getWeiboInfo())),weiboTextLineHight).toString());
		//处理微博文字超链接等
//		WeiboTextDispose.disposeMentions(holder.weiboInfo);
		imgurls = weibo.getImgUrls();
//		Log.d("imgurls.size() = "+imgurls.size());
		holder.weiboContendImages.setAdapter(new WeiboContentImagesAdapter(context, imgurls, holder.weiboContendImages));
		holder.transCount.setText(weibo.getTransCount());
		holder.commentCount.setText(weibo.getCommentCount());
		holder.likeCount.setText(weibo.getLikeCount());
		Weibo rWeibo = (Weibo)holder.rweiboInfo.getTag();
		if(rWeibo!=null){
			Log.d("retweetedWeibo","HomeListAdapter:weiboName->"+weibo.getWeiboName());
			Log.d("retweetedWeibo","HomeListAdapter:rweiboName->"+retweetedWeibo.getWeiboName());
			//设置转发微博内容
			holder.rweiboInfo.setText("@"+rWeibo.getWeiboName()+" "+rWeibo.getWeiboInfo());
			holder.rweiboContendImages.setAdapter(new WeiboContentImagesAdapter(context, rWeibo.getImgUrls(), holder.rweiboContendImages));
		}else{
			holder.retweetedWeibo.setVisibility(View.GONE);
		}
		
		holder.weiboHeader.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.getInstatnce(context).showToast("微博头部被点击");
			}
			
		});
		holder.trans.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		holder.comment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		holder.like.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		holder.retweetedWeibo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return convertView;
	}
	
	private class ViewHolder{
		RelativeLayout weiboHeader;
		TableRow like;
		TableRow trans;
		TableRow comment;
		RelativeLayout retweetedWeibo;
		ImageView headImage;  
		TextView weiboName;
		TextView sendTime;
		TextView comeFrom;
		TextView weiboInfo;
		//	ImageView weiboImage;
		GridView weiboContendImages;
		TextView transCount;
		TextView commentCount;
		TextView likeCount;
		//转发微博信息
		TextView rweiboInfo;
		//	ImageView weiboImage;
		GridView rweiboContendImages;
	}
	
}