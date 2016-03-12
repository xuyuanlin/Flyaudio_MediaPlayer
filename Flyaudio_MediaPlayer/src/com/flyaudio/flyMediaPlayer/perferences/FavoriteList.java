package com.flyaudio.flyMediaPlayer.perferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.until.PinyinComparator;

/**
 *  Open Source Project
 * 
 * <br>
 * <b>创建一个公用的最喜爱歌曲列表</b></br>
 * 
 *  新增按字母排序方法</br>
 */
public class FavoriteList {

	public static  List<MusicInfo> list = new ArrayList<MusicInfo>();

	/**
	 * 按字母排序
	 */
	public static void sort() {
		Collections.sort(list, new PinyinComparator());
	}

}
