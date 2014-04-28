package com.wqy.weibo.fragment;

import com.wqy.weibo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

	private final String TAG = "weiquanyun";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"HomeFragment onCreateView");
		return inflater.inflate(R.layout.home_fragment_layout,container,false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG,"HomeFragment onCreate");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG,"HomeFragment onStart");
	}
}
