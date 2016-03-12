package com.flyaudio.flyMediaPlayer.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.os.CountDownTimer;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.activity.AffectActivity;
import com.flyaudio.flyMediaPlayer.activity.MainActivity;
import com.flyaudio.flyMediaPlayer.activity.PlayerActivity;
import com.flyaudio.flyMediaPlayer.data.DBDao;
import com.flyaudio.flyMediaPlayer.objectInfo.LyricItem;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.ArtistList;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
import com.flyaudio.flyMediaPlayer.perferences.FolderList;
import com.flyaudio.flyMediaPlayer.perferences.LyricList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.sdl.NativePlayer;
import com.flyaudio.flyMediaPlayer.sdl.PlayerResult;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnServiceBinderListener;
import com.flyaudio.flyMediaPlayer.until.AlbumUtil;
import com.flyaudio.flyMediaPlayer.until.AllListActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.until.LyricParser;
import com.flyaudio.flyMediaPlayer.until.ScanUtil;
import com.flyaudio.flyMediaPlayer.view.MyLyricView;

public class MediaService extends Service {
	private static String TAG = "MediaService";
	public static MusicInfo mMusicInfo;// 歌曲的详情
	private List<LyricItem> mLyricList;// 歌词列表
	private String sLyricPath;// 歌词路径
	private int nMode = Constant.MODE_NORMAL;// 播放模式(默认顺序播放)
	public static int nPlayingPage = Constant.VIEWPAHER_MENU_MUSICNAME;// 曲目页面(默认全部)playing
	public static int nPage = Constant.VIEWPAHER_MENU_MUSICNAME;// 曲目页面(默认全部)
	public static int nMusicPosition = 0;// 列表当前项
	private int nFolderPosition = 0;// 文件夹列表当前项
	public static int nMusicCurrent = 0;// 歌曲当前时间
	public static int nMusicDuration;// 歌曲总时间
	private boolean isLyric = false;// 是否有歌词
	private MediaBinder mBinder;
	private AlbumUtil mAlbumUtil;
	private MyLyricView mAllLyricView;
	private RemoteViews mRemoteViews;
	private ServiceHandler mHandler;
	private SharedPreferences mPreferences;
	private boolean running = true;
	public static boolean isStart;
	public static PlayerResult result;
	private Bundle bundle;
	private boolean isSeekBarEnd;// seekbar滚动结束
	private int seekBarMoveTime;// seekbar滚动结束时间
	private int current;// 快进快退结束时间
	private boolean isRewind;// 快退标志
	private boolean isForward;// 快进标志
	private int dit;// timestamp和seekbartime相减的结果
	private boolean DITflag;
	public static String sPath;
	public static String sMusicPath;// MP3文件路径
	private boolean isNext = true;// 是否切歌的标示
	private int nLyricindex;
	private boolean isRunning = false;// 是否正在播放的标示
	private int nMusicSize;
	public static boolean isPause;
	private NotificationReceiver mReceiver;
	private Notification mNotification;
	private int nArtistPosition;
	private int nAlbumPosition;
	private BroadcastSD mBroadcastSD;
	private boolean isFristServier;
	private boolean isUpdateUIFrist;
	private int musicCount;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stubf
		Flog.d(TAG, "onCreate()-----start");
		super.onCreate();
		registSDCardListener();
		registNotificationListener();
		mHandler = new ServiceHandler();
		mBinder = new MediaBinder();
		mAlbumUtil = new AlbumUtil();
		mLyricList = new ArrayList<LyricItem>();
		mPreferences = getSharedPreferences(Constant.PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		// positionList = new ArrayList<Integer>();
		mBinder.setOnServiceBinderListener(new OnServiceBinderListener() {
			@Override
			public void seekBarStartTrackingTouch() {// 滚动条开始
				// TODO Auto-generated method stub
				Flog.d(TAG,
						"MediaService---seekBarStartTrackingTouch---before--"
								+ isStart);
				if (isStart == true) {
					removeUpdateMsg();
					Flog.d(TAG,
							"MediaService---seekBarStartTrackingTouch-----end");
				}
			}

			@Override
			public void seekBarStopTrackingTouch(int progress) {// 滑动进度条停止
				// TODO Auto-generated method stub
				Flog.d(TAG, "MediaService---seekBarStopTrackingTouch----"
						+ isStart);
				if (isStart == true && MainActivity.isSDMove == false) {// isSDMove是SDCARD未移除
					Flog.d(TAG,
							"MediaService---seekBarStopTrackingTouch-----progress----"
									+ progress);
					isSeekBarEnd = true;//
					DITflag = false;// 初始化为最初的状态
					seekBarMoveTime = progress;
					updateUI();// 更新进度条
					Flog.d(TAG,
							"MediaService---seekBarStopTrackingTouch-----progress----nativePlayerSeekTo");
					NativePlayer.nativePlayerSeekTo(progress / 1000);
					Flog.d(TAG,
							"MediaService---seekBarStopTrackingTouch-----progress----nativePlayerSeekTo----end");

				}
			}

			@Override
			public void lrc(MyLyricView lView1) {
				// TODO Auto-generated method stub
				Flog.d(TAG, "mBinder.setOnServiceBinderListener-----lrc()");
				MediaService.this.mAllLyricView = lView1;

				Flog.d(TAG,
						"mBinder.setOnServiceBinderListener-----lrc()----end");
			}

			@Override
			public void control(int command) {
				// TODO Auto-generated method stub
				switch (command) {
				case Constant.CONTROL_COMMAND_PLAY:// 播放与暂停
					Flog.d(TAG,
							"MediaService------button---control---isStart---"
									+ isStart);
					if (isStart == true) {
						Flog.d(TAG, "MediaService-----button---pause");
						pause();
					} else {
						if (sMusicPath != null && isPause == true) {
							nMusicPosition = mPreferences.getInt(
									Constant.PREFERENCES_POSITION, 0);

							Flog.d(TAG, "MediaService-----11111111111111111");
							getMusciInfo();
							MainActivity.isFrist = false;
							PlayerActivity.isPlaying = false;
							try {
								NativePlayer.nativePlayerResume();
								isPause = false;
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							Flog.d(TAG, "MediaService-------button---play---"
									+ sMusicPath + "-------------"
									+ nMusicPosition);

							isStart = true;
							getLyricUI();
						} else {// 无指定情况下播放全部歌曲列表的第一首
							nMusicPosition = mPreferences.getInt(
									Constant.PREFERENCES_POSITION, 0);
							startServiceCommand();
						}
					}
					break;

				case Constant.CONTROL_COMMAND_PREVIOUS:// 上一首
					previous();
					break;

				case Constant.CONTROL_COMMAND_NEXT:// 下一首
					next();
					break;

				case Constant.CONTROL_COMMAND_MODE:// 播放模式
					if (nMode < Constant.MODE_RANDOM) {
						nMode++;
					} else {
						nMode = Constant.MODE_NORMAL;
					}
					switch (nMode) {
					case Constant.MODE_NORMAL:
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.xml_mode_normal),
								Toast.LENGTH_SHORT).show();
						break;

					case Constant.MODE_REPEAT_ONE:
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.xml_mode_repeat_one),
								Toast.LENGTH_SHORT).show();
						break;

					case Constant.MODE_REPEAT_ALL:
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.xml_mode_repeat_all),
								Toast.LENGTH_SHORT).show();
						break;

					case Constant.MODE_RANDOM:
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.xml_mode_repeat_random),
								Toast.LENGTH_SHORT).show();
						break;
					}
					mBinder.modeChange(nMode);
					break;

				case Constant.CONTROL_COMMAND_REWIND:// 快退
					if (isStart == true) {
						removeAllMsg();
						rewind();
					}
					break;

				case Constant.CONTROL_COMMAND_FORWARD:// 快进MP3Path
					if (isStart == true) {
						removeAllMsg();
						forward();
					}
					break;
				case Constant.CONTROL_COMMAND_REPLAY:// 用于快退、快进后的继续播放
					if (isStart == true) {
						replay();
						mHandler.sendEmptyMessage(Constant.MEDIA_PLAY_START);// 更新界面
					}
					break;
				}
			}

		});
		mPreferences = getSharedPreferences(Constant.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		nMode = mPreferences.getInt(Constant.PREFERENCES_MODE,
				Constant.MODE_NORMAL);// 取出上次的播放模式
		initNotification();
		Flog.d(TAG, "onCreate()-----end");
	}

	private void initNotification() {

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new Notification();// 通知栏相关
		mNotification.icon = R.drawable.logo;
		mNotification.flags = Notification.FLAG_NO_CLEAR;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mNotification.contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);
		mRemoteViews = new RemoteViews(getPackageName(),
				R.layout.notification_item);

	}

	private void registSDCardListener() {

		mBroadcastSD = new BroadcastSD();
		// 在IntentFilter中选择你要监听的行为
		IntentFilter mFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);// sd卡被插入，且已经挂载
		mFilter.setPriority(1000);// 设置最高优先级
		mFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd卡存在，但还没有挂载
		mFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd卡被移除
		mFilter.addAction(Intent.ACTION_MEDIA_SHARED);// sd卡作为 USB大容量存储被共享，挂载被解除
		mFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd卡已经从sd卡插槽拔出，但是挂载点还没解除
		mFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);// 开始扫描
		mFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);// 扫描完成
		mFilter.addDataScheme("file");
		registerReceiver(mBroadcastSD, mFilter);// 注册监听函数

	}

	private void registNotificationListener() {

		mReceiver = new NotificationReceiver();// 注册广播通知栏
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		intentFilter.addAction(Constant.BROADCAST_ACTION_SERVICE);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_NEXT);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_PLAY);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_PREV);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_STATE);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_EXIT);
		intentFilter.addAction(Constant.BROADCAST_ACTION_NOT_ALBUM);
		registerReceiver(mReceiver, intentFilter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "---MediaService------onStartCommand()-----start");
		if (intent != null) {
			bundle = intent.getExtras();
			if (bundle != null && !bundle.isEmpty()) {
				Flog.d(TAG, "---MediaService------onStartCommand()-------2");
				// nPlayingPage = bundle.getInt(Constant.INTENT_LIST_PAGE, 0);
				nMusicPosition = bundle
						.getInt(Constant.INTENT_LIST_POSITION, 0);
				Flog.d(TAG, "MediaService--onStartCommand()---position---"
						+ nMusicPosition);
				Flog.d(TAG, "MediaService--onStartCommand()---nPlayingPage---"
						+ nPlayingPage);
				nPlayingPage = bundle.getInt(Constant.INTENT_LIST_PAGE,
						Constant.VIEWPAHER_MENU_MUSICNAME);
				nFolderPosition = bundle.getInt(
						Constant.INTENT_FOLDER_POSITION, nFolderPosition);
				nAlbumPosition = bundle.getInt(Constant.INTENT_ALBUM_POSITION,
						nAlbumPosition);
				nArtistPosition = bundle.getInt(
						Constant.INTENT_ARTIST_POSITION, nArtistPosition);
				prepared();
			}

		}
		/*
		 * intent的参数是null，原因是这个intent参数是通过startService(Intent)方法所传递过来的，
		 * 但是如果Service在你的进程退出后有可能被系统自动重启，这个时候intent就会是null. 解决方法：
		 * 所以在使用intent前需要判断一下是否为空。 还有另外一种解决方法： 如果实现
		 * onStartCommand去调度异步工作或者其他的线程，
		 * 有必要设置START_FLAG_REDELIVERY让系统重发intent到service以便service被killed后不会丢失intent数据
		 * 。
		 */
		Flog.d(TAG, "---MediaService------onStartCommand()----3");
		Flog.d(TAG, "---MediaService------onStartCommand()-----end");
		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	private void getMusicList() {
		Flog.d(TAG, "getMusicList-----start");
		DBDao db = new DBDao(getApplicationContext());
		ScanUtil scanUtil = new ScanUtil(getApplicationContext());
		db.queryAll();
		db.close();
		Flog.d(TAG, "getMusicList-----end");
	}

	public String getMusciPath(int postion) {
		int nMusicSize = 0;
		String pathString = null;

		switch (nPlayingPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:// 曲名listview
			nMusicSize = MusicList.list.size();
			Flog.d(TAG, "MediaService--VIEWPAHER_MENU_MUSICNAME--size--"
					+ nMusicSize);

			if (nMusicSize > 0 && nMusicSize > postion) {
				pathString = MusicList.list.get(postion).getPath();
			}
			break;

		case Constant.VIEWPAHER_MENU_ARTIST_LIST:// 歌手的音乐listview

			nMusicSize = ArtistList.list.get(nArtistPosition).getMusicList()
					.size();
			Flog.d(TAG, "MediaService----VIEWPAHER_MENU_ARTIST_LIST--size--"
					+ nMusicSize);
			if (nMusicSize > 0 && nMusicSize > postion) {
				pathString = ArtistList.list.get(nArtistPosition)
						.getMusicList().get(postion).getPath();
			}

			break;

		case Constant.VIEWPAHER_MENU_ALBUM_LIST:// 专辑音乐的listview
			nMusicSize = AlbumList.list.get(nAlbumPosition).getMusicList()
					.size();
			Flog.d(TAG, "MediaService----VIEWPAHER_MENU_ALBUM_LIST--size--"
					+ nMusicSize);
			if (nMusicSize > 0 && nMusicSize > postion) {
				pathString = AlbumList.list.get(nAlbumPosition).getMusicList()
						.get(postion).getPath();
			}

			break;
		case Constant.VIEWPAHER_MENU_MUSICFAVORITES://
			nMusicSize = FavoriteList.list.size();
			if (nMusicSize > 0 && nMusicSize > postion) {
				pathString = FavoriteList.list.get(postion).getPath();
			}

			break;

		}
		return pathString;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "---MediaService------onDestroy()-------start");
		super.onDestroy();
		isRunning = true;
		Flog.d(TAG, "---MediaService------onDestroy()-------end-----------"
				+ nMusicDuration + "------------" + nMusicPosition
				+ "-------------" + MainActivity.nMusicPosition);
		Flog.d(TAG, "---MediaService------onDestroy()-------end");
		if (isStart == true) {
			isStart = false;
			NativePlayer.nativePlayerStop();
			removeAllMsg();
		}
		unregisterReceiver(mReceiver);
		unregisterReceiver(mBroadcastSD);
		Flog.d(TAG, "---MediaService------onDestroy()-------end");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "MediaService-----onUnbind()-----start");
		mAllLyricView = null;
		removeAllMsg();// 移除所有消息
		Flog.d(TAG, "MediaService-----onUnbind()-----end");
		return true;// 一定返回true，允许执行onRebind
	}

	// onRebind()方法执行的时机是Service在内存中已经存在，然后使用bindService()方法再次与Service取得通信，这时onRebind()方法被调用。
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		getMusciInfo();
		Flog.d(TAG, "MediaService----OnRebind()-----start");
		super.onRebind(intent);
		if (isStart == true) {// 如果正在播放重新绑定服务的时候重新注册
			Flog.d(TAG, "MediaService----OnRebind()-----------1");
			getLyricUI();// 因为消息已经移除，所有需要重新开启更新操作
		} else {
			Flog.d(TAG, "MediaService----OnRebind()-----------2");
			if (sMusicPath != null) {// 暂停原先播放重新开页面需要恢复原先的状态
				Flog.d(TAG, "MediaService----OnRebind()-----------3");
				mMusicInfo.setMp3Duration(nMusicDuration);
				mAlbumUtil.scanAlbumImage(mMusicInfo.getPath(), this);
				mBinder.playStart(mMusicInfo, nPlayingPage);
				nMusicCurrent = nMusicCurrent;//
				mBinder.playUpdate(nMusicCurrent, sLyricPath, nPlayingPage,
						mMusicInfo);
				mBinder.playPause();
			}
		}

		mBinder.modeChange(nMode);
		Flog.d(TAG, "MediaService----OnRebind()-----end");
	}

	public void getMusciInfo() {
		Flog.d(TAG, "MediaService----getMusciInfo()-----start");
		nMusicSize = 0;
		Flog.d(TAG, "MediaService--getMusciInfo()--nMusicPosition----"
				+ nMusicPosition);
		Flog.d(TAG, "MediaService--getMusciInfo()--page--" + nPlayingPage);
		Flog.d(TAG, "MediaService--getMusciInfo()--nAlbumPosition--"
				+ nAlbumPosition);
		Flog.d(TAG, "MediaService--getMusciInfo()--nArtistPosition--"
				+ nArtistPosition);

		switch (nPlayingPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:// 曲名listview
			nMusicSize = MusicList.list.size();
			Flog.d(TAG, "MediaService--VIEWPAHER_MENU_MUSICNAME--size--"
					+ nMusicSize);

			if (nMusicSize > 0 && nMusicSize > nMusicPosition) {
				mMusicInfo = MusicList.list.get(nMusicPosition);
			}
			break;

		case Constant.VIEWPAHER_MENU_ARTIST_LIST:// 歌手的音乐listview

			nMusicSize = ArtistList.list.get(nArtistPosition).getMusicList()
					.size();
			Flog.d(TAG, "MediaService----VIEWPAHER_MENU_ARTIST_LIST--size--"
					+ nMusicSize);
			if (nMusicSize > 0 && nMusicSize > nMusicPosition) {
				mMusicInfo = ArtistList.list.get(nArtistPosition)
						.getMusicList().get(nMusicPosition);
			}

			break;

		case Constant.VIEWPAHER_MENU_ALBUM_LIST:// 专辑音乐的listview
			nMusicSize = AlbumList.list.get(nAlbumPosition).getMusicList()
					.size();
			Flog.d(TAG, "MediaService----VIEWPAHER_MENU_ALBUM_LIST--size--"
					+ nMusicSize);
			if (nMusicSize > 0 && nMusicSize > nMusicPosition) {
				mMusicInfo = AlbumList.list.get(nAlbumPosition).getMusicList()
						.get(nMusicPosition);
			}

			break;
		case Constant.VIEWPAHER_MENU_MUSICFAVORITES://
			nMusicSize = FavoriteList.list.size();
			if (nMusicSize > 0 && nMusicSize > nMusicPosition) {
				mMusicInfo = FavoriteList.list.get(nMusicPosition);
			}
			break;

		}

		// Flog.d(TAG, "MediaService--prepared()--musicCount%2==" +
		// musicCount%2);
		// if(musicCount%2==0){
		// mPreferences.edit().putInt(Constant.PREFERENCES_POSITION_TWO,
		// nMusicPosition).commit();
		// }else {
		// mPreferences.edit().putInt(Constant.PREFERENCES_POSITION_ONE,
		// nMusicPosition).commit();
		// }
		// musicCount++;
		Flog.d(TAG, "MediaService----getMusciInfo()-----end");
	}

	/**
	 * 播放操作
	 * 
	 * @throws InterruptedException
	 */
	private void prepared() {// TODO prepared
		Flog.d(TAG, "MediaService--prepared()--start");
		getMusciInfo();
		mBinder.playStart(mMusicInfo, nPlayingPage);
		// Flog.d(TAG, "MediaService--prepared()--musicCount==" + musicCount);
		// Flog.d(TAG, "MediaService--prepared()--musicCount%2==" +
		// musicCount%2);
		// Flog.d(TAG, "MediaService--prepared()--nMusicPosition==" +
		// nMusicPosition);
		// if (musicCount>1) {
		// int postion=0;
		// if(musicCount%2==0){
		// postion=mPreferences.getInt(Constant.PREFERENCES_POSITION_TWO, 0);
		// }else {
		// postion=mPreferences.getInt(Constant.PREFERENCES_POSITION_ONE, 0);
		// }
		// String mMusicPath = getMusciPath(postion);
		// Flog.d(TAG, "MediaService--prepared()--postion==" + postion);
		// Flog.d(TAG, "MediaService--prepared()--mMusicPath==" + mMusicPath);
		// if (mMusicPath != null) {
		// CoverList.bitmap = null;
		// mAlbumUtil.scanAlbumImage(mMusicPath, this);
		// Flog.d(TAG,
		// "MediaService--prepared()--CoverList.bitmap=================="
		// + CoverList.bitmap);
		// if (CoverList.bitmap != null && !CoverList.bitmap.isRecycled()) {
		// Flog.d(TAG,
		// "MediaService--prepared()--CoverList.cover.isRecycled()==================");
		// CoverList.bitmap.recycle();
		// CoverList.bitmap = null;
		//
		// }
		// }
		// }
		CoverList.cover = null;
		CoverList.bitmap = null;
		mAlbumUtil.scanAlbumImage(mMusicInfo.getPath(), this);// 1
		System.gc();

		if (nMusicSize > 0 && mMusicInfo != null) {// TODO
			Flog.d(TAG, "mediaservice---play()---if");
			isPause = false;
			sMusicPath = mMusicInfo.getPath();
			Flog.d(TAG, "mediaservice---play()---if---" + mMusicInfo.toString());
			Flog.d(TAG, "mediaservice---play()---if---" + mMusicInfo.getFile());
			String subLrcPath2 = sMusicPath.substring(
					sMusicPath.lastIndexOf("/") + 1,
					sMusicPath.lastIndexOf("."));
			Flog.d(TAG,
					"mediaservice---play()---if---------------------sMusicPath--------------------------"
							+ sMusicPath);
			sLyricPath = LyricList.map.get(subLrcPath2);
			Flog.d(TAG, "mediaservice---play()---if---sLyricPath---"
					+ sLyricPath);
			if (sMusicPath != null) {
				Flog.d(TAG, "mediaservice-----------play()------1");
				if (isNext) {
					Flog.d(TAG, "mediaservice------play()--------2");
					isNext = false;
					sPath = sMusicPath;
					NativePlayer.nativePlayerStop();
					if (PlayerActivity.mVisualizerView != null) {
						PlayerActivity.mVisualizerView.releaseVisualizerFx();
					}
					// System.gc();
				}
				isStart = false;

				// getLyricUI();// 显示UI
				timer.cancel();// 取消前一次的计时
				timer.start();// 重新开始计时

			}
		}
		Flog.d(TAG, "MediaService--prepared()--end");
	}

	CountDownTimer timer = new CountDownTimer(1600, 400) {

		@Override
		public void onTick(long millisUntilFinished) { // 可更新UI
			Flog.d(TAG, "mediaservice-------------CountDownTimer-----onTick()");

		}

		@Override
		public void onFinish() { // 计时结束，开始播放
			Flog.d(TAG,
					"mediaservice-------------CountDownTimer-----onFinish()");
			Flog.d(TAG, "mediaservice------play()--------" + sMusicPath);
			Flog.d(TAG, "mediaservice------play()--------" + sPath);
			PlayerStart(sMusicPath);

		}
	};

	void PlayerStart(final String filename) {
		Flog.d(TAG, "PlayerStart()-------start");
		isStart = true;
		isUpdateUIFrist = false;
		if (mPreferences.getBoolean("mToggleSwitch", false) == true
				&& AffectActivity.isAffectActivity == true) {
			Flog.d(TAG,
					"mediaservice--------------&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&2");
			AffectActivity.mToggleSwitch.setChecked(false);
			AffectActivity.mToggleSwitch.setChecked(true);
		}

		new Thread() {
			public void run() {
				synchronized (this) {
					while (isStart) {
						Flog.d(TAG, "mediaservice--------------4");
						Flog.d(TAG,
								"nativePlayerInit--------------enter-------play-------"
										+ filename);
						if (isPause) {
							continue;
						}
						result = NativePlayer.nativePlayerInit(filename);
						Flog.d(TAG, "nativePlayerInit--------------out");
						if (result == null) {
							Log.e("MainActivity", "result:" + result);
							break;
						}

						if (result.isRealease == 1) {// 释放内存结束
							continue;

						}

						if (result.initFlag == 1)// 开始播放
						{
							Flog.d(TAG,
									"mediaservice-------------44444444444444444444444444444");

							MainActivity.isSDMove = false;
							isPause = false;
							isForward = false;
							isRewind = false;
							isSeekBarEnd = false;
							MainActivity.isFrist = false;
							PlayerActivity.isFrist = true;
							PlayerActivity.isFristCover3D = false;
							Flog.d(TAG, "mediaservice--------------5");
							NativePlayer.nativePlayerStart();
							PlayerActivity.isPlaying = false;
							isFristServier = true;
							isNext = true;
							initLrc();
							getLyricUI();
							nMusicDuration = (int) result.totalTime * 1000;

						}

						if (result.isPlayEnd == 1)// 播放结束
						{
							Flog.d(TAG, "mediaservice----isPlayEnd");

							removeAllMsg();
							mHandler.sendEmptyMessage(Constant.MEDIA_PLAY_COMPLETE);

						}

					}
				}
			}
		}.start();
		Flog.d(TAG, "PlayerStart()-------end");
	}

	private void autoPlay() {// /播放结束自动播放
		Flog.d(TAG, "---MediaService------autoPlay()----start");
		if (nMode == Constant.MODE_NORMAL) {
			if (nMusicPosition != getMusicSize() - 1) {
				next();
			} else {
				mBinder.playPause();
			}
		} else if (nMode == Constant.MODE_REPEAT_ONE) {
			prepared();
		} else {
			next();
		}
		Flog.d(TAG, "---MediaService------autoPlay()----end");
	}

	private void previous() {
		Flog.d(TAG, "---MediaService------previous()-----start");
		int nMusicSize = getMusicSize();
		if (nMusicSize > 0) {
			if (nMode == Constant.MODE_RANDOM) {
				Flog.d(TAG, "---MediaService------previous()----3");
				nMusicPosition = (int) (Math.random() * nMusicSize);
			} else {
				if (nMusicPosition == 0) {
					nMusicPosition = nMusicSize - 1;
				} else {
					nMusicPosition--;

				}
			}
			// mAllListActivity.setPosition(nMusicPosition);
			Flog.d(TAG, "MediaService--previous()--");
			Flog.d(TAG, "MediaService--previous()--nMusicPosition--"
					+ nMusicPosition);
			startServiceCommand();
		}
		Flog.d(TAG, "---MediaService------previous()-----end");
	}

	private void next() {
		Flog.d(TAG, "---MediaService------next()-----start");
		int nMusicSize = getMusicSize();
		if (nMusicSize > 0) {
			if (nMode == Constant.MODE_RANDOM) {
				nMusicPosition = (int) (Math.random() * nMusicSize);
			} else {
				if (nMusicPosition == nMusicSize - 1) {
					nMusicPosition = 0;
				} else {
					nMusicPosition++;

				}
			}
			// mAllListActivity.setPosition(nMusicPosition);
			Flog.d(TAG, "MediaService--next()--xyznMusicPosition---"
					+ nMusicPosition);
			startServiceCommand();

		}

		Flog.d(TAG, "MediaService--next()-----end");
	}

	// 后退
	private void rewind() {
		isRewind = true;
		Flog.d(TAG, "---MediaService------rewind()-----start");
		Flog.d(TAG, "---MediaService------rewind()-----start----1-----"
				+ nMusicCurrent);
		current = nMusicCurrent - 3000;
		nMusicCurrent = current > 0 ? current : 0;
		Flog.d(TAG, "---MediaService------rewind()-----start-----2-----"
				+ nMusicCurrent);
		NativePlayer.nativePlayerSeekTo(nMusicCurrent / 1000);//
		mBinder.playUpdate(nMusicCurrent, sLyricPath, nPlayingPage, mMusicInfo);
		mHandler.sendEmptyMessageDelayed(Constant.MEDIA_PLAY_REWIND, 100);
		Flog.d(TAG, "MediaService--rewind()--end");
	}

	// 快进
	private void forward() {
		isForward = true;
		Flog.d(TAG, "---MediaService------forward()-------start");
		Flog.d(TAG, "---MediaService------forward()-------start-----1----"
				+ nMusicCurrent);
		current = nMusicCurrent + 3000;
		nMusicCurrent = current < nMusicDuration ? current : nMusicDuration;
		Flog.d(TAG, "---MediaService------forward()-------start-----2----"
				+ nMusicCurrent);
		NativePlayer.nativePlayerSeekTo(nMusicCurrent / 1000);//
		mBinder.playUpdate(nMusicCurrent, sLyricPath, nPlayingPage, mMusicInfo);
		mHandler.sendEmptyMessageDelayed(Constant.MEDIA_PLAY_FORWARD, 100);
		Flog.d(TAG, "MediaService--forward()--end");
	}

	// 重播
	private void replay() {// 快进快退的播放
		Flog.d(TAG, "---MediaService------replay()-------start");

		if (mHandler.hasMessages(Constant.MEDIA_PLAY_REWIND)) {
			mHandler.removeMessages(Constant.MEDIA_PLAY_REWIND);
		}
		if (mHandler.hasMessages(Constant.MEDIA_PLAY_FORWARD)) {
			mHandler.removeMessages(Constant.MEDIA_PLAY_FORWARD);
		}
		mHandler.sendEmptyMessage(Constant.MEDIA_PLAY_UPDATE);
		if (isLyric && mAllLyricView != null) {
			mAllLyricView.setSentenceEntities(mLyricList);

			mHandler.sendEmptyMessageDelayed(Constant.MEDIA_PLAY_UPDATE_LYRIC,
					Constant.UPDATE_LYRIC_TIME);
		}
		Flog.d(TAG, "MediaService--replay()--end");
	}

	private int getMusicSize() {
		Flog.d(TAG, "---MediaService------getMusicSize()-------start");
		int nMusicSize = 0;
		switch (nPlayingPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:
			nMusicSize = MusicList.list.size();
			break;
		case Constant.VIEWPAHER_MENU_ALBUM_LIST:
			nMusicSize = AlbumList.list.get(nAlbumPosition).getMusicList()
					.size();
			break;
		case Constant.VIEWPAHER_MENU_ARTIST_LIST:
			nMusicSize = ArtistList.list.get(nArtistPosition).getMusicList()
					.size();
			break;
		case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
			nMusicSize = FavoriteList.list.size();
			break;
		}
		Flog.d(TAG, "---MediaService------getMusicSize()-------nMusicSize--"
				+ nMusicSize);
		Flog.d(TAG, "---MediaService------getMusicSize()-------end");
		return nMusicSize;
	}

	private void startServiceCommand() {
		Flog.d(TAG, "MediaService---startServiceCommand()----start");
		Intent intent = new Intent(getApplicationContext(), MediaService.class);
		intent.putExtra(Constant.INTENT_LIST_PAGE, nPlayingPage);
		intent.putExtra(Constant.INTENT_LIST_POSITION, nMusicPosition);
		Flog.d(TAG, "MediaService---startServiceCommand()--position--"
				+ nMusicPosition);
		startService(intent);
		Flog.d(TAG, "MediaService---startServiceCommand()----end");
	}

	public int getLyricIndex() {
		Flog.d(TAG, "---MediaService------getLyricIndex()--start");

		if (isStart == true) {
			// currentTime = nMusicCurrent;

			// duration = nMusicDuration;

		}
		if (nMusicCurrent < nMusicDuration) {
			for (int i = 0; i < mLyricList.size(); i++) {
				if (i < mLyricList.size() - 1) {

					if (nMusicCurrent < mLyricList.get(i).getTime() && i == 0) {
						nLyricindex = i;
					}
					if (nMusicCurrent > mLyricList.get(i).getTime()
							&& nMusicCurrent < mLyricList.get(i + 1).getTime()) {
						nLyricindex = i;
					}
				}
				if (i == mLyricList.size() - 1
						&& nMusicCurrent > mLyricList.get(i).getTime()) {
					nLyricindex = i;
				}
			}
		}

		Flog.d(TAG, "---MediaService------getLyricIndex()--end");
		return nLyricindex;

	}

	/**
	 * 初始化歌词
	 */
	private void initLrc() {
		Flog.d(TAG, "---MediaService------initLrc()---start");
		isLyric = false;
		if (sLyricPath != null) {

			Flog.d(TAG, "---MediaService------initLrc()----lyric---"
					+ sLyricPath);
			try {
				Flog.d(TAG, "---MediaService------initLrc()-------if");
				LyricParser parser = new LyricParser(sLyricPath);// 解析歌词
				mLyricList = parser.parser();
				Flog.d(TAG,
						"MediaService------initLrc()----mLyricLis	mBinder.playStart(mMusicInfo, nPlayingPage);t----------------"
								+ mLyricList.toString());
				isLyric = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Flog.d(TAG, "---MediaService------initLrc()---else");
			if (mAllLyricView != null) {
				try {
					mAllLyricView.clear();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}
		}
		Flog.d(TAG, "---MediaService------initLrc()---end");
	}

	/**
	 * 准备好开始播放工作
	 */
	private void getLyricUI() {// TODO 方法名改
		Flog.d(TAG, "---MediaService------getLyricUI()----start");
		mHandler.sendEmptyMessage(Constant.MEDIA_PLAY_START);// 更新界面
		if (sLyricPath != null && mAllLyricView != null) {
			Flog.d(TAG, "---MediaService------getLyricUI()-------1");

			if (isLyric) {
				Flog.d(TAG, "---MediaService------getLyricUI()-------2");
				mAllLyricView.setSentenceEntities(mLyricList);

				mHandler.sendEmptyMessageDelayed(
						Constant.MEDIA_PLAY_UPDATE_LYRIC,
						Constant.UPDATE_LYRIC_TIME); // 通知刷新歌词
				// 开线程用于获取当前播放时间轴
				running = true;
			}
		}
		Flog.d(TAG, "---MediaService------getLyricUI()----end");
	}

	/**
	 * 开始播放，获得总时间和AudioSessionId，并启动更新UI任务
	 */
	private void startUpdateUIView() {// 更新？界面
		Flog.d(TAG, "MediaService--startUpdateView()-----start");
		Flog.d(TAG, "MediaService--startUpdateView()----" + nMusicDuration);
		mMusicInfo.setMp3Duration(nMusicDuration);
		mBinder.playStart(mMusicInfo, nPlayingPage);
		mHandler.sendEmptyMessageDelayed(Constant.MEDIA_PLAY_UPDATE,
				Constant.UPDATE_UI_TIME);
		Flog.d(TAG, "MediaService--startUpdateView()-----------------------end");
	}

	/**
	 * 近期的时间给服务更新界面
	 */
	private void updateUI() {// TODO 方法名
		Flog.d(TAG, "---MediaService----xyz--updateUI()-----start");

		// TODO Auto-generated method stub
		if (isStart == true) {

			if (isNext == true) {
				Flog.d(TAG, "---MediaService---xyz---update()-----if111111");
				if (isFristServier) {
					String artist = mMusicInfo.getArtist();
					String name = mMusicInfo.getName();
					if (artist == null || artist.equals("")) {
						artist = "未知";
					}

					mNotification.tickerText = artist + " - " + name;
						Intent playIntent = new Intent(Constant.BROADCAST_ACTION_NOT_PLAY);
						PendingIntent playPendingIntent = PendingIntent.getBroadcast(
								getApplicationContext(), 0, playIntent, 0);
						mRemoteViews.setOnClickPendingIntent(R.id.not_play,
								playPendingIntent);

						Intent prevIntent = new Intent(Constant.BROADCAST_ACTION_NOT_PREV);
						PendingIntent prevPendingIntent = PendingIntent.getBroadcast(
								getApplicationContext(), 0, prevIntent, 0);
						mRemoteViews.setOnClickPendingIntent(R.id.not_previous,
								prevPendingIntent);
						Intent nextIntent = new Intent(Constant.BROADCAST_ACTION_NOT_NEXT);
						PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
								getApplicationContext(), 0, nextIntent, 0);
						mRemoteViews.setOnClickPendingIntent(R.id.not_next,
								nextPendingIntent);
						Intent exitIntent = new Intent(Constant.BROADCAST_ACTION_NOT_EXIT);
						PendingIntent exitPendingIntent = PendingIntent.getBroadcast(
								getApplicationContext(), 0, exitIntent, 0);
						mRemoteViews.setOnClickPendingIntent(R.id.not_exit,
								exitPendingIntent);
						Flog.d(TAG,
								"---MediaService----xyz--updateUI()-----CoverList.bitmap--"
										+ CoverList.bitmap);
						if (CoverList.bitmap != null) {
							Flog.d(TAG,
									"---MediaService----xyz--updateUI()-----CoverList.bitmap!=null");
							mRemoteViews.setImageViewBitmap(R.id.notification_item_album,
									CoverList.bitmap);
						} else {
							mRemoteViews.setImageViewResource(R.id.notification_item_album,
									R.drawable.main_album_item);
						}
						// mRemoteViews.setImageViewResource(R.id.notification_item_album,
						// R.drawable.main_album_item);

						mRemoteViews.setTextViewText(R.id.notification_item_name, name);
						mRemoteViews.setTextViewText(R.id.notification_item_artist, artist);
						mRemoteViews.setImageViewResource(R.id.not_play,
								R.drawable.main_btn_play);
						mNotification.contentView = mRemoteViews;
						startForeground(1, mNotification);// id为0则不显示Notification

						Intent stateIntent = new Intent(Constant.BROADCAST_ACTION_APP_STATE);
						sendBroadcast(stateIntent);
						Intent appstateIntent = new Intent(
								Constant.BROADCAST_ACTION_APP_MUSICINFO);
						sendBroadcast(appstateIntent);
						isFristServier=false;
				}
				if (isSeekBarEnd == true) {// 是否滚动滚动条

					Flog.d(TAG,
							"---MediaService--xyz----update()if-----stamp----"
									+ (int) result.timeStamp);
					Flog.d(TAG,
							"---MediaService----xyz--update()if-----seekBarMoveTime-----"
									+ (int) seekBarMoveTime / 1000);

					int a = seekBarMoveTime - (int) result.timeStamp * 1000;
					Flog.d(TAG, "---MediaService---xyz---update()if-----a-----"
							+ a);

					if (DITflag == false) {
						dit = a;// 判断seekbar是向前或向后唯一的标示

						DITflag = true;
					}
					Flog.d(TAG,
							"---MediaService--xyz----update()if-----dit-----"
									+ dit);
					nMusicCurrent = seekBarMoveTime;
					Flog.d(TAG,
							"---MediaService----xyz--update()if-----mp3----"
									+ nMusicCurrent / 1000);
					if (dit > 0
							&& (int) seekBarMoveTime / 1000 <= (int) result.timeStamp) {// 向前滑动
						isSeekBarEnd = false;
						Flog.d(TAG, "---MediaService--xyz----update()if-----1");

					} else if (dit < 0
							&& ((int) result.timeStamp - (int) seekBarMoveTime / 1000) <= 2) {// 向后滑动
						isSeekBarEnd = false;

						Flog.d(TAG, "---MediaService---xyz---update()if-----2");

					}
					Flog.d(TAG,
							"---MediaService----xyz--update()if-----2-----------------------------"
									+ ((int) result.timeStamp - (int) seekBarMoveTime / 1000));
				} else if (isForward == true || isRewind == true) {// 快进快退

					Flog.d(TAG, "---MediaService----xyz--update()else if-----"
							+ (int) result.timeStamp * 1000);
					Flog.d(TAG, "---MediaService---xyz---update()else if-----"
							+ current);
					nMusicCurrent = current;

					if (isForward == true
							&& nMusicCurrent / 1000 < (int) result.timeStamp) {// 快进
						Flog.d(TAG,
								"---MediaService--xyz----update()else if-----1");
						isForward = false;
					} else if (isRewind == true
							&& (int) result.timeStamp - (int) current / 1000 <= 2) {// 快退

						Flog.d(TAG,
								"---MediaService--xyz----update()else if-----2");
						isRewind = false;
					}

				} else {

					if (isUpdateUIFrist == false) {
						nMusicCurrent = 0;
						isUpdateUIFrist = true;
					} else {
						nMusicCurrent = (int) result.timeStamp * 1000;
					}

					Flog.d(TAG, "---MediaService---xyz---update()else-----"
							+ nMusicCurrent);
				}
				Flog.d(TAG, "MediaServer-xyz-update()--" + nMusicCurrent);

				mBinder.playUpdate(nMusicCurrent, sLyricPath, nPlayingPage,
						mMusicInfo);
				mHandler.sendEmptyMessageDelayed(Constant.MEDIA_PLAY_UPDATE,
						Constant.UPDATE_UI_TIME);
			} else {
				Flog.d(TAG,
						"---MediaService---xyz---update()-----if22222222222");
				if (sMusicPath != sPath) {

					nMusicCurrent = 0;
					mBinder.playStart(mMusicInfo, nPlayingPage);
					mHandler.sendEmptyMessageDelayed(
							Constant.MEDIA_PLAY_UPDATE, Constant.UPDATE_UI_TIME);
				}
			}
			Flog.d(TAG, "---MediaService----xyz--updateUI()-----end");
		}
	}

	/**
	 * 暂停音乐
	 */
	private void pause() {
		Flog.d(TAG, "MediaService---pause()------start");
		removeAllMsg();
		isPause = true;
		try {
			NativePlayer.nativePlayerPause();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		isStart = false;
		mBinder.playPause();
		mRemoteViews.setImageViewResource(R.id.not_play,
				R.drawable.main_btn_pause);
		mNotification.contentView = mRemoteViews;
		startForeground(1, mNotification);
		Intent stateIntent = new Intent(Constant.BROADCAST_ACTION_APP_STATE);
		sendBroadcast(stateIntent);
		Flog.d(TAG, "MediaService---pause()----end");
	}

	/**
	 * 播放完成
	 */
	private void complete() {
		Flog.d(TAG, "---MediaService------complete()----start");
		// TODO Auto-generated method stub
		mBinder.playComplete();
		mBinder.playUpdate(nMusicDuration, sLyricPath, nPlayingPage, mMusicInfo);
		autoPlay();// ////
		Flog.d(TAG, "MediaService---complete()----end");
	}

	/**
	 * 刷新歌词
	 */
	private void updateLrcView() {
		Flog.d(TAG, "---MediaService------updateLrcView()-----start");
		if (mLyricList.size() > 0 && mAllLyricView != null) {

			mAllLyricView.setIndex(getLyricIndex());
			mAllLyricView.invalidate();
			Flog.d(TAG, "---MediaService------updateLrcView()-----running--"
					+ running);
			if (running) {
				mHandler.sendEmptyMessageDelayed(
						Constant.MEDIA_PLAY_UPDATE_LYRIC,
						Constant.UPDATE_LYRIC_TIME);
			}
		}
		Flog.d(TAG, "---MediaService------updateLrcView()-----end");

	}

	/**
	 * 移除更新歌词的消息
	 */
	private void removeUpdateLrcViewMsg() {// TODO removeXXMsg
		Flog.d(TAG, "---MediaService------removeUpdateLrcViewMsg()-------");
		if (mHandler != null
				&& mHandler.hasMessages(Constant.MEDIA_PLAY_UPDATE_LYRIC)) {
			mHandler.removeMessages(Constant.MEDIA_PLAY_UPDATE_LYRIC);
		}
		Flog.d(TAG, "MediaService---removeUpdateLrcViewMsg()--");
	}

	/**
	 * 移除更新UI的消息
	 */
	private void removeUpdateMsg() {// TODO
		Flog.d(TAG, "---MediaService------removeUpdateMsg()");
		if (mHandler != null
				&& mHandler.hasMessages(Constant.MEDIA_PLAY_UPDATE)) {
			mHandler.removeMessages(Constant.MEDIA_PLAY_UPDATE);
		}
	}

	/**
	 * 移除所有消息
	 */
	private void removeAllMsg() {
		Flog.d(TAG, "---MediaService------removeAllMsg()");
		removeUpdateMsg();
		removeUpdateLrcViewMsg();
		Flog.d(TAG, "MediaService---removeAllMsg()----end");
	}

	/**
	 * 歌词同步处理
	 */
	private int[] getLrcIndex(int currentTime, int duration) {
		Flog.d(TAG, "---MediaService------getLrcIndex()-----start");
		int index = 0;
		int size = mLyricList.size();
		if (currentTime < duration) {
			for (int i = 0; i < size; i++) {
				if (i < size - 1) {
					if (currentTime < mLyricList.get(i).getTime() && i == 0) {
						index = i;
					}
					if (currentTime > mLyricList.get(i).getTime()
							&& currentTime < mLyricList.get(i + 1).getTime()) {
						index = i;
					}
				}
				if (i == size - 1 && currentTime > mLyricList.get(i).getTime()) {
					index = i;
				}
			}
		}
		int temp1 = mLyricList.get(index).getTime();
		int temp2 = (index == (size - 1)) ? 0 : mLyricList.get(index + 1)
				.getTime() - temp1;
		Flog.d(TAG, "---MediaService------getLrcIndex()-----end");
		return new int[] { index, currentTime, temp1, temp2 };
	}

	// 电话监听
	private class PhoneListener extends PhoneStateListener {// TODO

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			// 如果有电话来的话暂停播放
			if (state == TelephonyManager.CALL_STATE_RINGING && isStart == true) {
				pause();
			}
		}
	}

	/*
	 * 通知栏和桌面插件的广播接受
	 */private class NotificationReceiver extends BroadcastReceiver {// TODO
																		// 名称要更改、常量移除

		@Override
		public void onReceive(Context context, Intent intent) {
			Flog.d(TAG, "---MediaService------onReceive()-------");
			// TODO Auto-generated method stub
			if (intent != null) {
				Flog.d(TAG, "---MediaService------onReceive()-------1");
				if (intent.getAction().equals(

				Constant.BROADCAST_ACTION_NOT_NEXT)) {// 通知栏的下一首
					Flog.d(TAG, "---MediaService------onReceive()------2");
					next();
				} else if (intent.getAction().equals(

				Constant.BROADCAST_ACTION_NOT_PREV)) {// 通知栏的上一首"com.flyaudio.action.previous"
					Flog.d(TAG, "---MediaService------onReceive()-------3");
					previous();

				} else if (intent.getAction().equals(
						Constant.BROADCAST_ACTION_NOT_PLAY)) {// 通知栏的播放或暂停
					if (isStart == true) {
						Flog.d(TAG, "---MediaService------onReceive()-------4");

						mRemoteViews.setImageViewResource(R.id.not_play,
								R.drawable.main_btn_pause);
						mNotification.contentView = mRemoteViews;
						startForeground(1, mNotification);
						pause();
					} else {
						Flog.d(TAG, "---MediaService------onReceive()-------5");
						mRemoteViews.setImageViewResource(R.id.not_play,
								R.drawable.main_btn_play);
						mNotification.contentView = mRemoteViews;
						startForeground(1, mNotification);

						if (sMusicPath != null) {
							Flog.d(TAG, "button---play");
							try {
								NativePlayer.nativePlayerResume();
								isPause = false;
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							isStart = true;
							getLyricUI();
						} else {// 无指定情况下播放全部歌曲列表的第一首
							startServiceCommand();
						}

					}

				} else if (intent.getAction().equals(
						Constant.BROADCAST_ACTION_NOT_STATE)) {
					Flog.d(TAG,
							"---MediaService------onReceive()-----xyzBROADCAST_ACTION_NOT_STATE--");
					mRemoteViews.setImageViewResource(R.id.not_play,
							R.drawable.main_btn_pause);
					mNotification.contentView = mRemoteViews;
					startForeground(1, mNotification);
					Flog.d(TAG,
							"---MediaService------onReceive()-----xyzBROADCAST_ACTION_NOT_STATE--end");
				}

				else if (intent.getAction().equals(
						Constant.BROADCAST_ACTION_NOT_EXIT)) {// 通知栏的退出
					Flog.d(TAG, "---MediaService------onReceive()-------6");
					stopForeground(true);
					Intent playstateIntent = new Intent(
							Constant.BROADCAST_ACTION_APP_PAUSE);
					sendBroadcast(playstateIntent);
					stopService(MainActivity.mPlayIntent);
					AllListActivity.getInstance().exit();
				}

			}
		}
	}

	private class BroadcastSD extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "BroadcastSD---onReceive()--start");
			Flog.d(TAG, "BroadcastSD---onReceive()--intent.getAction()--"
					+ intent.getAction());
			if (intent.getAction()
					.equals("android.intent.action.MEDIA_MOUNTED")) {// SD卡已经成功挂载

				Intent mIntent = new Intent();
				mIntent.setAction(Constant.BROADCAST_ACTION_MEDIA_MOUNTED);
				sendBroadcast(mIntent);
			} else if (intent.getAction().equals(
					"android.intent.action.MEDIA_REMOVED")
					|| intent.getAction().equals(
							"android.intent.action.ACTION_MEDIA_UNMOUNTED")
					|| intent.getAction().equals(
							"android.intent.action.ACTION_MEDIA_BAD_REMOVAL")) {

				Intent mIntent = new Intent();
				mIntent.setAction(Constant.BROADCAST_ACTION_MEDIA_REMOVED);
				sendBroadcast(mIntent);
			}
		}
	};

	private class ServiceHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case Constant.MEDIA_PLAY_START:

				startUpdateUIView();// TODO

				break;

			case Constant.MEDIA_PLAY_UPDATE:
				updateUI();
				break;

			case Constant.MEDIA_PLAY_COMPLETE:
				complete();
				break;
			case Constant.MEDIA_PLAY_UPDATE_LYRIC:
				updateLrcView();
				break;

			case Constant.MEDIA_PLAY_REWIND:
				rewind();
				break;

			case Constant.MEDIA_PLAY_FORWARD:
				forward();
				break;
			}
		}
	}
}
