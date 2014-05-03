package com.wqy.weibo.adapter;

import java.util.ArrayList;

import com.wqy.weibo.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeListAdapter extends BaseAdapter {

	private ArrayList<String> testItems;
	private Activity context;
	//int page = 1;

	public HomeListAdapter(Activity context) {
		testItems = new ArrayList<String>();
		this.context = context;
	}

	public void addItems(ArrayList<String> newItems) {
		if (newItems == null || newItems.size() <= 0)
			return;
		//testItems.add("----加载第" + page + "页数据----");
		for (int i = 0; i < newItems.size(); i++) {
			testItems.add(newItems.get(i));
		}
		notifyDataSetChanged();
		//page++;
	}

	public void clear() {
		testItems.clear();
		//page = 1;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (testItems == null || testItems.size() <= 0)
			return 0;
		return testItems.size();
	}

	@Override
	public Object getItem(int position) {
		return testItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = context.getLayoutInflater().inflate(R.layout.homelist_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(testItems.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView title;
	}

}