package com.flyaudio.flyMediaPlayer.data;

/**
 * 
 * 
 * <br>
 * <b>数据库字段</b></br>
 * 
 * @version 2013.05.19 v1.0 暂定数据库几个字段<br>
 *          2013.06.16 v1.1 新增数据库几个字段<br>
 *          2013.06.23 v1.2 新增歌词表及几个字段<br>
 *          2013.08.05 v1.3 新增音乐专辑字段</br>
 */
public class DBData {

	/**
	 * 音乐数据库名称
	 */
	public static final String MUSIC_DB_NAME = "media.db";
	/**
	 * 音乐数据库版本号
	 */
	public static final int MUSIC_DB_VERSION = 1;
	/**
	 * 音乐信息表
	 */
	public static final String MUSIC_TABLENAME = "music";
	/**
	 * 歌词信息表
	 */
	public static final String LYRIC_TABLENAME = "lyric";

	/**
	 * 音乐ID字段
	 */
	public static final String MUSIC_ID = "id";
	/**
	 * 音乐文件名称字段(作为判断是否唯一存在)
	 */
	public static final String MUSIC_FILE = "file";
	/**
	 * 音乐名称字段
	 */
	public static final String MUSIC_NAME = "name";
	/**
	 * 音乐路径字段
	 */
	public static final String MUSIC_PATH = "path";
	/**
	 * 音乐所属文件夹字段
	 */
	public static final String MUSIC_FOLDER = "folder";
	/**
	 * 是否最喜爱音乐字段
	 */
	public static final String MUSIC_FAVORITE = "favorite";
	/**
	 * 音乐时长字段
	 */
	public static final String MUSIC_TIME = "time";
	/**
	 * 音乐文件大小字段
	 */
	public static final String MUSIC_SIZE = "size";
	/**
	 * 音乐艺术家字段
	 */
	public static final String MUSIC_ARTIST = "artist";
	/**
	 * 音乐格式(编码类型)字段
	 */
	public static final String MUSIC_FORMAT = "format";
	/**
	 * 音乐专辑字段
	 */
	public static final String MUSIC_ALBUM = "album";
	/**
	 * 音乐艺术家时间字段
	 */
	public static final String MUSIC_YEARS = "years";
	/**
	 * 音乐声道字段
	 */
	public static final String MUSIC_CHANNELS = "channels";
	/**
	 * 音乐风格字段
	 */
	public static final String MUSIC_GENRE = "genre";
	/**
	 * 音乐比特率字段
	 */
	public static final String MUSIC_KBPS = "kbps";
	/**
	 * 音乐采样率字段
	 */
	public static final String MUSIC_HZ = "hz";

	/**
	 * 歌词ID字段
	 */
	public static final String LYRIC_ID = "id";
	/**
	 * 歌词文件名字段(作为判断是否唯一存在)
	 */
	public static final String LYRIC_FILE = "file";
	/**
	 * 歌词路径字段
	 */
	public static final String LYRIC_PATH = "path";
}
