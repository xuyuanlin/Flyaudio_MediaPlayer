package com.flyaudio.flyMediaPlayer.sdl;

public class PlayerResult {
	public int initFlag=0;           //初始化：1：成功，0：失败
	public double totalTime=0;   //文件时长（单位：s）
	public int isPlayEnd=0;           //1：播放结束
	public double timeStamp=0; //播放时间timeStamp
	public double filesize=0;//文件大小
	public int isRealease=0;//释放资源
	public PlayerResult() {
		super();
	}
	public int getIsRealease() {
		return isRealease;
	}
	public void setIsRealease(int isRealease) {
		this.isRealease = isRealease;
	}
	public int getInitFlag() {
		return initFlag;
	}
	public double getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(double timeStamp) {
		this.timeStamp = timeStamp;
	}
	public double getFilesize() {
		return filesize;
	}
	public void setFilesize(double filesize) {
		this.filesize = filesize;
	}
	public void setInitFlag(int initFlag) {
		this.initFlag = initFlag;
	}
	
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	public int getIsPlayEnd() {
		return isPlayEnd;
	}
	public void setIsPlayEnd(int isPlayEnd) {
		this.isPlayEnd = isPlayEnd;
	}

}
