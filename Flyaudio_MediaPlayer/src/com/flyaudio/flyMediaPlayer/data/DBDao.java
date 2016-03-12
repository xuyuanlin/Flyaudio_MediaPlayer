package com.flyaudio.flyMediaPlayer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.flyaudio.flyMediaPlayer.objectInfo.AlbumInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.AristInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.FolderInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.ScanInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.SearchInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.ArtistList;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
import com.flyaudio.flyMediaPlayer.perferences.FolderList;
import com.flyaudio.flyMediaPlayer.perferences.LyricList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.until.CharacterParser;
import com.flyaudio.flyMediaPlayer.until.Flog;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore.Audio.Artists;
import android.util.Log;

public class DBDao {
	private static String TAG = "DBDao";
	private DBHelper helper;
	private SQLiteDatabase db;
	private Cursor mCursor;
	private String[] artists;
	private CharacterParser mCharacterParser;

	/**
	 * 创建和初始化数据库，使用完记得调用close方法关闭数据库
	 * 
	 * @param context
	 */
	public DBDao(Context context) {
		// TODO Auto-generated constructor stub
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * 新增单条音乐数据信息
	 * 
	 * @param fileName
	 *            文件名(目的是用来作为唯一名称用以判断是否存在)
	 * @param musicName
	 *            音乐名称
	 * @param musicPath
	 *            音乐路径
	 * @param musicFolder
	 *            音乐隶属文件夹
	 * @param isFavorite
	 *            是否为最喜爱音乐
	 * @param musicTime
	 *            音乐时长
	 * @param musicSize
	 *            音乐文件大小
	 * @param musicArtist
	 *            音乐艺术家
	 * @param musicFormat
	 *            音乐格式(编码类型)
	 * @param musicAlbum
	 *            音乐专辑
	 * @param musicYears
	 *            音乐年代
	 * @param musicChannels
	 *            音乐声道
	 * @param musicGenre
	 *            音乐风格
	 * @param musicKbps
	 *            音乐比特率
	 * @param musicHz
	 *            音乐采样率
	 * @return 新增成功的条数，失败返回-1
	 */
	public long add(String fileName, String musicName, String musicPath,
			String musicFolder, boolean isFavorite, String musicTime,
			String musicSize, String musicArtist, String musicFormat,
			String musicAlbum, String musicYears, String musicChannels,
			String musicGenre, String musicKbps, String musicHz) {
		Flog.d(TAG, "add------" + fileName);
		Flog.d(TAG, "add------" + musicName);
		Flog.d(TAG, "add------" + musicPath);
		Flog.d(TAG, "add------" + musicFolder);
		ContentValues values = new ContentValues();
		values.put(DBData.MUSIC_FILE, fileName);
		values.put(DBData.MUSIC_NAME, musicName);
		values.put(DBData.MUSIC_PATH, musicPath);
		values.put(DBData.MUSIC_FOLDER, musicFolder);
		values.put(DBData.MUSIC_FAVORITE, isFavorite ? 1 : 0);// 数据库定义字段数据为整型
		values.put(DBData.MUSIC_TIME, musicTime);
		values.put(DBData.MUSIC_SIZE, musicSize);
		values.put(DBData.MUSIC_ARTIST, musicArtist);
		values.put(DBData.MUSIC_FORMAT, musicFormat);
		values.put(DBData.MUSIC_ALBUM, musicAlbum);
		values.put(DBData.MUSIC_YEARS, musicYears);
		values.put(DBData.MUSIC_CHANNELS, musicChannels);
		values.put(DBData.MUSIC_GENRE, musicGenre);
		values.put(DBData.MUSIC_KBPS, musicKbps);
		values.put(DBData.MUSIC_HZ, musicHz);
		long result = db.insert(DBData.MUSIC_TABLENAME, DBData.MUSIC_FILE,
				values);
		return result;
	}

	/**
	 * 新增单条音乐歌词信息
	 * 
	 * @param fileName
	 *            文件名(目的是用来作为唯一名称用以判断是否存在)
	 * @param lrcPath
	 *            歌词路径
	 * @return 新增成功的条数，失败返回-1
	 */
	public long addLyric(String fileName, String lrcPath) {

		// Flog.d(TAG, "DBDao---addlryic()");
		ContentValues values = new ContentValues();
		values.put(DBData.LYRIC_FILE, fileName);
		values.put(DBData.LYRIC_PATH, lrcPath);
		long result = db.insert(DBData.LYRIC_TABLENAME, DBData.LYRIC_FILE,
				values);
		return result;
	}

	/**
	 * 更新音乐相关记录，只更新用户是否标记为最喜爱音乐
	 * 
	 * @param musicName
	 *            音乐名称
	 * @param isFavorite
	 *            是否为最喜爱音乐(true:1 else false:0)
	 * @return 影响的行数
	 */
	public int update(String musicName, boolean isFavorite) {
		ContentValues values = new ContentValues();
		values.put(DBData.MUSIC_FAVORITE, isFavorite ? 1 : 0);// 数据库定义字段数据为整型
		int result = db.update(DBData.MUSIC_TABLENAME, values,
				DBData.MUSIC_NAME + "=?", new String[] { musicName });

		return result;
	}

	public List<SearchInfo> getNameList() {
		// int nArtistCount = 0;
		List<SearchInfo> mList = new ArrayList<SearchInfo>();
		mCursor = db.query(DBData.MUSIC_TABLENAME, new String[] {
				DBData.MUSIC_NAME, DBData.MUSIC_PATH }, null, null, null, null,
				null);
		if (mCursor != null && mCursor.getCount() > 0) {
			while (mCursor.moveToNext()) {
				SearchInfo mSearchInfo = new SearchInfo();
				String mName = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_NAME));
				String mPath = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_PATH));
				mSearchInfo.setnPath(mPath);
				mSearchInfo.setmName(mName);
				mList.add(mSearchInfo);

			}

		}
		if (mCursor != null) {
			mCursor.close();
		}
		Flog.d(TAG, "getNameList()--mList--" + mList);
		return mList;
	}

	public int getArtistCount(String nArtist) {
		int nArtistCount = 0;
		mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
				+ " WHERE " + DBData.MUSIC_ARTIST + "='" + nArtist + "'", null);
		if (mCursor != null && mCursor.getCount() > 0) {
			nArtistCount = mCursor.getCount();
		}
		if (mCursor != null) {
			mCursor.close();
		}
		return nArtistCount;
	}

	public int getAlbumCount(String nAlbum) {
		int nAlbumCount = 0;
		mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
				+ " WHERE " + DBData.MUSIC_ALBUM + "='" + nAlbum + "'", null);
		if (mCursor != null && mCursor.getCount() > 0) {
			nAlbumCount = mCursor.getCount();
		}
		if (mCursor != null) {
			mCursor.close();
		}
		Flog.d(TAG, "getAlbumCount==" + nAlbumCount);
		return nAlbumCount;
	}

	/**
	 * 查询对应条件的数据库信息是否存在
	 * 
	 * 建议此处不要写SQL语句，即rawQuery查询。因为某些文件名中就带有'，所以肯定报错！
	 * 
	 * @param musicName
	 *            音乐名称
	 * @param musicFolder
	 *            音乐隶属文件夹
	 * @return 是否存在
	 */
	public boolean queryExist(String fileName, String musicFolder) {
		boolean isExist = false;

		Cursor cursor = db.query(DBData.MUSIC_TABLENAME, null,
				DBData.MUSIC_FILE + "=? AND " + DBData.MUSIC_FOLDER + "=?",
				new String[] { fileName, musicFolder }, null, null, null);
		if (cursor.getCount() > 0) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * 查询数据库保存的各媒体库目录下所有音乐信息和歌词
	 * 
	 * @param scanList
	 *            音乐媒体库所有目录
	 */
	public void queryAll() {
		Flog.d(TAG, "DBDao-----queryAll()");
		MusicList.list.clear();
		FolderList.list.clear();
		FavoriteList.list.clear();
		LyricList.map.clear();
		Flog.d(TAG, "DBDao-----queryAll()-----MusicList.list------"
				+ MusicList.list.toString());

		mCharacterParser = CharacterParser.getInstance();

		mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME, null);
		List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
		if (mCursor != null && mCursor.getCount() > 0) {

			while (mCursor.moveToNext()) {
				MusicInfo musicInfo = new MusicInfo();
				final String file = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_FILE));
				final String name = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_NAME));
				final String path = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_PATH));
				final int favorite = mCursor.getInt(mCursor
						.getColumnIndex(DBData.MUSIC_FAVORITE));
				final String time = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_TIME));
				final String size = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_SIZE));
				final String artist = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_ARTIST));
				final String format = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_FORMAT));
				final String album = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_ALBUM));
				final String years = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_YEARS));
				final String channels = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_CHANNELS));
				final String genre = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_GENRE));
				final String kbps = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_KBPS));
				final String hz = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_HZ));
				final int id = mCursor.getInt(mCursor
						.getColumnIndex(DBData.MUSIC_ID));
				Flog.d(TAG, "DBDao-----queryAll()--name--" + name);
				Flog.d(TAG, "DBDao-----queryAll()--ID--" + id);

				musicInfo.setFile(file);
				musicInfo.setName(name);
				musicInfo.setPath(path);
				musicInfo.setFavorite(favorite == 1 ? true : false);
				musicInfo.setTime(time);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setFormat(format);
				musicInfo.setAlbum(album);
				musicInfo.setYears(years);
				musicInfo.setChannels(channels);
				musicInfo.setGenre(genre);
				musicInfo.setKbps(kbps);
				musicInfo.setHz(hz);
				musicInfo.setId(id);
				String pinyin = mCharacterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					musicInfo.setSortLetters(sortString.toUpperCase());
				} else {
					musicInfo.setSortLetters("#");
				}
				// 加入所有歌曲列表
				MusicList.list.add(musicInfo);
				MusicList.sort();
				// 加入文件夹临时列表
				listInfo.add(musicInfo);
				// 加入我的最爱列表
				if (favorite == 1) {
					Flog.d(TAG, "favorite" + FavoriteList.list.size());
					FavoriteList.list.add(musicInfo);
					FavoriteList.sort();
				}
			}

		}

		Flog.d(TAG, "DBDao-----queryAll()-----MusicList.list222------"
				+ MusicList.list.toString());
		// 查询歌词
		mCursor = db.rawQuery("SELECT * FROM " + DBData.LYRIC_TABLENAME, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			while (mCursor.moveToNext()) {
				final String file = mCursor.getString(mCursor
						.getColumnIndex(DBData.LYRIC_FILE));
				final String path = mCursor.getString(mCursor
						.getColumnIndex(DBData.LYRIC_PATH));
				LyricList.map.put(file, path);
			}
		}
		if (mCursor != null) {
			mCursor.close();
		}
	}

	/*
	 * 查询艺术家的的名字和曲目数
	 */public List<AristInfo> getArtistList() {
		List<AristInfo> mAristInfos = new ArrayList<AristInfo>();
		mCursor = db.query(DBData.MUSIC_TABLENAME, new String[] {
				DBData.MUSIC_ARTIST, DBData.MUSIC_PATH }, null, null, null,
				null, null);
		int count = 0;
		HashSet<String> set = new HashSet<String>();
		Map<String, String> map = new HashMap<String, String>();
		if (mCursor != null && mCursor.getCount() > 0) {
			while (mCursor.moveToNext()) {
				String nArtist = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_ARTIST));
				String musicPath = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_PATH));
				if (nArtist == null || nArtist.equals("")) {
					nArtist = "未知";
				}
				set.add(nArtist);
				map.put(nArtist, musicPath);

			}
			Flog.d(TAG,
					"DBDao-----getArtistList()-----set-----" + set.toString());
		}
		Iterator<String> iterator = set.iterator();
		String artists[] = new String[mCursor.getCount()];
		int i = 0;
		while (iterator.hasNext()) {
			artists[i] = iterator.next();
			i++;
		}
		if (mCursor != null) {
			mCursor.close();
		}
		// Flog.d(TAG, "mCursor---" + mCursor);
		Flog.d(TAG, "artists.length---" + artists.length);
		for (int j = 0; j < artists.length; j++) {
			AristInfo mAristInfo = new AristInfo();
			if (artists[j] != null) {
				mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
						+ " WHERE " + DBData.MUSIC_ARTIST + "='" + artists[j]
						+ "'", null);
				if (mCursor != null) {// xyz
					count = mCursor.getCount();
				}
				Flog.d(TAG, "mCursor.getCount()---" + mCursor.getCount());
				Flog.d(TAG, "count---" + count);
				mAristInfo.setArist(artists[j]);
				mAristInfo.setAristCount(count);
				mAristInfo.setMusicPath(map.get(artists[j]));
				Flog.d(TAG, "mAristInfo---" + mAristInfo.toString());
				mAristInfos.add(mAristInfo);

			}
		}

		if (mCursor != null) {
			mCursor.close();
		}
		Flog.d(TAG, "mAristInfos.size()---" + mAristInfos.size());
		for (int j = 0; j < mAristInfos.size(); j++) {
			Flog.d(TAG, "mAristInfos---" + mAristInfos.get(j).toString());
		}
		return mAristInfos;
	}

	/*
	 * 查询专辑的名字和年代
	 */public List<AlbumInfo> getAlbumList() {
		Flog.d(TAG, "getAlbumList---start");
		List<AlbumInfo> mAlbumInfos = new ArrayList<AlbumInfo>();
		mCursor = db.query(DBData.MUSIC_TABLENAME, new String[] {
				DBData.MUSIC_YEARS, DBData.MUSIC_ALBUM, DBData.MUSIC_PATH },
				null, null, null, null, null);
		HashSet<String> set = new HashSet<String>();
		Map<String, String> map = new HashMap<String, String>();
		// Map<String, String> map2 = new HashMap<String, String>();
		if (mCursor != null && mCursor.getCount() > 0) {
			while (mCursor.moveToNext()) {
				String nAlbum = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_ALBUM));
				String nYear = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_YEARS));
				String musicPath = mCursor.getString(mCursor
						.getColumnIndex(DBData.MUSIC_PATH));
				if (nAlbum == null || nAlbum.equals("")) {
					nAlbum = "未知";
				} else {
					if (nYear == null || nYear.equals("") || nYear.equals("未知")) {
						nYear = "年代未知";
					}

				}
				map.put(nAlbum, nYear);
				map.put(nYear, musicPath);

				set.add(nAlbum);
			}
		}
		Iterator<String> iterator = set.iterator();
		String albums[] = new String[mCursor.getCount()];
		int i = 0;
		while (iterator.hasNext()) {
			albums[i] = iterator.next();
			i++;
		}

		for (int j = 0; j < albums.length; j++) {
			AlbumInfo mAlbumInfo = new AlbumInfo();
			if (albums[j] != null) {
				String year = map.get(albums[j]);
				String musicPath = map.get(year);
				mAlbumInfo.setAlbum(albums[j]);
				mAlbumInfo.setYear(year);
				mAlbumInfo.setMusicPath(musicPath);
				Flog.d(TAG, "mAlbumInfo---" + mAlbumInfo.toString());
				mAlbumInfos.add(mAlbumInfo);
			}
		}
		Flog.d(TAG, "getAlbumList---end");
		return mAlbumInfos;
	}

	/**
	 * 根据文件路径来判断是否音乐存在
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public boolean isQuery(String filePath) {
		boolean isExist = false;
		Cursor cursor = db.query(DBData.MUSIC_TABLENAME, null,
				DBData.MUSIC_PATH + "=?", new String[] { filePath }, null,
				null, null);

		if (cursor.getCount() > 0) {
			isExist = true;
		}
		return isExist;

	}

	public boolean isLryicQuery(String filePath) {
		boolean isExist = false;
		Cursor cursor = db.query(DBData.LYRIC_TABLENAME, null,
				DBData.LYRIC_PATH + "=?", new String[] { filePath }, null,
				null, null);
		if (cursor != null) {
			// if (cursor.getCount() > 0) {
			isExist = true;
			// }
		} else {
			isExist = false;
		}
		return isExist;

	}

	/**
	 * 删除歌曲信息
	 */
	public void deleteAll() {
		Flog.d(TAG, "deleteAll");
		db.execSQL("delete from " + DBData.MUSIC_TABLENAME + ";");

		Flog.d(TAG, "deleteAll()-----end");
	}

	/**
	 * 根据文件路径来删除音乐信息
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 成功删除的条数
	 */
	public int delete(String filePath) {

		int result = db.delete(DBData.MUSIC_TABLENAME, DBData.MUSIC_PATH + "='"
				+ filePath + "'", null);
		return result;
	}

	/**
	 * 删除歌词信息表
	 */
	public void deleteLyric() {
		// 可能不存在该表，需要拋异常
		try {

			db.execSQL("delete from " + DBData.LYRIC_TABLENAME + ";");
			db.execSQL("update sqlite_sequence set seq=0 where name='"
					+ DBData.LYRIC_TABLENAME + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用完数据库必须关闭
	 */
	public void close() {
		db.close();
		db = null;
	}

	public void queryArtist(List<AristInfo> mAristInfos) {
		Flog.d(TAG, "DBDao-----queryArtist()--start");
		ArtistList.list.clear();
		MusicList.list.clear();
		FavoriteList.list.clear();
		mCharacterParser = CharacterParser.getInstance();
		final int listSize = mAristInfos.size();
		// final int listSize=artists.length;

		// 查询各媒体库目录下所有音乐信息
		for (int i = 0; i < listSize; i++) {
			final String nArtist = mAristInfos.get(i).getArist();

			mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
					+ " WHERE " + DBData.MUSIC_ARTIST + "='" + nArtist + "'",
					null);
			Flog.d(TAG,
					"queryArtist--mCursor.getCount()--" + mCursor.getCount());
			List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
			if (mCursor != null && mCursor.getCount() > 0) {
				// FolderInfo folderInfo = new FolderInfo();
				AristInfo mAristInfo = new AristInfo();

				while (mCursor.moveToNext()) {
					MusicInfo musicInfo = new MusicInfo();
					final String file = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_FILE));
					final String name = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_NAME));
					final String path = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_PATH));
					final int favorite = mCursor.getInt(mCursor
							.getColumnIndex(DBData.MUSIC_FAVORITE));
					final String time = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_TIME));
					final String size = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_SIZE));
					final String artist = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_ARTIST));
					final String format = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_FORMAT));
					final String album = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_ALBUM));
					final String years = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_YEARS));
					final String channels = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_CHANNELS));
					final String genre = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_GENRE));
					final String kbps = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_KBPS));
					final String hz = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_HZ));
					final int id = mCursor.getInt(mCursor
							.getColumnIndex(DBData.MUSIC_ID));
					musicInfo.setFile(file);
					musicInfo.setName(name);
					musicInfo.setPath(path);
					musicInfo.setFavorite(favorite == 1 ? true : false);
					musicInfo.setTime(time);
					musicInfo.setSize(size);
					musicInfo.setArtist(artist);
					musicInfo.setFormat(format);
					musicInfo.setAlbum(album);
					musicInfo.setYears(years);
					musicInfo.setChannels(channels);
					musicInfo.setGenre(genre);
					musicInfo.setKbps(kbps);
					musicInfo.setHz(hz);
					musicInfo.setId(id);
					String pinyin = mCharacterParser.getSelling(name);
					String sortString = pinyin.substring(0, 1).toUpperCase();

					// 正则表达式，判断首字母是否是英文字母
					if (sortString.matches("[A-Z]")) {
						musicInfo.setSortLetters(sortString.toUpperCase());
					} else {
						musicInfo.setSortLetters("#");
					}

					// 加入文件夹临时列表

					listInfo.add(musicInfo);
					// 加入我的最爱列表
					MusicList.list.add(musicInfo);
					MusicList.sort();
					if (favorite == 1) {
						Flog.d(TAG, "favorite" + FavoriteList.list.size());
						FavoriteList.list.add(musicInfo);
						FavoriteList.sort();
					}

				}
				mAristInfo.setArist(nArtist);
				mAristInfo.setMusicList(listInfo);
				mAristInfo.sort();
				ArtistList.list.add(mAristInfo);
				Flog.d(TAG, "DBDao-----queryArtist()--nArtist--" + nArtist);
				Flog.d(TAG, "DBDao-----queryArtist()--mAristInfo--"
						+ mAristInfo.toString());

			}
		}

		if (mCursor != null) {
			mCursor.close();
		}
		Flog.d(TAG, "DBDao-----queryArtist()--end--");
	}

	public void queryAlbum(List<AlbumInfo> mAlbumInfos) {
		Flog.d(TAG, "DBDao-----queryAlbum()--start");
		AlbumList.list.clear();
		MusicList.list.clear();
		FavoriteList.list.clear();
		final int listSize = mAlbumInfos.size();
		mCharacterParser = CharacterParser.getInstance();
		// 查询各媒体库目录下所有音乐信息
		for (int i = 0; i < listSize; i++) {
			final String nAlbum = mAlbumInfos.get(i).getAlbum();
			Flog.d(TAG, "DBDao-----queryAlbum()===nAlbum==" + nAlbum);

			nAlbum.replaceAll("", "'");

			mCursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
					+ " WHERE " + DBData.MUSIC_ALBUM + "='" + nAlbum + "'",
					null);
			List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
			if (mCursor != null && mCursor.getCount() > 0) {
				AlbumInfo mAlbumInfo = new AlbumInfo();
				while (mCursor.moveToNext()) {
					MusicInfo musicInfo = new MusicInfo();
					final String file = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_FILE));
					final String name = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_NAME));
					final String path = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_PATH));
					final int favorite = mCursor.getInt(mCursor
							.getColumnIndex(DBData.MUSIC_FAVORITE));
					final String time = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_TIME));
					final String size = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_SIZE));
					final String artist = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_ARTIST));
					final String format = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_FORMAT));
					final String album = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_ALBUM));
					final String years = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_YEARS));
					final String channels = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_CHANNELS));
					final String genre = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_GENRE));
					final String kbps = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_KBPS));
					final String hz = mCursor.getString(mCursor
							.getColumnIndex(DBData.MUSIC_HZ));
					final int id = mCursor.getInt(mCursor
							.getColumnIndex(DBData.MUSIC_ID));
					musicInfo.setFile(file);
					musicInfo.setName(name);
					musicInfo.setPath(path);
					musicInfo.setFavorite(favorite == 1 ? true : false);
					musicInfo.setTime(time);
					musicInfo.setSize(size);
					musicInfo.setArtist(artist);
					musicInfo.setFormat(format);
					musicInfo.setAlbum(album);
					musicInfo.setYears(years);
					musicInfo.setChannels(channels);
					musicInfo.setGenre(genre);
					musicInfo.setKbps(kbps);
					musicInfo.setHz(hz);
					musicInfo.setId(id);

					String pinyin = mCharacterParser.getSelling(name);
					String sortString = pinyin.substring(0, 1).toUpperCase();

					// 正则表达式，判断首字母是否是英文字母
					if (sortString.matches("[A-Z]")) {
						musicInfo.setSortLetters(sortString.toUpperCase());
					} else {
						musicInfo.setSortLetters("#");
					}

					// 加入文件夹临时列表
					listInfo.add(musicInfo);
					MusicList.list.add(musicInfo);
					MusicList.sort();
					if (favorite == 1) {
						Flog.d(TAG, "favorite" + FavoriteList.list.size());
						FavoriteList.list.add(musicInfo);
						FavoriteList.sort();
					}

				}
				mAlbumInfo.setAlbum(nAlbum);
				mAlbumInfo.setMusicList(listInfo);
				mAlbumInfo.sort();
				Flog.d(TAG,
						"queryAlbum()---mAlbumInfo--" + mAlbumInfo.toString());
				// mAlbumInfos.add(mAlbumInfo);
				AlbumList.list.add(mAlbumInfo);
			}
		}
		if (mCursor != null) {
			mCursor.close();
		}
		Flog.d(TAG, "DBDao-----queryAlbum()--end");
	}

}
