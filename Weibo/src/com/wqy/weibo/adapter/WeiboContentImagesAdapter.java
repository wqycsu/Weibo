package com.wqy.weibo.adapter;

import java.util.ArrayList;

import com.wqy.weibo.R;
import com.wqy.weibo.utils.ImageLoader;
import com.wqy.weibo.utils.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

public class WeiboContentImagesAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> imgurls;
	private GridView gridView;
	private ImageLoader mImageLoader;
	private Drawable d;
	/**
	 * 要在最后计算gridView的高度
	 */
	private boolean isLast = false;
	public WeiboContentImagesAdapter(Context context,ArrayList<String> imgurls,GridView gridView){
		this.context = context;
		this.imgurls = imgurls;
		this.gridView = gridView;
		mImageLoader = new ImageLoader(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(imgurls==null||imgurls.size()==0)
			return 0;
		return imgurls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imgurls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(position==imgurls.size()-1)
			isLast = true;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.weibo_content_img_item_layout, null);
			
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.weibo_content_gridview_img_cell);
			
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		if(imgurls.size()<=0)
			return null;
		d = context.getResources().getDrawable(R.drawable.chat_pic_loading);
		d.setBounds(0, 0, 100, 100);
		holder.imageView.setBackgroundDrawable(d);
		holder.imageView.setTag(imgurls.get(position));
		mImageLoader.displayImage(imgurls.get(position), holder.imageView, false);
//		new LoadImageTask(holder.imageView,convertView).execute(imgurls.get(position));
		return convertView;
	}

	class ViewHolder{
		private ImageView imageView;
	}
	
	class LoadImageTask extends AsyncTask<String, Integer, Bitmap>{
		private ImageView imgView;
		private View convertView;
		private String url;
		
		public LoadImageTask(ImageView imgview,View convertView) {
			this.imgView = imgview;
			this.convertView = convertView;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			synchronized(imgView){
				url = params[0];
				try {
					return mImageLoader.loadBitmap(url);
				} catch (Exception e) {
					
					e.printStackTrace();
					return null;
				} 
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if(result!=null){
				if(imgView.getTag().equals(url))
					imgView.setImageBitmap(result);
				if(isLast){
					setListViewHeightBasedOnChildren(gridView);
				}
			}
		}
		public void setListViewHeightBasedOnChildren(GridView gridView) {    
			ListAdapter listAdapter = gridView.getAdapter();     
			if (listAdapter == null) {    
				return;    
			}    
			int totalHeight = 0;  
			int row =1;
			//计算行数,每列最多显示三张图片
			if(listAdapter.getCount()<=3){
				row=1;
			}else{
				if(listAdapter.getCount()%3==0){
					row = listAdapter.getCount()/3;
				}else{
					row = listAdapter.getCount()/3+1;
				}
			}
			for (int i = 0; i < row; i++) {
				View listItem = listAdapter.getView(i, convertView, gridView);    
				listItem.measure(0, listItem.getHeight());    
				totalHeight += listItem.getMeasuredHeight();    
				totalHeight += 15;//行间距是15px
			}    
			ViewGroup.LayoutParams params = gridView.getLayoutParams();    
			params.height = totalHeight + 50;
			gridView.setLayoutParams(params);    
		}    
	}
}
