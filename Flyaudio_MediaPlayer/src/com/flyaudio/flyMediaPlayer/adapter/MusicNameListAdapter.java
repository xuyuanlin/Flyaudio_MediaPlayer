package com.flyaudio.flyMediaPlayer.adapter;

import java.util.ArrayList;
import java.util.List;
import com.flyaudio.flyMediaPlayer.objectInfo.MusicInfo;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaService;
import com.flyaudio.flyMediaPlayer.until.AlbumUtil;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyAudio.flyMediaPlayer.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicNameListAdapter extends BaseAdapter implements
		OnClickListener {
	private static final String TAG = "MusicNameListAdapter";
	private Context mContext;
	private LayoutInflater layoutInflater;
	// private List<MusicInfo> list = new ArrayList<MusicInfo>();
	private int nPage;

	public MusicNameListAdapter(Context context, int page) {
		Flog.d(TAG, "MusicNameListAdapter");
		mContext = context;
		this.nPage = page;
		layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getCount()--start");
		Flog.d(TAG,
				"getCount()--MusicList.list.size()--" + MusicList.list.size());
		return MusicList.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItem");
		return MusicList.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItemId");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getView()------start");
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater
					.inflate(R.layout.music_name_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mSong = (TextView) convertView
					.findViewById(R.id.song_item);
			viewHolder.mAlbum = (ImageView) convertView
					.findViewById(R.id.album_item);
			viewHolder.mArtist = (TextView) convertView
					.findViewById(R.id.artist_item);
			viewHolder.mFavorites = (ImageButton) convertView
					.findViewById(R.id.favorites_item);
			viewHolder.mDetail = (ImageButton) convertView
					.findViewById(R.id.detail_item);
			viewHolder.mClear = (ImageButton) convertView
					.findViewById(R.id.clear_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (MusicList.list.size() > 0) {
			viewHolder.mSong.setText(MusicList.list.get(position).getName());

			viewHolder.mArtist
					.setText(MusicList.list.get(position).getArtist());

			viewHolder.mAlbum.setImageResource(R.drawable.main_album_item);

			viewHolder.mFavorites.setImageResource(MusicList.list.get(position)
					.isFavorite() ? R.drawable.main_favorites
					: R.drawable.main_favorites_u);
			// viewHolder.mMenu.setTag(position);
			// viewHolder.mMenu.setOnClickListener(this);

			if (MediaService.sMusicPath != null) {
				Flog.d(TAG, "getView()--MediaService.sMusicPath--"
						+ MediaService.sMusicPath);
				Flog.d(TAG, "getView()--position==" + position);
				for (int i = 0; i < MusicList.list.size(); i++) {

					if (MusicList.list.get(i).getPath()
							.equals(MediaService.sMusicPath)) {
						Flog.d(TAG, "getView()--if--i==" + i);
						if (i == position) {
							viewHolder.mSong.setTextColor(Color.BLUE);
							viewHolder.mArtist.setTextColor(Color.BLUE);
							break;
						}
					} else {
						viewHolder.mSong.setTextColor(Color.WHITE);
						viewHolder.mArtist.setTextColor(Color.WHITE);
					}
				}

			}

			viewHolder.mClear.setTag(position);
			viewHolder.mClear.setOnClickListener(this);
			viewHolder.mDetail.setTag(position);
			viewHolder.mDetail.setOnClickListener(this);
			viewHolder.mFavorites.setTag(position);
			viewHolder.mFavorites.setOnClickListener(this);
		}

		Flog.d(TAG, "getView-----end");

		return convertView;
	}

	public void update(int page) {
		this.nPage = page;
		notifyDataSetChanged();

	}

	public int getPage() {
		return nPage;
	}

	class ViewHolder {
		public TextView mSong;
		public TextView mArtist;
		public ImageView mAlbum;
		public ImageButton mFavorites;
		public ImageButton mDetail;
		public ImageButton mClear;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		Flog.d(TAG, "onClick()--tag--" + v.getTag());
		switch (v.getId()) {
		case R.id.favorites_item:
			intent = new Intent(Constant.BROADCAST_ACTION_FAVORITE);
			break;
		case R.id.clear_item:
			intent = new Intent(Constant.BROADCAST_ACTION_CLEAR);
			break;
		case R.id.detail_item:
			intent = new Intent(Constant.BROADCAST_ACTION_DETAIL);
			break;
		}
		if (intent != null) {
			intent.putExtra(Constant.BROADCAST_INTENT_PAGE, nPage);
			intent.putExtra(Constant.BROADCAST_INTENT_POSITION,
					(Integer) v.getTag());
			mContext.sendBroadcast(intent);
		}
	}
}
