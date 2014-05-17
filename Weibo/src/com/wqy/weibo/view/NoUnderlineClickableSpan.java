package com.wqy.weibo.view;

import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NoUnderlineClickableSpan extends ClickableSpan {

	
	public NoUnderlineClickableSpan(){
		
	}
	
	@Override
	public void updateDrawState(TextPaint ds) {
		// TODO Auto-generated method stub
		ds.setUnderlineText(false);
	}

	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		String url = ((TextView)widget).getText().toString();
		Log.d("weiquanyun","onClick-->url:"+url);
	}

}
