/* 
 * Copyright (c) 2015-2020 Founder Ltd. All Rights Reserved. 
 * 
 *zhx for  org
 * 
 * 
 */

package org.zhx.view.camera.widget;

import org.zhx.view.camera.util.DisplayUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * 希望有一天可以开源出来 org.zhx
 * 
 * @version 1.0, 2015-11-15 下午7:11:49
 * @author zhx
 */
public class OverlayerView extends ImageView {
	private static final String TAG = OverlayerView.class.getSimpleName();

	private Paint mLinePaint;
	private Paint mAreaPaint;
	private Rect mCenterRect = null;
	private Context mContext;
	private Paint paint;
	private int widthScreen, heightScreen;

	public OverlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
		mContext = context;
		// 获取屏幕px值 组装成一个 point对象
		Point p = DisplayUtil.getScreenMetrics(mContext);
		widthScreen = p.x;
		heightScreen = p.y;
	}

	private void initPaint() {
		// 绘制中间透明区域矩形边界的Paint
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.BLUE);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5f);
		// 值不为0 那么透明取景框 周围就会有线 看需求 修改值就行 我的项目部需要 线 所以透明
		mLinePaint.setAlpha(0);

		// 绘制四周阴影区域 的画笔
		mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAreaPaint.setColor(Color.GRAY);
		mAreaPaint.setStyle(Style.FILL);
		mAreaPaint.setAlpha(100);
		paint = new Paint();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDraw...");
		if (mCenterRect == null)
			return;

		// 绘制阴影区域 2 为 4个角上短线的 高度
		canvas.drawRect(0, 0, widthScreen, mCenterRect.top - 2, mAreaPaint);
		canvas.drawRect(0, mCenterRect.bottom + 2, widthScreen, heightScreen,
				mAreaPaint);
		canvas.drawRect(0, mCenterRect.top - 2, mCenterRect.left - 2,
				mCenterRect.bottom + 2, mAreaPaint);
		canvas.drawRect(mCenterRect.right + 2, mCenterRect.top - 2,
				widthScreen, mCenterRect.bottom + 2, mAreaPaint);
		// 短线的颜色 和透明度
		paint.setColor(Color.WHITE);
		paint.setAlpha(150);
		// 50为 4角短线的长度
		canvas.drawRect(mCenterRect.left - 2, mCenterRect.bottom,
				mCenterRect.left + 50, mCenterRect.bottom + 2, paint);// 左下 底部

		canvas.drawRect(mCenterRect.left - 2, mCenterRect.bottom - 50,
				mCenterRect.left, mCenterRect.bottom, paint);// 左下 左侧

		canvas.drawRect(mCenterRect.right - 50, mCenterRect.bottom,
				mCenterRect.right + 2, mCenterRect.bottom + 2, paint);// 右下 右侧
		canvas.drawRect(mCenterRect.right, mCenterRect.bottom - 50,
				mCenterRect.right + 2, mCenterRect.bottom, paint);// 右下 底部

		canvas.drawRect(mCenterRect.left - 2, mCenterRect.top - 2,
				mCenterRect.left + 50, mCenterRect.top, paint);// 左上 顶部
		canvas.drawRect(mCenterRect.left - 2, mCenterRect.top,
				mCenterRect.left, mCenterRect.top + 50, paint);// 左上 侧边
		canvas.drawRect(mCenterRect.right - 50, mCenterRect.top - 2,
				mCenterRect.right + 2, mCenterRect.top, paint);// 右上 顶部
		canvas.drawRect(mCenterRect.right, mCenterRect.top,
				mCenterRect.right + 2, mCenterRect.top + 50, paint);// 右上 右侧

		// 绘制目标透明区域
		canvas.drawRect(mCenterRect, mLinePaint);
		super.onDraw(canvas);
	}

	public Rect getmCenterRect() {
		return mCenterRect;
	}

	/**
	 * 设置 取景框 的矩形框 。
	 * 
	 * @param value
	 *            矩形取景框
	 * @return
	 * @throws Exception
	 * @author zhx
	 */

	public void setmCenterRect(Rect mCenterRect) {
		this.mCenterRect = mCenterRect;
		postInvalidate();
	}

}
