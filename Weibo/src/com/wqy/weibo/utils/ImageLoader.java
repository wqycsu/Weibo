package com.wqy.weibo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

/**
 * 多线程下载图片
 * @author Administrator
 *
 */
public class ImageLoader {
	private MemoryCache memoryCache = new MemoryCache();
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	private ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	/**
	 * 显示图片
	 * @param url
	 * @param imageView 
	 * @param isLoadOnlyFromCache 是否是从缓存中加载
	 */
	public void displayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		url = (String)(imageView.getTag());
		imageViews.put(imageView, url);
		
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if(bitmap==null){
			File f = fileCache.getFile(url);
			if (f != null && f.exists()){
				bitmap = decodeFile(f);
			}
		}
		if (bitmap != null){
			imageView.setBackgroundDrawable(null);
			imageView.setImageBitmap(bitmap);
		}
		else if (!isLoadOnlyFromCache){
			// 若没有的话则开启新线程加载图片
			loadImage(url, imageView);
		}
	}

	/**
	 * 使用线程池加载图片
	 * @param url
	 * @param imageView
	 */
	private void loadImage(String url, ImageView imageView) {
		ImageToLoad p = new ImageToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	/**
	 * 从url中加载图片 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);
		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}
		// 最后从指定的url中下载图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			copyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 150;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			Log.d("gridview","width_tmp = "+width_tmp+" ,height_tmp = "+height_tmp);
			//如果原图片近似为正方形，则转为正方形
			if(width_tmp/(float)height_tmp<1.3f|height_tmp/(float)width_tmp<1.3f){
				
			}
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public Bitmap loadBitmap(String url){
		Bitmap bitmap = memoryCache.get(url);
		if(bitmap==null)
			bitmap = decodeFile(fileCache.getFile(url));
		if(bitmap!=null){
			return bitmap;
		}else{
			try {
				URL imageUrl = new URL(url);
				InputStream is = imageUrl.openStream();
				bitmap = BitmapFactory.decodeStream(is);
				memoryCache.put(url, bitmap);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	/**
	 * 保存Url和ImageView对应关系的类
	 * @author Administrator
	 *
	 */
	private class ImageToLoad {
		public String url;
		public ImageView imageView;

		public ImageToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		ImageToLoad imageToLoad;

		PhotosLoader(ImageToLoad imageToLoad) {
			this.imageToLoad = imageToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(imageToLoad))
				return;
			Bitmap bmp = getBitmap(imageToLoad.url);
			memoryCache.put(imageToLoad.url, bmp);
			if (imageViewReused(imageToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, imageToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) imageToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位，判断图片是否重用
	 * @param photoToLoad 要加载的图片
	 * @return
	 */
	 boolean imageViewReused(ImageToLoad photoToLoad) {
		 String tag = imageViews.get(photoToLoad.imageView);
		 if (tag == null || !tag.equals(photoToLoad.url))
			 return true;
		 return false;
	 }

	 // 用于在UI线程中更新界面
	 class BitmapDisplayer implements Runnable {
		 Bitmap bitmap;
		 ImageToLoad photoToLoad;

		 public BitmapDisplayer(Bitmap b, ImageToLoad p) {
			 bitmap = b;
			 photoToLoad = p;
		 }

		 public void run() {
			 if (imageViewReused(photoToLoad))
				 return;
			 if (bitmap != null){
				 photoToLoad.imageView.setBackgroundDrawable(null);
				 photoToLoad.imageView.setImageBitmap(bitmap);
			 }
		 }
	 }

	 public void clearCache() {
		 memoryCache.clear();
		 fileCache.clear();
	 }

	 public static void copyStream(InputStream is, OutputStream os) {
		 final int buffer_size = 1024;
		 try {
			 byte[] bytes = new byte[buffer_size];
			 for (;;) {
				 int count = is.read(bytes, 0, buffer_size);
				 if (count == -1)
					 break;
				 os.write(bytes, 0, count);
			 }
		 } catch (Exception ex) {
			 Log.e("", "CopyStream catch Exception...");
		 }
	 }
}
