/* 
 * Copyright (c) 2015-2020 Founder Ltd. All Rights Reserved. 
 * 
 *zhx for  org
 * 
 * 
 */

package org.zhx.view.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * 
 * 希望有一天可以开源出来 org.zhx
 * 
 * @version 1.0, 2015-11-15 下午7:21:09
 * @author zhx
 */

public class ImageUtil {

	private static final String TAG = ImageUtil.class.getSimpleName();

	/**
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 * @author zhx
	 */
	public static Bitmap getRectBmp(Context context, Rect rect, Bitmap bm) {
		// TODO Auto-generated method stub

		Point p = DisplayUtil.getScreenMetrics(context);

		int width = rect.right - rect.left;
		int hight = rect.bottom - rect.top;
		Log.i(TAG,hight + "@"+ width);
		Bitmap rectbitmap = Bitmap.createBitmap(bm, rect.left, rect.top, width,
				hight);
		return rectbitmap;
	}

	/**
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 * @author zhx
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

}
