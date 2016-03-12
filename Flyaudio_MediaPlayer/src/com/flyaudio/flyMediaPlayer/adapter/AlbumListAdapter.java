package com.flyaudio.flyMediaPlayer.adapter;

import java.util.List;

import com.flyaudio.flyMediaPlayer.objectInfo.AlbumInfo;
import com.flyaudio.flyMediaPlayer.objectInfo.AristInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;
import com.flyaudio.flyMediaPlayer.perferences.FavoriteList;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlbumListAdapter extends BaseAdapter implements OnClickListener {
	private static final String TAG = "AlbumListAdapter";
	private Context mContext;
	private LayoutInflater layoutInflater;
	private List<AlbumInfo> mAlbumInfo;
	private int nAlbumPosition;
	private int page;
	private AlbumUtil mAlbumUtil;

	public AlbumListAdapter(Context context, List<AlbumInfo> mAlbumInfo,
			int page,AlbumUtil mAlbumUtil) {
		this.mAlbumUtil=mAlbumUtil;
		mContext = context;
		this.mAlbumInfo = mAlbumInfo;
		this.page = page;
		layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getCount()--start");

		int count = 0;
		switch (page) {
		case Constant.VIEWPAHER_MENU_ALBUM:
			if (mAlbumInfo != null) {
				count = mAlbumInfo.size();
			} else {
				count = 0;
			}

			break;
		case Constant.VIEWPAHER_MENU_ALBUM_LIST:
			// count = MusicList.albumList.size();
			if (AlbumList.list.size() > nAlbumPosition) {
				count = AlbumList.list.get(nAlbumPosition).getMusicList()
						.size();
			} else {
				count = 0;
			}

			break;
		}
		Flog.d(TAG, "getCount--count--" + count);
		Flog.d(TAG, "getCount()--end");
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItem");
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItemId");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Flog.d(TAG, "getView()------start");
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.music_album_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mAlbumView = (LinearLayout) convertView
					.findViewById(R.id.album_view);
			viewHolder.mYear = (TextView) convertView
					.findViewById(R.id.album_list_year);
			viewHolder.mAlbum = (ImageView) convertView
					.findViewById(R.id.album_list);
			viewHolder.mAlbum_txt = (TextView) convertView
					.findViewById(R.id.album_list_artist);
			viewHolder.mAlbumItemView = (LinearLayout) convertView
					.findViewById(R.id.album_itemView);
			viewHolder.mSongItem = (TextView) convertView
					.findViewById(R.id.song_item);
			viewHolder.mAlbumItem = (ImageView) convertView
					.findViewById(R.id.album_item);
			viewHolder.mArtistItem = (TextView) convertView
					.findViewById(R.id.artist_item);
			viewHolder.mFavoritesItem = (ImageButton) convertView
					.findViewById(R.id.favorites_item);
			viewHolder.mDetailItem = (ImageButton) convertView
					.findViewById(R.id.detail_item);
			viewHolder.mClearItem = (ImageButton) convertView
					.findViewById(R.id.clear_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		switch (page) {
		case Constant.VIEWPAHER_MENU_ALBUM:
			if (mAlbumInfo != null) {
				if (viewHolder.mAlbumItemView.getVisibility() == View.VISIBLE) {
					viewHolder.mAlbumItemView.setVisibility(View.GONE);
				}
				if (viewHolder.mAlbumView.getVisibility() != View.VISIBLE) {
					viewHolder.mAlbumView.setVisibility(View.VISIBLE);
				}
				viewHolder.mAlbum_txt.setText(mAlbumInfo.get(position)
						.getAlbum());
				viewHolder.mYear.setText(mAlbumInfo.get(position).getYear());
				CoverList.cover=null;
				CoverList.bitmap=null;
				mAlbumUtil.scanAlbumImage(mAlbumInfo.get(position).getMusicPath(), mContext);
				if (CoverList.cover==null) {
					viewHolder.mAlbum.setImageResource(R.drawable.viewpager_album);
				}else {
					viewHolder.mAlbum.setImageDrawable(CoverList.cover);
				}
				

			}
			break;
		case Constant.VIEWPAHER_MENU_ALBUM_LIST:
			if (AlbumList.list.size() > 0) {
				if (viewHolder.mAlbumView.getVisibility() == View.VISIBLE) {
					viewHolder.mAlbumView.setVisibility(View.GONE);
				}
				if (viewHolder.mAlbumItemView.getVisibility() != View.VISIBLE) {
					viewHolder.mAlbumItemView.setVisibility(View.VISIBLE);
				}
				viewHolder.mSongItem.setText(AlbumList.list.get(nAlbumPosition)
						.getMusicList().get(position).getName());

				viewHolder.mArtistItem.setText(AlbumList.list
						.get(nAlbumPosition).getMusicList().get(position)
						.getArtist());

				Flog.d(TAG,
						"VIEWPAHER_MENU_ALBUM_LIST---AlbumList.list.get(nAlbumPosition.getMusicList().get(position).getPath()--"
								+ AlbumList.list.get(nAlbumPosition)
										.getMusicList().get(position).getPath());

				viewHolder.mAlbumItem.setImageResource(R.drawable.main_album_item);

				viewHolder.mFavoritesItem.setImageResource(AlbumList.list
						.get(nAlbumPosition).getMusicList().get(position)
						.isFavorite() ? R.drawable.main_favorites
						: R.drawable.main_favorites_u);

				if (MediaService.sMusicPath != null) {
					for (int i = 0; i < AlbumList.list.get(nAlbumPosition)
							.getMusicList().size(); i++) {
						if (MediaService.sMusicPath.equals(AlbumList.list
								.get(nAlbumPosition).getMusicList()
								.get(position).getPath())) {
							if (i == position) {
								viewHolder.mSongItem.setTextColor(Color.BLUE);
								viewHolder.mArtistItem.setTextColor(Color.BLUE);
								break;
							}

						} else {
							viewHolder.mSongItem.setTextColor(Color.WHITE);
							viewHolder.mArtistItem.setTextColor(Color.WHITE);
						}

					}
				}
				viewHolder.mClearItem.setTag(position);
				viewHolder.mClearItem.setOnClickListener(this);
				viewHolder.mDetailItem.setTag(position);
				viewHolder.mDetailItem.setOnClickListener(this);
				viewHolder.mFavoritesItem.setTag(position);
				viewHolder.mFavoritesItem.setOnClickListener(this);
			}

			break;

		}

		Flog.d(TAG, "getView()-----end");
		return convertView;
	}

	public void setAlbumPosition(int position) {
		this.nAlbumPosition = position;
	}

	public void setAlbumIfo(List<AlbumInfo> mAlbumInfos) {
		this.mAlbumInfo = mAlbumInfos;
	}

	public void update(int page) {
		this.page = page;
		notifyDataSetChanged();
	}

	// 返回页面的状态

	public int getPage() {
		return page;
	}

	class ViewHolder {
		public TextView mAlbum_txt;
		public TextView mYear;
		public ImageView mAlbum;
		private LinearLayout mAlbumView;
		private LinearLayout mAlbumItemView;
		public TextView mSongItem;
		public TextView mArtistItem;
		public ImageView mAlbumItem;
		public ImageButton mFavoritesItem;
		public ImageButton mDetailItem;
		public ImageButton mClearItem;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "---onClick()----start");
		Intent intent = null;
		Flog.d(TAG, "---onClick()--tag--" + v.getTag());
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
			intent.putExtra(Constant.BROADCAST_INTENT_PAGE, page);
			intent.putExtra(Constant.BROADCAST_INTENT_POSITION,
					(Integer) v.getTag());
			mContext.sendBroadcast(intent);
		}
		Flog.d(TAG, "---onClick()----end");

	}
}
