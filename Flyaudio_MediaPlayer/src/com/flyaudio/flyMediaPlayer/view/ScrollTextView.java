package com.flyaudio.flyMediaPlayer.view;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Open Source Project
 * 
 * <br>
 * <b>自定义效果-由下往上动态滑动文字动画(已废弃！保留代码给各位研究参考)</b></br>
 * 
 * <br>
 * 本来考虑这效果自己实现，但是发现很多问题没法解决，特别是文字居中问题，
 * 通过自己onDraw()就会损失很多TextView的原有特性，只能维持一种动画效果，不能达到我一View多用的效果，必须放弃。
 * 好处是自己能够随意控制，可以继续定制修改。外部调用setTextList()就可以看到动画效果。</br>
 *  实现动态刷新效果
 */
public class ScrollTextView extends TextView {

	private static final int MSG_FIRST = 0;// 第一次启动
	private static final int MSG_START = 1;// 启动
	private static final int MSG_UPDATE = 2;// 更新
	private static final int TIME_START = 5000;// 启动，5秒延时
	private static final int TIME_UPDATE = 10;// 更新,0.01秒延时

	private int i;// 当前句
	private int size;// 列表总数

	private float height1;// 第一句的高
	private float height2;// 第一句的高
	private float speed;// 速率，允许外部修改
	private float x;// X轴位置
	private float y1;// 第一句的Y轴位置
	private float y2;// 第二句的Y轴位置

	private boolean isAuto = false;

	private ScrollHandler handler;
	private ArrayList<String> arrays;

	public ScrollTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		i = 0;
		speed = 3.0f;
		arrays = new ArrayList<String>();
		handler = new ScrollHandler(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (size == 0 || !isAuto) {
			return;
		}

		if (y2 <= height1) {
			restart();
		}

		final Paint paint = getPaint();
		canvas.drawText(arrays.get(i), x, y1, paint);
		canvas.drawText(arrays.get((i == size - 1) ? 0 : i + 1), x, y2, paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		this.height1 = (float) h / 2 + (float) getPaddingTop()
				+ (float) getLineHeight() / 4;
		this.height2 = (float) height1 * 2.5f;
		reset();
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			stop();
			super.onDetachedFromWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);

		switch (visibility) {
		case View.VISIBLE:// 可见时恢复
			if (size > 0) {
				create();
			}
			break;
		case View.INVISIBLE:// 不可见时停止动画
			stop();
			break;
		default:
			break;
		}
	}

	/**
	 * 第一次启动
	 */
	private void create() {
		stop();
		this.i = 0;
		invalidate();
		if (handler != null && !handler.hasMessages(MSG_FIRST)) {
			handler.sendEmptyMessageDelayed(MSG_FIRST, TIME_START);
			isAuto = true;
		}
	}

	/**
	 * 启动
	 */
	private void start() {
		if (handler != null && !handler.hasMessages(MSG_START)) {
			handler.sendEmptyMessageDelayed(MSG_START, TIME_START);
			isAuto = true;
		}
	}

	/**
	 * 暂停
	 */
	private void pause() {
		if (handler != null && handler.hasMessages(MSG_UPDATE)) {
			handler.removeMessages(MSG_UPDATE);
		}
	}

	/**
	 * 停止
	 */
	private void stop() {
		if (handler != null) {
			if (handler.hasMessages(MSG_FIRST)) {
				handler.removeMessages(MSG_FIRST);
			}
			if (handler.hasMessages(MSG_START)) {
				handler.removeMessages(MSG_START);
			}
			if (handler.hasMessages(MSG_UPDATE)) {
				handler.removeMessages(MSG_UPDATE);
			}
		}
		isAuto = false;
	}

	/**
	 * ִ执行更新
	 */
	private void play() {
		this.i = (i == size - 1) ? 0 : i + 1;
		reset();
		update();
	}

	/**
	 * 重置相关参数
	 */
	private void reset() {
		this.x = getPaddingLeft();
		this.y1 = height1;
		this.y2 = height2;
	}

	/**
	 * 实时更新
	 */
	private void update() {
		y1 = y1 - speed;
		y2 = y2 - speed;
		invalidate();
		handler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_UPDATE);
	}


	/**
	 * 恢复动态显示效果
	 */
	private void restart() {
		pause();
		start();
	}

	/**
	 * 设定字符组
	 * 
	 * @param texts
	 *            字符组
	 */
	public void setTextList(ArrayList<String> texts) {
		this.arrays.clear();
		this.arrays = texts;
		this.size = arrays.size();
		create();
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	private static class ScrollHandler extends Handler {

		private WeakReference<ScrollTextView> reference;

		public ScrollHandler(ScrollTextView view) {
			// TODO Auto-generated constructor stub
			reference = new WeakReference<ScrollTextView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (reference.get() != null) {
				ScrollTextView theView = reference.get();
				switch (msg.what) {
				case MSG_FIRST:
					theView.update();
					break;
				case MSG_START:
					theView.play();
					break;
				case MSG_UPDATE:
					theView.update();
					break;
				}
			}
		}
	}

}
