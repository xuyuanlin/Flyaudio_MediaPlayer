package com.flyaudio.flyMediaPlayer.dialog;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.activity.MainActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;


public class TVAnimDialog extends Dialog {

	private int dialogId = Constant.DIALOG_DISMISS;
	private OnTVAnimDialogDismissListener listener;

	public TVAnimDialog(Context context) {
		super(context, R.style.TVAnimDialog);
		// TODO Auto-generated constructor stub
	}

	public TVAnimDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected TVAnimDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setWindowAnimations(R.style.TVAnimDialogWindowAnim);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		
		super.dismiss();
		if (listener != null) {
			listener.onDismiss(dialogId);
		}
	}

	/**
	 * 用于区分Dialog用途
	 * 
	 * @param dialogId
	 *            Dialog ID
	 */
	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 *            OnTVAnimDialogDismissListener
	 */
	public void setOnTVAnimDialogDismissListener(
			OnTVAnimDialogDismissListener listener) {
		this.listener = listener;
	}

	/**
	 * 用于监听对话框关闭的接口
	 */
	public interface OnTVAnimDialogDismissListener {
		/**
		 * 对话框关闭
		 * 
		 * @param dialogId
		 *            Dialog ID
		 */
		void onDismiss(int dialogId);
	}

}
