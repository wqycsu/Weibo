package com.wqy.weibo.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wqy.weibo.utils.DBHelper;
import com.wqy.weibo.utils.Log;
import com.wqy.weibo.utils.TimeUtils;

/**
 * 微博信息
 * @author Administrator
 *
 */
public class Weibo implements Comparable<Weibo>,Serializable{
	/** 微博发送人头像URL */
	private String headImageUrl ;
	/** 微博发送人昵称 */
	private String weiboName ;
	/** 微博发送时间 */
	private String createAt ;
	/** 微博来自哪里 */
	private String comeFrom ;
	/** 微博内容 */
	private String weiboInfo ;
	/** 微博内容图片URL */
	private ArrayList<String> imgUrls ;
	/** 微博转发数量 */
	private String transCount ;
	/** 微博评论数量 */
	private String commentCount ;
	/** 微博点赞数量 */
	private String likeCount ;
	/** 微博ID */
	private String weiboId ;
	/**
	 * 是否收藏
	 */
	private boolean favorited = false;
	/**
	 * 是否是转发的，如果是，则有内容
	 */
	private Weibo retweed_weibo;
	
	/**
	 * 获取头像链接
	 * @return
	 */
	public String getHeadImageUrl() {
		return headImageUrl;
	}
	/**
	 * 设置头像链接
	 * @param headImageUrl
	 */
	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}
	/**
	 * 获取微博发送人昵称
	 * @return
	 */
	public String getWeiboName() {
		return weiboName;
	}
	/**
	 * 设置微博发送人昵称
	 * @param weiboName
	 */
	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}
	/**
	 * 获取微博发送时间
	 * @return
	 */
	public String getCreateAt() {
		return createAt;
	}
	/**
	 * 设置微博发送时间
	 * @param createAt
	 */
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	/**
	 * 获取微博发送平台
	 * @return
	 */
	public String getComeFrom() {
		return comeFrom;
	}
	/**
	 * 设置微博发送平台
	 * @param comeFrom
	 */
	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}
	/**
	 * 获取微博内容
	 * @return
	 */
	public String getWeiboInfo() {
		return weiboInfo;
	}
	/**
	 * 设置微博内容
	 * @param weiboInfo
	 */
	public void setWeiboInfo(String weiboInfo) {
		this.weiboInfo = weiboInfo;
	}
	/**
	 * 获取图片的url
	 * @return
	 */
	public ArrayList<String> getImgUrls() {
		return imgUrls;
	}
	/**
	 * 设置图片的url
	 * @param imgUrls
	 */
	public void setImgUrls(ArrayList<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	/**
	 * 获取转发数量
	 * @return
	 */
	public String getTransCount() {
		return transCount;
	}
	/**
	 * 设置转发数量
	 * @param transCount
	 */
	public void setTransCount(String transCount) {
		this.transCount = transCount;
	}
	/**
	 * 获取评论数量
	 * @return
	 */
	public String getCommentCount() {
		return commentCount;
	}
	/**
	 * 设定评论数量
	 * @param commentCount
	 */
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	/**
	 * 获取点赞数量
	 * @return
	 */
	public String getLikeCount() {
		return likeCount;
	}
	/**
	 * 设置点赞数量
	 * @param likeCount
	 */
	public void setLikeCount(String likeCount) {
		this.likeCount = likeCount;
	}
	/**
	 * 获取微博ID
	 * @return
	 */
	public String getWeiboId() {
		return weiboId;
	}
	/**
	 * 设置微博ID
	 * @param weiboId
	 */
	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}
	/**
	 * 判断是否收藏
	 * @return
	 */
	public boolean isFavorited() {
		return favorited;
	}
	/**
	 * 设置是否收藏
	 * @param favorited
	 */
	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}
	/**
	 * 获取转发微博内容
	 * @return
	 */
	public Weibo getRetweed_weibo() {
		return retweed_weibo;
	}
	/**
	 * 设置转发微博内容
	 * @param retweed_weibo
	 */
	public void setRetweed_weibo(Weibo retweed_weibo) {
		this.retweed_weibo = retweed_weibo;
	}
	
	/**
	 * 将JSONObject转为Weibo
	 * @param jsonObject
	 * @return
	 */
	public static ArrayList<Weibo> parseJson(JSONObject jsonObject,Context context){
		ArrayList<Weibo> weiboList = new ArrayList<Weibo>();
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("statuses");
//			Log.d("jsonArray.size = "+jsonArray.length());
			JSONObject jsonObjOfArray;
			JSONObject userObj;
			String weiboName;
			String headImgURL;
			ArrayList<String> contentURLs;
			JSONArray picJsonArray;
			String weiboInfo;
			String timeString;
			long weiboTime;
			String comeFrom;
			int transCount;
			int commentCount;
			int likeCount;
			String weiboId;
			for(int i=0;i<jsonArray.length();i++){
				Weibo weibo = new Weibo();
				Weibo retweetWeibo = null;
				JSONObject jsonOfRtWeibo = null;
				jsonObjOfArray = jsonArray.getJSONObject(i);
				userObj = jsonObjOfArray.getJSONObject("user");
				weiboName = userObj.getString("screen_name");
				headImgURL = userObj.getString("profile_image_url");
				picJsonArray = jsonObjOfArray.getJSONArray("pic_urls");
				contentURLs = new ArrayList<String>();
				for(int j=0;j<picJsonArray.length();j++){
					JSONObject imageItem = picJsonArray.getJSONObject(j);
					String thumbnail_pic_url = imageItem
							.getString("thumbnail_pic");
					contentURLs.add(thumbnail_pic_url);
				}
				weiboInfo = jsonObjOfArray.getString("text");
				timeString = jsonObjOfArray.getString("created_at");
				weiboTime = TimeUtils.stringTime2LongTime(timeString);
				comeFrom = getComeSource(jsonObjOfArray.getString("source"));
				transCount = jsonObjOfArray.getInt("reposts_count");
				commentCount = jsonObjOfArray.getInt("comments_count");
				likeCount = jsonObjOfArray.getInt("attitudes_count");
				weiboId = jsonObjOfArray.getString("id");
				try{
					jsonOfRtWeibo = jsonObjOfArray.getJSONObject("retweeted_status");
				}catch(JSONException e){
					e.printStackTrace();
				}
				weibo.setHeadImageUrl(headImgURL);
				weibo.setWeiboName(weiboName);
				weibo.setCreateAt(weiboTime+"");
				weibo.setComeFrom(comeFrom);
				weibo.setWeiboInfo(weiboInfo);
				weibo.setImgUrls(contentURLs);
				weibo.setTransCount(transCount+"");
				weibo.setCommentCount(commentCount+"");
				weibo.setLikeCount(likeCount+"");
				weibo.setWeiboId(weiboId);
				if(jsonOfRtWeibo!=null){
					retweetWeibo = parseWeibo(jsonOfRtWeibo,context);//转发的只有一条数据
					Log.d("retweeted","jsonOfRtWeibo = "+jsonOfRtWeibo);
					Log.d("retweeted","weiboName = "+weibo.getWeiboName());
				}else{
					retweetWeibo = null;
				}
				Log.d("weibo","微博名字："+weibo.getWeiboName()+" 转发微博名字："+retweetWeibo.getWeiboName());
				weibo.setRetweed_weibo(retweetWeibo);
				DBHelper.getDBHelperInstance(context).addWeibo(weibo);
//				Log.d("retweetWeibo","parseWeibo-->retweetWeibo = "+retweetWeibo);
				weiboList.add(weibo);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return weiboList;
	}
	
	/**
	 * 解析转发的源微博
	 */
	private static Weibo parseWeibo(JSONObject jsonObject,Context context){
		Weibo weibo = new Weibo();
//		Log.d("retweetWeibo","parseWeibo-->jsonObject = "+jsonObject);
		try {
			JSONObject userObj;
			String weiboName;
			String headImgURL;
			ArrayList<String> contentURLs;
			JSONArray picJsonArray;
			String weiboInfo;
			String timeString;
			long weiboTime;
			String comeFrom;
			int transCount;
			int commentCount;
			int likeCount;
			String weiboId;
			userObj = jsonObject.getJSONObject("user");
			weiboName = userObj.getString("screen_name");
			headImgURL = userObj.getString("profile_image_url");
			picJsonArray = jsonObject.getJSONArray("pic_urls");
			contentURLs = new ArrayList<String>();
			for(int j=0;j<picJsonArray.length();j++){
				JSONObject imageItem = picJsonArray.getJSONObject(j);
				String thumbnail_pic_url = imageItem
						.getString("thumbnail_pic");
				contentURLs.add(thumbnail_pic_url);
			}
			weiboInfo = jsonObject.getString("text");
			timeString = jsonObject.getString("created_at");
			weiboTime = TimeUtils.stringTime2LongTime(timeString);
			comeFrom = getComeSource(jsonObject.getString("source"));
			transCount = jsonObject.getInt("reposts_count");
			commentCount = jsonObject.getInt("comments_count");
			likeCount = jsonObject.getInt("attitudes_count");
			weiboId = jsonObject.getString("id");
			weibo.setHeadImageUrl(headImgURL);
			weibo.setWeiboName(weiboName);
			weibo.setCreateAt(weiboTime+"");
			weibo.setComeFrom(comeFrom);
			weibo.setWeiboInfo(weiboInfo);
			weibo.setImgUrls(contentURLs);
			weibo.setTransCount(transCount+"");
			weibo.setCommentCount(commentCount+"");
			weibo.setLikeCount(likeCount+"");
			weibo.setWeiboId(weiboId);
			weibo.setRetweed_weibo(null);
			Log.d("weibo","转发微博名字"+weibo.getWeiboName()+" ");
			DBHelper.getDBHelperInstance(context).addRetweetedWeibo(weibo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return weibo;
	}
	
	/**
	 * 获取微博来源
	 * @param comFrom
	 * @return
	 */
	private static String getComeSource(String comFrom){
		int start = comFrom.indexOf('>');
		int end = comFrom.lastIndexOf('<');
		return comFrom.substring(start+1, end);
	}
	
	/**
	 * 当前微博的创建时间小于another微博创建时间，即当前微博创建时间较早,在微博列表中就靠后
	 */
	@Override
	public int compareTo(Weibo another) {
		// TODO Auto-generated method stub
		if(this.getCreateAt().compareTo(another.getCreateAt())<0)
			return 1;
		else if(this.getCreateAt().compareTo(another.getCreateAt())>0)
			return -1;
		else
			return 0;
	}
}
