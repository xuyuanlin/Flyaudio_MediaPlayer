package com.flyaudio.flyMediaPlayer.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import antlr.debug.NewLineEvent;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.adapter.AlbumListAdapter;
import com.flyaudio.flyMediaPlayer.adapter.ArtistListAdapter;
import com.flyaudio.flyMediaPlayer.adapter.MusicFavoritesListAdapter;
import com.flyaudio.flyMediaPlayer.adapter.MusicNameListAdapter;
import com.flyaudio.flyMediaPlayer.adapter.MusicSearchAdapter;
import com.flyaudio.flyMediaPlayer.adapter.ViewPagerAdapter;
import com.flyaudio.flyMediaPlayer.data.DBDao;
import com.flyaudio.flyMediaPlayer.dialog.DeleteDialog;
import com.flyaudio.flyMediaPlayer.dialog.InfoDialog;
import com.flyaudio.flyMediaPlayer.dialog.TVAnimDialog.OnTVAnimDialogDismissListener;
import com.flyaudio.flyMediaPlayer.objectInfo.AlbumInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.AristInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.ScanInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.ArtistList;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.sdl.NativePlayer;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayCompleteListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayPauseListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayStartListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaBinder.OnPlayingListener;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaService;

import com.flyaudio.flyMediaPlayer.until.AlbumUtil;
import com.flyaudio.flyMediaPlayer.until.AllListActivity;
import com.flyaudio.flyMediaPlayer.until.CharacterParser;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.until.ScanUtil;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnTVAnimDialogDismissListener,
		OnSeekBarChangeListener, OnScrollListener {
	private static final String TAG = "MainActivity";
	private ViewPager mPager;
	private LinearLayout mSlideLayout, mBottomLayout;
	private TextView mMusic_favorites_viewPager, mMusic_name_viewPager,
			mMusic_artist_viewPager, mMusic_album_viewPager,
			mMusic_netWork_viewPager;
	private ImageView mMainAlbum;// 专辑图片
	private ImageButton mBtnPrevious;
	private ImageButton mBtnPlay;
	private ImageButton mBtnNext;
	private ImageButton mBtnHome;
	private ImageButton mBtnScan;
	private TextView mMainArtist;// 艺术家
	private TextView mMainName;// 歌曲名称
	private ImageView ivcursor;
	private TextView mMusicNameDialog;
	private TextView mMusicFavoritesDialog;
	private AutoCompleteTextView mCompleteSearch;
	private ListView mMusicNameList;
	private ListView mMusicNetworkList;
	private ListView mMusicFavoritesList;
	private ListView mMusicArtistList;
	private GridView mMusicAblumListGridView;
	private ListView mMusicAblumListListView;
	private View mViewMusicAblum;
	private List<View> pagerViews;
	private int bmpWidth; // 移动图标cursor的宽度
	private int nCurrentIndexOfPager; // 当前viewPager所处的页卡
	private List<AlbumInfo> mAlbumInfos = new ArrayList<AlbumInfo>();
	private List<AristInfo> mAristInfos = new ArrayList<AristInfo>();
	private MusicNameListAdapter mMusicNameListAdapter;
	private ArtistListAdapter mArtistListAdapter;
	private AlbumListAdapter mAlbumListAdapter;
	private MusicFavoritesListAdapter mFavoritesListAdapter;
	private MusicSearchAdapter mSearchAdapter;
	public static Intent mPlayIntent;
	private ServiceConnection mServiceConnection;
	private MediaBinder mBinder;
	private boolean isMainActivity;
	public static int nMusicPosition;// 当前播放歌曲索引
	private boolean isBindState = false;// 绑定状态
	private ScanHandler mHandler;
	private static ProgressDialog mDialog;
	private DBDao mDao;
	private MainReceiver receiver;
	public static final Object lockObject = new Object();
	private int nArtistPosition;
	private int nAlbumPosition;//
	private int nMenuPosition;// 记住弹出歌曲列表菜单的歌曲索引
	private String mMenuPath;// 记住弹出歌曲列表菜单的歌曲列表
	private int slidingPage = Constant.VIEWPAHER_MENU_MUSICNAME;// 页面状态
	private SeekBar mMainSeekBar;
	private SharedPreferences mPreferences;
	private ScanUtil mManager;
	private Animation mAnim;
	private boolean isScan;
	private AbsoluteLayout mMainLayout;
	public static boolean isFrist;
	private boolean isDown = true;// 防止用户频繁点击造成多次解除服务
	public Object mObject = new Object();
	private int offset = 0;
	private SDListenerReceiver mSDListenerReceiver;
	public static boolean isSDMove = false;
	private CharacterParser mCharacterParser;
	private ScanUtil scanUtil;
	private AllListActivity mAllListActivity;
	public static int number = 0;
	private AlbumUtil mAlbumUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Flog.d(TAG, "onCreate()-----begin");
		setContentView(R.layout.main);
		mAllListActivity = (AllListActivity) getApplication();
		AllListActivity.getInstance().addActivity(this);
		mCharacterParser = mCharacterParser.getInstance();
		mDao = new DBDao(getApplicationContext());
		mPreferences = getSharedPreferences(Constant.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mManager = new ScanUtil(getApplicationContext());
		mAnim = AnimationUtils.loadAnimation(this, R.anim.scan_loading);
		mAnim.setInterpolator(new LinearInterpolator());
		mPlayIntent = new Intent(getApplicationContext(), MediaService.class);
		mAlbumUtil=new AlbumUtil();
		init();
		initServiceConnection();
		Flog.d(TAG, "onCreate()-----end");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Flog.d(TAG, "onResume()--start");
		Flog.d(TAG, "onResume()--MainActivity.isSDMove--" + isSDMove);
		isFrist = false;
		isMainActivity = true;
		Intent intent2 = new Intent(Constant.BROADCAST_ACTION_SERVICE);
		intent2.putExtra(Constant.INTENT_ACTIVITY, Constant.ACTIVITY_MAIN);
		sendBroadcast(intent2);
		isBindState = bindService(mPlayIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
		if (MediaService.isStart == false) {
			mMainSeekBar.setProgress(0);
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		isMainActivity = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "onDestroy()----start");
		super.onDestroy();
		if (mServiceConnection != null) {
			if (isBindState) {
				unbindService(mServiceConnection);
			}
			mServiceConnection = null;
		}
		unregisterReceiver(receiver);
		unregisterReceiver(mSDListenerReceiver);

		Flog.d(TAG, "onDestroy()----end");
	}

	private void init() {
		Flog.d(TAG, "init()-----begin");

		InitView();
		InitWidth();
		initViewPaper();
		initSubViewWidget();
		InitReceiver();
		if (!mPreferences.getBoolean(Constant.PREFERENCES_SCAN, false)) {// 第一次进来，最爱认为false
			Flog.d(TAG, "init()-----begin----if");
			mMainLayout.setEnabled(false);
			mBtnScan.startAnimation(mAnim);
			new ScanTask(MainActivity.this).execute();
		} else {// 以后进来都是从数据库获取
			Flog.d(TAG, "init()-----begin----else");

			getListViewAdapter();
		}

		Flog.d(TAG, "init()-----end");
	}

	private void InitReceiver() {
		receiver = new MainReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.BROADCAST_ACTION_SCAN);
		filter.addAction(Constant.BROADCAST_ACTION_FAVORITE);
		filter.addAction(Constant.BROADCAST_ACTION_DETAIL);
		filter.addAction(Constant.BROADCAST_ACTION_CLEAR);
		registerReceiver(receiver, filter);

		mSDListenerReceiver = new SDListenerReceiver();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Constant.BROADCAST_ACTION_MEDIA_MOUNTED);
		mFilter.addAction(Constant.BROADCAST_ACTION_MEDIA_REMOVED);
		registerReceiver(mSDListenerReceiver, mFilter);
	}

	private void InitView() {
		Flog.d(TAG, "initView()-----begin");
		mMainLayout = (AbsoluteLayout) findViewById(R.id.main);
		mMusic_name_viewPager = (TextView) findViewById(R.id.music_name);
		mMusic_artist_viewPager = (TextView) findViewById(R.id.music_artist);
		mMusic_album_viewPager = (TextView) findViewById(R.id.music_album);
		mMusic_favorites_viewPager = (TextView) findViewById(R.id.music_favorites);
		mMusic_netWork_viewPager = (TextView) findViewById(R.id.music_network);
		mCompleteSearch = (AutoCompleteTextView) findViewById(R.id.main_edit_search);
		mBottomLayout = (LinearLayout) findViewById(R.id.main_bottom);
		ivcursor = (ImageView) findViewById(R.id.iv_bottom_line);
		mBtnPrevious = (ImageButton) findViewById(R.id.main_music_previous);
		mBtnPlay = (ImageButton) findViewById(R.id.main_music_playing);
		mBtnNext = (ImageButton) findViewById(R.id.main_music_next);
		mMainAlbum = (ImageView) findViewById(R.id.main_play_album);
		mMainName = (TextView) findViewById(R.id.main_play_song);
		mMainArtist = (TextView) findViewById(R.id.main_play_arist);
		mBtnHome = (ImageButton) findViewById(R.id.main_home);
		mBtnScan = (ImageButton) findViewById(R.id.main_scan);
		mPager = (ViewPager) findViewById(R.id.vPager);
		mMainSeekBar = (SeekBar) findViewById(R.id.main_play_progress);
		mSlideLayout = (LinearLayout) findViewById(R.id.main_slide_layout);
		mPlayIntent = new Intent(getApplicationContext(), MediaService.class);
		mMusic_name_viewPager.setOnClickListener(new ViewPagerOnClickListener(
				Constant.VIEWPAHER_MENU_MUSICNAME));
		mMusic_artist_viewPager
				.setOnClickListener(new ViewPagerOnClickListener(
						Constant.VIEWPAHER_MENU_ARTIST));
		mMusic_album_viewPager.setOnClickListener(new ViewPagerOnClickListener(
				Constant.VIEWPAHER_MENU_ALBUM));
		mMusic_favorites_viewPager
				.setOnClickListener(new ViewPagerOnClickListener(
						Constant.VIEWPAHER_MENU_MUSICFAVORITES));
		mMusic_netWork_viewPager
				.setOnClickListener(new ViewPagerOnClickListener(
						Constant.VIEWPAHER_MENU_NETWORK));
		mBottomLayout.setOnClickListener(this);
		mBtnPrevious.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnHome.setOnClickListener(this);
		mBtnScan.setOnClickListener(this);
		mMainSeekBar.setOnSeekBarChangeListener(this);

		mSearchAdapter = new MusicSearchAdapter(getApplicationContext());
		mCompleteSearch.setAdapter(mSearchAdapter);
		mCompleteSearch.setDropDownBackgroundDrawable(getResources()
				.getDrawable(R.drawable.main_brackground));
		mCompleteSearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Flog.d(TAG,
						"MainActivity--------------onListItemClick==================");
				mCompleteSearch.setText("");
				String mPath = mSearchAdapter.getNameList().get(position)
						.getnPath();
				for (int i = 0; i < MusicList.list.size(); i++) {
					if (MusicList.list.get(i).getPath().equals(mPath)) {
						nMusicPosition = i;
					}
				}
				Flog.d(TAG,
						"MainActivity--------------onListItemClick===============nMusicPosition==="
								+ nMusicPosition);
				mPlayIntent.putExtra(Constant.INTENT_LIST_POSITION,
						nMusicPosition);
				mPlayIntent.putExtra(Constant.INTENT_LIST_PAGE,
						Constant.VIEWPAHER_MENU_MUSICNAME);
				startService(mPlayIntent);

			}
		});
//		mCompleteSearch.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//				Flog.d(TAG,
//						"MainActivity--------------onTextChanged===============");
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				Flog.d(TAG,
//						"MainActivity--------------beforeTextChanged===============");
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				Flog.d(TAG,
//						"MainActivity--------------afterTextChanged===============");
//				mCompleteSearch.setAdapter(mSearchAdapter);
//			}
//		});

		Flog.d(TAG, "initView()-----end");
	}

	private class ViewPagerOnClickListener implements View.OnClickListener {

		private int index = 1;

		public ViewPagerOnClickListener(int index) {

			super();
			Flog.d(TAG, "ViewPagerOnClickListener-----ViewPagerOnClickListener");
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Flog.d(TAG,
					"ViewPagerOnClickListener-----ViewPagerOnClickListener----onclick");
			mPager.setCurrentItem(index);

		}
	}

	private void InitWidth() {
		Flog.d(TAG, "initWidth()-----begin");
		bmpWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.main_slide_view).getWidth();
		Flog.d(TAG, "initWidth()-----bmpWidth-----" + bmpWidth);
		int screenW = mSlideLayout.getLayoutParams().width;
		Flog.d(TAG, "initWidth()-----screenW-----" + screenW);
		offset = (int) ((screenW / 5.0 - bmpWidth) / 2);
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ivcursor.setImageMatrix(matrix);
		Flog.d(TAG, "initWidth()-----end");
	}

	private void initViewPaper() {
		Flog.d(TAG, "initViewPaper()-----begin");
		pagerViews = new ArrayList<View>();// 注意添加的add的顺序问题不然会有问题
		LayoutInflater inflater = getLayoutInflater();
		pagerViews.add(inflater.inflate(R.layout.viewpager_layout_name, null));
		pagerViews
				.add(inflater.inflate(R.layout.viewpager_layout_artist, null));
		pagerViews.add(inflater.inflate(R.layout.viewpager_layout_album, null));
		pagerViews.add(inflater.inflate(R.layout.viewpager_layout_favorites,
				null));
		pagerViews.add(inflater
				.inflate(R.layout.viewpager_layout_newwork, null));
		mPager.setAdapter(new ViewPagerAdapter(pagerViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new VideoOnPageChangedListener());
	}

	private void initSubViewWidget() {
		Flog.d(TAG, "initSubViewWidget()-----begin");
		Flog.d(TAG, "initSubViewWidget()-----pagerViews-----------"
				+ pagerViews.size());
		View mViewMusicName = pagerViews.get(Constant.VIEWPAHER_MENU_MUSICNAME);
		mMusicNameList = (ListView) mViewMusicName
				.findViewById(R.id.musicName_listView);

		mMusicNameDialog = (TextView) mViewMusicName
				.findViewById(R.id.musicName_dialog);
		Flog.d(TAG, "initSubViewWidget()-------------mMusicNameList-----------"
				+ mMusicNameList);

		View mViewMusicArtist = pagerViews.get(Constant.VIEWPAHER_MENU_ARTIST);
		mMusicArtistList = (ListView) mViewMusicArtist
				.findViewById(R.id.musicArtist_listView);
		mViewMusicAblum = pagerViews.get(Constant.VIEWPAHER_MENU_ALBUM);
		mMusicAblumListGridView = (GridView) mViewMusicAblum
				.findViewById(R.id.musicAblum_gridView);
		mMusicAblumListListView = (ListView) mViewMusicAblum
				.findViewById(R.id.musicAblum_listView);
		View mViewMusicList = pagerViews
				.get(Constant.VIEWPAHER_MENU_MUSICFAVORITES);
		mMusicFavoritesList = (ListView) mViewMusicList
				.findViewById(R.id.musicFavorites_listView);
		mMusicFavoritesDialog = (TextView) mViewMusicList
				.findViewById(R.id.musicFavorites_dialog);

		View mViewMusicNetwork = pagerViews
				.get(Constant.VIEWPAHER_MENU_NETWORK);
		mMusicNetworkList = (ListView) mViewMusicNetwork
				.findViewById(R.id.musicNetwork_listView);

		Flog.d(TAG, "initSubViewWidget()-----end");

	}

	private void getListViewAdapter() {
		Flog.d(TAG, "getListViewAdapter()-----begin");
		setMusicFavoritesListViewData();// 播放列表数据及显示
		setMusicNameListViewData();// 曲目数据及显示
		setMusicArtistListViewData();// 歌手数据及显示
		setMusicAlbumListViewData();// 专辑数据及显示

		Flog.d(TAG, "getListViewAdapter()-----end");

	}

	private void setMusicFavoritesListViewData() {
		Flog.d(TAG, "setMusicFavoritesListViewData()-----begin");
		mFavoritesListAdapter = new MusicFavoritesListAdapter(
				MainActivity.this, Constant.VIEWPAHER_MENU_MUSICFAVORITES);
		mMusicFavoritesList.setAdapter(mFavoritesListAdapter);
		mMusicFavoritesList.setOnItemClickListener(this);
		mMusicFavoritesList.setOnScrollListener(this);
		Flog.d(TAG, "setMusicFavoritesListViewData()-----end");
	}

	private void setMusicNameListViewData() {// 查询曲名
		Flog.d(TAG, "setMusicNameListViewData()-----begin");
		mMusicNameListAdapter = new MusicNameListAdapter(MainActivity.this,
				Constant.VIEWPAHER_MENU_MUSICNAME);
		mMusicNameList.setAdapter(mMusicNameListAdapter);
		mMusicNameList.setOnItemClickListener(this);
		mMusicNameList.setOnScrollListener(this);
		Flog.d(TAG, "setMusicNameListViewData()-----end");
	}

	private void setMusicAlbumListViewData() {// 查询专辑
		Flog.d(TAG, "setMusicArtistListViewData()-----begin");

		mAlbumInfos = mDao.getAlbumList();

		if (mMusicAblumListGridView == null) {
			mMusicAblumListGridView = (GridView) mViewMusicAblum
					.findViewById(R.id.musicAblum_gridView);

		}
		mAlbumListAdapter = new AlbumListAdapter(MainActivity.this,
				mAlbumInfos, Constant.VIEWPAHER_MENU_ALBUM,mAlbumUtil);

		mMusicAblumListGridView.setAdapter(mAlbumListAdapter);
		mMusicAblumListListView.setAdapter(mAlbumListAdapter);
		mMusicAblumListGridView.setVisibility(View.VISIBLE);
		mMusicAblumListListView.setVisibility(View.GONE);
		mMusicAblumListGridView.setOnItemClickListener(this);
		Flog.d(TAG, "setMusicArtistListViewData()-----end");

	}

	public void setMusicArtistListViewData() {
		Flog.d(TAG, "setMusicArtistListViewData()-----begin");
		mAristInfos = mDao.getArtistList();
		mArtistListAdapter = new ArtistListAdapter(MainActivity.this,
				mAristInfos, Constant.VIEWPAHER_MENU_ARTIST,mAlbumUtil);
		mMusicArtistList.setAdapter(mArtistListAdapter);
		mMusicArtistList.setOnItemClickListener(this);
		Flog.d(TAG, "setMusicArtistListViewData()-----end");

	}

	public void getMusicDetials(MusicInfo info, int page) {
		Flog.d(TAG, "getMusicDetials()");
		Flog.d(TAG, "getMusicDetials()--isSDMove--" + isSDMove);

		if (MainActivity.isSDMove) {
			clearView();

		} else {
			mMainArtist.setText(info.getArtist());
			mMainName.setText(info.getName());
		}
	}

	private void initServiceConnection() {// 服务绑定初始化
		Flog.d(TAG, "----MainActivity-----initServiceConnection()---start");
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				mBinder = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Flog.d(TAG, "----MainActivity-----onServiceConnected()");
				mBinder = (MediaBinder) service;
				Flog.d(TAG, "----MainActivity-----onServiceConnected()---1");
				if (mBinder != null) {
					Flog.d(TAG,
							"----MainActivity-----onServiceConnected()---2----"
									+ mBinder.toString());
					isDown = true;// 重置
					mBinder.setOnPlayStartListener(new OnPlayStartListener() {

						@Override
						public void onStart(MusicInfo info, int page) {

							// TODO Auto-generated method stub
							if (isMainActivity) {
								getMusicDetials(info, page);
								Flog.d(TAG,
										"----MainActivity-----onServiceConnected()---onStart()");

								if (CoverList.cover == null) {
									if (isSDMove == false) {
										mMainAlbum
												.setImageResource(R.drawable.main_bottom_album);
									}
								} else {

									if (isSDMove == false) {

										mMainAlbum
												.setImageDrawable(CoverList.cover);

									}
								}
								switch (MediaService.nPage) {
								case Constant.VIEWPAHER_MENU_MUSICNAME:
									mMusicNameListAdapter
											.update(Constant.VIEWPAHER_MENU_MUSICNAME);
									break;

								case Constant.VIEWPAHER_MENU_ARTIST_LIST:
									mArtistListAdapter
											.update(Constant.VIEWPAHER_MENU_ARTIST_LIST);
									break;
								case Constant.VIEWPAHER_MENU_ALBUM_LIST:
									mAlbumListAdapter
											.update(Constant.VIEWPAHER_MENU_ALBUM_LIST);
									break;
								case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
									mFavoritesListAdapter
											.update(Constant.VIEWPAHER_MENU_MUSICFAVORITES);
									break;
								}

							}
						}
					});
					mBinder.setOnPlayingListener(new OnPlayingListener() {

						@Override
						public void onPlay(int currentPosition, String path,
								int page, MusicInfo info) {
							// TODO Auto-generated method stub

							Flog.d(TAG,
									"----MainActivity-----onServiceConnected()---onPlaying()--start");
							if (isMainActivity && MediaService.isStart == true) {
								Flog.d(TAG,
										"----MainActivity-----onServiceConnected()---isMainActivity--");

								if (isFrist == false) {
									mBtnPlay.setImageResource(R.drawable.main_btn_play);
									mMainSeekBar
											.setMax((int) MediaService.nMusicDuration);
									isFrist = true;
									mMainSeekBar.setProgress(0);// lyl+
								} else {// lyl+ 切换下一首歌时，播放进度时间显示应该从0开始

									Flog.d(TAG,
											"MainActivity--onServiceConnected--onPlaying()----"
													+ currentPosition);
									mMainSeekBar.setProgress(currentPosition);

									Flog.d(TAG,
											"MainActivity--onServiceConnected--onPlaying()--MediaService.nPage--"
													+ MediaService.nPage);
								}

							}
							Flog.d(TAG,
									"----MainActivity-----onServiceConnected()---onPlaying()--end");

						}
					});
					mBinder.setOnPlayPauseListener(new OnPlayPauseListener() {

						@Override
						public void onPause() {
							// TODO Auto-generated method stub
							Flog.d(TAG,
									"----MainActivity-----onServiceConnected()---onPause()--");
							mBtnPlay.setImageResource(R.drawable.main_btn_pause);
						}
					});
					mBinder.setOnPlayCompletionListener(new OnPlayCompleteListener() {

						@Override
						public void onPlayComplete() {
							// TODO Auto-generated method stub
							Flog.d(TAG,
									"----MainActivity-----onServiceConnected()---onPlayComplete()--");
							MediaService.isStart = false;
							mMainSeekBar.setProgress(0);

						}
					});

					Flog.d(TAG,
							"----MainActivity-----onServiceConnected()---end");
				}

			}
		};
		Flog.d(TAG, "----MainActivity-----initServiceConnection()---end");
	}

	private class VideoOnPageChangedListener implements OnPageChangeListener {

		int one = offset * 2 + bmpWidth;
		int two = one * 2;
		int three = one * 3;
		int four = one * 4;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int page) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "VideoOnPageChangedListener---onPageSelected--start");
			Animation animation = null;
			switch (page) {
			case Constant.VIEWPAHER_MENU_MUSICNAME:
				if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ARTIST) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ALBUM) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICFAVORITES) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_NETWORK) {
					animation = new TranslateAnimation(four, 0, 0, 0);
				}

				break;
			case Constant.VIEWPAHER_MENU_ARTIST:
				Flog.d(TAG, "VideoOnPageChangedListener---1");
				if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICNAME) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ALBUM) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICFAVORITES) {
					animation = new TranslateAnimation(three, one, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_NETWORK) {
					animation = new TranslateAnimation(four, one, 0, 0);
				}
				Flog.d(TAG,
						"onPageSelected()--VIEWPAHER_MENU_ARTIST--MediaService.nPage--"
								+ MediaService.nPage);

				break;
			case Constant.VIEWPAHER_MENU_ALBUM:
				Flog.d(TAG, "VideoOnPageChangedListener---2");
				if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICNAME) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ARTIST) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICFAVORITES) {
					animation = new TranslateAnimation(three, two, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_NETWORK) {
					animation = new TranslateAnimation(four, two, 0, 0);
				}
				Flog.d(TAG,
						"onPageSelected()--VIEWPAHER_MENU_ALBUM--MediaService.nPage--"
								+ MediaService.nPage);

				break;
			case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
				Flog.d(TAG, "VideoOnPageChangedListener---3");
				if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICNAME) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ARTIST) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ALBUM) {
					animation = new TranslateAnimation(two, three, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_NETWORK) {
					animation = new TranslateAnimation(four, three, 0, 0);
				}
				mFavoritesListAdapter
						.update(Constant.VIEWPAHER_MENU_MUSICFAVORITES);
				break;
			case Constant.VIEWPAHER_MENU_NETWORK:
				Flog.d(TAG, "VideoOnPageChangedListener---4");
				if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICNAME) {
					animation = new TranslateAnimation(offset, four, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ARTIST) {
					animation = new TranslateAnimation(one, four, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_ALBUM) {
					animation = new TranslateAnimation(two, four, 0, 0);
				} else if (nCurrentIndexOfPager == Constant.VIEWPAHER_MENU_MUSICFAVORITES) {
					animation = new TranslateAnimation(three, four, 0, 0);
				}

				break;

			}
			nCurrentIndexOfPager = page;
			MediaService.nPage = nCurrentIndexOfPager;
			if (MediaService.nPage == Constant.VIEWPAHER_MENU_ALBUM) {
				MediaService.nPage = mAlbumListAdapter.getPage();
			}
			if (MediaService.nPage == Constant.VIEWPAHER_MENU_ARTIST) {
				MediaService.nPage = mArtistListAdapter.getPage();
			}
			animation.setFillAfter(true); // ture
			animation.setDuration(400); //
			ivcursor.startAnimation(animation);
			Flog.d(TAG, "onPageSelected()---MediaService.nPage--"
					+ MediaService.nPage);
			Flog.d(TAG, "VideoOnPageChangedListener---onPageSelected--end");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_bottom:
			Flog.d(TAG, "MainActivity--onclick--main_bottom");
			if (mServiceConnection != null && isDown) {
				unbindService(mServiceConnection);
				isBindState = false;
				isDown = false;
			}
			Intent playIntent = new Intent(this, PlayerActivity.class);
			startActivity(playIntent);
			PlayerActivity.isFristCover3D = false;

			break;
		case R.id.main_music_previous:// 向前
			Flog.d(TAG, "MainActivity--onclick--activity_player_ib_previous");

			if (mBinder != null) {
				clearView();// lyl+
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.main_music_playing:// 播放
			Flog.d(TAG, "MainActivity--onclick--activity_player_ib_play");
			if (mBinder != null) {
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.main_music_next:// 向后
			Flog.d(TAG, "MainActivity--onclick--activity_player_ib_next");
			if (mBinder != null) {
				clearView();// lyl+
				mBinder.setControlCommand(Constant.CONTROL_COMMAND_NEXT);
			}
			break;
		case R.id.main_home:// 返回到桌面
			Flog.d(TAG, "MainActivity--onclick--main_home");
			if (MediaService.nPage == Constant.VIEWPAHER_MENU_ARTIST_LIST) {

				mArtistListAdapter.update(Constant.VIEWPAHER_MENU_ARTIST);
				MediaService.nPage = Constant.VIEWPAHER_MENU_ARTIST;

			} else if (MediaService.nPage == Constant.VIEWPAHER_MENU_ALBUM_LIST) {

				mMusicAblumListListView.setVisibility(View.GONE);
				mMusicAblumListGridView.setVisibility(View.VISIBLE);
				mAlbumListAdapter.update(Constant.VIEWPAHER_MENU_ALBUM);
				MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM;
			} else {
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(mHomeIntent);
			}

			break;
		case R.id.main_scan:// 扫描控件
			Flog.d(TAG, "MainActivity--onclick--main_scan");
			isScan = true;
			mMainLayout.setEnabled(false);
			mBtnScan.setEnabled(false);
			mAnim = AnimationUtils.loadAnimation(this, R.anim.scan_loading);
			mAnim.setInterpolator(new LinearInterpolator());
			mBtnScan.startAnimation(mAnim);
			new ScanTask(MainActivity.this).execute();

			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "MainActivity--------------onListItemClick");

		Flog.d(TAG,
				"MainActivity--------------onListItemClick--MediaService.nPage--"
						+ MediaService.nPage);
		Flog.d(TAG, "MainActivity--------------onListItemClick--v.getId()--"
				+ v.getId());
		switch (MediaService.nPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:// 曲目item的点击事件
			MediaService.nPlayingPage = Constant.VIEWPAHER_MENU_MUSICNAME;
			clickStartSerivce(position);
			break;

		case Constant.VIEWPAHER_MENU_ARTIST:// 歌手列表的点击

			MediaService.nPage = Constant.VIEWPAHER_MENU_ARTIST_LIST;
			nArtistPosition = position;
			mDao.queryArtist(mAristInfos);
			mArtistListAdapter.setArtistPosition(nArtistPosition);
			mArtistListAdapter.update(Constant.VIEWPAHER_MENU_ARTIST_LIST);
			mPlayIntent.putExtra(Constant.INTENT_ARTIST_POSITION,
					nArtistPosition);
			mMusicArtistList.setAdapter(mArtistListAdapter);

			break;
		case Constant.VIEWPAHER_MENU_ARTIST_LIST:// 歌手列表圈套的对应的歌曲的点击事件
			MediaService.nPlayingPage = Constant.VIEWPAHER_MENU_ARTIST_LIST;
			clickStartSerivce(position);

			break;

		case Constant.VIEWPAHER_MENU_ALBUM:// 专辑列表的点击
			MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM_LIST;
			Flog.d(TAG,
					"MainActivity--------------onListItemClick---------------------VIEWPAHER_MENU_ALBUM-------------------"
							+ nMusicPosition);
			nAlbumPosition = position;
			mDao.queryAlbum(mAlbumInfos);
			mAlbumListAdapter.setAlbumPosition(nAlbumPosition);
			mAlbumListAdapter.update(Constant.VIEWPAHER_MENU_ALBUM_LIST);
			mMusicAblumListListView.setVisibility(View.VISIBLE);
			mMusicAblumListGridView.setVisibility(View.GONE);
			mMusicAblumListListView.setOnItemClickListener(this);
			mPlayIntent
					.putExtra(Constant.INTENT_ALBUM_POSITION, nAlbumPosition);

			break;

		case Constant.VIEWPAHER_MENU_ALBUM_LIST:// 专辑列表圈套的对应的歌曲的点击事件
			clickStartSerivce(position);
			MediaService.nPlayingPage = Constant.VIEWPAHER_MENU_ALBUM_LIST;
			break;
		case Constant.VIEWPAHER_MENU_MUSICFAVORITES:// 最爱列表
			MediaService.nPlayingPage = Constant.VIEWPAHER_MENU_MUSICFAVORITES;
			clickStartSerivce(position);

			break;

		}

	}

	private void clickStartSerivce(int position) {

		nMusicPosition = position;

		Flog.d(TAG, "onItemClick()---nMusicPosition--" + nMusicPosition);
		Flog.d(TAG, "onItemClick()---MediaService.nPage--" + MediaService.nPage);
		mPlayIntent.putExtra(Constant.INTENT_LIST_POSITION, nMusicPosition);
		mPlayIntent.putExtra(Constant.INTENT_LIST_PAGE, MediaService.nPage);
		startService(mPlayIntent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			switch (MediaService.nPage) {
			case Constant.VIEWPAHER_MENU_MUSICNAME:
				Flog.d(TAG,
						"onKeyDown()------------------VIEWPAHER_MENU_MUSICNAME");

				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(mHomeIntent);

				break;
			case Constant.VIEWPAHER_MENU_ARTIST:
				Flog.d(TAG,
						"onKeyDown()------------------VIEWPAHER_MENU_ARTIST");

				mArtistListAdapter.update(Constant.VIEWPAHER_MENU_ARTIST);
				break;
			case Constant.VIEWPAHER_MENU_ARTIST_LIST:
				Flog.d(TAG,
						"onKeyDown()------------------VIEWPAHER_MENU_ARTIST_LIST");
				mArtistListAdapter.update(Constant.VIEWPAHER_MENU_ARTIST);
				MediaService.nPage = Constant.VIEWPAHER_MENU_ARTIST;
				break;

			case Constant.VIEWPAHER_MENU_ALBUM:
				Flog.d(TAG, "onKeyDown()------------------VIEWPAHER_MENU_ALBUM");
				mMusicAblumListListView.setVisibility(View.GONE);
				mMusicAblumListGridView.setVisibility(View.VISIBLE);
				mAlbumListAdapter.update(Constant.VIEWPAHER_MENU_ALBUM);
				MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM;
				break;

			case Constant.VIEWPAHER_MENU_ALBUM_LIST:
				Flog.d(TAG,
						"onKeyDown()------------------VIEWPAHER_MENU_ALBUM_LIST");
				mMusicAblumListListView.setVisibility(View.GONE);
				mMusicAblumListGridView.setVisibility(View.VISIBLE);
				mAlbumListAdapter.update(Constant.VIEWPAHER_MENU_ALBUM);
				MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM;
				break;
			case Constant.VIEWPAHER_MENU_MUSICFAVORITES:

				Flog.d(TAG,
						"onKeyDown()------------------VIEWPAHER_MENU_MUSICFAVORITES");

				break;
			}
			return true;
		} else {
			Flog.d(TAG, "onKeyDown()--------default");
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 从当前歌曲列表中移除
	 */
	private void removeList() {
		Flog.d(TAG, "removeList()---start");
		Flog.d(TAG, "removeList()---slidingPage--" + slidingPage);
		Flog.d(TAG, "removeList()---nMenuPosition--" + nMenuPosition);
		Flog.d(TAG, "removeList()---nArtistPosition--" + nArtistPosition);
		Flog.d(TAG, "removeList()---nAlbumPosition--" + nAlbumPosition);
		MusicInfo info = null;
		int size = 0;
		switch (slidingPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:
			size = MusicList.list.size();
			if (size > nMenuPosition) {
				info = MusicList.list.get(nMenuPosition);
			}

			break;

		case Constant.VIEWPAHER_MENU_ARTIST_LIST:
			size = ArtistList.list.get(nArtistPosition).getMusicList().size();
			if (size > nMenuPosition) {
				info = ArtistList.list.get(nArtistPosition).getMusicList()
						.get(nMenuPosition);
				// MediaService.nPage = Constant.VIEWPAHER_MENU_ARTIST_LIST;
			}
			// MediaService.nPage = Constant.VIEWPAHER_MENU_ARTIST;

			break;

		case Constant.VIEWPAHER_MENU_ALBUM_LIST:
			size = AlbumList.list.get(nAlbumPosition).getMusicList().size();
			if (size > nMenuPosition) {
				info = AlbumList.list.get(nAlbumPosition).getMusicList()
						.get(nMenuPosition);
				// MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM_LIST;
			}

			// MediaService.nPage = Constant.VIEWPAHER_MENU_ALBUM;

			break;

		case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
			size = FavoriteList.list.size();
			if (FavoriteList.list.isEmpty()) {
				return;
			} else {
				if (size <= nMenuPosition) {
					nMenuPosition = 0;
				}
				info = FavoriteList.list.get(nMenuPosition);
			}
			break;
		}

		if (info != null) {
			mMenuPath = info.getPath();
			if (mMenuPath.equals(MediaService.sMusicPath)
					&& MediaService.isStart == true) {
				Flog.d(TAG, "removeList()---equalsxyz---"
						+ MediaService.sMusicPath);
				Flog.d(TAG, "removeList()---equalsxyz---" + mMenuPath);
				NativePlayer.nativePlayerStop();
				MediaService.isStart = false;
				mMainSeekBar.setProgress(0);
				// mBtnPlay.setImageResource(R.drawable.main_btn_pause);

			}
			DBDao db = new DBDao(getApplicationContext());

			db.delete(mMenuPath);
			db.close();
		}

		Flog.d(TAG, "removeList()---info==>>" + info);
		Flog.d(TAG, "removeList()---mMenuPath==" + mMenuPath);
		Flog.d(TAG, "removeList()---size==>>" + size);

		if (mDao.getAlbumCount(info.getAlbum()) < 1) {
			Flog.d(TAG,
					"removeList()---mAlbumInfos==>>" + mAlbumInfos.toString());
			int nAlbumPosition = 0;
			for (int i = 0; i < mAlbumInfos.size(); i++) {
				if (mAlbumInfos.get(i).getAlbum().equals(info.getAlbum())) {
					nAlbumPosition = i;
				}
			}
			Flog.d(TAG, "removeList()---nAlbumPosition=" + nAlbumPosition);
			synchronized (Constant.lockObject) {
				if (mAlbumInfos.size() > nAlbumPosition) {
					mAlbumInfos.remove(nAlbumPosition);

				}
				Constant.lockObject.notify();
			}

			mAlbumListAdapter.setAlbumIfo(mAlbumInfos);

		}

		if (mDao.getArtistCount(info.getArtist()) < 1) {
			Flog.d(TAG,
					"removeList()---mAristInfos==>" + mAristInfos.toString());
			int nArtistPosition = 0;
			for (int i = 0; i < mAristInfos.size(); i++) {
				if (mAristInfos.get(i).getArist().equals(info.getArtist())) {
					nArtistPosition = i;
				}
			}
			Flog.d(TAG, "removeList()---nArtistPosition=" + nArtistPosition);
			synchronized (Constant.lockObject) {
				if (mAristInfos.size() > nArtistPosition) {
					mAristInfos.remove(nArtistPosition);
				}

				Constant.lockObject.notify();
			}
			mArtistListAdapter.setArtistIfo(mAristInfos);

		} else {
			Flog.d(TAG, "removeList()---mAristInfos---------------1");
			for (int i = 0; i < mAristInfos.size(); i++) {
				if (mAristInfos.get(i).getArist().equals(info.getArtist())) {
					nArtistPosition = i;
				}
			}
			int nMusicCount = mAristInfos.get(nArtistPosition).getAristCount();
			Flog.d(TAG, "removeList()---nMusicCount==>>" + nMusicCount);
			mAristInfos.get(nArtistPosition).setAristCount(nMusicCount - 1);
			mArtistListAdapter.setArtistIfo(mAristInfos);
		}
		synchronized (Constant.lockObject) {
			Flog.d(TAG, "Constant.lockObject");
			MusicList.list.remove(info);
			FavoriteList.list.remove(info);
			for (int i = 0; i < AlbumList.list.size(); i++) {
				AlbumList.list.get(i).getMusicList().remove(info);
			}
			for (int i = 0; i < ArtistList.list.size(); i++) {
				ArtistList.list.get(i).getMusicList().remove(info);
			}
			Constant.lockObject.notifyAll();
		}
		mMainName.setText("");
		mMainArtist.setText("");
		mMainAlbum.setImageResource(R.drawable.main_bottom_album);
		synchronized (mObject) {
			Flog.d(TAG, "mObject");
			mMusicNameListAdapter.update(Constant.VIEWPAHER_MENU_MUSICNAME);

			mFavoritesListAdapter
					.update(Constant.VIEWPAHER_MENU_MUSICFAVORITES);

			mArtistListAdapter.update(mArtistListAdapter.getPage());
			mAlbumListAdapter.update(mAlbumListAdapter.getPage());
			mObject.notifyAll();
		}

		Flog.d(TAG, "removeList()---end");
	}

	private void deleteFile() {
		Flog.d(TAG, "MainActivity--deleFile()");
		File file = new File(mMenuPath);
		if (file.delete()) {
			Toast.makeText(getApplicationContext(), "文件以被删除！",
					Toast.LENGTH_LONG).show();
			removeList();
		}
	}

	@Override
	public void onDismiss(int dialogId) {
		Flog.d(TAG, "onDismiss()--start");
		switch (dialogId) {

		case Constant.DIALOG_MENU_REMOVE:// 执行移除
			removeList();
			break;

		case Constant.DIALOG_MENU_DELETE:// 显示删除对话框
			DeleteDialog deleteDialog = new DeleteDialog(this);
			deleteDialog.setOnTVAnimDialogDismissListener(this);
			deleteDialog.show();
			break;

		case Constant.DIALOG_MENU_INFO:// 显示歌曲详情
			Flog.d(TAG, "----------DIALOG_MENU_INFO--------------");
			InfoDialog infoDialog = new InfoDialog(this);
			infoDialog.setOnTVAnimDialogDismissListener(this);
			infoDialog.show();
			switch (slidingPage) {// 必须在show后执行
			case Constant.VIEWPAHER_MENU_MUSICNAME:

				infoDialog.setInfo(MusicList.list.get(nMenuPosition));
				break;

			case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
				synchronized (lockObject) {
					infoDialog.setInfo(FavoriteList.list.get(nMenuPosition));
					lockObject.notifyAll();
				}
				break;

			case Constant.VIEWPAHER_MENU_ALBUM_LIST:
				infoDialog.setInfo(AlbumList.list.get(nAlbumPosition)
						.getMusicList().get(nMenuPosition));
				break;
			case Constant.VIEWPAHER_MENU_ARTIST_LIST:
				infoDialog.setInfo(ArtistList.list.get(nArtistPosition)
						.getMusicList().get(nMenuPosition));
				break;
			}
			Flog.d(TAG,
					"MainActivity----Constant.DIALOG_MENU_INFO--nMenuPosition--"
							+ nMenuPosition);
			break;

		case Constant.DIALOG_DELETE:// 执行删除
			deleteFile();
			break;

		}
		Flog.d(TAG, "onDismiss()--end");

	}

	private void getMusicDetail() {
		InfoDialog infoDialog = new InfoDialog(this);
		infoDialog.setOnTVAnimDialogDismissListener(this);
		infoDialog.show();
		switch (slidingPage) {// 必须在show后执行
		case Constant.VIEWPAHER_MENU_MUSICNAME:

			infoDialog.setInfo(MusicList.list.get(nMenuPosition));
			break;

		case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
			synchronized (lockObject) {
				infoDialog.setInfo(FavoriteList.list.get(nMenuPosition));
				lockObject.notifyAll();
			}
			break;

		case Constant.VIEWPAHER_MENU_ALBUM_LIST:
			infoDialog.setInfo(AlbumList.list.get(nAlbumPosition)
					.getMusicList().get(nMenuPosition));
			break;
		case Constant.VIEWPAHER_MENU_ARTIST_LIST:
			infoDialog.setInfo(ArtistList.list.get(nArtistPosition)
					.getMusicList().get(nMenuPosition));
			break;
		}
		Flog.d(TAG,
				"MainActivity----Constant.DIALOG_MENU_INFO--nMenuPosition--"
						+ nMenuPosition);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.main_play_progress) {
			if (mBinder != null) {
				mBinder.seekBarStartTrackingTouch();
			}
		}

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.main_play_progress) {
			if (mBinder != null) {
				mBinder.seekBarStopTrackingTouch(seekBar.getProgress());
			}
		}
	}

	/**
	 * 执行扫描任务的异步任务嵌套类
	 * 
	 * 实现扫描
	 * 
	 */
	private class ScanTask extends AsyncTask<Void, Void, Void> {

		Context mContext;

		public ScanTask(Context context) {
			this.mContext = context;
			mHandler = new ScanHandler(ScanTask.this);
			scanUtil = new ScanUtil(getApplicationContext());

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.setTitle("正在扫描中");
			mDialog.setMessage("正在扫描，请稍后...");
			mDialog.setCancelable(false);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setIndeterminate(false);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "ScanTask---------doInBackground()");
			if (scanUtil.sdCardExists()) {
				Flog.d(TAG, "ScanTask---------doInBackground()----sdCardExists");
				List<String> listSD = new ArrayList<String>();
				listSD = scanUtil.getFiles(Constant.PATH_SDCARD + "/");
				scanUtil.scanMusicFromSD(listSD, mHandler);

			}
			if (scanUtil.usbExists()) {
				Flog.d(TAG, "ScanTask---------doInBackground()----usbExists");
				List<String> listUSB = new ArrayList<String>();
				listUSB = scanUtil.getFiles(Constant.PATH_USB + "/");
				scanUtil.scanMusicFromSD(listUSB, mHandler);

			}

			if (isScan == true) {
				mAristInfos = mDao.getArtistList();
				mAlbumInfos = mDao.getAlbumList();
				mDao.queryAlbum(mAlbumInfos);
				mDao.queryArtist(mAristInfos);
				mDao.queryAll();
				Flog.d(TAG,
						"doInBackground()--mAristInfos=="
								+ mAristInfos.toString());
				Flog.d(TAG, "doInBackground()--MusicList.list.size()=="
						+ MusicList.list.size());

			}
			Flog.d(TAG, "doInBackground()--isScan");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "ScanTask---------onPostExecute()");
			super.onPostExecute(result);

			SharedPreferences preferences = getSharedPreferences(
					Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
			preferences.edit().putBoolean(Constant.PREFERENCES_SCAN, true)
					.commit();// 扫描完成！
			scanUtil.count = 0;
			if (isScan == true) {
				mBtnScan.setEnabled(true);
				synchronized (mObject) {
					mArtistListAdapter.setArtistIfo(mAristInfos);
					mArtistListAdapter.update(mArtistListAdapter.getPage());
					mAlbumListAdapter.setAlbumIfo(mAlbumInfos);
					mAlbumListAdapter.update(mAlbumListAdapter.getPage());

					mFavoritesListAdapter.update(mFavoritesListAdapter
							.getPage());
					mMusicNameListAdapter.update(mMusicNameListAdapter
							.getPage());
					mObject.notifyAll();
				}
				isScan = false;
			} else {
				Flog.d(TAG, "ScanTask---------onPostExecute()----1");
				getListViewAdapter();
			}
			Flog.d(TAG, "ScanTask---------onPostExecute()----2");
			mMainLayout.setEnabled(true);
			Flog.d(TAG, "ScanTask---------onPostExecute()----3");
			mBtnScan.clearAnimation();
			Flog.d(TAG, "ScanTask---------onPostExecute()----4");
			mDialog.dismiss();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
			mMusicFavoritesDialog.setVisibility(View.GONE);
			mMusicNameDialog.setVisibility(View.GONE);
			break;
		case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态
			mMusicFavoritesDialog.setVisibility(View.VISIBLE);
			mMusicNameDialog.setVisibility(View.VISIBLE);

			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
			mMusicFavoritesDialog.setVisibility(View.VISIBLE);
			mMusicNameDialog.setVisibility(View.VISIBLE);
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Flog.d(TAG, "onScroll()---MediaService.nPage--" + MediaService.nPage);
		Flog.d(TAG, "onScroll()---firstVisibleItem==" + firstVisibleItem);
		switch (MediaService.nPage) {
		case Constant.VIEWPAHER_MENU_MUSICNAME:
			if (MusicList.list.size() > 0) {
				mMusicNameDialog.setText(MusicList.list.get(firstVisibleItem)
						.getSortLetters());
			}
			break;

		case Constant.VIEWPAHER_MENU_MUSICFAVORITES:
			if (FavoriteList.list.size() > firstVisibleItem) {
				mMusicFavoritesDialog.setText(FavoriteList.list.get(
						firstVisibleItem).getSortLetters());
			}
			break;
		}

	}

	private class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Flog.d(TAG, "onReceive()--start");
			Flog.d(TAG,
					"onReceive()---intent.getAction()--" + intent.getAction());
			Flog.d(TAG, "onReceive()---nAlbumPosition--" + nAlbumPosition);
			Flog.d(TAG, "onReceive()---nArtistPosition--" + nArtistPosition);
			// boolean isFavorites=false;
			if (intent != null) {
				final String action = intent.getAction();
				// 没有传值的就是通过播放界面标记我的最爱的，所以默认赋值上次点击播放的页面，为0则默认为全部歌曲
				slidingPage = intent.getIntExtra(
						Constant.BROADCAST_INTENT_PAGE,
						Constant.VIEWPAHER_MENU_MUSICNAME);
				nMenuPosition = intent.getIntExtra(
						Constant.BROADCAST_INTENT_POSITION, 0);
				MusicInfo info = null;
				switch (slidingPage) {
				case Constant.VIEWPAHER_MENU_MUSICNAME:// 曲目listview
					if (MusicList.list.size() > nMenuPosition) {
						info = MusicList.list.get(nMenuPosition);
					}
					break;

				case Constant.VIEWPAHER_MENU_ARTIST_LIST:// artist
					if (ArtistList.list.size() > nArtistPosition) {
						if (ArtistList.list.get(nArtistPosition).getMusicList()
								.size() > nMenuPosition) {
							info = ArtistList.list.get(nArtistPosition)
									.getMusicList().get(nMenuPosition);
						}

					}

					break;

				case Constant.VIEWPAHER_MENU_ALBUM_LIST:// album
					if (AlbumList.list.size() > nAlbumPosition) {
						if (AlbumList.list.get(nAlbumPosition).getMusicList()
								.size() > nMenuPosition) {
							info = AlbumList.list.get(nAlbumPosition)
									.getMusicList().get(nMenuPosition);
						}
					}

					break;

				case Constant.VIEWPAHER_MENU_MUSICFAVORITES:// album
					int size = FavoriteList.list.size();
					if (FavoriteList.list.isEmpty()) {
						return;
					} else {
						if (size <= nMenuPosition) {
							nMenuPosition = 0;
						}
						info = FavoriteList.list.get(nMenuPosition);
					}
					break;
				}
				Flog.d(TAG, "onReceive()---slidingPage--" + slidingPage);
				Flog.d(TAG, "onReceive()---nMenuPosition--" + nMenuPosition);
				Flog.d(TAG, "onReceive()---info--" + info);
				if (action.equals(Constant.BROADCAST_ACTION_CLEAR)) {
					removeList();
				} else if (action.equals(Constant.BROADCAST_ACTION_DETAIL)) {
					getMusicDetail();
				} else if (action.equals(Constant.BROADCAST_ACTION_FAVORITE)) {
					// 因为源数据是静态的，所以赋值给info也指向了静态数据的那块内存，直接改info的数据就行
					// 不知我的理解对否。而且这算不算内存泄露？？？
					if (info != null && !info.equals("")) {

						Flog.d(TAG,
								"MainActivity--isFavorite--"
										+ info.isFavorite());
						String pinyin = mCharacterParser.getSelling(info
								.getName());
						String sortString = pinyin.substring(0, 1)
								.toUpperCase();

						// 正则表达式，判断首字母是否是英文字母
						if (sortString.matches("[A-Z]")) {
							info.setSortLetters(sortString.toUpperCase());
						} else {
							info.setSortLetters("#");
						}
						Flog.d(TAG, "MainActivity--info--" + info.toString());
						Flog.d(TAG,
								"MainActivity--info.isFavorite()--"
										+ info.isFavorite());
						info.setFavorite(intent.getBooleanExtra(
								Constant.BROADCAST_INTENT_FAVORITE,
								info.isFavorite()));
						Flog.d(TAG, "MainActivity--info.isFavorite()--1--"
								+ info.isFavorite());
						if (info.isFavorite()) {
							synchronized (lockObject) {
								Flog.d(TAG, "MainActivity--lockObject--info--"
										+ info);
								FavoriteList.list.remove(info);// 移除
								lockObject.notifyAll();
							}
							info.setFavorite(false);// 删除标记
							Flog.d(TAG, "MainActivity--isFavorite--remove--"
									+ FavoriteList.list.size());
						} else {
							info.setFavorite(true);// 标记为喜爱
							synchronized (lockObject) {
								FavoriteList.list.add(info);// 新增
								lockObject.notifyAll();
							}
							Flog.d(TAG, "MainActivity--isFavorite--add--"
									+ FavoriteList.list.size());
							FavoriteList.sort();// 重新排序
						}
						DBDao db = new DBDao(getApplicationContext());
						db.update(info.getName(), info.isFavorite());// 更新数据库
						db.queryAlbum(mAlbumInfos);
						db.queryArtist(mAristInfos);
						db.close();// 必须关闭
						synchronized (mObject) {
							// MediaService.nPage=Constant.VIEWPAHER_MENU_ALBUM;
							mArtistListAdapter.update(mArtistListAdapter
									.getPage());
							mAlbumListAdapter.update(mAlbumListAdapter
									.getPage());

							mFavoritesListAdapter
									.update(Constant.VIEWPAHER_MENU_MUSICFAVORITES);

							mMusicNameListAdapter
									.update(Constant.VIEWPAHER_MENU_MUSICNAME);
							mObject.notifyAll();

						}

						mMenuPath = info.getPath();
					}
				}
				Flog.d(TAG, "onReceive()--end");
			}
		}
	}

	private void clearView() {
		mMainArtist.setText("");
		mMainName.setText("");
		mMainSeekBar.setProgress(0);
		mBtnPlay.setImageResource(R.drawable.main_btn_pause);
		mMainAlbum.setImageResource(R.drawable.main_album_item);
	}

	private class SDListenerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Flog.d(TAG, "SDListenerReceiver--onReceive()--start");
			Flog.d(TAG, "SDListenerReceiver--onReceive()--intent.getAction()=="
					+ intent.getAction());

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
				}

				MusicList.list.clear();
				AlbumList.list.clear();
				ArtistList.list.clear();
				FavoriteList.list.clear();

				synchronized (mObject) {
					mArtistListAdapter.setArtistIfo(null);
					mAlbumListAdapter.setAlbumIfo(null);
					mArtistListAdapter.update(mArtistListAdapter.getPage());
					mAlbumListAdapter.update(mAlbumListAdapter.getPage());
					mFavoritesListAdapter.update(mFavoritesListAdapter
							.getPage());
					mMusicNameListAdapter
							.update(Constant.VIEWPAHER_MENU_MUSICNAME);

					mObject.notifyAll();
				}

				clearView();
				isSDMove = true;
				mBtnScan.setEnabled(false);

			} else if (intent.getAction().equals(
					Constant.BROADCAST_ACTION_MEDIA_MOUNTED)) {
				Flog.d(TAG,
						"onReceive()--Constant.BROADCAST_ACTION_MEDIA_MOUNTED==========");
				Toast.makeText(getApplicationContext(),
						Constant.SDCARD_REMOUNT, 4000).show();
				mBtnScan.setEnabled(true);

			}
		}
	}

	/**
	 * 实时更新UI静态嵌套类
	 * 
	 * 之所以这样写，是因为Handler是个极易内存泄露的对象，我可是吃过亏的，保持软引用可以有效避免
	 * 
	 * 实现更新UI
	 */
	private static class ScanHandler extends Handler {

		private WeakReference<ScanTask> mReference;

		public ScanHandler(ScanTask activity) {
			// TODO Auto-generated constructor stub
			mReference = new WeakReference<ScanTask>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (mReference.get() != null) {
				ScanTask theActivity = mReference.get();
				mDialog.setMessage(msg.obj.toString());// /////////////////////////////////////////////////////////////////
			}
		}

	}
}
