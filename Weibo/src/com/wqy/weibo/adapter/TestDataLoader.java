package com.wqy.weibo.adapter;

import java.util.ArrayList;

public class TestDataLoader {
	ArrayList<String> items = new ArrayList<String>();

	public TestDataLoader() {
		for (int i = 0; i < 24; i++)
			items.add("测试数据--->" + i);
	}

	public ArrayList<String> getCurrentPageItems(int pageindex) {
		ArrayList<String> nextPageItems = new ArrayList<String>();
		// 最后一页数据
		if (items.size() - (pageindex - 1) * 10 <= 10 && items.size() - (pageindex - 1) * 10 > 0) {
			for (int i = 10 * (pageindex - 1); i < items.size(); i++) {
				nextPageItems.add(items.get(i));
			}
		} else if (items.size() - (pageindex - 1) * 10 <= 0) {
			return null;
		} else {
			for (int i = 10 * (pageindex - 1); i < pageindex * 10; i++) {
				nextPageItems.add(items.get(i));
			}
		}
		return nextPageItems;
	}
}
