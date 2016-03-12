package com.flyaudio.flyMediaPlayer.until;

import com.flyAudio.flyMediaPlayer.R;

import android.R.integer;
import android.graphics.Color;
import android.os.Environment;
import android.view.animation.Interpolator;

public class Constant {

	public static final String BROADCAST_ACTION_MEDIA_MOUNTED = "com.flyaudio.action.MEDIA_MOUNTED";
	public static final String BROADCAST_ACTION_MEDIA_REMOVED = "com.flyaudio.action.MEDIA_REMOVED";
	// 相关广播的定义
	public static final String BROADCAST_ACTION_SCAN = "com.flyaudio.action.scan";// 扫描广播־
	public static final String BROADCAST_ACTION_MENU = "com.flyaudio.action.menu";// 弹出菜单标志
	public static final String BROADCAST_ACTION_EXIT = "com.flyaudio.action.exit";// 退出的索引
	public static final String BROADCAST_ACTION_CLEAR = "com.flyaudio.action.clear";// 删除广播
	public static final String BROADCAST_ACTION_DETAIL = "com.flyaudio.action.detail";// 详情广播
	public static final String BROADCAST_ACTION_FAVORITE = "com.flyaudio.action.favorite";// 最爱广播
	public static final String BROADCAST_ACTION_SERVICE = "com.flyaudio.action.service";// 服务的广播标志־
	public static final String BROADCAST_INTENT_PAGE = "com.flyaudio.intent.page";// 页面状态״̬
	public static final String BROADCAST_INTENT_POSITION = "com.flyaudio.intent.position";// 歌曲的索引
	public static final String BROADCAST_INTENT_FAVORITE = "com.flyaudio.intent.favorites";// 歌曲的最爱
	public static final String SERVICE_STATE = "com.flyaudio.flyMediaPlayer.serviceImpl.MediaService";// 服务的标志

	public static final String BROADCAST_ACTION_NOT_ALBUM = "com.flyaudio.action.album";
	public static final String BROADCAST_ACTION_NOT_PLAY = "com.flyaudio.action.play";
	public static final String BROADCAST_ACTION_NOT_NEXT = "com.flyaudio.action.next";
	public static final String BROADCAST_ACTION_NOT_PREV = "com.flyaudio.action.previous";
	public static final String BROADCAST_ACTION_NOT_STATE = "com.flyaudio.action.state";
	public static final String BROADCAST_ACTION_NOT_EXIT = "com.flyaudio.action.exit";
	public static final String BROADCAST_ACTION_APP_PLAY = "com.flyaudioMedia.playMusic";
	public static final String BROADCAST_ACTION_APP_PAUSE = "com.flyaudioMedia.pause";
	public static final String BROADCAST_ACTION_APP_NEXT = "com.flyaudioMedia.nextOne";
	public static final String BROADCAST_ACTION_APP_PREVIOUS = "com.flyaudioMedia.previousOne";
	public static final String BROADCAST_ACTION_APP_STATE = "com.flyaudioMedia.state";
	public static final String BROADCAST_ACTION_APP_MUSICINFO = "com.flyaudioMedia.musicInfo";// noification的音乐信息
	public static final String BROADCAST_ACTION_APP_INFO = "com.flyaudioMedia.myAppMusicInfo";// myApp的音乐信息
	// mediaserivce的相关控制命令
	public static final int CONTROL_COMMAND_PLAY = 0;// 控制命令：播放或者暂停
	public static final int CONTROL_COMMAND_PREVIOUS = 1;// 控制命令：上一首
	public static final int CONTROL_COMMAND_NEXT = 2;// 控制命令：下一首
	public static final int CONTROL_COMMAND_MODE = 3;// 控制命令：播放模式切换
	public static final int CONTROL_COMMAND_REWIND = 4;// 控制命令：快退
	public static final int CONTROL_COMMAND_FORWARD = 5;// 控制命令：快进
	public static final int CONTROL_COMMAND_REPLAY = 6;// 控制命令：用于快退、快进后的继续播放
	// 侧边按钮

	public static final int VIEWPAHER_MENU_MUSICNAME = 0;// 曲目
	public static final int VIEWPAHER_MENU_ARTIST = 1;// 歌手
	public static final int VIEWPAHER_MENU_ALBUM = 2;// 专辑
	public static final int VIEWPAHER_MENU_MUSICFAVORITES = 3;//
	public static final int VIEWPAHER_MENU_NETWORK = 4;// 在线
	public static final int VIEWPAHER_MENU_ARTIST_LIST = 5;// 歌手listview点击进入音乐listview
	public static final int VIEWPAHER_MENU_ALBUM_LIST = 6;// 专辑点击进入音乐listview
	// player的menu按扭
	public static final int DIALOG_DISMISS = 0;// 对话框消失
	public static final int DIALOG_SCAN = 1;
	public static final int DIALOG_MENU_REMOVE = 2;
	public static final int DIALOG_MENU_DELETE = 3;
	public static final int DIALOG_MENU_INFO = 4;
	public static final int DIALOG_DELETE = 5;
	public static final int DIALOG_MENU_RINGTONE = 6;
	public static final int DIALOG_MENU_SHARE = 7;

	public final static String ACTION_TURN_LAUNCHER = "cn.flyaudio.launcher.senddata";
	/*
	 * public static final int VIEWPAGER_NAME = 0; public static final int
	 * VIEWPAGER_ARTIST = 1; public static final int VIEWPAGER_ALBUM = 2; public
	 * static final int VIEWPAGER_FAVORITES = 3; public static final int
	 * VIEWPAGER_NEWWORK = 4;
	 */
	public final static String PATH_SDCARD = Environment
			.getExternalStorageDirectory().toString();
	public final static String PATH_USB = "/storage/udisk";
	public static final int ACTIVITY_MAIN = 0x101;// 主界面
	public static final int ACTIVITY_PLAYER = 0x102;// 播放界面
	//
	public static final int MEDIA_PLAY_ERROR = 0;
	public static final int MEDIA_PLAY_START = 1;
	public static final int MEDIA_PLAY_UPDATE = 2;
	public static final int MEDIA_PLAY_COMPLETE = 3;
	public static final int MEDIA_PLAY_UPDATE_LYRIC = 4;
	public static final int MEDIA_PLAY_REWIND = 5;
	public static final int MEDIA_PLAY_FORWARD = 6;
	// 服务中定义的播放模式
	public static final int MODE_NORMAL = 0;// 顺序播放，放到最后一首停止
	public static final int MODE_REPEAT_ONE = 1;// 单曲循环
	public static final int MODE_REPEAT_ALL = 2;// 全部循环
	public static final int MODE_RANDOM = 3;// 随即播放
	public static final int UPDATE_LYRIC_TIME = 500;// 歌词更新间隔0.15秒
	public static final int UPDATE_UI_TIME = 400;// UI更新间隔1秒
	public static final int MAX_SETTLE_DURATION = 400; // ms
	public static final int MIN_DISTANCE_FOR_FLING = 25; // dips
	public static final int EQUALIZER_MAX_BANDS = 32;
	// 存储的相关信息
	public static final String PREFERENCES_NAME = "settings";
	public static final String PREFERENCES_MODE = "mode";// 存储播放模式
	public static final String PREFERENCES_SCAN = "scan";// 存储是否扫描过
	public static final String PREFERENCES_SKIN = "skin";// 存储背影图
	public static final String PREFERENCES_LYRIC = "lyric";// 存储歌词的高亮颜色
	public static final String PREFERENCES_PATH = "musicpath";
	public static final String PREFERENCES_STATE = "state";
	public static final String PREFERENCES_POSITION = "position";
	public static final String PREFERENCES_DURATION = "duration";

	public static final String PREFERENCES_POSITION_ONE = "position_one";
	public static final String PREFERENCES_POSITION_TWO = "position_two";

	public static final String INTENT_ACTIVITY = "activity";// 区分来自哪个界面
	public static final String INTENT_LIST_PAGE = "list_page";// 列表页面
	public static final String INTENT_LIST_POSITION = "list_position";// 列表当前项
	public static final String INTENT_ALBUM_POSITION = "album_position";
	public static final String INTENT_ARTIST_POSITION = "artist_position";
	public static final String INTENT_ALBUM_ITEM_POSITION = "album_item_position";
	public static final String INTENT_ARTIST_ITEM_POSITION = "artist_item_position";
	public static final String INTENT_FOLDER_POSITION = "folder_position";// 文件夹列表当前项

	public static final String SHOW_LRC = "com.flyaudio.SHOW_LRC";
	public static final String TITLE_ALL = "播放列表";
	public static final String TITLE_FAVORITE = "我的最爱";
	public static final String TITLE_FOLDER = "文件夹";
	public static final String TITLE_NORMAL = "无音乐播放";
	public static final String TIME_NORMAL = "00:00";
	public static final String MUSIC_SIZE = "首";
	public static final String MUSIC_ARTIST = "未知艺术家";
	public static final String SDCARD_REMOVE = "无SDCARD!!!!";
	public static final String SDCARD_REMOUNT = "扫描音乐!!!!";

	public static final Object lockObject = new Object();

	public static final int[] modeImage = {
			R.drawable.player_btn_mode_normal_style,
			R.drawable.player_btn_mode_repeat_one_style,
			R.drawable.player_btn_mode_repeat_all_style,
			R.drawable.player_btn_mode_random_style };

	public static final String[] PRESETREVERBPRESETSTRINGS = { "None",
			"SmallRoom", "MediumRoom", "LargeRoom", "MediumHall", "LargeHall",
			"Plate" };

	public static final boolean USE_CACHE = false;

	public static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};
	/*
	 * public static final int[][] EQViewElementIds = { { R.id.EQBand0TextView,
	 * R.id.EQBand0SeekBar }, { R.id.EQBand1TextView, R.id.EQBand1SeekBar }, {
	 * R.id.EQBand2TextView, R.id.EQBand2SeekBar }, { R.id.EQBand3TextView,
	 * R.id.EQBand3SeekBar }, { R.id.EQBand4TextView, R.id.EQBand4SeekBar }, {
	 * R.id.EQBand5TextView, R.id.EQBand5SeekBar }, { R.id.EQBand6TextView,
	 * R.id.EQBand6SeekBar }, { R.id.EQBand7TextView, R.id.EQBand7SeekBar }, {
	 * R.id.EQBand8TextView, R.id.EQBand8SeekBar }, { R.id.EQBand9TextView,
	 * R.id.EQBand9SeekBar }, { R.id.EQBand10TextView, R.id.EQBand10SeekBar }, {
	 * R.id.EQBand11TextView, R.id.EQBand11SeekBar }, { R.id.EQBand12TextView,
	 * R.id.EQBand12SeekBar }, { R.id.EQBand13TextView, R.id.EQBand13SeekBar },
	 * { R.id.EQBand14TextView, R.id.EQBand14SeekBar }, { R.id.EQBand15TextView,
	 * R.id.EQBand15SeekBar }, { R.id.EQBand16TextView, R.id.EQBand16SeekBar },
	 * { R.id.EQBand17TextView, R.id.EQBand17SeekBar }, { R.id.EQBand18TextView,
	 * R.id.EQBand18SeekBar }, { R.id.EQBand19TextView, R.id.EQBand19SeekBar },
	 * { R.id.EQBand20TextView, R.id.EQBand20SeekBar }, { R.id.EQBand21TextView,
	 * R.id.EQBand21SeekBar }, { R.id.EQBand22TextView, R.id.EQBand22SeekBar },
	 * { R.id.EQBand23TextView, R.id.EQBand23SeekBar }, { R.id.EQBand24TextView,
	 * R.id.EQBand24SeekBar }, { R.id.EQBand25TextView, R.id.EQBand25SeekBar },
	 * { R.id.EQBand26TextView, R.id.EQBand26SeekBar }, { R.id.EQBand27TextView,
	 * R.id.EQBand27SeekBar }, { R.id.EQBand28TextView, R.id.EQBand28SeekBar },
	 * { R.id.EQBand29TextView, R.id.EQBand29SeekBar }, { R.id.EQBand30TextView,
	 * R.id.EQBand30SeekBar }, { R.id.EQBand31TextView, R.id.EQBand31SeekBar }
	 * };
	 */
	public static final int[][] EQViewElementIds = {
			{ R.id.EQBand0TextView, R.id.EQBand0SeekBar },
			{ R.id.EQBand1TextView, R.id.EQBand1SeekBar },
			{ R.id.EQBand2TextView, R.id.EQBand2SeekBar },
			{ R.id.EQBand3TextView, R.id.EQBand3SeekBar },
			{ R.id.EQBand4TextView, R.id.EQBand4SeekBar }, };
	public static final String Normal = "/普通";
	public static final String Classical = "/古典";
	public static final String Dance = "/舞曲";
	public static final String Flat = "/平柔";
	public static final String Folk = "/民族";
	public static final String HeavyMetal = "/重金属";
	public static final String HipHop = "/说唱";
	public static final String Pop = "/流行";
	public static final String Rock = "/摇滚";
	public static final String FXbooster = "";
	public static final String Jazz = "/爵士";

}
