package com.flyaudio.flyMediaPlayer.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.activity.MainActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;



public class DeleteDialog extends TVAnimDialog implements
		android.view.View.OnClickListener {

	private Button positive;
	private Button negative;

	public DeleteDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DeleteDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected DeleteDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_delete);

		positive = (Button) findViewById(R.id.dialog_delete_btn_positive);
		negative = (Button) findViewById(R.id.dialog_delete_btn_negative);
		positive.setOnClickListener(this);
		negative.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dialog_delete_btn_positive:
			setDialogId(Constant.DIALOG_DELETE);
			break;

		case R.id.dialog_delete_btn_negative:
			setDialogId(Constant.DIALOG_DISMISS);
			break;
		}
		dismiss();
	}

}
