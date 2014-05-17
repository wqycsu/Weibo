package com.wqy.weibo.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.wqy.weibo.R;
import com.wqy.weibo.activity.LoginActivity;
import com.wqy.weibo.utils.DBHelper;
import com.wqy.weibo.utils.SharedPreferenceHandler;

public class MoreFragment extends Fragment {
	
	private final String TAG = "weiquanyun";
	private Button logoutButton;
	private View rootView;
	 /** 注销操作回调 */
    private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();
    
    private Oauth2AccessToken mAccessToken;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.more_fragment_layout, container,false);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		logoutButton = (Button)rootView.findViewById(R.id.logout);
		mAccessToken = SharedPreferenceHandler.getSharedPreferenceHandler(getActivity().getApplicationContext()).getAccessToken();
		// 注销按钮
		logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                	Log.d(TAG,"logout onclick...");
                    new LogoutAPI(mAccessToken).logout(mLogoutRequestListener);
                    DBHelper.getDBHelperInstance(getActivity()).clearDatabase();
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });
	}

	class LogOutRequestListener implements RequestListener{

		@Override
		public void onComplete(String response) {
			// TODO Auto-generated method stub
			if(!TextUtils.isEmpty(response)){
				try{
					JSONObject obj = new JSONObject(response);
					String value = obj.getString("result");
					if("true".equalsIgnoreCase(value)){
						SharedPreferenceHandler.getSharedPreferenceHandler(getActivity().getApplicationContext()).clearAccessToken();
						 mAccessToken = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
