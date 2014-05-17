package com.wqy.weibo.utils;

import com.wqy.weibo.R;

import android.os.Environment;

public class Config {
	public static final String APP_KEY = "2981531864";
	public static final String APP_SECRET = "edc8d0039048e2db8e87e0faba1f7433";
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static final String WEIBO_PATH = SDCARD_PATH+"/myweibo";
	/**
	 * 缓存头像文件夹 sdcard/myweibo/head_icons/
	 */
	public static final String HEADICON_DIR = WEIBO_PATH+"/head_icons/";
	/**
	 * cache目录
	 */
	public static final String CACHE_DIR = WEIBO_PATH+"/cache/";
	
	/** 判断加载数据的来源 
	 *  1:来自LoadingActivity
	 */
	public static final int COME_FROM_LOADINGACTIVITY = 1;
	/** 判断加载数据的来源 
	 *  2:来自LoginActivity
	 * */
	public static final int COME_FROM_LOGINACTIVITY = 2;
	/** 判断加载数据的来源
	 *  3:来自HomeFragment
	 *  */
	public static final int COME_FROM_HOMEFRAGMENT = 3;
	
	public static final int[] EmotionGroupDefault = new int[] {
		R.drawable.d_aini, R.drawable.d_aoteman, R.drawable.d_baibai,
		R.drawable.d_beishang, R.drawable.d_bishi, R.drawable.d_bizui,
		R.drawable.d_chanzui, R.drawable.d_chijing, R.drawable.d_dahaqi,
		R.drawable.d_ding, R.drawable.d_feizao, R.drawable.d_fennu,
		R.drawable.d_ganmao, R.drawable.d_guzhang, R.drawable.d_haha,
		R.drawable.d_haixiu, R.drawable.d_han, R.drawable.d_hehe,
		R.drawable.d_heixian, R.drawable.d_heng, R.drawable.d_huaxin,
		R.drawable.d_keai, R.drawable.d_kelian, R.drawable.d_ku,
		R.drawable.d_kun, R.drawable.d_landelini, R.drawable.d_lei,
		R.drawable.d_lvxing, R.drawable.d_nanhaier, R.drawable.d_nu,
		R.drawable.d_numa, R.drawable.d_nvhaier, R.drawable.d_qian,
		R.drawable.d_zuohengheng, R.drawable.d_zuoguilian,
		R.drawable.d_zhutou, R.drawable.d_zhuakuang, R.drawable.d_yun,
		R.drawable.d_youhengheng, R.drawable.d_yiwen, R.drawable.d_yinxian,
		R.drawable.d_xu, R.drawable.d_xixi, R.drawable.d_xiongmao,
		R.drawable.d_weiqu, R.drawable.d_wabishi, R.drawable.d_tuzi,
		R.drawable.d_tu, R.drawable.d_touxiao, R.drawable.d_taikaixin,
		R.drawable.d_sikao, R.drawable.d_shuijiao, R.drawable.d_shudaizi,
		R.drawable.d_shuai, R.drawable.d_shiwang, R.drawable.d_shengbing,
		R.drawable.d_qinqin,R.drawable.o_lazhu, R.drawable.o_huatong };

public static final String[] DEFAULTEMOTIONS_TEXT = { "[爱你]", "[奥特曼]", "[拜拜]",
		"[悲伤]", "[鄙视]", "[闭嘴]", "[馋嘴]", "[吃惊]", "[打哈气]", "[顶]", "[肥皂]", "[愤怒]", "[感冒]", "[鼓掌]",
		"[哈哈]", "[害羞]", "[汗]", "[呵呵]", "[黑线]", "[哼]", "[花心]", "[可爱]", "[可怜]", "[酷]", "[困]",
		"[懒得理你]", "[累]", "[旅行]", "[男孩儿]", "[怒]", "[怒骂]", "[女孩儿]", "[钱]", "[左哼哼]", "[做鬼脸]",
		"[猪头]", "[抓狂]", "[晕]", "[右哼哼]", "[疑问]", "[阴险]", "[嘘]", "[嘻嘻]", "[熊猫]", "[委屈]", "[挖鼻屎]",
		"[兔子]", "[吐]", "[偷笑]", "[太开心]", "[思考]", "[睡觉]", "[书呆子]", "[帅]", "[失望]", "[生病]", "[亲亲]","[蜡烛]",
		"[话筒]"};
}
