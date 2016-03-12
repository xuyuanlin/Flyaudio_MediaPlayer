package com.flyaudio.flyMediaPlayer.objectInfo;

public class AppInfo {
	private String path;
	private int max;
	private String musicName;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public AppInfo(String path, int max, String musicName) {
		super();
		this.path = path;
		this.max = max;
		this.musicName = musicName;
	}

	public AppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

}
