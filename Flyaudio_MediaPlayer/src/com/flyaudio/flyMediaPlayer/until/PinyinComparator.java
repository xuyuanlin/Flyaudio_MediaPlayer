package com.flyaudio.flyMediaPlayer.until;

import java.util.Comparator;

import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;


/**
 * 
 * @author Mr.Z
 */
public class PinyinComparator implements Comparator<MusicInfo> {

	public int compare(MusicInfo o1, MusicInfo o2) {
		if(o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if(o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
