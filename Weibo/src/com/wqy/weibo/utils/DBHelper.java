package com.wqy.weibo.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.wqy.weibo.model.Weibo;
/**
 * 缓存微博内容的数据库帮助类
 * @author Administrator
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	/**
	 * 数据库名
	 */
	public static final String DATABASE_NAME = "weibo.db";
	/**
	 * 微博内容表名
	 */
	public static final String WEIBOINFO_TABLE_NAME = "weibo_info";
	/**
	 * 联系人表名
	 */
	public static final String CONTACT_TABLE_NAME = "contact_info";
	/**
	 * 转发微博的源微博内容
	 */
	private static final String RETWEET_WEIBO_TABLE_NAME = "retweet_weibo";
	/**
	 * 数据库版本号
	 */
	private final static int version = 1;
	
	private final String INDEX_WEIBO_ID = "index_id";
	/**
	 * 存储微博的最大数量 80
	 */
	private final int MAX_CACHE_SIZE = 80;
	private static DBHelper mDBHelper;
	
	public static DBHelper getDBHelperInstance(Context context){
		if(mDBHelper==null)
			mDBHelper = new DBHelper(context);
		return mDBHelper;
	}
	private DBHelper(Context context){
		super(context, DATABASE_NAME, null, version);
	}
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/** 创建联系人表的SQL语句 */
		String createContactTableSQL = "CREATE TABLE IF NOT EXISTS "
				+ CONTACT_TABLE_NAME + "(" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ WeiboContactsColumns.CONTACT_NAME + " TEXT NOT NULL,"
				+ WeiboContactsColumns.FIRST_CHAR + " TEXT NOT NULL,"
				+ WeiboContactsColumns.IMAGE_URL + " TEXT NOT NULL,"
				+ WeiboContactsColumns.NAME_PINYIN + " TEXT NOT NULL)";
		/**
		 * 创建联系人表
		 */
		db.execSQL(createContactTableSQL);
		
		String createWeiboInfoTableSQL = "CREATE TABLE IF NOT EXISTS "
				+ WEIBOINFO_TABLE_NAME + "("
				+ WeiboInfoColumns.WEIBO_ID + " TEXT PRIMARY KEY NOT NULL, "
				+ WeiboInfoColumns.HEADIMAGE_URL + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBONAME + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBOTIME + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBO_INFO + " TEXT NOT NULL, "
				+ WeiboInfoColumns.IMAGE_URLS + " TEXT NOT NULL, "
				+ WeiboInfoColumns.COME_FROM + " TEXT NOT NULL, "
				+ WeiboInfoColumns.TRANS_COUNT + " TEXT NOT NULL, "
				+ WeiboInfoColumns.COMMENT_COUNT + " TEXT NOT NULL, "
				+ WeiboInfoColumns.LIKE_COUNT + " TEXT NOT NULL,"
				+ WeiboInfoColumns.RETWEETED_WEIBO_ID+ " TEXT )";
		db.execSQL(createWeiboInfoTableSQL);
		
		String createIndex = "CREATE INDEX IF NOT EXISTS " + INDEX_WEIBO_ID
				+ " ON " + WEIBOINFO_TABLE_NAME + "(" + WeiboInfoColumns.WEIBO_ID
				+ ")";
		db.execSQL(createIndex);
		
		String createRetweetWeiboInfoTableSQL = "CREATE TABLE IF NOT EXISTS "
				+ RETWEET_WEIBO_TABLE_NAME + "("
				+ WeiboInfoColumns.WEIBO_ID + " TEXT PRIMARY KEY NOT NULL, "
				+ WeiboInfoColumns.HEADIMAGE_URL + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBONAME + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBOTIME + " TEXT NOT NULL, "
				+ WeiboInfoColumns.WEIBO_INFO + " TEXT NOT NULL, "
				+ WeiboInfoColumns.IMAGE_URLS + " TEXT NOT NULL, "
				+ WeiboInfoColumns.COME_FROM + " TEXT NOT NULL, "
				+ WeiboInfoColumns.TRANS_COUNT + " TEXT NOT NULL, "
				+ WeiboInfoColumns.COMMENT_COUNT + " TEXT NOT NULL, "
				+ WeiboInfoColumns.LIKE_COUNT + " TEXT NOT NULL)";
		db.execSQL(createRetweetWeiboInfoTableSQL);
	}

	/**
	 * 通过联系人首字母查找联系人
	 * @param firstChar 首字母
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> findContactsByFirstChar(String firstChar){
		ArrayList<HashMap<String,Object>> contacts = new ArrayList<HashMap<String,Object>>();
		SQLiteDatabase db = getReadableDatabase();
		/**
		 * query 查询
		 * @param 表名，查询列(null表示所有列),查询条件,查询参数
		 */
		Cursor cursor = db.query(CONTACT_TABLE_NAME, null,
				WeiboContactsColumns.FIRST_CHAR + " = ? ", new String[] { firstChar },
				null, null, null);
		int i = 0;
		
		while(cursor.moveToNext()&&i<30){
			HashMap<String,Object> contact = new HashMap<String,Object>();
			contact.put(
					WeiboContactsColumns.CONTACT_NAME,
					cursor.getString(cursor
							.getColumnIndexOrThrow(WeiboContactsColumns.CONTACT_NAME)));
			contact.put(
					WeiboContactsColumns.FIRST_CHAR,
					cursor.getString(cursor
							.getColumnIndexOrThrow(WeiboContactsColumns.FIRST_CHAR)));
			contact.put(
					WeiboContactsColumns.IMAGE_URL,
					cursor.getString(cursor
							.getColumnIndexOrThrow(WeiboContactsColumns.IMAGE_URL)));
			contact.put(
					WeiboContactsColumns.NAME_PINYIN,
					cursor.getString(cursor
							.getColumnIndexOrThrow(WeiboContactsColumns.NAME_PINYIN)));
			contacts.add(contact);
			i++;
		}
		if(db.isOpen()){
			db.close();
		}
		cursor.close();
		return contacts;
	}
	
	/**
	 * 添加联系人
	 * @param name 昵称
	 * @param imageUrl 头像URL
	 * @param firstChar 昵称首字母
	 * @param namePinyin 昵称拼音
	 */
	public void addContact(String name,String imageUrl,String firstChar,String namePinyin){
		String insertSQL = "INSERT INTO " + CONTACT_TABLE_NAME + "("
				+ WeiboContactsColumns.CONTACT_NAME + ","
				+ WeiboContactsColumns.IMAGE_URL + ","
				+ WeiboContactsColumns.FIRST_CHAR + ","
				+ WeiboContactsColumns.NAME_PINYIN + ")" + " values(?,?,?,?)";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(insertSQL, new String[] {name,imageUrl,firstChar,namePinyin});
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	/**
	 * 清除联系人
	 */
	public void clearContacts(){
		SQLiteDatabase db = getWritableDatabase();
		String clearContactsSQL = "delete from "+CONTACT_TABLE_NAME;
		db.execSQL(clearContactsSQL);
		if(db!=null&&db.isOpen())
			db.close();
	}
	
	/**
	 * 清除缓存微博内容
	 */
	public void clearWeiboData(){
		SQLiteDatabase db = getWritableDatabase();
		String clearWeiboInfoSQL = "delete from "+WEIBOINFO_TABLE_NAME;
		db.execSQL(clearWeiboInfoSQL);
		if(db!=null&&db.isOpen())
			db.close();
	}
	/**
	 * 获取所有缓存的微博
	 * @return
	 */
	public ArrayList<HashMap<String,Object>> getAllWeiboInfo(){
		ArrayList<HashMap<String,Object>> allWeiboInfo = new ArrayList<HashMap<String,Object>>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(WEIBOINFO_TABLE_NAME, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			HashMap<String,Object> weiboInfo = new HashMap<String,Object>();
			weiboInfo.put(WeiboInfoColumns.WEIBO_ID, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.WEIBO_ID)));
			
			weiboInfo.put(WeiboInfoColumns.HEADIMAGE_URL, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.HEADIMAGE_URL)));
			
			weiboInfo.put(WeiboInfoColumns.WEIBONAME, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.WEIBONAME)));
			
			weiboInfo.put(WeiboInfoColumns.WEIBOTIME, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.WEIBOTIME)));
			
			weiboInfo.put(WeiboInfoColumns.WEIBO_INFO, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.WEIBO_INFO)));
			
			String urls = cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.IMAGE_URLS));
			weiboInfo.put(WeiboInfoColumns.IMAGE_URLS, changeToList(urls));
			
			weiboInfo.put(WeiboInfoColumns.COME_FROM, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.COME_FROM)));
			
			weiboInfo.put(WeiboInfoColumns.TRANS_COUNT, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.TRANS_COUNT)));
			
			weiboInfo.put(WeiboInfoColumns.COMMENT_COUNT, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.COMMENT_COUNT)));
			
			weiboInfo.put(WeiboInfoColumns.LIKE_COUNT, cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.LIKE_COUNT)));
			
			allWeiboInfo.add(weiboInfo);
		}
		if(db!=null&&db.isOpen())
			db.close();
		cursor.close();
		return allWeiboInfo;
	}
	
	/**
	 * 向数据库中增加新的微博内容 使用RPLACE INTO 防止插入重复数据
	 * @param weiboId
	 * @param weiboName
	 * @param weiboTime
	 * @param comeFrom
	 * @param headImageUrl
	 * @param weiboInfo
	 * @param contectImagUrls
	 * @param transCount
	 * @param likeCount
	 * @param commentCount
	 */
	public void addWeibo(String weiboId, String weiboName, String weiboTime,
			String comeFrom, String headImageUrl, String weiboInfo,
			ArrayList<String> contectImagUrls, String transCount,
			String likeCount, String commentCount) {
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(WEIBOINFO_TABLE_NAME, null, null, null, null, null, null);
		//超出缓存数量则清空
		if(cursor.moveToLast()){
			if(cursor.getCount()>=MAX_CACHE_SIZE){
				clearWeiboData();
			}
		}	
		db = getWritableDatabase();
		String insertSQL = "REPLACE INTO " + WEIBOINFO_TABLE_NAME + "("
				+ WeiboInfoColumns.WEIBO_ID + ","
				+ WeiboInfoColumns.HEADIMAGE_URL + ","
				+ WeiboInfoColumns.WEIBONAME + ","
				+ WeiboInfoColumns.WEIBOTIME + ","
				+ WeiboInfoColumns.WEIBO_INFO + ","
				+ WeiboInfoColumns.IMAGE_URLS + ","
				+ WeiboInfoColumns.COME_FROM + ","
				+ WeiboInfoColumns.TRANS_COUNT + ","
				+ WeiboInfoColumns.COMMENT_COUNT + ","
				+ WeiboInfoColumns.LIKE_COUNT
				+ ")" + " values(?,?,?,?,?,?,?,?,?,?)";
		db.execSQL(insertSQL, new String[] { weiboId, headImageUrl, weiboName,
				weiboTime, weiboInfo, changeToString(contectImagUrls), comeFrom, transCount,
				commentCount, likeCount });
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	/**
	 * 向数据库中添加微博
	 * @param weibo
	 */
	public void addWeibo(Weibo weibo){
		String weiboId = weibo.getWeiboId();
		String weiboName = weibo.getWeiboName();
		String weiboTime = weibo.getCreateAt();
		String comeFrom = weibo.getComeFrom();
		String headImageUrl = weibo.getHeadImageUrl();
		String weiboInfo = weibo.getWeiboInfo();
		ArrayList<String> contectImagUrls = weibo.getImgUrls();
		String transCount = weibo.getTransCount();
		String likeCount = weibo.getLikeCount();
		String commentCount = weibo.getCommentCount();
		Weibo retweetedWeibo = weibo.getRetweed_weibo();
		String retweetedWeiboId;
		if(retweetedWeibo==null)
			retweetedWeiboId = null;
		else
			retweetedWeiboId = retweetedWeibo.getWeiboId();
		Log.d("retweetedWeibo","DBhelper317:retweetedWeiboId = "+retweetedWeiboId);
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(WEIBOINFO_TABLE_NAME, null, null, null, null, null, null);
		//超出缓存数量则清空
		if(cursor.moveToLast()){
			if(cursor.getCount()>=MAX_CACHE_SIZE){
				clearWeiboData();
			}
		}	
		db = getWritableDatabase();
		String insertSQL = "REPLACE INTO " + WEIBOINFO_TABLE_NAME + "("
				+ WeiboInfoColumns.WEIBO_ID + ","
				+ WeiboInfoColumns.HEADIMAGE_URL + ","
				+ WeiboInfoColumns.WEIBONAME + ","
				+ WeiboInfoColumns.WEIBOTIME + ","
				+ WeiboInfoColumns.WEIBO_INFO + ","
				+ WeiboInfoColumns.IMAGE_URLS + ","
				+ WeiboInfoColumns.COME_FROM + ","
				+ WeiboInfoColumns.TRANS_COUNT + ","
				+ WeiboInfoColumns.COMMENT_COUNT + ","
				+ WeiboInfoColumns.LIKE_COUNT +","
				+ WeiboInfoColumns.RETWEETED_WEIBO_ID
				+ ")" + " values(?,?,?,?,?,?,?,?,?,?,?)";
		db.execSQL(insertSQL, new String[] { weiboId, headImageUrl, weiboName,
				weiboTime, weiboInfo, changeToString(contectImagUrls), comeFrom, transCount,
				commentCount, likeCount, retweetedWeiboId});
		
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	/**
	 * 向转发微博数据表中插入内容
	 */
	public void addRetweetedWeibo(Weibo retweetedWeibo){
		SQLiteDatabase db = getWritableDatabase();
		
		String insertRetweetedSQL = "REPLACE INTO " + RETWEET_WEIBO_TABLE_NAME + "("
				+ WeiboInfoColumns.WEIBO_ID + ","
				+ WeiboInfoColumns.HEADIMAGE_URL + ","
				+ WeiboInfoColumns.WEIBONAME + ","
				+ WeiboInfoColumns.WEIBOTIME + ","
				+ WeiboInfoColumns.WEIBO_INFO + ","
				+ WeiboInfoColumns.IMAGE_URLS + ","
				+ WeiboInfoColumns.COME_FROM + ","
				+ WeiboInfoColumns.TRANS_COUNT + ","
				+ WeiboInfoColumns.COMMENT_COUNT + ","
				+ WeiboInfoColumns.LIKE_COUNT
				+ ")" + " values(?,?,?,?,?,?,?,?,?,?)";
		db.execSQL(insertRetweetedSQL, new String[] { retweetedWeibo.getWeiboId(), retweetedWeibo.getHeadImageUrl(), retweetedWeibo.getWeiboName(),
				retweetedWeibo.getCreateAt(), retweetedWeibo.getWeiboInfo(), changeToString(retweetedWeibo.getImgUrls()), retweetedWeibo.getComeFrom(), 
				retweetedWeibo.getTransCount(),retweetedWeibo.getCommentCount(), retweetedWeibo.getLikeCount() });
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	
	/**
	 * 获取微博信息
	 * @return
	 */
	public ArrayList<Weibo> getWeibo(String fromWeiboId){
		ArrayList<Weibo> list = new ArrayList<Weibo>();
		int i = 0;
		SQLiteDatabase db = getReadableDatabase();
		String orderBy = WeiboInfoColumns.WEIBO_ID+" DESC";
		String selections = null;
		String [] selectionArgs = null;
		if(fromWeiboId!=null){
			//时间越晚，ID越大
			selections = " weibo_id < ?";
			selectionArgs = new String[]{fromWeiboId};
		}
		Cursor cursor = db.query(WEIBOINFO_TABLE_NAME, null, selections, selectionArgs, null, null, orderBy);
		while(cursor.moveToNext()&&i<20){
			Weibo weibo = getWeiboFromDatabase(cursor,db,true);
			list.add(weibo);
			i++;
			android.util.Log.d("weibo","get weibo from database:"+weibo.getWeiboId());
		}
		if(db!=null&&db.isOpen())
			db.close();
		cursor.close();
		return list;
	}
	
	/**
	 * 从给定的cursor中获取微博内容
	 * @param cursor 游标
	 * @param db 数据库 
	 * @param flag 标志是从哪个表中获取微博，false 转发微博表
	 * @return
	 */
	private Weibo getWeiboFromDatabase(Cursor cursor,SQLiteDatabase db,boolean flag){
		Weibo weibo = new Weibo();
		String weiboId ;
		String weiboName ;
		String weiboTime ;
		String comeFrom ;
		String headImageUrl ;
		String weiboInfo ;
		ArrayList<String> imgurls;
		String imgurl;
		String transCount ;
		String likeCount ;
		String commentCount ;
		Weibo retweetedWeibo = null;
		String retweetedWeiboId = null;
		weiboId = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.WEIBO_ID));
		
		headImageUrl = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.HEADIMAGE_URL));
		
		weiboName = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.WEIBONAME));
		
		weiboTime = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.WEIBOTIME));
		
		weiboInfo = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.WEIBO_INFO));
		
		imgurl = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.IMAGE_URLS));
		imgurls = changeToList(imgurl);
		
		
		comeFrom = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.COME_FROM));
		
		transCount = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.TRANS_COUNT));
		
		commentCount = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.COMMENT_COUNT));
		
		likeCount = cursor.getString(cursor
				.getColumnIndexOrThrow(WeiboInfoColumns.LIKE_COUNT));
		if(flag){
			retweetedWeiboId = cursor
					.getString(cursor
							.getColumnIndexOrThrow(WeiboInfoColumns.RETWEETED_WEIBO_ID));
		}
		weibo.setComeFrom(comeFrom);
		weibo.setCommentCount(commentCount);
		weibo.setCreateAt(weiboTime);
		weibo.setHeadImageUrl(headImageUrl);
		weibo.setImgUrls(imgurls);
		weibo.setTransCount(transCount);
		weibo.setLikeCount(likeCount);
		weibo.setWeiboId(weiboId);
		weibo.setWeiboInfo(weiboInfo);
		weibo.setWeiboName(weiboName);
		Log.d("retweetedWeiboId","DBHlper:retweetedWeiboId = "+retweetedWeiboId);
		if(retweetedWeiboId!=null){
			String sql = "select * from "+RETWEET_WEIBO_TABLE_NAME+" where retweet_weibo.weibo_id=?";
			Cursor c = db.rawQuery(sql, new String[]{retweetedWeiboId});
			if(c.moveToFirst())
				retweetedWeibo = getWeiboFromDatabase(c, db, false);
			c.close();
			Log.d("retweetedWeibo = "+retweetedWeibo);
		}
		weibo.setRetweed_weibo(retweetedWeibo);
		return weibo;
	}
	
	/**
	 * 获取数据库中存储的最大微博ID，即在数据库中缓存的发送时间最新的微博
	 * @return 微博ID
	 */
	public String getMaxWeiboId(){
		String weiboId = null;
		SQLiteDatabase db = getReadableDatabase();
		String orderBy = " ORDER BY "+WeiboInfoColumns.WEIBO_ID+" DESC";
		Cursor cursor = db.query(WEIBOINFO_TABLE_NAME, null, null, null, null, null, orderBy);
		if(cursor.moveToNext()){
			weiboId = cursor.getString(cursor
					.getColumnIndexOrThrow(WeiboInfoColumns.WEIBO_ID));
		}
		return weiboId;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String DROP_CONTACT_TABLE = "DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME;
		db.execSQL(DROP_CONTACT_TABLE);
		String DROP_WEIBOINFO_TABLE = "DROP TABLE IF EXISTS" + WEIBOINFO_TABLE_NAME;
		db.execSQL(DROP_WEIBOINFO_TABLE);
		onCreate(db);
		
	}
	
	/**
	 * 注销用户时清除用户的数据
	 */
	public void clearDatabase(){
		SQLiteDatabase db = getWritableDatabase();
		String DROP_CONTACT_TABLE = "DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME;
		db.execSQL(DROP_CONTACT_TABLE);
		String DROP_WEIBOINFO_TABLE = "DROP TABLE IF EXISTS " + WEIBOINFO_TABLE_NAME;
		db.execSQL(DROP_WEIBOINFO_TABLE);
		onCreate(db);
	}
	/**
	 * 微博联系人
	 * @author Administrator
	 *
	 */
	class WeiboContactsColumns implements BaseColumns{
		/** 联系人昵称 */
		public static final String CONTACT_NAME = "name";
		/** 联系人昵称首字母 */
		public static final String FIRST_CHAR = "name_firstchar";
		/** 联系人头像的URL */
		public static final String IMAGE_URL = "image_url";
		/** 联系人昵称拼音 */
		public static final String NAME_PINYIN = "name_pin";
	}
	/**
	 * 微博内容
	 * @author Administrator
	 *
	 */
	class WeiboInfoColumns implements BaseColumns{
		/** 微博发送人头像URL */
		public static final String HEADIMAGE_URL = "headImg_url";
		/** 微博发送人昵称 */
		public static final String WEIBONAME = "name";
		/** 微博发送时间 */
		public static final String WEIBOTIME = "time";
		/** 微博来自哪里 */
		public static final String COME_FROM = "come_from";
		/** 微博内容 */
		public static final String WEIBO_INFO = "weibo_info";
		/** 微博内容图片URL */
		public static final String IMAGE_URLS = "image_urls";
		/** 微博转发数量 */
		public static final String TRANS_COUNT = "trans_count";
		/** 微博评论数量 */
		public static final String COMMENT_COUNT = "comment_count";
		/** 微博点赞数量 */
		public static final String LIKE_COUNT = "like_count";
		/** 微博ID */
		public static final String WEIBO_ID = "weibo_id";
		/**
		 * 源微博ID
		 */
		public static final String RETWEETED_WEIBO_ID = "retweeted_weibo_id";
	}

	/**
	 * 将imageUrls转换为ArrayList
	 * @param urlsData
	 * @return
	 */
	public static ArrayList<String> changeToList(String urlsData){
		ArrayList<String> urlsList = new ArrayList<String>();
		if(urlsData.contains(",")){
			String[] urls = urlsData.split(",");
			for(int i=0;i<urls.length;i++){
				urlsList.add(urls[i]);
			}
		}else if(urlsData.length()>2){
			urlsList.add(urlsData.trim());
		}else{
			return new ArrayList<String>();
		}
		
		return  urlsList;
	}
	
	/**
	 * 将ArrayList转为String
	 * @param urlsData
	 * @return
	 */
	private String changeToString(ArrayList<String> urlsData){
		StringBuffer urlsString = new StringBuffer();
		if(urlsData!=null&&urlsData.size()>0){
			for(int i=0;i<urlsData.size();i++){
				String url = urlsData.get(i);
				if(i==0){
					urlsString.append(url);
				}else{
					urlsString.append(",");
					urlsString.append(url);
				}
			}
		}else{
			urlsString.append("");
		}
		return urlsString.toString();
	}
}
