package com.flyaudio.flyMediaPlayer.activity;

import java.util.List;

import com.flyaudio.flyMediaPlayer.data.DBDao;
import com.flyaudio.flyMediaPlayer.until.AllListActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.until.ScanUtil;
import com.flyAudio.flyMediaPlayer.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * 执行动画、扫描数据库、第一次进入创建桌面图标，
 * 动画灵感来自http://video.sina.com.cn/v/b/69976687-1784435580.html，非常有意思，可以欣赏欣赏
 * </br
 */
public class LogoActivity extends Activity {
	private static final String TAG = "LogoActivity";
	private Handler mHandler;
	private ImageView mLogoView;// LOGO动画控件

	private ScanUtil mManager;
	private DBDao mDao;
	private ScanUtil scanUtil;
	private AllListActivity mAllListActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Flog.d(TAG, "onCreate()-----begin");
		mAllListActivity = (AllListActivity) getApplication();
		AllListActivity.getInstance().addActivity(this);
		mManager = new ScanUtil(getApplicationContext());
		mDao = new DBDao(getApplicationContext());
		if (isServiceRunning()) {
			Flog.d(TAG, "isServiceRunning()-----");

			Intent intent = new Intent(LogoActivity.this, MainActivity.class);
			startActivity(intent);
			LogoActivity.this.finish();
		} else {
			initActivity();
		}

		Flog.d(TAG, "onCreate()-----end");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Flog.d(TAG, "onDestroy()-----start");
		if (mHandler != null) {
			mHandler.removeCallbacks(scan);
			mHandler.removeCallbacks(runnable);
		}
		Flog.d(TAG, "onDestroy()-----end");
	}

	// 控件的初始化
	private void initActivity() {
		Flog.d(TAG, "initActivity()-----start");
		setContentView(R.layout.activity_logo);
		mLogoView = (ImageView) findViewById(R.id.activity_logo_name);
		final Animation logoAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.activity_logo);
		mLogoView.startAnimation(logoAnim);

		// 动画监听，结束时播放GIF动画
		logoAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});

		mHandler = new Handler();

		Flog.d(TAG, "LogoActivity-----------------------thread");

		mHandler.post(scan);

		mHandler.postDelayed(runnable, 2000);
		Flog.d(TAG, "initActivity()-----end");
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(LogoActivity.this, MainActivity.class);
			startActivity(intent);
			LogoActivity.this.finish();
		}
	};

	private Runnable scan = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			scanUtil = new ScanUtil(getApplicationContext());
			if (scanUtil.usbExists() || scanUtil.sdCardExists()) {
				mDao.queryAll();

			} else {
				Toast.makeText(getApplicationContext(), Constant.SDCARD_REMOVE,
						4000).show();
			}
		}
	};

	/**
	 * 检查服务是否正在运行
	 * 
	 * @return true/false
	 */
	public boolean isServiceRunning() {// TODO 移动到工具类
		Flog.d(TAG, "isServiceRunning()-----start");
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					Constant.SERVICE_STATE)) {
				isRunning = true;
				break;
			}
		}
		Flog.d(TAG, "isServiceRunning()-----endss");
		return isRunning;
	}
}