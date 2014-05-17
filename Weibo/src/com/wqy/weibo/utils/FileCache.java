package com.wqy.weibo.utils;

import android.content.Context;

public class FileCache extends AbstractFileCache{

	public FileCache(Context context) {
		super(context);
	}

	/**
	 * 获取url所指文件在cache中的路径
	 */
	@Override
	public String getSavePath(String url) {
		String filename = String.valueOf(url.hashCode());
		return getCacheDir() + filename;
	}

	/**
	 * 获取缓存目录路径
	 */
	@Override
	public String getCacheDir() {
		return Config.CACHE_DIR;
	}

}