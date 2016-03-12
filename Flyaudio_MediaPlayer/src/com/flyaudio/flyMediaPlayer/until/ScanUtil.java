package com.flyaudio.flyMediaPlayer.until;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.data.DBDao;
import com.flyaudio.flyMediaPlayer.objectInfo.FolderInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.ScanInfo;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
import com.flyaudio.flyMediaPlayer.perferences.FolderList;
import com.flyaudio.flyMediaPlayer.perferences.LyricList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;

public class ScanUtil {
	private static String TAG = "ScanUtil";
	private Context mContext;
	private DBDao db;
	List<String> list;
	private List<String> filelist;
	List<String> listLrc;
	String[] str = { "mp3", "ape", "atrial", "dts", "ac3", "mp2", "flac", "ra",
			"vorbis", "aac", "hevc", "divx", "flv1", "vc1", "vpx", "h264",
			"rv", "wav", "mpeg4", "h263", "mpeg2v", "m4a", "flac", "flv",
			"mpg", "vob", "wmv" };
	String[] audioString = { "aac", "mp3", "vorbis", "wma", "ra", "flac",
			"mp2", "ac3", "ape", "dts", "atrial", "m4a", "wav", "lrc" };
	String[] videosStrings = { "mpeg2v", "h263", "h264", "mpeg4", "wmv", "rv",
			"vpx", "vc", "flv1", "divx", "hevc", "vtrial" };
	private String path;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	String music;
	String end;
	String name;
	String folder;
	public static int count = 0;// 统计新增的数

	public ScanUtil(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		list = new ArrayList<String>();
		filelist = new ArrayList<String>();
		listLrc = new ArrayList<String>();
	}

	public List<ScanInfo> searchAllDirectory() {
		Flog.d(TAG, "ScanUtil--searchAllDirectory");
		List<ScanInfo> list = new ArrayList<ScanInfo>();

		StringBuffer sb = new StringBuffer();
		String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA };
		Cursor cr = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Audio.Media.DISPLAY_NAME);

		String displayName = null;
		String data = null;
		if (cr == null) {
			return list;
		}
		while (cr.moveToNext()) {
			displayName = cr.getString(cr
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			// Flog.d(TAG,"ScanUtil--searchAllDirectory--displayName" +
			// displayName);
			data = cr.getString(cr.getColumnIndex(MediaStore.Audio.Media.DATA));
			// Flog.d(TAG,"ScanUtil--searchAllDirectory--data1" + data);
			data = data.replace(displayName, "");
			// Flog.d(TAG,"ScanUtil--searchAllDirectory--data2" + data);
			if (!sb.toString().contains(data)) {
				list.add(new ScanInfo(data, true));
				// list.add(new ScanInfo("/Android/data/class", true));
				sb.append(data);
			}
			// Flog.d(TAG,"ScanUtil--searchAllDirectory--" + sb.toString());
		}
		cr.close();
		Flog.d(TAG, "ScanUtil--searchAllDirectory--ed");
		return list;

	}

	public boolean usbExists() {
		File usbFile = new File(Constant.PATH_USB + "/");
		if (!usbFile.exists())
			return false;

		if (usbFile.listFiles() == null)
			return false;
		return usbFile.listFiles().length > 0;
	}

	public boolean sdCardExists() {
		File sdFile = new File(Constant.PATH_SDCARD + "/");
		return sdFile.listFiles() != null;
	}

	/*
	 * 通过递归得到某一路径下所有的目录及其文件 getFiles("mnt/sdcard"); /storage/udisk";
	 */
	public List<String> getFiles(String filePath) {
		Flog.d(TAG, "getFiles");

		File root = new File(filePath);
		File[] files = root.listFiles();
		if (files != null && !files.equals("")) {

			Flog.d(TAG, "getFiles------files-----" + files.toString());
			for (File file : files) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath());
				} else {
					Flog.d(TAG, "显示下所有子目录" + file.getAbsolutePath());
					String fileName = file.getName();
					Flog.d(TAG, "getFiles---fileName---" + fileName);

					if (fileName.contains(".")) {
						String end = fileName.substring(
								fileName.lastIndexOf(".") + 1,
								fileName.length());
						Flog.d(TAG, "getFiles---end---" + end);

						for (int j = 0; j < audioString.length; j++) {
							Flog.d(TAG, "getFiles---for");
							if (end.equalsIgnoreCase(audioString[j])) {
								Flog.d(TAG, "getFiles---for---if");
								list.add(file.getAbsolutePath());

							}
						}
					}
				}
			}
		}
		Flog.d(TAG,
				"getFiles---list-------------------------------------------------------------------------------------------"
						+ list.size());
		Flog.d(TAG, "getFiles---end");
		return list;
	}

	public void scanMusicFromSD(List<String> PathList, Handler handler) {
		Flog.d(TAG, "ScanUtil--scanMusicFromSD");
		int count = 0;// 统计新增的数
		Flog.d(TAG, "scanMusicFromSD()---------FavoriteList.list1111----------"
				+ FavoriteList.list.toString());
		db = new DBDao(mContext);
		db.deleteLyric();// 不做歌词是否存在的判断，全部删除后重新扫描
		db.deleteAll();
		MusicList.list.clear();
		int size = PathList.size();
		characterParser = characterParser.getInstance();

		for (int i = 0; i < size; i++) {
			String path = PathList.get(i);

			String end = path.substring(path.lastIndexOf(".") + 1,
					path.length());
			if (end.equals("lrc")) {

				String subLrcPath = path.substring(path.lastIndexOf("/") + 1,
						path.lastIndexOf("."));

				db.addLyric(subLrcPath, path);

				LyricList.map.put(subLrcPath, path);
			} else {

				List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
				Flog.d(TAG, "scanMusicFromSD()------path----" + path);
				// lyl+:有些文件并没有后缀，所以要判断文件名是否包含“.”
				if (path.contains(".")) {
					end = path.substring(path.lastIndexOf(".") + 1,
							path.length());
					music = path.substring(path.lastIndexOf("/") + 1,
							path.length());
					name = music.substring(0, music.lastIndexOf("."));
					folder = path.substring(0, path.lastIndexOf("/"));
				}
				Flog.d(TAG,
						"scanMusicFromSD()---------FavoriteList.list1---name------"
								+ name);
				for (int j = 0; j < audioString.length; j++) {
					if (end.equalsIgnoreCase(audioString[j])) {
						// if (!db.queryExist(fileName, folder)) {
						MusicInfo musicInfo = scanMusicTag(music, path);

						musicInfo.setPath(path);
						musicInfo.setName(name);
						musicInfo.setFile(folder);
						musicInfo.setFormat(end);

						String pinyin = characterParser.getSelling(name);
						String sortString = pinyin.substring(0, 1)
								.toUpperCase();

						// 正则表达式，判断首字母是否是英文字母
						if (sortString.matches("[A-Z]")) {
							musicInfo.setSortLetters(sortString.toUpperCase());
						} else {
							musicInfo.setSortLetters("#");
						}
						if (musicInfo.getArtist() == null
								|| musicInfo.getArtist().equals("")) {
							musicInfo.setArtist(mContext.getResources()
									.getString(R.string.xml_music_info));
						}
						if (musicInfo.getAlbum() == null
								|| musicInfo.getAlbum().equals("")) {
							musicInfo.setAlbum(mContext.getResources()
									.getString(R.string.xml_music_info));
						}
						if (FavoriteList.list.size() <= 0) {
							Flog.d(TAG, "scanMusicFromSDxyz------" + music);
							Flog.d(TAG, "scanMusicFromSDxyz------" + name);
							Flog.d(TAG, "scanMusicFromSDxyz------" + path);
							Flog.d(TAG, "scanMusicFromSDxyz------" + folder);
							db.add(music, name, path, folder, false,
									musicInfo.getTime(), musicInfo.getSize(),
									musicInfo.getArtist(),
									musicInfo.getFormat(),
									musicInfo.getAlbum(), musicInfo.getYears(),
									musicInfo.getChannels(),
									musicInfo.getGenre(), musicInfo.getKbps(),
									musicInfo.getHz());
						} else

						{
							Flog.d(TAG,
									"scanMusicFromSD()---------FavoriteList.list1");
							Flog.d(TAG,
									"scanMusicFromSD()---------FavoriteList.list2----------"
											+ FavoriteList.list.toString());
							for (int k = 0; k < FavoriteList.list.size(); k++) {
								Flog.d(TAG,
										"scanMusicFromSD()---------FavoriteList.list3----------"
												+ FavoriteList.list.get(k)
														.getName());
								if (path.equals(FavoriteList.list.get(k)
										.getPath())) {

									Flog.d(TAG,
											"scanMusicFromSD()---------FavoriteList.list----------"
													+ path);

									musicInfo.setFavorite(FavoriteList.list
											.get(k).isFavorite());
								}
							}
							Flog.d(TAG, "scanMusicFromSDxyz------" + music);
							Flog.d(TAG, "scanMusicFromSDxyz------" + name);
							Flog.d(TAG, "scanMusicFromSDxyz------" + path);
							Flog.d(TAG, "scanMusicFromSDxyz------" + folder);

							db.add(music, name, path, folder,
									musicInfo.isFavorite(),
									musicInfo.getTime(), musicInfo.getSize(),
									musicInfo.getArtist(),
									musicInfo.getFormat(),
									musicInfo.getAlbum(), musicInfo.getYears(),
									musicInfo.getChannels(),
									musicInfo.getGenre(), musicInfo.getKbps(),
									musicInfo.getHz());

						}
						Flog.d(TAG,
								"scanMusicFromSD()---------musicInfo----------"
										+ musicInfo.toString());
						if (handler != null) {
							Message msg = handler.obtainMessage();
							msg.obj = music;

							msg.sendToTarget();
						}
						count++;
						MusicList.list.add(musicInfo);
						Flog.d(TAG,
								"scanMusicFromSD()---------xyzMusicList.list.size()----------"
										+ MusicList.list.size());
						MusicList.sort();
						listInfo.add(musicInfo);

					}
				}
			}
		}

		/*
		 * listLrc = findFileList("mnt/sdcard/lyric"); if (listLrc == null) {
		 * 
		 * } else {
		 * 
		 * for (int j = 0; j < listLrc.size(); j++) {
		 * 
		 * String lrcPath = listLrc.get(j);
		 * 
		 * String subLrcPath = lrcPath.substring( lrcPath.lastIndexOf("/") + 1,
		 * lrcPath.lastIndexOf("."));
		 * 
		 * db.addLyric(subLrcPath, lrcPath);
		 * 
		 * LyricList.map.put(subLrcPath, lrcPath); } }
		 */

		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.obj = mContext.getResources().getString(
					R.string.xml_scan_finish)
					+ count
					+ mContext.getResources().getString(
							R.string.xml_musicsize_text);

			msg.sendToTarget();
		}
		db.close();
		Flog.d(TAG, "----ScanUtil--LyricList---" + LyricList.map.toString());

		Flog.d(TAG, "----ScanUtil--scanMusicFromSD---" + MusicList.list.size());
		Flog.d(TAG, "----ScanUtil--scanMusicFromSD--end");
	}

	// 遍历文件
	public List<String> findFileList(String strPath) {
		Flog.d(TAG, "scanutil---findfilelist()");
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				findFileList(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath();

				String end = strFileName.substring(
						strFileName.lastIndexOf(".") + 1, strFileName.length());
				if (end.equalsIgnoreCase("lrc") || end.equalsIgnoreCase("trc")) {
					filelist.add(files[i].getAbsolutePath());
				}

			}
		}
		Flog.d(TAG, "scanutil---findfilelist()---list---" + filelist.toString());
		Flog.d(TAG, "scanutil---findfilelist()---end");
		return filelist;

	}

	public void scanMusicFromDB() {
		Flog.d(TAG, "ScanUtil--scanMusicFromDB");
		db = new DBDao(mContext);
		db.queryAll();
		// db.close();
	}

	// 获取音乐的信息
	public MusicInfo scanMusicTag(String fileName, String path) {
		Flog.d(TAG, "ScanUtil--scanMusicTag");

		Flog.d(TAG, "ScanUtil--scanMusicTag---fileName---" + fileName + "---"
				+ path);
		File file = new File(path);
		MusicInfo info = new MusicInfo();
		characterParser = CharacterParser.getInstance();

		if (file.exists()) {
			try {
				// MP3File mp3File = (MP3File) AudioFileIO.read(file);
				// MP3AudioHeader header = mp3File.getMP3AudioHeader();
				AudioFile mp3File = AudioFileIO.read(file);
				Flog.d(TAG, "-------scanMusicTag-------" + mp3File.toString());
				AudioHeader header = mp3File.getAudioHeader();
				Flog.d(TAG, "-------scanMusicTag-------" + header.toString());
				// info.setPath(path);

				// info.setFile(fileName);
				// 时间
				info.setTime(FormatUtil.formatTime((int) (header
						.getTrackLength() * 1000)));
				info.setSize(FormatUtil.formatSize(file.length()));

				info.setFormat(header.getEncodingType());
				final String channels = header.getChannels();
				if (channels.equals("Joint Stereo")) {
					info.setChannels(mContext.getResources().getString(
							R.string.xml_music_channels_normal));
				} else {
					info.setChannels(mContext.getResources().getString(
							R.string.xml_music_channels)
							+ header.getChannels());
				}
				info.setKbps(header.getBitRate()
						+ mContext.getResources().getString(
								R.string.xml_music_person_kpbs));
				info.setHz(header.getSampleRate()
						+ mContext.getResources().getString(
								R.string.xml_music_person_hz));

				/* if (mp3File.) { */
				Tag tag = mp3File.getTag();
				try {
					// 设置歌曲名
					final String tempName = tag.getFirst(FieldKey.TITLE);
					Flog.d(TAG, "--ScanUtil---tempName----" + tempName);
					Flog.d(TAG, "---ScanUtil---fileName----" + fileName);
					if (tempName == null || tempName.equals("")) {
						info.setName(fileName);

					} else {
						// info.setName((tempName));
						info.setName(fileName);
					}

				} catch (KeyNotFoundException e) {
					// TODO Auto-generated catch block
					info.setName(fileName);
				}

				try {
					// 设置歌手
					final String tempArtist = tag.getFirst(FieldKey.ARTIST);
					Flog.d(TAG, "-----tempArtist----" + tempArtist);
					if (tempArtist == null || tempArtist.equals("")) {
						info.setArtist(mContext.getResources().getString(
								R.string.xml_music_info));
					} else {
						info.setArtist(tempArtist);
					}
				} catch (KeyNotFoundException e) {
					// TODO Auto-generated catch block
					info.setArtist(mContext.getResources().getString(
							R.string.xml_music_info));
				}

				try {
					// 设置专辑
					final String tempAlbum = tag.getFirst(FieldKey.ALBUM);
					if (tempAlbum == null || tempAlbum.equals("")) {
						info.setAlbum(mContext.getResources().getString(
								R.string.xml_music_info));
					} else {
						info.setAlbum((tempAlbum));
					}
				} catch (KeyNotFoundException e) {
					// TODO Auto-generated catch block
					info.setAlbum(mContext.getResources().getString(
							R.string.xml_music_info));
				}

				try {
					// 设置年代
					final String tempYears = tag.getFirst(FieldKey.YEAR);
					if (tempYears == null || tempYears.equals("")) {
						info.setYears(mContext.getResources().getString(
								R.string.xml_music_info));
					} else {
						info.setYears(tempYears);

					}
				} catch (KeyNotFoundException e) {
					// TODO Auto-generated catch block
					info.setYears(mContext.getResources().getString(
							R.string.xml_music_info));
				}

				try {
					// 设置风格
					final String tempGener = tag.getFirst(FieldKey.GENRE);
					if (tempGener == null || tempGener.equals("")) {
						info.setGenre(mContext.getResources().getString(
								R.string.xml_music_info));
					} else {
						info.setGenre(tempGener);
					}
				} catch (KeyNotFoundException e) {
					// TODO Auto-generated catch block
					info.setGenre(mContext.getResources().getString(
							R.string.xml_music_info));
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Flog.d(TAG, "ScanUtil--scanMusicTag" + info.toString());

		return info;
	}

}
