package com.flyaudio.flyMediaPlayer.serviceImpl;

import android.os.Binder;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyaudio.flyMediaPlayer.view.MyLyricView;

public class MediaBinder extends Binder {
	private static String TAG = "MediaBinder";
	private OnPlayStartListener onPlayStartListener;
	private OnPlayingListener onPlayingListener;
	private OnPlayPauseListener onPlayPauseListener;
	private OnPlayCompleteListener onPlayCompleteListener;
	private OnPlayErrorListener onPlayErrorListener;
	private OnModeChangeListener onModeChangeListener;

	private OnServiceBinderListener onServiceBinderListener;

	protected void playStart(MusicInfo info, int page) {
		if (onPlayStartListener != null) {
			onPlayStartListener.onStart(info, page);
		}
	}

	protected void playUpdate(int currentPosition, String path, int page,
			MusicInfo info) {
		if (onPlayingListener != null) {
			onPlayingListener.onPlay(currentPosition, path, page, info);
		}
	}

	protected void playPause() {
		if (onPlayPauseListener != null) {
			onPlayPauseListener.onPause();
		}
	}

	protected void playComplete() {
		if (onPlayCompleteListener != null) {
			onPlayCompleteListener.onPlayComplete();
		}
	}

	protected void playError() {
		if (onPlayErrorListener != null) {
			onPlayErrorListener.onPlayError();
		}
	}

	protected void modeChange(int mode) {
		if (onModeChangeListener != null) {
			onModeChangeListener.onModeChange(mode);
		}
	}

	/**
	 * 触及SeekBar响应
	 */
	public void seekBarStartTrackingTouch() {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.seekBarStartTrackingTouch();
		}
	}

	/**
	 * 移开SeekBar
	 * 
	 * @param progress
	 * 
	 */
	public void seekBarStopTrackingTouch(int progress) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.seekBarStopTrackingTouch(progress);
		}
	}

	public void setLyricView(MyLyricView lvView1) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.lrc(lvView1);
		}
	}

	public void setControlCommand(int command) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.control(command);
		}
	}

	public void setOnPlayStartListener(OnPlayStartListener onPlayStartListener) {
		this.onPlayStartListener = onPlayStartListener;
	}

	public void setOnPlayingListener(OnPlayingListener onPlayingListener) {
		this.onPlayingListener = onPlayingListener;
	}

	public void setOnPlayPauseListener(OnPlayPauseListener onPlayPauseListener) {
		this.onPlayPauseListener = onPlayPauseListener;
	}

	public void setOnPlayCompletionListener(
			OnPlayCompleteListener onPlayCompleteListener) {
		this.onPlayCompleteListener = onPlayCompleteListener;
	}

	public void setOnPlayErrorListener(OnPlayErrorListener onPlayErrorListener) {
		this.onPlayErrorListener = onPlayErrorListener;
	}

	public void setOnModeChangeListener(
			OnModeChangeListener onModeChangeListener) {
		this.onModeChangeListener = onModeChangeListener;
	}

	protected void setOnServiceBinderListener(
			OnServiceBinderListener onServiceBinderListener) {
		this.onServiceBinderListener = onServiceBinderListener;
	}

	public interface OnPlayStartListener {

		public void onStart(MusicInfo info, int page);
	}

	public interface OnPlayingListener {

		public void onPlay(int currentPosition, String path, int page,
				MusicInfo info);
	}

	public interface OnPlayPauseListener {

		public void onPause();
	}

	// 播放完成时
	public interface OnPlayCompleteListener {

		public void onPlayComplete();
	}

	public interface OnPlayErrorListener {

		public void onPlayError();
	}

	public interface OnModeChangeListener {

		public void onModeChange(int mode);
	}

	protected interface OnServiceBinderListener {

		void seekBarStartTrackingTouch();

		void seekBarStopTrackingTouch(int progress);

		// 设置歌词
		// void lrc(LyricView lyricView, LrcView lrcView, boolean isKLOK);
		void lrc(MyLyricView lvView1);

		/*
		 * 控制播放
		 */void control(int command);
	}

}
