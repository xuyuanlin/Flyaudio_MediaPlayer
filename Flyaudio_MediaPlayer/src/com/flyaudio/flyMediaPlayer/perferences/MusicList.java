package com.flyaudio.flyMediaPlayer.perferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.until.PinyinComparator;

public class MusicList {

	public static List<MusicInfo> list = new ArrayList<MusicInfo>();

	// public static final List<MusicInfo> aristList = new
	// ArrayList<MusicInfo>();
	// public static final List<MusicInfo> albumList = new
	// ArrayList<MusicInfo>();
	/**
	 * 按字母排序
	 */
	public static void sort() {
		Collections.sort(list, new PinyinComparator());
	}

}
