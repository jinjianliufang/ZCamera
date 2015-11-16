package org.zhx.view.camera.ui;

import java.io.IOException;
import org.zhx.view.R;
import org.zhx.view.camera.util.DisplayUtil;
import org.zhx.view.camera.util.ImageUtil;
import org.zhx.view.camera.widget.OverlayerView;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 
 * 希望有一天可以开源出来 org.zhx
 * 
 * @version 1.0, 2015-11-15 下午5:22:17
 * @author zhx
 */
public class ZCameraBaseAcitivy extends Activity implements PictureCallback,
		Callback, OnClickListener {
	private static final String TAG = ZCameraBaseAcitivy.class.getSimpleName();

	public static BitmapFactory.Options opt;
	static {
		// 缩小原图片大小
		opt = new BitmapFactory.Options();
		opt.inSampleSize = 2;
	}

	private SurfaceView mPreView;
	private SurfaceHolder mHolder;

	private Camera mCamera;
	private boolean isPreview = false;
	private Point displayPx;
	private ImageView tpImg, showImg;
	private Button saveBtn;
	// 取景框
	private OverlayerView mLayer;
	private Rect rect;
	private boolean isTake = false;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.zcamera_base_layout);

		displayPx = DisplayUtil.getScreenMetrics(this);
		mPreView = (SurfaceView) findViewById(R.id.z_base_camera_preview);
		tpImg = (ImageView) findViewById(R.id.z_take_pictrue_img);
		saveBtn = (Button) findViewById(R.id.z_base_camera_save);
		showImg = (ImageView) findViewById(R.id.z_base_camera_showImg);
		mLayer = (OverlayerView) findViewById(R.id.z_base_camera_over_img);
		// 设置取景框的 magin 这里最好 将 这些从dp 转化为px; 距 左 、上 、右、下的 距离 单位是dp
		rect = DisplayUtil.createCenterScreenRect(this, new Rect(50, 100, 50,
				100));
		mLayer.setmCenterRect(rect);

		saveBtn.setOnClickListener(this);
		showImg.setOnClickListener(this);
		tpImg.setOnClickListener(this);
		mHolder = mPreView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (!isPreview) {
			mCamera = Camera.open();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mCamera != null) {
			if (isPreview) {
				mCamera.stopPreview();
				isPreview = false;
			}

		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		if (mCamera != null) {
			mCamera.stopPreview();

			mCamera.startPreview();

			isPreview = true;

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		initCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 当holder被回收时 释放硬件
		if (mCamera != null) {
			if (isPreview) {
				mCamera.stopPreview();
			}
			mCamera.release();
			mCamera = null;
		}

	};

	/**
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 * @author zhx
	 */
	public void initCamera() {

		if (mCamera != null && !isPreview) {
			try {
				Camera.Parameters parameters = mCamera.getParameters();
				// 设置闪光灯为自动
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCamera.setParameters(parameters);
				resetCameraSize(parameters);
				// 设置图片格式
				parameters.setPictureFormat(ImageFormat.JPEG);
				// 设置JPG照片的质量
				parameters.set("jpeg-quality", 100);
				// 通过SurfaceView显示取景画面
				mCamera.setPreviewDisplay(mHolder);
				// 开始预览
				mCamera.startPreview();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isPreview = true;
		}

	}

	/**
	 * 旋转相机和设置预览大小
	 * 
	 * @param parameters
	 */
	public void resetCameraSize(Parameters parameters) {
		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			//
			mCamera.setDisplayOrientation(90);
		} else {
			mCamera.setDisplayOrientation(0);
		}
		// 设置预览图片大小 为设备 长宽
		parameters.setPreviewSize(displayPx.x, displayPx.y);
		// 设置图片大小 为设备 长宽
		parameters.setPictureSize(displayPx.x, displayPx.y);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.z_take_pictrue_img:
			// 拍照前 先对焦 对焦后 拍摄（适用于自动对焦）
			isTake = true;
			mCamera.autoFocus(autoFocusCallback);
			// 拍照前 不对焦 就直接调用
			// mCamera.takePicture(null, null, ZCameraBaseAcitivy.this);
			break;

		default:
			break;
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			if (isTake) {
				// 点击拍照按钮 对焦 后 拍照
				// 第一个参数 是拍照的声音，未压缩的数据，压缩后的数据
				mCamera.takePicture(null, null, ZCameraBaseAcitivy.this);
			}
		}
	};

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		isTake = false;
		// 拍照回掉回来的 图片数据。
		Bitmap bitmap = BitmapFactory
				.decodeByteArray(data, 0, data.length, opt);
		Bitmap bm = null;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			Matrix matrix = new Matrix();
			matrix.setRotate(90, 0.1f, 0.1f);
			bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
		} else {
			bm = bitmap;
		}
		if (rect != null) {
			bitmap = ImageUtil.getRectBmp(this, rect, bm);
		}
		ImageUtil.recycleBitmap(bm);
		showImg.setImageBitmap(bitmap);
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.startPreview();
			isPreview = true;
		}
	}

}
