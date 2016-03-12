package com.flyaudio.flyMediaPlayer.objectInfo;

import java.util.Collections;
import java.util.List;

import com.flyaudio.flyMediaPlayer.until.PinyinComparator;

import android.graphics.Bitmap;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>歌曲文件夹对应的歌曲信息</b></br> 创建文件夹歌曲列表<br>
 * 修正错误，创建和设定歌曲列表</br>
 */
public class AristInfo {

	

	@Override
	public String toString() {
		return "AristInfo [arist=" + arist + ", aristCount=" + aristCount
				+ ", musicPath=" + musicPath + ", musicList=" + musicList + "]";
	}
	// private String musicFolder;// 歌曲隶属文件夹
	private String arist;
	private int aristCount;
	private String musicPath;
	private List<MusicInfo> musicList;// 歌曲列表

	public String getArist() {
		return arist;
	}

	public void setArist(String arist) {
		this.arist = arist;
	}

	public int getAristCount() {
		return aristCount;
	}

	public void setAristCount(int aristCount) {
		this.aristCount = aristCount;
	}

	

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}



	/**
	 * 获得文件夹下的歌曲列表
	 * 
	 * @return 歌曲列表
	 */
	public List<MusicInfo> getMusicList() {
		return musicList;
	}

	/**
	 * 设置文件夹下歌曲列表
	 * 
	 * @param musicList
	 *            歌曲列表
	 */
	public void setMusicList(List<MusicInfo> musicList) {
		this.musicList = musicList;
	}
	/**
	 * 按字母排序
	 */
	public  void sort() {
		Collections.sort(musicList, new PinyinComparator());
	}

}
