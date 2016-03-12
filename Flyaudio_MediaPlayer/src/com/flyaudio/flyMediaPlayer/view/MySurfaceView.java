package com.flyaudio.flyMediaPlayer.view;

import com.flyaudio.flyMediaPlayer.sdl.NativePlayer;
import com.flyaudio.flyMediaPlayer.until.Flog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MySurfaceView extends View {
	private static String TAG = "MySurfaceView";
	private final int ACCELERATION = 1;// 下落加速度(感觉最合适的一个数值)
	private Handler mHandler = null;
	private int mSpectrumNum = 64;// 截取一部分
	private boolean canDrawLines = false;// 是否允许画线
	private float[] mBytes = null;// FFT源数组
	private float[] linesArray = null;// 线数组
	private float[] reflectsArray = null;// 倒影线数组
	private float[] pointsArray = null;// 点数组
	private float[] tempArray = null;// 临时数组，用于记住点的位置
	private float[] line_tempArray = null;
	private Rect mRect = null;// 矩形区域
	public static Paint linesPaint = null;// 频谱线画笔
	public static Paint pointsPaint = null;// 频谱点画笔
	private Paint reflectsPaint = null;// 倒影频谱线画笔
	public static int a = 255;
	public static int r = 221;
	public static int g = 160;
	public static int b = 221;
	private static MyThread mt = null;
	private static boolean isStart = false;
	private static boolean isInit = false;
	private static int RecHeight = 0;

	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		Flog.d(TAG, "super(context)");
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Flog.d(TAG, "super(context, attrs)");
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		Flog.d(TAG, "super(context, attrs, defStyle)");
	}

	public void setupVisualizerFx() {
		Flog.i(TAG, "setupVisualizerFx-----------start:canDrawLines="+canDrawLines);

		canDrawLines = true;// 允许画线
		
		if (isInit) {
			return;
		}
		init();
		isStart = false;
		if (mt != null) {
			mt = null;
		}
		isStart = true;
		mt = new MyThread();
		new Thread(mt).start();
		Flog.i(TAG, "setupVisualizerFx-----------end");
	}

	/**
	 * 释放并回收VisualizerView对象
	 */
	public void releaseVisualizerFx() {
		Flog.i(TAG, "releaseVisualizerFx-----------start:canDrawLines="+canDrawLines);
		
		canDrawLines = false;		
		if (mRect!=null) {
			initByte();
			if (tempArray != null) {
				for (int l = 0; l < tempArray.length/ 2; l++) {
					tempArray[l * 2] = 1024;
				}
			}
			if (line_tempArray != null){ 
				for (int l = 0; l < line_tempArray.length / 2; l++) {
					line_tempArray[l * 2] = 1024;
				}
			}
			MySurfaceView.this.invalidate();
		}
		isInit = false;
		
		isStart = false;
		if (mt != null) {
			mt = null;
		}
		if (mHandler != null) {
			mHandler.removeCallbacks(runnableUi);
		}
	}

	class MyThread implements Runnable {

		public void run() {
			synchronized (this) {

				while (isStart) {
					float array[] = NativePlayer.getFFTBuffer();

					if (mHandler != null && array != null) {
						updateFFT(array);
						mHandler.post(runnableUi);
						try {
							Thread.sleep(120);
						} catch (InterruptedException e) {
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		}
	}

	// 初始化画笔
	private void init() {

		Flog.d(TAG, "init");
		isInit = true;
		mRect = new Rect();
		mHandler = new Handler();
		linesPaint = new Paint();
		linesPaint.setStrokeWidth(10f);
		linesPaint.setColor(Color.argb(a, r, g, b));

		reflectsPaint = new Paint();
		reflectsPaint.setColor(Color.argb(a, r, g, b));
		reflectsPaint.setAlpha(120);

		pointsPaint = new Paint();
		pointsPaint.setStrokeWidth(6f);
		pointsPaint.setAlpha(10);
		pointsPaint.setColor(Color.argb(a - 100, r, g, b));

		initByte();
	}

	/**
	 * 初始化FFT源数组，赋值为1的目的是能够一开始就绘制频谱，初始化的目地即重新归位
	 */
	private void initByte() {
		Flog.d(TAG, "initByte");
		mBytes = new float[mSpectrumNum];
		for (int i = 0; i < mSpectrumNum; i++) {
			mBytes[i] = 0;
		}
	}

	private void updateFFT(float[] array) {

		int length = array.length;

		float[] average = new float[mSpectrumNum];
		int len = length / mSpectrumNum;

		if (len < 1) {
			for (int j = 0; j < length; j++) {
				average[j] = (array[j]);
			}
		} else {
			float max = 0;
			for (int i = 0; i < mSpectrumNum; i++) {
				max = 0;
				for (int j = 0; j < len; j++) {
					if (max < array[i]) {
						max = array[i];
					}

				}
				average[i] = max / 22.0f;// 取最大值

				if (average[i] >= 1000.0f) {
					average[i] = average[i] / 10.0f;
				}
				// Flog.d(TAG, "updateFFT()---RecHeight=="+RecHeight);
				if (RecHeight > 0) {
					while (average[i] > RecHeight / 3) {
						average[i] = average[i] / 2.0f;
					}
					while ((i >= 8 && i < 16) && (average[i] > RecHeight / 5)) {
						average[i] = average[i] / 2.0f;
					}
					while ((i >= 16 && i < 24) && (average[i] > RecHeight / 8)) {
						average[i] = average[i] / 2.0f;
					}
					while ((i >= 24 && i < mSpectrumNum - 5)
							&& (average[i] > RecHeight / 20)) {
						average[i] = average[i] / 2.0f;
					}
					while ((i >= mSpectrumNum - 5 && i < mSpectrumNum)
							&& (average[i] > RecHeight / 30)) {
						average[i] = average[i] / 2.0f;
					}
				} else {
					average[i] = 0;
				}
			}
		}

		mBytes = average;
	}

	/**
	 * 自由落体运算{h=(1/2)*g*t^2}
	 * 
	 * @param time
	 *            时间
	 * @return 运动的距离
	 */
	private float freeFall(float time) {
		float h = ACCELERATION * time * time / 2;
		return h;
	}

	private float line_freeFall(float time) {
		float h = 2 * ACCELERATION * time * time / 2;
		return h;
	}

	// // 构建Runnable对象，在runnable中更新界面
	private Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			MySurfaceView.this.invalidate();

		}

	};

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		if (mBytes == null) {
			Flog.d(TAG, "onDraw:mBytes=" + mBytes);
			return;
		}

		int length = mSpectrumNum * 4;
		if (linesArray == null || linesArray.length < length) {
			linesArray = new float[length];
		}
		if (reflectsArray == null || reflectsArray.length < length) {
			reflectsArray = new float[length];
		}
		length = mSpectrumNum * 2;
		if (pointsArray == null || pointsArray.length < length) {
			pointsArray = new float[length];
		}
		length = mSpectrumNum * 2;
		if (tempArray == null || tempArray.length < length) {
			tempArray = new float[length];
			for (int l = 0; l < length / 2; l++) {
				tempArray[l * 2] = 1024;
			}
		}
		if (line_tempArray == null || line_tempArray.length < length) {
			line_tempArray = new float[length];
			for (int l = 0; l < length / 2; l++) {
				line_tempArray[l * 2] = 1024;
			}
		}

		mRect.set(0, 0, getWidth(), getHeight());

		/************ 绘制柱状频谱 ************/
		final int baseX = mRect.width() / mSpectrumNum;
		final int height = mRect.height();
		RecHeight = height / 2;
		for (int i = 0; i < mSpectrumNum - 1; i++) {

			final int x = baseX * (i + 1) + baseX;

			// 线终点位置的高度减去一个数就会出现在线的上方，反之在下方
			int y = (int) (RecHeight - mBytes[i] * 3 - 3);
			int reflect_y = (int) (RecHeight + mBytes[i]);
			if (tempArray[i * 2] > y) {// 记住的位置比现在的大，也就是最新的位置更高了
				tempArray[i * 2 + 0] = y;// 记住现在的最高位置
				tempArray[i * 2 + 1] = 0;// 一定要归0
				pointsArray[i * 2] = x;
				pointsArray[i * 2 + 1] = y;// 更高了就刷新呗

			} else {// 记住的位置比现在的小，说明该自由落体了
				float ti = tempArray[i * 2 + 1];// 取出上次记忆的次数
				float temp = tempArray[i * 2 + 0] + freeFall(ti);// 加上自由落体运算
				tempArray[i * 2 + 1] = ++ti;// 自增一次
				temp = temp > y ? y : temp;// 不能跑到线下挡住了，判断一下让点归位
				tempArray[i * 2 + 0] = temp;
				pointsArray[i * 2] = x;
				pointsArray[i * 2 + 1] = tempArray[i * 2 + 0];// 刷新当前点

			}

			if (canDrawLines) {
				if (line_tempArray[i * 2] > y) {// 记住的位置比现在的大，也就是最新的位置更高了
					line_tempArray[i * 2 + 0] = y;// 记住现在的最高位置
					line_tempArray[i * 2 + 1] = 0;// 一定要归0

					// 两点构成一条直线(垂直)
					linesArray[i * 4] = x;// 起始点X坐标
					linesArray[i * 4 + 1] = RecHeight;// 起始点Y坐标
					linesArray[i * 4 + 2] = x;// 终点X坐标
					linesArray[i * 4 + 3] = y + 5;// 终点Y坐标

					reflectsArray[i * 4] = x;// 起始点X坐标
					reflectsArray[i * 4 + 1] = RecHeight + 4;// 起始点Y坐标
					reflectsArray[i * 4 + 2] = x;// 终点X坐标
					reflectsArray[i * 4 + 3] = reflect_y + 3;// 终点Y坐标
				} else {
					float line_ti = line_tempArray[i * 2 + 1];// 取出上次记忆的次数
					float line_temp = line_tempArray[i * 2 + 0]+ line_freeFall(line_ti);
					line_tempArray[i * 2 + 1] = ++line_ti;// 自增一次
					line_temp = line_temp > y ? y : line_temp;
					line_tempArray[i * 2 + 0] = line_temp;

					linesArray[i * 4] = x;// 起始点X坐标
					linesArray[i * 4 + 1] = RecHeight;// 起始点Y坐标
					linesArray[i * 4 + 2] = x;// 终点X坐标
					linesArray[i * 4 + 3] = (line_temp + 15) > y ? y: (line_temp + 15);

//					int reTemp = (int) (2 * RecHeight - 3 - line_temp) / 3;
					float reTemp = (linesArray[i * 4 + 3])+mBytes[i] * 4;
					reflectsArray[i * 4] = x;// 起始点X坐标
					reflectsArray[i * 4 + 1] = RecHeight + 4;// 起始点Y坐标
					reflectsArray[i * 4 + 2] = x;// 终点X坐标
					reflectsArray[i * 4 + 3] = (reTemp) < (reflect_y + 3) ? (reflect_y + 3): (reTemp);// 终点Y坐标
				}

			}

		}

		if (canDrawLines && linesPaint != null) {
			linesPaint.setStrokeWidth(baseX - 2.0f);
			canvas.drawLines(linesArray, linesPaint);

			reflectsPaint.setStrokeWidth(baseX - 2.0f);
			LinearGradient shader = new LinearGradient(baseX, RecHeight + 4,
					baseX, RecHeight + RecHeight / 3, Color.argb(a, r, g, b),
					Color.argb(0, r, g, b), TileMode.CLAMP);
			reflectsPaint.setShader(shader);

			canvas.drawLines(reflectsArray, reflectsPaint);

		}
		if (pointsPaint != null) {
			pointsPaint.setStrokeWidth(baseX - 4.0f);
			canvas.drawPoints(pointsArray, pointsPaint);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "onDetachedFromWindow");
		super.onDetachedFromWindow();
		try {
			isStart = false;
			mt = null;
			mBytes = null;// FFT源数组
			linesArray = null;// 线数组
			pointsArray = null;// 点数组
			tempArray = null;// 临时数组，用于记住点的位置
			mRect = null;// 矩形区域
			linesPaint = null;// 频谱线画笔
			pointsPaint = null;
			if (mHandler != null) {
				mHandler.removeCallbacks(runnableUi);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
