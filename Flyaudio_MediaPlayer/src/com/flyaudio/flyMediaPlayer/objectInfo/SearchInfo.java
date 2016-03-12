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
public class SearchInfo {

	private String mName;
	private String mPath;
	public String getnPath() {
		return mPath;
	}
	public void setnPath(String mPath) {
		this.mPath = mPath;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	@Override
	public String toString() {
		return "SearchInfo [mName=" + mName + ", mPath=" + mPath + "]";
	}

	
}
