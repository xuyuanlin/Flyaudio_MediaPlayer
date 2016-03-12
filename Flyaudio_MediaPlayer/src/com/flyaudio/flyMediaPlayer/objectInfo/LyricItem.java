package com.flyaudio.flyMediaPlayer.objectInfo;

//歌词的信息
public class LyricItem {

	private String lyric;// 单句歌词
	private int time;// 歌词的时间

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "LyricItem [lyric=" + lyric + ", time=" + time + "]";
	}

}