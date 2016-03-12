package com.flyaudio.flyMediaPlayer.activity;

import java.io.InputStream;
import java.util.ArrayList;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.ArtistList;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.sdl.NativePlayer;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaService;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnModeChangeListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayCompleteListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayErrorListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayPauseListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayStartListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayingListener;

import com.flyaudio.flyMediaPlayer.until.AllListActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.until.FormatUtil;
import com.flyaudio.flyMediaPlayer.until.Rotate3dAnimation;
import com.flyaudio.flyMediaPlayer.view.MyLyricView;
import com.flyaudio.flyMediaPlayer.view.MySurfaceView;
import com.flyaudio.flyMediaPlayer.view.PushView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract.Constants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnTouchListener, OnSeekBarChangeListener {
	private boolean isFavorite = false;// 是否最爱

	private ImageButton mBtnReturn;
	private ImageButton mBtnMode;
	private ImageButton mBtnPrevious;
	private ImageButton mBtnPlay;
	private ImageButton mBtnNext;
	private ImageButton mBtnFavorite;
	private ImageButton mVolume;
	private SeekBar mPlaySeekBar;
	private TextView mViewForMusicName;
	private TextView mViewForArtist;
	private ImageView mMusicCover;
	private ImageView mMusicFavorite;
	public static MySurfaceView mVisualizerView;
	private MyLyricView mAllLyricView;
	private ServiceConnection mServiceConnection;
	private static final String TAG = "PlayerActivity";
	private MediaBinder mBinder;
	private boolean isPlayActivity;
	private TextView mCurrentTime;
	private TextView mTotalTime;
	private boolean isFirstTransition3dAnimation;
	private Intent mPlayIntent;
	private AudioManager mAudioManager;
	private PopupWindow mPopupVolume;
	private SeekBar seekVolumeBar;
	private int nMusicPosition;
	private ImageButton Affect;
	public static boolean isPlaying;
	public static boolean isFrist = true;
	public static boolean isFristCover3D;
	private SDListenerPlayReceiver mListenerReceiver;

	private AllListActivity mAllListActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_player);
		mAllListActivity = (AllListActivity) getApplication();
		AllListActivity.getInstance().addActivity(this);
		Intent intent = new Intent(Constant.BROADCAST_ACTION_SERVICE);
		intent.putExtra(Constant.INTENT_ACTIVITY, Constant.ACTIVITY_PLAYER);
		sendBroadcast(intent);
		mPlayIntent = new Intent(getApplicationContext(), MediaService.class);
		initPortraitActivity();
		initPopupVolume();
		mListenerReceiver = new SDListenerPlayReceiver();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Constant.BROADCAST_ACTION_MEDIA_MOUNTED);
		mFilter.addAction(Constant.BROADCAST_ACTION_MEDIA_REMOVED);
		registerReceiver(mListenerReceiver, mFilter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Flog.d(TAG, "onResume()--start");
		Flog.d(TAG, "onResume()--MainActivity.isSDMove--"
				+ MainActivity.isSDMove);
		isPlayActivity = true;
		isPlaying = false;
		isFristCover3D = false;
		if (MediaService.isStart == false) {
			mPlaySeekBar.setProgress(0);
		}
	}

	public void finish() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "finish()---start");
		super.finish();
		if (mServiceConnection != null) {
			Flog.d(TAG, "finish()---mServiceConnection != null");
			unbindService(mServiceConnection);// 一定要在finish之前解除绑定
			mServiceConnection = null;
		}
		mVisualizerView.releaseVisualizerFx();

		Flog.d(TAG, "finish()---end");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mListenerReceiver);
	}

	// 竖频初始化控件及相应的监听
	private void initPortraitActivity() {
		Flog.d(TAG, "PlayerActivity-----initPortraitActivity()-----start");
		setContentView(R.layout.music_player);
		mBtnReturn = (ImageButton) findViewById(R.id.play_back);
		mVolume = (ImageButton) findViewById(R.id.play_voice);
		Affect = (ImageButton) findViewById(R.id.play_affect);
		mBtnMode = (ImageButton) findViewById(R.id.play_music_mode);
		mBtnPrevious = (ImageButton) findViewById(R.id.play_music_previous);
		mBtnPlay = (ImageButton) findViewById(R.id.play_music_playing);
		mBtnNext = (ImageButton) findViewById(R.id.play_music_next);
		mBtnFavorite = (ImageButton) findViewById(R.id.play_favorites);
		mPlaySeekBar = (SeekBar) findViewById(R.id.play_progress);
		mCurrentTime = (TextView) findViewById(R.id.play_currentTime);
		mTotalTime = (TextView) findViewById(R.id.play_allTime);
		mViewForMusicName = (TextView) findViewById(R.id.music_lyric_title);
		mViewForArtist = (TextView) findViewById(R.id.music_lyric_arist);
		mMusicCover = (ImageView) findViewById(R.id.play_album);
		mMusicFavorite = (ImageView) findViewById(R.id.play_favorites);
		mAllLyricView = (MyLyricView) findViewById(R.id.activity_player_lview);
		mVisualizerView = (MySurfaceView) findViewById(R.id.activity_player_visualizer);
		mMusicFavorite = (ImageView) findViewById(R.id.activity_player_iv_favorite);
		mCurrentTime.setText(Constant.TIME_NORMAL);
		mTotalTime.setText(Constant.TIME_NORMAL);
		mBtnReturn.setOnClickListener(this);
		Affect.setOnClickListener(this);
		mBtnMode.setOnClickListener(this);
		mBtnPrevious.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnFavorite.setOnClickListener(this);
		mVolume.setOnClickListener(this);
		mBtnPrevious.setOnLongClickListener(this);
		mBtnNext.setOnLongClickListener(this);
		mBtnPrevious.setOnTouchListener(this);
		mBtnNext.setOnTouchListener(this);
		mPlaySeekBar.setOnSeekBarChangeListener(this);

		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				mBinder = null;
			}

			@SuppressLint("NewApi")
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Flog.d(TAG, "PlayerActivity--------onServiceConnected---");
				mBinder = (MediaBinder) service;
				if (mBinder != null) {

					Flog.d(TAG,
							"PlayerActivity----onServiceConnected----mBinder---"
									+ (mBinder == null));
					mBinder.setOnPlayStartListener(new OnPlayStartListener() {

						@Override
						public void onStart(MusicInfo info, int page) {
							Flog.d(TAG,
									"PlayerActivity--onServiceConnected--onStart()");
							// TODO Auto-generated method stub

							if (isPlayActivity) {
								Flog.d(TAG,
										"PlayerActivity--onServiceConnected--onStart()-----------------------------------------------------");
								getMusicDetials(info, page);
								if (MediaService.isStart == false) {

									mTotalTime.setText(Constant.TIME_NORMAL);
									mPlaySeekBar.setProgress(0);
									mMusicCover
											.setImageResource(R.drawable.playing_album);

								}
								mCurrentTime.setText(Constant.TIME_NORMAL);
								isFavorite = info.isFavorite();
								Flog.d(TAG,
										"PlayerActivity----onServiceConnected()---isisFavorite---"
												+ isFavorite);
								mBtnFavorite
										.setImageResource(isFavorite ? R.drawable.music_favorites_d
												: R.drawable.music_favorites_u);

								Flog.d(TAG,
										"PlayerActivity----onServiceConnected()---onStart---end");
							}
						}
					});
					mBinder.setOnPlayingListener(new OnPlayingListener() {

						@Override
						public void onPlay(int currentPosition, String path,
								int page, MusicInfo info) {// //定位到了----------------------------currentPosition
							// TODO Auto-generated method stub
							if (isPlayActivity && MediaService.isStart == true) {
								if (mVisualizerView != null) {
									mVisualizerView.setVisibility(View.VISIBLE);

								}
								if (isPlaying == false) {
									Flog.d(TAG,
											"PlayerActivity--onServiceConnected--onPlaying()----1");
									getMusicDetials(info, page);
									mVisualizerView.setupVisualizerFx();
									mBtnPlay.setImageResource(R.drawable.main_btn_play);
									isPlaying = true;
									mTotalTime.setText(FormatUtil
											.formatTime((int) MediaService.result.totalTime * 1000));
									mPlaySeekBar
											.setMax((int) MediaService.result.totalTime * 1000);
									mPlaySeekBar.setProgress(0);// lyl+
								} else if (isFristCover3D == false) {
									isFirstTransition3dAnimation = true;
									isFristCover3D = true;
									if (CoverList.cover == null) {
										if (MainActivity.isSDMove == false) {
											Drawable drawable = getApplicationContext()
													.getResources()
													.getDrawable(
															R.drawable.main_bottom_album);
											startTransition3dAnimation(drawable);

										}
									} else {
										if (MainActivity.isSDMove == false) {

											startTransition3dAnimation(CoverList.cover);

										}

									}

								} else {// lyl+ 切换下一首歌时，播放进度时间显示应该从0开始
									Flog.d(TAG,
											"PlayerActivity--onServiceConnected--onPlaying()----"
													+ currentPosition);
									mPlaySeekBar.setProgress(currentPosition);
									mCurrentTime.setText(FormatUtil
											.formatTime(currentPosition));
								}
							}
						}
					});

					mBinder.setOnPlayPauseListener(new OnPlayPauseListener() {

						@Override
						public void onPause() {
							// TODO Auto-generated method stub
							Flog.d(TAG,
									"PlayerActivity--onServiceConnected--onPause()");
							mBtnPlay.setImageResource(R.drawable.main_btn_pause);

						}
					});
					mBinder.setOnPlayCompletionListener(new OnPlayCompleteListener() {

						@Override
						public void onPlayComplete() {
							// TODO Auto-generated method stub
							// clearView();
							MediaService.isStart = false;
							mPlaySeekBar.setProgress(0);
							mCurrentTime.setText("00:00");
							mTotalTime.setText("00:00");
							if (mVisualizerView != null) {
								mVisualizerView.setVisibility(View.INVISIBLE);

							}

						}
					});
					mBinder.setOnPlayErrorListener(new OnPlayErrorListener() {

						@Override
						public void onPlayError() {
							// TODO Auto-generated method stub

						}
					});
					mBinder.setOnModeChangeListener(new OnModeChangeListener() {

						@Override
						public void onModeChange(int mode) {
							// TODO Auto-generated method stub
							mBtnMode.setImageResource(Constant.modeImage[mode]);
						}
					});
					mBinder.setLyricView(mAllLyricView);// 设置歌词视图，是卡拉OK模式S

				}

			}
		};

		bindService(mPlayIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		Flog.d(TAG, "PlayerActivity-----initPortraitActivity()-----end");
	}

	public void getMusicDetials(MusicInfo info, int page) {
		Flog.d(TAG, "getMusicDetials()-------start");
		Flog.d(TAG, "getMusicDetials()-------MainActivity.isSDMove=="
				+ MainActivity.isSDMove);

		if (MainActivity.isSDMove) {
			clearView();

		} else {
			mViewForMusicName.setText(info.getName());
			ArrayList<String> list = new ArrayList<String>();
			list.add(info.getFormat());
			list.add(getResources().getString(R.string.xml_music_size)
					+ info.getSize());
			list.add(info.getGenre());
			list.add(info.getAlbum());
			list.add(info.getYears());
			list.add(info.getChannels());
			list.add(info.getKbps());
			list.add(info.getHz());
			mViewForArtist.setText(info.getArtist());
			mViewForMusicName.setText(info.getName());
		}

		Flog.d(TAG, "getMusicDetials()-------end");

	}

	private void initPopupVolume() {
		Flog.d(TAG, "PlayerActivity----initPopupVolume()-----start");
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		View view = LayoutInflater.from(this).inflate(R.layout.popup_volume,
				null);// 引入窗口配置文件
		mPopupVolume = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		seekVolumeBar = (SeekBar) view.findViewById(R.id.pupup_volume_seek);
		seekVolumeBar.setMax(mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekVolumeBar.setOnSeekBarChangeListener(this);
		mPopupVolume.setBackgroundDrawable(new BitmapDrawable());
		mPopupVolume.setOutsideTouchable(true);
		Flog.d(TAG, "PlayerActivity----initPopupVolume()-----end");
	}

	/**
	 * 专辑封面翻转动画
	 * 
	 * @param bitmap
	 *            专辑封面图
	 */
	private void startTransition3dAnimation(final Drawable bitmap) {
		Flog.d(TAG, "PlayerActivity-----startTransition3dAnimation------start");
		int w = mMusicCover.getWidth() / 2;
		int h = mMusicCover.getHeight() / 2;
		MarginLayoutParams params = (MarginLayoutParams) mMusicCover
				.getLayoutParams();

		Rotate3dAnimation rotation1 = new Rotate3dAnimation(0.0f, 90.0f,
				params.leftMargin + w, params.topMargin + h, 300.0f, true);
		rotation1.setDuration(500);
		rotation1.setFillAfter(true);
		rotation1.setInterpolator(new AccelerateInterpolator());

		final Rotate3dAnimation rotation2 = new Rotate3dAnimation(270.0f,
				360.0f, params.leftMargin + w, params.topMargin + h, 300.0f,
				false);
		rotation2.setDuration(500);
		rotation2.setFillAfter(true);
		rotation2.setInterpolator(new AccelerateInterpolator());

		rotation1.setAnimationListener(new AnimationListener() {

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
				// TODO Auto-generated method stub
				if (isFirstTransition3dAnimation) {
					isFirstTransition3dAnimation = false;
					mMusicCover.setImageDrawable(bitmap);
					mMusicCover.startAnimation(rotation2);
				}
			}
		});
		mMusicCover.startAnimation(rotation1);
		Flog.d(TAG, "PlayerActivity-----startTransition3dAnimation------end");
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "PlayActivity--onProgressChanged()-----start");
		switch (seekBar.getId()) {

		case R.id.play_progress:
			Flog.d(TAG,
					"PlayerActivity---onProgressChanged---seekbar---max----"
							+ seekBar.getMax());
			Flog.d(TAG, "PlayerActivity---onProgressChanged---2----" + fromUser);
			Flog.d(TAG, "PlayerActivity---onProgressChanged---3----" + progress);
			if (fromUser && seekBar.getMax() > 0) {
				Flog.d(TAG, "PlayerActivity---onProgressChanged---"
						+ FormatUtil.formatTime(progress));
				mCurrentTime.setText(FormatUtil.formatTime(progress));
			}
			break;

		case R.id.pupup_volume_seek:
			Flog.d(TAG,
					"PlayerActivity---onProgressChanged---pupup_volume_seek");
			if (fromUser) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}
			break;

		}
		Flog.d(TAG, "PlayActivity--onProgressChanged()-----end");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			isPlaying = false;
			return true;
		} else {
			Flog.d(TAG, "onKeyDown()--------default");
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "PlayActivity--onStartTrackingTouch()-----start");
		if (seekBar.getId() == R.id.play_progress) {
			if (mBinder != null) {
				Flog.d(TAG, "PlayerActivity------onStartTrackingTouch");
				mBinder.seekBarStartTrackingTouch();
			}
		}
		Flog.d(TAG, "PlayActivity--onStartTrackingTouch()-----end");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.play_progress) {
			if (mBinder != null) {
				Flog.d(TAG, "PlayerActivity------onStopTrackingTouch----"
						+ seekBar.getProgress());
				mBinder.seekBarStopTrackingTouch(seekBar.getProgress());
			}
		}
		Flog.d(TAG, "PlayActivity--onStopTrackingTouch()-----end");
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "PlayActivity--onTouch()-----start");
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.play_music_previous:// 竖屏模式松手播放
			Flog.d(TAG,
					"PlayerActivity---------onTouch---activity_player_ib_previous");
			if (mBinder != null && event.getAction() == MotionEvent.ACTION_UP) {

				mBinder.setControlCommand(Constant.CONTROL_COMMAND_REPLAY);
			}
			break;

		case R.id.play_music_next:// 竖屏模式松手播放
			Flog.d(TAG,
					"PlayerActivity---------onTouch---activity_player_ib_next");
			if (mBinder != null && event.getAction() == MotionEvent.ACTION_UP) {
				Flog.d(TAG,
						"PlayerActivity---------onTouch---activity_player_ib_next----and-----MotionEvent.ACTION_UP");
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_REPLAY);
			}
			break;

		}
		Flog.d(TAG, "PlayActivity--onTouch()-----end");
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "PlayActivity--onLongClick()-----start");
		switch (v.getId()) {
		case R.id.play_music_previous:// 竖屏模式快退
			Flog.d(TAG,
					"PlayerActivity---------onLongClick---CONTROL_COMMAND_REWIND");
			if (mBinder != null) {

				Flog.d(TAG,
						"PlayerActivity---------onLongClick---CONTROL_COMMAND_REWIND------1");
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_REWIND);
			}
			break;

		case R.id.play_music_next:// 竖屏模式快进
			Flog.d(TAG,
					"PlayerActivity---------onLongClick---CONTROL_COMMAND_FORWARD");
			if (mBinder != null) {

				Flog.d(TAG,
						"PlayerActivity---------onLongClick---CONTROL_COMMAND_FORWARD-----1");
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_FORWARD);
			}
			break;

		}
		Flog.d(TAG, "PlayActivity--onLongClick()-----end");
		return true;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.play_back:// 返回
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_return");
			finish();
			isPlaying = false;

			break;
		case R.id.play_voice:// 关闭菜单
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_volume");
			seekVolumeBar.setProgress(mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			mPopupVolume.showAsDropDown(mViewForMusicName);
			break;

		case R.id.play_affect:
			mVisualizerView.releaseVisualizerFx();
			Intent affectIntent = new Intent(this, AffectActivity.class);
			Flog.d(TAG,
					"PlayActivity--onClick()-----------activity_player_visualizer");
			startActivityForResult(affectIntent, 0);
			/*
			 * Intent intent_audio = new Intent(Constant.ACTION_TURN_LAUNCHER);
			 * intent_audio.putExtra("ID", (byte) 0x0a);
			 * sendBroadcast(intent_audio);
			 */
			break;

		case R.id.play_music_mode:// 模式
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_mode");
			if (mBinder != null) {
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_MODE);
			}
			break;

		case R.id.play_music_previous:// 向前
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_previous");

			if (mBinder != null) {
				try {
					Flog.d(TAG,
							"PlayActivity--onclick--activity_player_ib_previous----1");
					clearView();// lyl+
					Flog.d(TAG,
							"PlayActivity--onclick--activity_player_ib_previous-----2");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				mBinder.setControlCommand(Constant.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.play_music_playing:// 播放
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_play");
			if (mBinder != null) {
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.play_music_next:// 向后
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_next");

			if (mBinder != null) {
				try {
					Flog.d(TAG,
							"PlayActivity--onclick--activity_player_ib_next---1");

					clearView();// lyl+
					Flog.d(TAG,
							"PlayActivity--onclick--activity_player_ib_next---2");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// mAllLyricView.setText("");

			}
			mBinder.setControlCommand(Constant.CONTROL_COMMAND_NEXT);

			break;

		case R.id.play_favorites:// 我的最爱
			Flog.d(TAG, "PlayActivity--onclick--activity_player_ib_favorite");
			Flog.d(TAG,
					"PlayActivity--onclick--ib_favorite--MediaService.nMusicPosition=="
							+ MediaService.nMusicPosition);
			Flog.d(TAG,
					"PlayActivity--onclick--ib_favorite--MediaService.nPlayingPage=="
							+ MediaService.nPlayingPage);
			Intent mIntent = new Intent(Constant.BROADCAST_ACTION_FAVORITE);
			// MediaService.nPage = Constant.VIEWPAHER_MENU_MUSICFAVORITES;
			mIntent.putExtra(Constant.BROADCAST_INTENT_PAGE,
					MediaService.nPlayingPage);
			mIntent.putExtra(Constant.BROADCAST_INTENT_POSITION,
					MediaService.nMusicPosition);
			if (MainActivity.isSDMove == false) {
				if (isFavorite) {
					mBtnFavorite.setImageResource(R.drawable.music_favorites_u);
					isFavorite = false;
					mIntent.putExtra(Constant.BROADCAST_INTENT_FAVORITE, true);
				} else {
					mBtnFavorite.setImageResource(R.drawable.music_favorites_d);
					startFavoriteImageAnimation();
					isFavorite = true;
					mIntent.putExtra(Constant.BROADCAST_INTENT_FAVORITE, false);
				}
				// mBtnPlay.setImageResource(R.drawable.main_btn_pause);
				sendBroadcast(mIntent);

			}
			Flog.d(TAG, "--PlayerActivity--isFaplay_affectvorite----"
					+ isFavorite);

			break;

		}
	}

	/**
	 * 我的最爱图片动画
	 */
	private void startFavoriteImageAnimation() {
		Flog.d(TAG, "PlayerActivity-----startFavoriteImageAnimation------start");
		AnimationSet animationset = new AnimationSet(false);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setInterpolator(new OvershootInterpolator(10F));
		scaleAnimation.setDuration(500);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(3000);
		alphaAnimation.setStartOffset(500);

		animationset.addAnimation(scaleAnimation);
		animationset.addAnimation(alphaAnimation);
		animationset.setFillAfter(true);

		animationset.setAnimationListener(new AnimationListener() {

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
				// TODO Auto-generated method stub
				mMusicFavorite.setVisibility(View.GONE);
			}
		});
		mMusicFavorite.setVisibility(View.VISIBLE);
		mMusicFavorite.startAnimation(animationset);
		Flog.d(TAG, "PlayerActivity-----startFavoriteImageAnimation------end");
	}

	private void clearView() {
		mViewForArtist.setText("");
		mViewForMusicName.setText("");
		mCurrentTime.setText("00:00");
		mTotalTime.setText("00:00");
		mPlaySeekBar.setProgress(0);
		mAllLyricView.clear();
		mBtnPlay.setImageResource(R.drawable.main_btn_pause);
		mMusicCover.setImageResource(R.drawable.playing_album);
		mMusicFavorite.setImageResource(R.drawable.main_favorites_u);
	}

	private class SDListenerPlayReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "SDListenerReceiver--onReceive()--start");
			Flog.d(TAG,
					"onReceive()--intent.getAction()=====" + intent.getAction());
			if (intent.getAction().equals(
					Constant.BROADCAST_ACTION_MEDIA_REMOVED)) {
				Flog.d(TAG,
						"onReceive()--Constant.BROADCAST_ACTION_MEDIA_REMOVED==========");

				Toast.makeText(getApplicationContext(), Constant.SDCARD_REMOVE,
						4000).show();
				if (MediaService.isStart == true
						|| MediaService.isPause == true) {
					NativePlayer.nativePlayerStop();
					Intent mIntent = new Intent(
							Constant.BROADCAST_ACTION_NOT_STATE);
					sendBroadcast(mIntent);
					Flog.d(TAG,
							"SDListenerReceiver--onReceive()--BROADCAST_ACTION_NOT_STATE");
					MediaService.isStart = false;
					if (mVisualizerView != null) {
						mVisualizerView.setVisibility(View.INVISIBLE);
					}
				}
				MainActivity.isSDMove = true;

				mVisualizerView.releaseVisualizerFx();
				MusicList.list.clear();
				AlbumList.list.clear();
				ArtistList.list.clear();
				FavoriteList.list.clear();
				clearView();

			} else if (intent.getAction().equals(
					Constant.BROADCAST_ACTION_MEDIA_MOUNTED)) {
				Flog.d(TAG,
						"onReceive()--Constant.BROADCAST_ACTION_MEDIA_MOUNTED==========");
				Toast.makeText(getApplicationContext(),
						Constant.SDCARD_REMOUNT, 4000).show();

			}
		}
	}

}
