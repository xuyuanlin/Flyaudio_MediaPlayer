package com.flyaudio.flyMediaPlayer.view;

import java.util.ArrayList;
import java.util.List;
import com.flyaudio.flyMediaPlayer.objectInfo.LyricItem;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.until.TimeParseTool;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class MyLyricView extends TextView {
	private static String TAG = "MyLyricView";
	private float width; // 歌词视图宽度
	private float height; // -----高度
	private Paint currentPaint; // 当前画笔对象
	private Paint notCurrentPaint; // 非当前画笔对象
	private float textHeight =36; // 字体的高度
	private float textSize = 20; // 字体大小
	private int index = 0; // list集合下标

	private List<LyricItem> mLrcList = new ArrayList<LyricItem>();
	private List<LyricItem> mSentenceEntities = new ArrayList<LyricItem>();
	private int lyricSize;

	public void setmLrcList(List<LyricItem> mLrcList) {
		Flog.d("TAG", "LrcView---setLrcList(");
		this.mLrcList = mLrcList;
	}

	public MyLyricView(Context context) {
		super(context);
		init();
	}

	public MyLyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyLyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setFocusable(true);

		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);
		currentPaint.setTextAlign(Paint.Align.CENTER);
		currentPaint.setTextSize(textSize);
		currentPaint.setTypeface(Typeface.SERIF);
		currentPaint.setColor(Color.argb(250, 251, 248, 29));

		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);
		notCurrentPaint.setColor(Color.argb(250, 255, 255, 255));
		notCurrentPaint.setTextSize(textSize);
		notCurrentPaint.setTypeface(Typeface.DEFAULT);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}

		try {
			setText("");

			canvas.drawText(mSentenceEntities.get(index).getLyric(), width / 2,
					height / 2, currentPaint);

			float tempY = height / 2;

			for (int i = index - 1; i >= 0; i--) {

				tempY = tempY - textHeight;
				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, notCurrentPaint);
			}
			tempY = height / 2;

			for (int i = index + 1; i < mSentenceEntities.size(); i++) {

				tempY = tempY + textHeight;
				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, notCurrentPaint);
			}
		} catch (Exception e) {
			setText("没有歌词文件......");
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h - 5;
	}

	public void setIndex(int index) {
		this.index = index;

	}

	public void setLyricHighlightColor(int color) {
		Flog.d("TAG", "LrcView--setLyricHighLightColor()");

		currentPaint.setColor(color);
	}

	public void clear() {
		this.mSentenceEntities.clear();
		this.index = 0;
		this.lyricSize = 0;
		this.invalidate();
	}

	public void setSentenceEntities(List<LyricItem> mSentenceEntities) {
		Flog.d("TAG", "LrcView--setSentenceEntities()");
		this.mSentenceEntities = mSentenceEntities;
		Flog.d("TAG",
				"LrcView--setSentenceEntities()--" + mSentenceEntities.size());
		this.lyricSize = mSentenceEntities.size();
	}
}