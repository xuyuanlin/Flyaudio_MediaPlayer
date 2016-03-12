package com.flyaudio.flyMediaPlayer.objectInfo;

public class ScanInfo {

	private String folderPath;// 文件夹的路径
	private boolean isChecked;// 是否选择

	public ScanInfo(String data,boolean isChecked){
		this.folderPath=data;
		this.isChecked=isChecked;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "ScanInfo [folderPath=" + folderPath + ", isChecked="
				+ isChecked + "]";
	}

}
