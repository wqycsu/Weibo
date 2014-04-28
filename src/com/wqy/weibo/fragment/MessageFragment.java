package com.wqy.weibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wqy.weibo.R;

public class MessageFragment extends Fragment {

	private final String TAG = "weiquanyun";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"MessageFragment onCreateView");
		return inflater.inflate(R.layout.message_fragment_layout, container,false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG,"MessageFragment onCreate");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG,"MessageFragment onStart");
	}
}
