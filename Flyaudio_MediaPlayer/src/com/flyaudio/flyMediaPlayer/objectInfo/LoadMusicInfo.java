package com.flyaudio.flyMediaPlayer.objectInfo;

public class LoadMusicInfo {

	String singername;
	String songname;
	String ablumname;
	int songid;
	int pubtime;
	public String getSingername() {
		return singername;
	}
	public void setSingername(String singername) {
		this.singername = singername;
	}
	public String getSongname() {
		return songname;
	}
	public void setSongname(String songname) {
		this.songname = songname;
	}
	public String getAblumname() {
		return ablumname;
	}
	public void setAblumname(String ablumname) {
		this.ablumname = ablumname;
	}
	public int getSongid() {
		return songid;
	}
	public void setSongid(int songid) {
		this.songid = songid;
	}
	public int getPubtime() {
		return pubtime;
	}
	public void setPubtime(int pubtime) {
		this.pubtime = pubtime;
	}
	@Override
	public String toString() {
		return "LoadMusicInfo [singername=" + singername + ", songname="
				+ songname + ", ablumname=" + ablumname + ", songid=" + songid
				+ ", pubtime=" + pubtime + "]";
	}
	
}
