package com.flyaudio.flyMediaPlayer.dialog;
import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.activity.MainActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Open Source Project
 * 
 * <br>
 * <b>歌曲列表菜单对话框</b></br>
 * 
 * <br>
 * 带电视机开关效果的弹出菜单</br>
 * 
 *  显示对话框界面
 */
public class MenuDialog extends TVAnimDialog implements
		android.view.View.OnClickListener {

	private TextView title;
	private TextView remove;
	private TextView delete;
	private TextView info;
	private TextView ringtone;
	private ImageButton share;
	private Button button;

	public MenuDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MenuDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected MenuDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_menu);

		title = (TextView) findViewById(R.id.dialog_menu_tv_title);
		remove = (TextView) findViewById(R.id.dialog_menu_tv_remove);
		delete = (TextView) findViewById(R.id.dialog_menu_tv_delete);
		info = (TextView) findViewById(R.id.dialog_menu_tv_info);
		ringtone = (TextView) findViewById(R.id.dialog_menu_tv_ringtone);
		share = (ImageButton) findViewById(R.id.dialog_menu_btn_share);
		button = (Button) findViewById(R.id.dialog_menu_btn_return);
		remove.setOnClickListener(this);
		delete.setOnClickListener(this);
		info.setOnClickListener(this);
		ringtone.setOnClickListener(this);
		button.setOnClickListener(this);
		share.setOnClickListener(this);
	}

	/**
	 * 设置对话框标题
	 * 
	 * @param text
	 *            内容
	 */
	public void setDialogTitle(String text) {
		title.setText(text);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dialog_menu_tv_remove:
			setDialogId(Constant.DIALOG_MENU_REMOVE);
			break;

		case R.id.dialog_menu_tv_delete:
			setDialogId(Constant.DIALOG_MENU_DELETE);
			break;

		case R.id.dialog_menu_tv_info:
			setDialogId(Constant.DIALOG_MENU_INFO);
			break;
		case R.id.dialog_menu_tv_ringtone:
			setDialogId(Constant.DIALOG_MENU_RINGTONE);
			break;
		case R.id.dialog_menu_btn_share:
			setDialogId(Constant.DIALOG_MENU_SHARE);
			break;
		default:
			setDialogId(Constant.DIALOG_DISMISS);
			break;
		}
		dismiss();
	}

}