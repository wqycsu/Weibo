package com.wqy.weibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class Utils {
	private static Utils utils;
	private Context context;
	private static BitmapFactory.Options options;
	public static int screenWidth;
	public static int screenHeight;
	private static Matrix matrix;
	private Utils(Context context){
		this.context = context;
		getScreenSize();
	}
	
	public static Utils getInstatnce(Context context){
		if(utils==null){
			utils = new Utils(context);
		}
		return utils;
	}
	
	public static SpannableString changeTextToFace(Context context,  
            Spanned spanned, int lineHeight) {  
        String text = spanned.toString();  
        SpannableString spannableString = new SpannableString(spanned);  
        //解析表情
        Pattern pattern = Pattern.compile("\\[[^\\]]+\\]");  
  
        Matcher matcher = pattern.matcher(text);  
  
        boolean b = true;  
  
        while (b = matcher.find()) {  
            String faceText = text.substring(matcher.start(), matcher.end());  
//            android.util.Log.d("weibo","表情："+faceText);
            int resourceId = getResourceId(faceText);  
            if (resourceId > 0) {  
            	Drawable d = context.getResources().getDrawable(resourceId);
            	d.setBounds(0, 0, lineHeight, lineHeight);
                ImageSpan imageSpan = new ImageSpan(d,ImageSpan.ALIGN_BASELINE);  
                
                spannableString.setSpan(imageSpan, matcher.start(),  
                        matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
            }
//            Log.d("faceText-->"+faceText);
        }  
//        Log.d("spannableString-->"+spannableString);
        return spannableString;  
    }  
  
	//解析话题、@和超链接
    public static String atBlue(String s) {  
  
        StringBuilder sb = new StringBuilder();  
        int commonTextColor = Color.BLACK;  
        String signColor = "#72777b";  
  
        int state = 1;  
        String str = "";  
        for (int i = 0; i < s.length(); i++) {  
            switch (state) {  
            case 1: // 普通字符状态  
                // 遇到@  
                if (s.charAt(i) == '@') {  
                    state = 2;  
                    str += s.charAt(i);  
                }  
                // 遇到#  
                else if (s.charAt(i) == '#') {  
                    str += s.charAt(i);  
                    state = 3;  
                }  
                // 添加普通字符  
                else {  
                    if (commonTextColor == Color.BLACK)  
                        sb.append(s.charAt(i));  
                    else  
                        sb.append("<font color=\"" + commonTextColor + "\">"  
                                + s.charAt(i) + "</font>");  
                }  
                break;  
            case 2: // 处理遇到@的情况  
                // 处理@后面的普通字符  
                if (Character.isJavaIdentifierPart(s.charAt(i))) {  
                    str += s.charAt(i);  
                }  
  
                else {  
                    // 如果只有一个@，作为普通字符处理  
                    if ("@".equals(str)) {  
                        sb.append(str);  
                    }  
                    // 将@及后面的普通字符变成蓝色  
                    else {  
                        sb.append(setTextColor(str, signColor));  
                    }  
                    // @后面有#的情况，首先应将#添加到str里，这个值可能会变成蓝色，也可以作为普通字符，要看后面还有没有#了  
                    if (s.charAt(i) == '#') {  
  
                        str = String.valueOf(s.charAt(i));  
                        state = 3;  
                    }  
                    // @后面还有个@的情况，和#类似  
                    else if (s.charAt(i) == '@') {  
                        str = String.valueOf(s.charAt(i));  
                        state = 2;  
                    }  
                    // @后面有除了@、#的其他特殊字符。需要将这个字符作为普通字符处理  
                    else {  
                        if (commonTextColor == Color.BLACK)  
                            sb.append(s.charAt(i));  
                        else  
                            sb.append("<font color=\"" + commonTextColor + "\">"  
                                    + s.charAt(i) + "</font>");  
                        state = 1;  
                        str = "";  
                    }  
                }  
                break;  
            case 3: // 处理遇到#的情况  
                // 前面已经遇到一个#了，这里处理结束的#  
                if (s.charAt(i) == '#') {  
                    str += s.charAt(i);  
                    sb.append(setTextColor(str, signColor));  
                    str = "";  
                    state = 1;  
  
                }  
                // 如果#后面有@，那么看一下后面是否还有#，如果没有#，前面的#作废，按遇到@处理  
                else if (s.charAt(i) == '@') {  
                    if (s.substring(i).indexOf("#") < 0) {  
                        sb.append(str);  
                        str = String.valueOf(s.charAt(i));  
                        state = 2;  
  
                    } else {  
                        str += s.charAt(i);  
                    }  
                }  
                // 处理#...#之间的普通字符  
                else {  
                    str += s.charAt(i);  
                }  
                break;  
            }  
  
        }  
        if (state == 1 || state == 3) {  
            sb.append(str);  
        } else if (state == 2) {  
            if ("@".equals(str)) {  
                sb.append(str);  
            } else {  
                sb.append(setTextColor(str, signColor));  
            }  
  
        }  
  
        return sb.toString();  
  
    }  
  
    public static String setTextColor(String s, String color) {  
        String result = "<font color=\"" + color + "\">" + s + "</font>";  
  
        return result;  
    } 
    
    private static int getResourceId(String text){
    	int resId = 0;
    	int len = Config.DEFAULTEMOTIONS_TEXT.length;
    	for(int i=0;i<len;i++){
    		if(Config.DEFAULTEMOTIONS_TEXT[i].equals(text)){
    			return Config.EmotionGroupDefault[i];
    		}
    	}
    	return resId;
    }
    
    public void showToast(int resID){
    	Toast.makeText(context, context.getText(resID), Toast.LENGTH_LONG).show();
    }
    
    public void showToast(CharSequence text){
    	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    
    private void getScreenSize(){
    	DisplayMetrics dm = new DisplayMetrics();  
    	((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);  
    	  
    	float density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）    
    	  
    	screenWidth  = (int)(dm.widthPixels * density + 0.5f);      // 屏幕宽（px，如：480px）  
    	screenHeight = (int)(dm.heightPixels * density + 0.5f);     // 屏幕高（px，如：800px）
    }
}
