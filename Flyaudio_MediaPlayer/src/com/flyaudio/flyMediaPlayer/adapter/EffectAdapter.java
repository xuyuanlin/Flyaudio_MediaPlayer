package com.flyaudio.flyMediaPlayer.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.flyaudio.flyMediaPlayer.objectInfo.ScanInfo;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyAudio.flyMediaPlayer.R;

public class EffectAdapter extends BaseAdapter {
	private static final String TAG = "EffectAdapter";
	private LayoutInflater inflater;

	private String[] mList;

	public EffectAdapter(Context context, String[] mList) {
		// TODO Auto-generated constructor stub
		this.inflater = LayoutInflater.from(context);

		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.effect_item, null);
			holder.mEffectItem = (TextView) convertView
					.findViewById(R.id.effct_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mList[position].equals("Normal")) {
			holder.mEffectItem.setText(mList[position] + Constant.Normal);
		} else if (mList[position].equals("Classical")) {
			holder.mEffectItem.setText(mList[position] + Constant.Classical);
		} else if (mList[position].equals("Dance")) {
			holder.mEffectItem.setText(mList[position] + Constant.Dance);
		} else if (mList[position].equals("Flat")) {
			holder.mEffectItem.setText(mList[position] + Constant.Flat);
		} else if (mList[position].equals("Folk")) {
			holder.mEffectItem.setText(mList[position] + Constant.Folk);
		} else if (mList[position].equals("Heavy Metal")) {
			holder.mEffectItem.setText(mList[position] + Constant.HeavyMetal);
		} else if (mList[position].equals("Hip Hop")) {
			holder.mEffectItem.setText(mList[position] + Constant.HipHop);
		} else if (mList[position].equals("Jazz")) {
			holder.mEffectItem.setText(mList[position] + Constant.Jazz);
		} else if (mList[position].equals("Pop")) {
			holder.mEffectItem.setText(mList[position] + Constant.Pop);
		} else if (mList[position].equals("Rock")) {
			holder.mEffectItem.setText(mList[position] + Constant.Rock);
		} else if (mList[position].equals("FX booster")) {
			holder.mEffectItem.setText(mList[position]);
		} else if (mList[position].equals("自定义")) {
			holder.mEffectItem.setText("Customer" + "/自定义");
		} else {
			Flog.d(TAG, "mList[position]---" + mList[position] + "-");
			holder.mEffectItem.setText(mList[position]);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView mEffectItem;
	}

}
