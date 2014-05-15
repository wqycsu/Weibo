package com.wqy.weibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

public class WeiboTextDispose {
	/**
	 * 处理@内容
	 * @param text 要处理的TextView
	 */
	public static void disposeMentions(TextView text){
		text.setAutoLinkMask(0);
		Pattern mentionPattern = Pattern.compile("@[\\u4e00-\\u9fa5\\w\\-]+");
		String mentionScheme = String.format("%s/?%s=", Defs.MENTIONS_SCHEMA,Defs.PARAM_UID);
		Linkify.addLinks(text, mentionPattern, mentionScheme,new MatchFilter(){

			@Override
			public boolean acceptMatch(CharSequence s, int start, int end) {
				// TODO Auto-generated method stub
				Log.d(s.toString());
				return s.charAt(end-1) !='+';
			}
			
		}, new TransformFilter(){

			@Override
			public String transformUrl(Matcher match, String url) {
				// TODO Auto-generated method stub
				return match.group();
			}} );
		
		Pattern trendsPattern = Pattern.compile("#(\\w+?)#");
		String trendsScheme = String.format("%s/?%s=", Defs.TRENDS_SCHEMA, Defs.PARAM_UID);
		Linkify.addLinks(text, trendsPattern, trendsScheme, null, new TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				return match.group(1); 
			}
		});
	}
}
